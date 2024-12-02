/**
 *
 *  RightFilter.cc
 *
 */

#include "RightFilter.h"
#include "../utils/jwtservice.h"

void RightFilter::doFilter(
    const HttpRequestPtr &req,
    FilterCallback &&fcb,
    FilterChainCallback &&fccb)
{
    cout << "doing filter" << endl;

    auto user = jwtService::getCurrentUserFromRequest(req);
    if (!user.has_value()) {
        cout << "no user" << endl;
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(drogon::k401Unauthorized);
        fcb(res);
    }

    auto json = req->getJsonObject();
    int postId = (*json)["post"]["post_id"].asInt();

    cout << "get post" << endl << postId <<endl;
    auto post = postMapper.findByPrimaryKey(postId);
    cout << post.getValueOfTextContent() << endl;

    if (user->getValueOfIsAdmin() || post.getValueOfUserId() == user->getValueOfUserId()) {
        cout << "approved" << endl;
        fccb();
        return;
    }

    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k403Forbidden);
    cout << "not approved" << endl;
    fcb(res);
}
