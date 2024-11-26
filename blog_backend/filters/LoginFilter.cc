/**
 *
 *  LoginFilter.cc
 *
 */

#include "LoginFilter.h"
#include "../utils/jwtservice.h"
#include "../models/User.h"

using namespace drogon;
using namespace drogon::orm;

void LoginFilter::doFilter(
    const HttpRequestPtr &req,
    FilterCallback &&fcb,
    FilterChainCallback &&fccb)
{
    auto userMapper = Mapper<User>(app().getDbClient());
    const auto userId = jwtService::getCurrentUserIdFromRequest(req);
    const auto criteria = Criteria(User::Cols::_user_id, CompareOperator::EQ, userId);
    const int userCount = userMapper.count(criteria);

    if (userId.has_value() && userCount == 1) {
        fccb();
        return;
    }
    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k401Unauthorized);
    fcb(res);
}
