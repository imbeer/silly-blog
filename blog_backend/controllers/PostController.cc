#include "PostController.h"

namespace drogon {
template<>
inline drogon_model::blog::Post fromRequest(const HttpRequest &req) {
    auto json = req.getJsonObject();
    auto postJson = (*json)["post"];
    auto post = drogon_model::blog::Post(postJson);
    return post;
}
}

void PostController::newPost(
    drogon_model::blog::Post &&newPost,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

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


// void PostController::updatePost(
//     drogon_model::blog::Post &&newPost,
//     function<void(const HttpResponsePtr &)> &&callback)
// {
//     auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
//     m_postMapper.findByPrimaryKey(
//         newPost.getPrimaryKey(),
//         [callbackPtr](const drogon_model::blog::Post &post) {

//         },
//         [callbackPtr](const drogon::orm::DrogonDbException &e) {
//             Json::Value responseBody;
//             responseBody["error"] = e.base().what();
//             auto response = HttpResponse::newHttpJsonResponse(responseBody);
//             response->setStatusCode(HttpStatusCode::k404NotFound);
//             (*callbackPtr)(response);
//         }
//         );
// }


void PostController::getPosts(
    const HttpRequestPtr& req,
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


