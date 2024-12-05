#include "PostController.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"

void PostController::create(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto newPost = parseService::getPostFromRequest(*req);
    auto userId = jwtService::getCurrentUserIdFromRequest(req);

    newPost.setPostId(userId.value());
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
    auto editedPost = parseService::getPostFromRequest(*req);

    try {
        auto json = Json::Value();
        auto post = m_postMapper.findByPrimaryKey(editedPost.getValueOfPostId());
        post.setTextContent(editedPost.getValueOfTextContent());
        m_postMapper.update(post);
        json["post"] = post.toJson();
        auto resp = HttpResponse::newHttpJsonResponse(json);
        resp->setStatusCode(HttpStatusCode::k200OK);
        cout << "updated" << endl;
        (*callbackPtr)(resp);

    } catch (const UnexpectedRows &e) {
        LOG_ERROR << e.what();
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        cout << "unexpected rows" << endl;
        (*callbackPtr)(resp);

    } catch (const DrogonDbException &e) {
        LOG_ERROR << e.base().what();
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        cout << "not found" << endl;
        (*callbackPtr)(resp);

    } catch (const std::exception &e) {
        LOG_ERROR << e.what();
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        cout << "not found" << endl;
        (*callbackPtr)(resp);

    }
}

void PostController::remove(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto deletedPost = parseService::getPostFromRequest(*req);

    const int postId = deletedPost.getValueOfPostId();

    try {
        // todo: deletion in comments, likes and this post
        // also in post-to-image
    } catch (const std::exception &e) {

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
    responseBody.resize(0);
    Criteria postCriteria;

    if (!authorUsername.empty()) {
        const Criteria userCriteria(drogon_model::blog::User::Cols::_username, CompareOperator::EQ, authorUsername);
        const auto users = m_userMapper.findBy(userCriteria);
        if (users.empty()) {
            // LOG_ERROR << "No user found with username: " << authorUsername;
            auto response = HttpResponse::newHttpResponse();
            response->setStatusCode(HttpStatusCode::k400BadRequest);
            (*callbackPtr)(response);
            return;
        }
        const auto userId = users[0].getValueOfUserId();
        postCriteria = Criteria(drogon_model::blog::Post::Cols::_user_id, CompareOperator::EQ, userId);
    }

    const auto posts = m_postMapper
                           .orderBy(drogon_model::blog::Post::Cols::_time)
                           .limit(limit)
                           .offset(offset)
                           .findBy(postCriteria);
    cout << "Posts selected: " << posts.size() << endl;
    for (const auto &post : posts) {
        responseBody.append(post.toJson());
    }
    auto response = HttpResponse::newHttpJsonResponse(responseBody);
    response->setStatusCode(HttpStatusCode::k200OK);
    (*callbackPtr)(response);
}


