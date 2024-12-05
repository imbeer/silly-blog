#pragma once
#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Comment.h"

#include <drogon/HttpRequest.h>

using namespace drogon;

class parseService
{
public:
    static inline drogon_model::blog::Post getPostFromRequest(const HttpRequest &req) {
        return drogon_model::blog::Post((*req.getJsonObject())["post"]);
    }

    static inline drogon_model::blog::Comment getCommentFromRequest(const HttpRequest &req) {
        return drogon_model::blog::Comment((*req.getJsonObject())["comment"]);
    }

    static inline drogon_model::blog::User getUserFromRequest(const HttpRequest &req) {
        return drogon_model::blog::User((*req.getJsonObject())["user"]);
    }

    static inline int getPostIdFromRequest(const HttpRequest &req) {
        return (*req.getJsonObject())["post"]["post_id"].asInt();
    }

    static inline int getCommentIdFromRequest(const HttpRequest &req) {
        return (*req.getJsonObject())["comment"]["comment_id"].asInt();
    }

    static inline int getUserIdFromRequest(const HttpRequest &req) {
        return (*req.getJsonObject())["user"]["user_id"].asInt();
    }
};
