#include "jwtservice.h"

#include <drogon/orm/Exception.h>
#include <drogon/orm/Mapper.h>

#include <optional>

const string jwtService::secret = drogon::app().getCustomConfig()["jwt-secret"].asString();
const int jwtService::duration = drogon::app().getCustomConfig()["jwt-sessionTime"].asInt();
const verifier<default_clock, traits::kazuho_picojson> jwtService::_verifier = verify()
                                                          .allow_algorithm(algorithm::hs256{jwtService::secret})
                                                          .with_issuer("auth0");

string jwtService::generateFromUser(const User& user)
{
    return jwt::create()
    .set_issuer("auth0")
        .set_type("JWS")
        //            .set_issued_at(std::chrono::system_clock::now())
        //            .set_expires_at(std::chrono::system_clock::now() + std::chrono::seconds{duration})
        .set_payload_claim("user", jwt::claim(to_string(user.getValueOfUserId())))
        .sign(jwt::algorithm::hs256{secret});
}

optional<int> jwtService::getUserIdFromJwt(const string& token)
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

optional<int> jwtService::getCurrentUserIdFromRequest(const HttpRequestPtr &req)
{

    string token = req->getHeader("Authorization");
    if (token.length() < 7) {
        return nullopt;
    }
    return getUserIdFromJwt(token.substr(7));
}

std::optional<drogon_model::blog::User> jwtService::getCurrentUserFromRequest(const HttpRequestPtr &req)
{
    drogon::orm::Mapper<drogon_model::blog::User> userMapper = drogon::orm::Mapper<drogon_model::blog::User>(app().getDbClient());
    auto id = getCurrentUserIdFromRequest(req);
    if (!id.has_value()) {
        return nullopt;
    }
    auto user = make_optional(userMapper.findByPrimaryKey(id.value()));
    return user;
}
