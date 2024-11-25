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
        ADD_METHOD_TO(PostController::create, "/posts", drogon::Post, "LoginFilter");
        // ADD_METHOD_TO(PostController::updatePost, "/posts", drogon::Put, "RightFilter");
        ADD_METHOD_TO(PostController::get, "/posts?author={}&offset={}&limit={}", Get);
    METHOD_LIST_END

    void create(
        drogon_model::blog::Post &&newPost,
        function<void(const HttpResponsePtr &)> &&callback);
    // void updatePost(
    //     drogon_model::blog::Post &&newPost,
    //     function<void(const HttpResponsePtr &)> &&callback);
    void get(
        const HttpRequestPtr& req,
        std::function<void (const HttpResponsePtr &)> &&callback,
        const string &author,
        const int offset, const int limit);

private:
    drogon::orm::Mapper<drogon_model::blog::Post> m_postMapper = drogon::orm::Mapper<drogon_model::blog::Post>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::User> m_userMapper = drogon::orm::Mapper<drogon_model::blog::User>(app().getDbClient());
};
