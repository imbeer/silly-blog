#include "jwtservice.h"

#include <drogon/orm/Exception.h>
#include <drogon/orm/Mapper.h>

const string jwtService::secret = drogon::app().getCustomConfig()["jwt-secret"].asString();
const int jwtService::duration = drogon::app().getCustomConfig()["jwt-sessionTime"].asInt();
const verifier<default_clock, traits::kazuho_picojson> jwtService::_verifier = verify()
                                                          .allow_algorithm(algorithm::hs256{jwtService::secret})
                                                          .with_issuer("auth0");

auto jwtService::generateFromUser(const User& user) -> string
{
    return jwt::create()
    .set_issuer("auth0")
        .set_type("JWS")
        //            .set_issued_at(std::chrono::system_clock::now())
        //            .set_expires_at(std::chrono::system_clock::now() + std::chrono::seconds{duration})
        .set_payload_claim("user", jwt::claim(to_string(user.getValueOfUserId())))
        .sign(jwt::algorithm::hs256{secret});
}

auto jwtService::getUserIdFromJwt(const string& token) -> optional<int>
{
    auto decoded = jwt::decode(token);
    try {
        _verifier.verify(decoded);
    } catch (const runtime_error &e) {
        LOG_ERROR << e.what();
        return nullopt;
    }
    return stoi(decoded.get_payload_claim("user").as_string());
}

auto jwtService::getCurrentUserIdFromRequest(const HttpRequestPtr &req) -> optional<int>
{
    auto token = req->getHeader("Authorization").substr(7);
    return getUserIdFromJwt(token);
}

auto jwtService::getCurrentUserFromRequest(const HttpRequestPtr &req, const function<void(optional<User>)>& callback) -> void
{
    drogon::orm::Mapper<User> userMapper = drogon::orm::Mapper<User>(app().getFastDbClient());
    auto id = getCurrentUserIdFromRequest(req);
    if (!id.has_value()) {
        callback(nullopt);
        return;
    }
    userMapper.findByPrimaryKey(id.value(), [callback](const User &user) {
        callback(user);
    },[callback](const drogon::orm::DrogonDbException &e) {
                                    callback(nullopt);
                                });
}
