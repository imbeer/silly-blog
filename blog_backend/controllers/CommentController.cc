#include "CommentController.h"
#include <string>
#include "../utils/parseservice.h"
#include "../utils/jwtservice.h"

void CommentController::get(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback,
    const int postId,
    const string author,
    const int offset, const int limit)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    Json::Value responseBody;
    responseBody.resize(0);
    Criteria postCriteria;
    Criteria userCriteria;

    if (!author.empty()) {
        const auto users = m_userMapper.findBy(
            Criteria(drogon_model::blog::User::Cols::_username, CompareOperator::EQ, author));
        if (!users.empty()) {
            const auto userId = users[0].getValueOfUserId();
            userCriteria = Criteria(drogon_model::blog::Comment::Cols::_user_id, CompareOperator::EQ, userId);
        }
    }
    if (postId > 0) {
        const auto posts = m_postMapper.findBy(
            Criteria(drogon_model::blog::Post::Cols::_post_id, CompareOperator::EQ, postId));
        if (!posts.empty()) {
            postCriteria = Criteria(drogon_model::blog::Comment::Cols::_post_id, CompareOperator::EQ, postId);
        }
    }

    const auto comments = m_commentMapper
                              .orderBy(drogon_model::blog::Comment::Cols::_time)
                              .limit(limit)
                              .offset(offset)
                              .findBy(postCriteria && userCriteria);

    for (const auto &comment : comments) {
        responseBody.append(comment.toJson());
    }
    auto response = HttpResponse::newHttpJsonResponse(responseBody);
    response->setStatusCode(HttpStatusCode::k200OK);
    (*callbackPtr)(response);
}

void CommentController::create(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto newComment = parseService::getCommentFromRequest(*req);
    int userId = jwtService::getCurrentUserIdFromRequest(req).value();

    newComment.setUserId(userId);
    newComment.setTime(::trantor::Date::now());

    m_commentMapper.insert(
        newComment,
        [callbackPtr](const drogon_model::blog::Comment &comment) {
            auto json = Json::Value();
            json["comment"] = comment.toJson();
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

void CommentController::update(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback,
    std::string &&id)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto editedComment = parseService::getCommentFromRequest(*req);

    try {
        auto json = Json::Value();
        auto comment= m_commentMapper.findByPrimaryKey(editedComment.getValueOfCommentId());
        comment.setTextContent(editedComment.getValueOfTextContent());
        m_commentMapper.update(comment);
        json["comment"] = comment.toJson();
        auto resp = HttpResponse::newHttpJsonResponse(json);
        resp->setStatusCode(HttpStatusCode::k200OK);
        (*callbackPtr)(resp);

    } catch (const std::exception &e) {
        LOG_ERROR << e.what();
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(resp);

    }
    // } catch (const UnexpectedRows &e) {
    //     LOG_ERROR << e.what();
    //     auto resp = HttpResponse::newHttpResponse();
    //     resp->setStatusCode(HttpStatusCode::k400BadRequest);
    //     (*callbackPtr)(resp);

    // } catch (const DrogonDbException &e) {
    //     LOG_ERROR << e.base().what();
    //     auto resp = HttpResponse::newHttpResponse();
    //     resp->setStatusCode(HttpStatusCode::k400BadRequest);
    //     (*callbackPtr)(resp);
}

void CommentController::remove(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback,
    std::string &&id)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    const int commentId = parseService::getCommentIdFromRequest(*req);

    try {
        m_commentMapper.deleteByPrimaryKey(commentId);

    } catch (const std::exception &e) {
        LOG_ERROR << e.what();
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(resp);

    }
}
