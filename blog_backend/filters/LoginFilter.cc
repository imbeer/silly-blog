/**
 *
 *  LoginFilter.cc
 *
 */

#include "LoginFilter.h"
#include "../utils/jwtservice.h"

using namespace drogon;

void LoginFilter::doFilter(
    const HttpRequestPtr &req,
    FilterCallback &&fcb,
    FilterChainCallback &&fccb)
{
    auto userId = jwtService::getCurrentUserIdFromRequest(req);
    if (userId.has_value()) {
        fccb();
        return;
    }
    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k401Unauthorized);
    fcb(res);
}
