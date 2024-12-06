/**
 *
 *  PostRightFilter.h
 *
 */

#pragma once

#include <drogon/HttpAppFramework.h>
#include <drogon/HttpFilter.h>
#include "../models/Post.h"
#include "../models/User.h"

using namespace drogon;
using namespace drogon::orm;

class PostRightFilter : public HttpFilter<PostRightFilter>
{
public:
    PostRightFilter() {}
    void doFilter(
        const HttpRequestPtr &req,
        FilterCallback &&fcb,
        FilterChainCallback &&fccb) override;

private:
    Mapper<drogon_model::blog::User> m_userMapper = Mapper<drogon_model::blog::User>(app().getDbClient());
    Mapper<drogon_model::blog::Post> m_postMapper = Mapper<drogon_model::blog::Post>(app().getDbClient());
};

