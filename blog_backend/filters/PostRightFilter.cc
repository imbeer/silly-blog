/**
 *
 *  PostRightFilter.cc
 *
 */

#include "PostRightFilter.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"

void PostRightFilter::doFilter(
    const HttpRequestPtr &req,
    FilterCallback &&fcb,
    FilterChainCallback &&fccb)
{
    auto user = jwtService::getCurrentUserFromRequest(req);
    if (!user.has_value()) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(drogon::k401Unauthorized);
        fcb(res);
    }

    auto json = req->getJsonObject();

    const int postId = parseService::getPostIdFromRequest(*req);
    const auto posts = m_postMapper.findBy(
        Criteria(
            drogon_model::blog::Post::Cols::_post_id,
            CompareOperator::EQ,
            postId));
    if (posts.empty()) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(k400BadRequest);
        fcb(res);
    }
    const auto post = posts[0];
    if (user->getValueOfIsAdmin() || post.getValueOfUserId() == user->getValueOfUserId()) {
        fccb();
        return;
    }


    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k403Forbidden);
    fcb(res);

}
