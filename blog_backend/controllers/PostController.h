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
        ADD_METHOD_TO(PostController::update, "/posts", drogon::Put, "PostRightFilter");
        ADD_METHOD_TO(PostController::remove, "/posts", drogon::Delete, "PostRightFilter");
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

private:
    Mapper<drogon_model::blog::Post> m_postMapper = Mapper<drogon_model::blog::Post>(app().getDbClient());
    Mapper<drogon_model::blog::User> m_userMapper = Mapper<drogon_model::blog::User>(app().getDbClient());
    Mapper<drogon_model::blog::Like> m_likeMapper = Mapper<drogon_model::blog::Like>(app().getDbClient());
    Mapper<drogon_model::blog::Comment> m_commentMapper = Mapper<drogon_model::blog::Comment>(app().getDbClient());
    Mapper<drogon_model::blog::Image> m_imageMapper = Mapper<drogon_model::blog::Image>(app().getDbClient());

};
