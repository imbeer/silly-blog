/**
 *
 *  RightFilter.cc
 *
 */

#include "RightFilter.h"
#include "../utils/jwtservice.h"

using namespace drogon;

void RightFilter::doFilter(const HttpRequestPtr &req,
                         FilterCallback &&fcb,
                         FilterChainCallback &&fccb)
{
    auto userId = jwtService::getCurrentUserIdFromRequest(req);
    drogon::orm::Mapper<User> userMapper = drogon::orm::Mapper<User>(app().getDbClient());
    auto user = userMapper.findByPrimaryKey(userId.value());

    auto json = req->getJsonObject();
    auto postUserId = (*json)["post"]["userId"];
    if (userId.has_value() && (userId.value() == postUserId.asInt() || user.getIsAdmin())) {
        fccb();
        return;
    }
    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k500InternalServerError);
    fcb(res);
}
