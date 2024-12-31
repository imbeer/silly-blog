#include "LikeController.h"

#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"
#include "../utils/httpservice.h"

void LikeController::getOne(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    const int &postId)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int userId = jwtService::getCurrentUserIdFromRequest(req).value();

    int likeCount = 0;
    bool likedByCurrentUser = false;
    const auto postCriteria = Criteria(
        drogon_model::blog::Like::Cols::_post_id,
        CompareOperator::EQ,
        postId);
    const auto userCriteria = Criteria(
        drogon_model::blog::Like::Cols::_user_id,
        CompareOperator::EQ,
        userId);
    try {
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

    const optional<int> postIdOptional = parseService::getPostIdFromRequest(*req);
    if (!postIdOptional.has_value() || postIdOptional.value() < 0) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        return;
    }
    const int postId = postIdOptional.value();

    const auto postCriteria = Criteria(
        drogon_model::blog::Like::Cols::_post_id,
        CompareOperator::EQ,
        postId);
    const auto userCriteria = Criteria(
        drogon_model::blog::Like::Cols::_user_id,
        CompareOperator::EQ,
        userId);

    try {
        m_postMapper.findByPrimaryKey(postId);
    } catch (const drogon::orm::DrogonDbException &e) {
        auto response = HttpResponse::newHttpResponse();
        response->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(response);
        return;
    }

    if (m_likeMapper.count(postCriteria && userCriteria) > 0) {
        httpService::sendEmptyResponse(callbackPtr, k409Conflict);
        return;
    }

    try {
        auto like = drogon_model::blog::Like();
        like.setPostId(postId);
        like.setUserId(userId);
        m_likeMapper.insert(like);

        auto resp = HttpResponse::newHttpJsonResponse(Json::Value("liked"));
        resp->setStatusCode(drogon::k201Created);
        (*callbackPtr)(resp);

    } catch (const drogon::orm::DrogonDbException &e) {
        httpService::sendEmptyResponse(callbackPtr, k500InternalServerError);
    }
}

void LikeController::dislike(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int userId = jwtService::getCurrentUserIdFromRequest(req).value();

    const optional<int> postIdOptional = parseService::getPostIdFromRequest(*req);
    if (!postIdOptional.has_value() || postIdOptional.value() < 0) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        return;
    }
    const int postId = postIdOptional.value();

    const auto postCriteria = Criteria(
        drogon_model::blog::Like::Cols::_post_id,
        CompareOperator::EQ,
        postId);
    const auto userCriteria = Criteria(
        drogon_model::blog::Like::Cols::_user_id,
        CompareOperator::EQ,
        userId);

    if (m_likeMapper.count(postCriteria && userCriteria) == 0) {
        httpService::sendEmptyResponse(callbackPtr, k409Conflict);
        return;
    }

    try {
        m_likeMapper.deleteBy(postCriteria && userCriteria);
        httpService::sendEmptyResponse(callbackPtr, k204NoContent);
    } catch (const drogon::orm::DrogonDbException &e) {
        httpService::sendEmptyResponse(callbackPtr, k500InternalServerError);
    }
}
