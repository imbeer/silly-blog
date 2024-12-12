#pragma once

#include <drogon/HttpController.h>

#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Like.h"

using namespace drogon;
using namespace drogon::orm;
using namespace std;

class LikeController : public HttpController<LikeController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(LikeController::getOne, "/post/like?post_id={}", drogon::Get, "LoginFilter");
        ADD_METHOD_TO(LikeController::like, "/post/like", drogon::Put, "LoginFilter");
        ADD_METHOD_TO(LikeController::dislike, "/post/like", drogon::Delete, "LoginFilter");
    METHOD_LIST_END

    void getOne(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback,
        const int &postId);
    void like(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    void dislike(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);

private:
    Mapper<drogon_model::blog::Post> m_postMapper = Mapper<drogon_model::blog::Post>(app().getDbClient());
    Mapper<drogon_model::blog::User> m_userMapper = Mapper<drogon_model::blog::User>(app().getDbClient());
    Mapper<drogon_model::blog::Like> m_likeMapper = Mapper<drogon_model::blog::Like>(app().getDbClient());
};
