#include "PostController.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"
#include "../utils/httpservice.h"

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
        [callbackPtr](const drogon_model::blog::Post &post) {
            auto json = Json::Value();
            json["post"] = post.toJson();
            auto response = HttpResponse::newHttpJsonResponse(json);
            response->setStatusCode(HttpStatusCode::k201Created);
            (*callbackPtr)(response);
        },
        [callbackPtr](const drogon::orm::DrogonDbException &e) {
            LOG_ERROR << e.base().what();
            auto response = HttpResponse::newHttpResponse();
            response->setStatusCode(HttpStatusCode::k400BadRequest);
            (*callbackPtr)(response);
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

        json["post"] = post.toJson();
        auto resp = HttpResponse::newHttpJsonResponse(json);
        resp->setStatusCode(HttpStatusCode::k200OK);
        (*callbackPtr)(resp);
    } catch (const UnexpectedRows &e) {
        LOG_ERROR << e.what();
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    } catch (const DrogonDbException &e) {
        LOG_ERROR << e.base().what();
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    } catch (const std::runtime_error &e) {
        LOG_ERROR << e.what();
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

void PostController::remove(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int postId = parseService::getPostIdFromRequest(*req);

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
    } catch (const UnexpectedRows &e) {
        LOG_ERROR << e.what();
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);

    } catch (const std::exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

void PostController::get(
    const HttpRequestPtr &req,
    std::function<void (const HttpResponsePtr &)> &&callback,
    const string &authorUsername,
    const int offset, const int limit)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    Json::Value responseBody;
    Criteria postCriteria;

    if (!authorUsername.empty()) {
        const Criteria userCriteria(
            drogon_model::blog::User::Cols::_username,
            CompareOperator::EQ,
            authorUsername);
        const auto users = m_userMapper.findBy(userCriteria);
        if (users.empty()) {
            httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
            return;
        }
        const auto userId = users[0].getValueOfUserId();
        postCriteria = Criteria(
            drogon_model::blog::Post::Cols::_user_id,
            CompareOperator::EQ,
            userId);
    }

    const auto posts = m_postMapper
        .orderBy(drogon_model::blog::Post::Cols::_time)
        .limit(limit)
        .offset(offset)
        .findBy(postCriteria);

    const int currentUserId = jwtService::getCurrentUserIdFromRequest(req).value();
    const auto likeUserCriteria = Criteria(
        drogon_model::blog::Like::Cols::_user_id,
        CompareOperator::EQ,
        currentUserId);

    for (const auto &post : posts)
    {
        auto postJson = post.toJson();
        const auto likePostCriteria = Criteria(
                drogon_model::blog::Like::Cols::_post_id,
                CompareOperator::EQ,
                post.getValueOfPostId());

        const int likes = m_likeMapper.count(likePostCriteria);
        const bool isLiked = m_likeMapper.count(likePostCriteria && likeUserCriteria) == 1;

        postJson["likes"] = likes;
        postJson["isLiked"] = isLiked;
        responseBody.append(postJson);
    }
    auto response = HttpResponse::newHttpJsonResponse(responseBody);
    response->setStatusCode(HttpStatusCode::k200OK);
    (*callbackPtr)(response);
}


