/**
 *
 *  LoginFilter.cc
 *
 */

#include "LoginFilter.h"
#include "../utils/jwtservice.h"

using namespace drogon;

void LoginFilter::doFilter(const HttpRequestPtr &req,
                         FilterCallback &&fcb,
                         FilterChainCallback &&fccb)
{
    auto userId = jwtService::getCurrentUserIdFromRequest(req);
    //Edit your logic here
    if (userId.has_value())
    {
        //Passed
        fccb();
        return;
    }
    //Check failed
    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k401Unauthorized);
    fcb(res);
}
