#pragma once

#include <drogon/HttpController.h>
#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Comment.h"

using namespace drogon;
using namespace std;
using namespace drogon::orm;

class CommentController: public drogon::HttpController<CommentController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(CommentController::get, "/post?id={}&author={}&offset={}&limit={}", Get);
        ADD_METHOD_TO(CommentController::create, "/post/comment", Post);
        ADD_METHOD_TO(CommentController::update, "/post/comment", Put, "CommentRightFilter");
        ADD_METHOD_TO(CommentController::remove, "/post/comment", Delete, "CommentRightFilter");
    METHOD_LIST_END

    void get(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        const int postId,
        const string author,
        const int offset, const int limit);
    void create(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback);
    void update(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        std::string &&id);
    void remove(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        std::string &&id);

private:
    drogon::orm::Mapper<drogon_model::blog::Post> m_postMapper = drogon::orm::Mapper<drogon_model::blog::Post>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::User> m_userMapper = drogon::orm::Mapper<drogon_model::blog::User>(app().getDbClient());
    drogon::orm::Mapper<drogon_model::blog::Comment> m_commentMapper = drogon::orm::Mapper<drogon_model::blog::Comment>(app().getDbClient());

};
