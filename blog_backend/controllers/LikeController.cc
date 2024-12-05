#include "LikeController.h"
#include <string>

#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"

void LikeController::getOne(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    const int &id)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int userId = jwtService::getCurrentUserIdFromRequest(req).value();
    const int &postId = id;

    int likeCount = 0;
    bool likedByCurrentUser = false;
    try {
        const auto postCriteria = Criteria(
            drogon_model::blog::Like::Cols::_post_id,
            CompareOperator::EQ,
            postId);
        const auto userCriteria = Criteria(
            drogon_model::blog::Like::Cols::_user_id,
            CompareOperator::EQ,
            userId);
        likeCount = m_likeMapper.count(postCriteria);
        likedByCurrentUser = m_likeMapper.count(postCriteria && userCriteria) == 1;
        Json::Value json;
        json["post_id"] = postId;
        json["count"] = likeCount;
        json["is_liked"] = likedByCurrentUser;
        auto response = HttpResponse::newHttpJsonResponse(json);
        response->setStatusCode(HttpStatusCode::k200OK);
        (*callbackPtr)(response);
    } catch (const std::exception &e) {
        auto response = HttpResponse::newHttpResponse();
        response->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(response);

    }
}

void LikeController::like(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int userId = jwtService::getCurrentUserIdFromRequest(req).value();
    const int postId = parseService::getPostIdFromRequest(*req);

    try {
        m_postMapper.findByPrimaryKey(postId);
    } catch (const std::exception &e) {
        auto response = HttpResponse::newHttpResponse();
        response->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(response);
    }

    try {
        auto like = drogon_model::blog::Like();
        like.setPostId(postId);
        like.setPostId(userId);
        m_likeMapper.insert(like);

        auto resp = HttpResponse::newHttpJsonResponse(Json::Value("liked"));
        resp->setStatusCode(drogon::k201Created);
        (*callbackPtr)(resp);

    } catch (const drogon::orm::DrogonDbException &e) {
        auto errorMessage = std::string(e.base().what());
        if (errorMessage.find("duplicate key") != std::string::npos) {
            auto resp = HttpResponse::newHttpJsonResponse(Json::Value("Already liked"));
            resp->setStatusCode(k409Conflict);
            (*callbackPtr)(resp);
        } else {
            auto resp = HttpResponse::newHttpJsonResponse(Json::Value("Database error"));
            resp->setStatusCode(k500InternalServerError);
            (*callbackPtr)(resp);
        }
    }
}

void LikeController::dislike(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int userId = jwtService::getCurrentUserIdFromRequest(req).value();
    const int postId = parseService::getPostIdFromRequest(*req);

    try {
        m_likeMapper.deleteByPrimaryKey(tuple<int, int>(userId, postId));
    } catch (const drogon::orm::DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(Json::Value("Already liked"));
        resp->setStatusCode(drogon::k400BadRequest);
        (*callbackPtr)(resp);
    }
}
