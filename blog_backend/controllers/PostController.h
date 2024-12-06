#pragma once
#include <drogon/HttpController.h>
#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Like.h"
#include "../models/Comment.h"
#include "../models/Image.h"

using namespace drogon;
using namespace std;
using namespace drogon::orm;

class PostController : public HttpController<PostController> {
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(PostController::create, "/posts", drogon::Post, "LoginFilter");
        ADD_METHOD_TO(PostController::update, "/posts", drogon::Put, "RightFilter");
        ADD_METHOD_TO(PostController::remove, "/posts", drogon::Delete, "RightFilter");
        ADD_METHOD_TO(PostController::get, "/posts?author={}&offset={}&limit={}", Get, "LoginFilter");
    METHOD_LIST_END

    void create(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    void update(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    void remove(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    void get(
        const HttpRequestPtr &req,
        std::function<void (const HttpResponsePtr &)> &&callback,
        const string &author,
        const int offset, const int limit);
    void sendEmptyResponse(
        const std::shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
        const HttpStatusCode &code);

private:
    drogon::orm::Mapper<drogon_model::blog::Post> m_postMapper = drogon::orm::Mapper<drogon_model::blog::Post>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::User> m_userMapper = drogon::orm::Mapper<drogon_model::blog::User>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::Like> m_likeMapper = drogon::orm::Mapper<drogon_model::blog::Like>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::Comment> m_commentMapper = drogon::orm::Mapper<drogon_model::blog::Comment>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::Image> m_imageMapper = drogon::orm::Mapper<drogon_model::blog::Image>(app().getDbClient());

};
