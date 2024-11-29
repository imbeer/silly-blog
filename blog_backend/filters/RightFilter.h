/**
 *
 *  RightFilter.h
 *
 */

#pragma once

#include <drogon/HttpAppFramework.h>
#include <drogon/HttpFilter.h>
#include "../models/Post.h"
#include "../models/User.h"

using namespace drogon;
using namespace drogon::orm;

class RightFilter : public HttpFilter<RightFilter>
{
public:
    RightFilter() {}
    void doFilter(
        const HttpRequestPtr &req,
        FilterCallback &&fcb,
        FilterChainCallback &&fccb) override;

private:
    Mapper<drogon_model::blog::User> userMapper = Mapper<drogon_model::blog::User>(app().getDbClient());
    Mapper<drogon_model::blog::Post> postMapper = Mapper<drogon_model::blog::Post>(app().getDbClient());
};

