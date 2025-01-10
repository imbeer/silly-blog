#include "PostController.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"
#include "../utils/httpservice.h"

void PostController::get(
    const HttpRequestPtr &req,
    std::function<void (const HttpResponsePtr &)> &&callback,
    const string sort, const string author, const string prompt,
    const int offset, const int limit)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));    

    const auto currentUser = jwtService::getCurrentUserFromRequest(req);

    const auto successHandler = [callbackPtr](const drogon::orm::Result &r) {
        Json::Value postArray(Json::arrayValue);
        cout << r.affectedRows() << endl;
        for (const auto &row : r) {
            Json::Value postJson;
            cout << row["post_id"].as<string>() << endl;
            postJson["post_id"] = row["post_id"].as<int>();
            postJson["text_content"] = row["text_content"].as<string>();
            postJson["time"] = row["time"].as<string>();
            postJson["likes"] = row["likes"].as<int>();
            postJson["is_liked"] = row["is_liked"].as<bool>();
            postJson["author"] = row["author"].as<string>();
            postJson["can_be_edited"] = row["can_be_edited"].as<bool>();

            Json::Value imageIds;
            const auto imageIdsArray = row["image_ids"].asArray<int>();
            for (const auto &imageId : imageIdsArray) {
                imageIds.append(*imageId);
            }
            postJson["image_ids"] = imageIds;
            postArray.append(postJson);
        }
        auto response = HttpResponse::newHttpJsonResponse(postArray);
        response->setStatusCode(drogon::k200OK);
        (*callbackPtr)(response);
    };

    const auto errorHandler = [callbackPtr](const DrogonDbException &e) { httpService::sendEmptyResponse(callbackPtr, k400BadRequest); };

    int authorId = -1;
    if (!author.empty()) {
        try {
            authorId = stoi(author);
            if (authorId < 0) throw runtime_error("negative");
        } catch (const exception &e) {
            cout << "cant parse string" << endl;
            cout << e.what() << endl;
            httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        }
    }

    const string startSql = R"(
        SELECT
        p.post_id as "post_id",
        p.text_content as "text_content",
        p.time as "time",
        COALESCE(like_count.likes, 0) AS likes,
        EXISTS (SELECT 1 FROM "like" l WHERE l.post_id = p.post_id AND l.user_id = $1) AS "is_liked",
        u.username AS author,
        (p.user_id = $1 OR u.is_admin = TRUE) AS "can_be_edited",
        COALESCE(image_array.image_ids, ARRAY[]::integer[]) AS image_ids

        FROM post p
        JOIN "user" u ON p.user_id = u.user_id
        LEFT JOIN (SELECT post_id, COUNT(*) AS likes FROM "like" GROUP BY post_id) like_count ON p.post_id = like_count.post_id
        LEFT JOIN (SELECT post_id, ARRAY_AGG(image_id) AS image_ids FROM image GROUP BY post_id) image_array ON p.post_id = image_array.post_id

        WHERE p.text_content ILIKE '%' || $2 || '%' )";

    string orderSql = "";
    if (sort == "t") { // order by time
        orderSql = "ORDER BY p.time DESC";
    } else if (sort == "l") { // order by likes
        orderSql = "ORDER BY COALESCE(like_count.likes, 0) DESC";
    }

    const string authorSql = author.empty() ? "" : "AND p.user_id = $3 ";
    const string paginateSql = author.empty() ? "LIMIT $3 OFFSET $4;" : "LIMIT $4 OFFSET $5;";

    cout << startSql + authorSql + orderSql + paginateSql << endl;

    if (author.empty()) {
        *m_dbClient
                << startSql + orderSql + paginateSql
                << currentUser->getValueOfUserId()
                << prompt
                << to_string(limit)
                << to_string(offset)
            >>  successHandler
            >>  errorHandler;
    } else {
        *m_dbClient
                << startSql + authorSql + orderSql + paginateSql
                << currentUser->getValueOfUserId()
                << prompt
                << to_string(authorId)
                << to_string(limit)
                << to_string(offset)
            >>  successHandler
            >>  errorHandler;
    }
}

void PostController::create(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto newPost = parseService::getPostFromRequest(*req);
    const auto userId = jwtService::getCurrentUserIdFromRequest(req);

    newPost.setUserId(userId.value());
    newPost.setTime(::trantor::Date::now());

    m_postMapper.insert(
        newPost,
        [callbackPtr, req, this](const drogon_model::blog::Post &post) {
            auto json = Json::Value();
            json["post"] = post.toJson();

            // addPostImages(req, post.getValueOfPostId());

            auto response = HttpResponse::newHttpJsonResponse(json);
            response->setStatusCode(HttpStatusCode::k201Created);
            (*callbackPtr)(response);
        },
        [callbackPtr](const drogon::orm::DrogonDbException &e) {
            httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        });
}

void PostController::update(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const auto editedPost = parseService::getPostFromRequest(*req);

    if (editedPost.getPostId() == nullptr) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        return;
    }
    const auto criteria = Criteria(
        drogon_model::blog::Post::Cols::_post_id,
        CompareOperator::EQ,
        editedPost.getValueOfPostId());

    try {
        auto json = Json::Value();

        auto posts = m_postMapper.limit(1).findBy(criteria);
        if (posts.empty()) throw std::runtime_error("Did not find post");

        auto post = posts[0];
        post.setTextContent(editedPost.getValueOfTextContent());
        m_postMapper.update(post);

        updatePostImages(req, post.getValueOfPostId());

        json["post"] = post.toJson();
        auto resp = HttpResponse::newHttpJsonResponse(json);
        resp->setStatusCode(HttpStatusCode::k200OK);
        (*callbackPtr)(resp);
    } catch (const exception &e) {
        LOG_ERROR << e.what();
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

void PostController::remove(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const optional<int> postIdOptional = parseService::getPostIdFromRequest(*req);
    if (!postIdOptional.has_value() || postIdOptional.value() < 0) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        return;
    }
    const int postId = postIdOptional.value();

    try {
        const auto commentCriteria = Criteria(
            drogon_model::blog::Comment::Cols::_post_id,
            CompareOperator::EQ,
            postId);
        const auto likeCriteria = Criteria(
            drogon_model::blog::Like::Cols::_post_id,
            CompareOperator::EQ,
            postId);
        const auto imageCriteria = Criteria(
            drogon_model::blog::Image::Cols::_post_id,
            CompareOperator::EQ,
            postId);
        m_commentMapper.deleteBy(commentCriteria);
        m_likeMapper.deleteBy(likeCriteria);
        m_imageMapper.deleteBy(imageCriteria);
        m_postMapper.deleteByPrimaryKey(postId);
        httpService::sendEmptyResponse(callbackPtr, k204NoContent);
    } catch (const std::exception &e) {
        cout << e.what() << endl;
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

Json::Value PostController::getImageIdsForPostId(const int& postId)
{
    Json::Value imageIds(Json::arrayValue);

    Criteria imageCriteria = Criteria(
        drogon_model::blog::Image::Cols::_post_id,
        CompareOperator::EQ,
        postId);

    auto images = m_imageMapper.findBy(imageCriteria);

    for (const auto &image : images) {
        imageIds.append(image.getValueOfImageId());
    }
    return imageIds;
}

string PostController::getPostOwnerName(const int &userId)
{
    auto user = m_userMapper.findByPrimaryKey(userId);
    return user.getValueOfUsername();
}

void PostController::updatePostImages(
    const HttpRequestPtr &req,
    const int &postId)
{
    const auto imageIdVector = parseService::getImageIdVectorFromRequest(*req);
    vector<Image> imagesToUpdate;
    vector<Image> existingImages;

    try {
        existingImages = m_imageMapper.findBy(Criteria(
            drogon_model::blog::Image::Cols::_post_id,
            CompareOperator::EQ,
            postId));
    } catch (const DrogonDbException &e) {
        cout << "error while finding existing images" << endl;
    }

    for (const int id : imageIdVector) {
        try {
            const auto image = m_imageMapper.findByPrimaryKey(id);
            imagesToUpdate.push_back(image);
            cout << "added  image" << endl;
        } catch (const DrogonDbException &e) {
            cout << "no such image" << endl;
            return;
        }
    }

    if (!existingImages.empty()) {
        for (auto image: existingImages) {
            image.setPostIdToNull();
            m_imageMapper.update(image);
            cout << "cleared  image" << endl;
        }
    }

    for (auto image: imagesToUpdate) {
        image.setPostId(postId);
        m_imageMapper.update(image);
        cout << "updated  image" << endl;
    }
}
