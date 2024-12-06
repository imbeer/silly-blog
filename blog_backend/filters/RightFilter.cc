/**
 *
 *  RightFilter.cc
 *
 */

#include "RightFilter.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"

void RightFilter::doFilter(
    const HttpRequestPtr &req,
    FilterCallback &&fcb,
    FilterChainCallback &&fccb)
{ // todo: comments right check
    auto user = jwtService::getCurrentUserFromRequest(req);
    if (!user.has_value()) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(drogon::k401Unauthorized);
        fcb(res);
    }

    auto json = req->getJsonObject();
    int postId = parseService::getPostIdFromRequest(*req);

    auto post = postMapper.findByPrimaryKey(postId);

    if (user->getValueOfIsAdmin() || post.getValueOfUserId() == user->getValueOfUserId()) {
        fccb();
        return;
    }

    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k403Forbidden);
    fcb(res);
}
