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

    const optional<int> postId = parseService::getPostIdFromRequest(*req);
    if (!postId.has_value() || postId.value() < 0) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(drogon::k400BadRequest);
        fcb(res);
    }
    const auto posts = m_postMapper.findBy(
        Criteria(
            drogon_model::blog::Post::Cols::_post_id,
            CompareOperator::EQ,
            postId.value()));
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
