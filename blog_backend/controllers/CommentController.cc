#include "CommentController.h"
#include <string>

namespace drogon {
template<>
inline drogon_model::blog::Comment fromRequest(const HttpRequest &req) {
    auto json = req.getJsonObject();
    auto commentJson = (*json)["comment"];
    auto comment = drogon_model::blog::Comment(commentJson);
    return comment;
}
}

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
    const drogon_model::blog::Comment &&newComment,
    std::function<void(const HttpResponsePtr &)> &&callback)
{
    // todo: тож проверку на пользователя
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

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

}

void CommentController::remove(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback,
    std::string &&id)
{

}
