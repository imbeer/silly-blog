/**
 *
 *  CommentRightFilter.h
 *
 */

#pragma once

#include <drogon/HttpAppFramework.h>
#include <drogon/HttpFilter.h>
#include "../models/Comment.h"
#include "../models/User.h"

using namespace drogon;
using namespace drogon::orm;

class CommentRightFilter : public HttpFilter<CommentRightFilter>
{
public:
    CommentRightFilter() {}
    void doFilter(
        const HttpRequestPtr &req,
        FilterCallback &&fcb,
        FilterChainCallback &&fccb) override;

private:
    Mapper<drogon_model::blog::User> m_userMapper = Mapper<drogon_model::blog::User>(app().getDbClient());
    Mapper<drogon_model::blog::Comment> m_commentMapper = Mapper<drogon_model::blog::Comment>(app().getDbClient());
};

