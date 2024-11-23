#pragma once
#include <drogon/HttpController.h>
#include "../models/Post.h"
#include "../models/User.h"

using namespace drogon;
using namespace std;
using namespace drogon::orm;

class PostController : public HttpController<PostController> {
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(PostController::newPost, "/posts", drogon::Post, "LoginFilter");
        ADD_METHOD_TO(PostController::getAllPosts, "/posts?author={}&offset={}&limit={}", Get);
    METHOD_LIST_END

    void newPost(
        drogon_model::blog::Post &&newPost,
        function<void(const HttpResponsePtr &)> &&callback);
    void getAllPosts(
        const HttpRequestPtr& req,
        std::function<void (const HttpResponsePtr &)> &&callback,
        const string &author,
        int offset, int limit);

private:
    drogon::orm::Mapper<drogon_model::blog::Post> m_postMapper = drogon::orm::Mapper<drogon_model::blog::Post>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::User> m_userMapper = drogon::orm::Mapper<drogon_model::blog::User>(app().getDbClient());
};
