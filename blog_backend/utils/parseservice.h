#pragma once
#include "../models/Post.h"
#include "../models/User.h"
#include "../models/Comment.h"

#include <drogon/HttpRequest.h>

using namespace drogon;

namespace parseService
{

inline drogon_model::blog::Post getPostFromRequest(const HttpRequest &req) {
    return drogon_model::blog::Post((*req.getJsonObject())["post"]);
}

inline drogon_model::blog::Comment getCommentFromRequest(const HttpRequest &req) {
    return drogon_model::blog::Comment((*req.getJsonObject())["comment"]);
}

inline drogon_model::blog::User getUserFromRequest(const HttpRequest &req) {
    return drogon_model::blog::User((*req.getJsonObject())["user"]);
}

inline std::optional<int> getPostIdFromRequest(const HttpRequest &req) {
    auto postId = (*req.getJsonObject())["post"]["post_id"];
    if (!postId.empty() && postId.isInt() && postId.asInt() >= 0) return postId.asInt();
    else return std::nullopt;
}

inline std::optional<int> getCommentIdFromRequest(const HttpRequest &req) {
    auto commentId =  (*req.getJsonObject())["comment"]["comment_id"];
    if (!commentId.empty() && commentId.isInt() && commentId.asInt() >= 0) return commentId.asInt();
    else return std::nullopt;
}

inline std::optional<int> getUserIdFromRequest(const HttpRequest &req) {
    auto userId = (*req.getJsonObject())["user"]["user_id"];
    if (!userId.empty() && userId.isInt() && userId.asInt() >= 0) return userId.asInt();
    else return std::nullopt;
}

inline std::optional<int> getImageIdFromRequest(const HttpRequest &req) {
    auto imageId = (*req.getJsonObject())["image"]["image_id"];
    if (!imageId.empty() && imageId.isInt() && imageId.asInt() >= 0) return imageId.asInt();
    else return std::nullopt;
}

inline std::vector<int> getImageIdVectorFromRequest(const HttpRequest &req) {
    auto imageIds = (*req.getJsonObject())["post"]["images"];
    std::vector<int> imageIdVector;
    if (imageIds.empty()) return imageIdVector;
    
    for (const auto &id : imageIds) {
        if (id.isInt()) imageIdVector.push_back(id.asInt());
    }

    return imageIdVector;
}

inline std::string getImageIdListStringFromRequest(const HttpRequest &req) {
    auto imageIds = (*req.getJsonObject())["post"]["images"];
    if (imageIds.empty()) return "";

    std::string list;
    for (const auto &id : imageIds) {
        if (id.isInt() && id > 0) {
            list += id.asString();
            list += ",";
        }
    }
    list[list.size()-1] = ' ';
    return list;
}

};
