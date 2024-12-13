#pragma once
#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Comment.h"

#include <drogon/HttpRequest.h>

using namespace drogon;

namespace parseService
{

// todo: add optionals when there is no such thing.

inline drogon_model::blog::Post getPostFromRequest(const HttpRequest &req) {
    return drogon_model::blog::Post((*req.getJsonObject())["post"]);
}

inline drogon_model::blog::Comment getCommentFromRequest(const HttpRequest &req) {
    return drogon_model::blog::Comment((*req.getJsonObject())["comment"]);
}

inline drogon_model::blog::User getUserFromRequest(const HttpRequest &req) {
    return drogon_model::blog::User((*req.getJsonObject())["user"]);
}

inline int getPostIdFromRequest(const HttpRequest &req) {
    return (*req.getJsonObject())["post"]["post_id"].asInt();
}

inline int getCommentIdFromRequest(const HttpRequest &req) {
    return (*req.getJsonObject())["comment"]["comment_id"].asInt();
}

inline int getUserIdFromRequest(const HttpRequest &req) {
    return (*req.getJsonObject())["user"]["user_id"].asInt();
}

inline int getImageIdFromRequest(const HttpRequest &req) {
    return (*req.getJsonObject())["image"]["image_id"].asInt();
}

};
