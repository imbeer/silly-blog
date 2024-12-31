/**
 *
 *  CommentRightFilter.cc
 *
 */

#include "CommentRightFilter.h"
#include "../utils/jwtservice.h"
#include "../utils/parseservice.h"

void CommentRightFilter::doFilter(
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

    const optional<int> commentId = parseService::getCommentIdFromRequest(*req);
    if (!commentId.has_value() || commentId.value() < 0) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(drogon::k400BadRequest);
        fcb(res);
    }
    const auto comments = m_commentMapper.findBy(
        Criteria(
            drogon_model::blog::Comment::Cols::_comment_id,
            CompareOperator::EQ,
            commentId.value()));
    if (comments.empty()) {
        auto res = drogon::HttpResponse::newHttpResponse();
        res->setStatusCode(k400BadRequest);
        fcb(res);
    }
    const auto comment = comments[0];
    if (user->getValueOfIsAdmin() || comment.getValueOfUserId() == user->getValueOfUserId()) {
        fccb();
        return;
    }


    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k403Forbidden);
    fcb(res);

}
