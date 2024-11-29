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
    auto userId = jwtService::getCurrentUserIdFromRequest(req);
    drogon_model::blog::User user;
    userMapper.findByPrimaryKey(
        userId.value(),
        [&user](const drogon_model::blog::User &userFromDb) {
            user = userFromDb;
        },
        [fcb](const DrogonDbException &e) {
            cout << "user not found" << endl;
            auto res = drogon::HttpResponse::newHttpResponse();
            res->setStatusCode(k401Unauthorized);
            fcb(res);
        }
    );
    auto json = req->getJsonObject();
    auto postId = (*json)["post"]["post_id"].asInt();

    postMapper.findByPrimaryKey(
        postId,
        [fccb, fcb, user](const drogon_model::blog::Post &post){
            if (post.getValueOfUserId() == user.getValueOfUserId() || user.getValueOfIsAdmin()) {
                cout << "approved" << endl;
                fccb();
                return;
            } else {
                cout << "not approved" << endl;
                auto res = drogon::HttpResponse::newHttpResponse();
                res->setStatusCode(k403Forbidden);
                fcb(res);
            }
        },
        [fcb](const DrogonDbException &e){
            cout << "post not found" << endl;
            auto res = drogon::HttpResponse::newHttpResponse();
            res->setStatusCode(k400BadRequest);
            fcb(res);
        });
}
