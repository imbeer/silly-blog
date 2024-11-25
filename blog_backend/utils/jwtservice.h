#pragma once

#include "../models/User.h"
#include <drogon/drogon.h>
#include <jwt-cpp/jwt.h>

using namespace std;
using namespace drogon;
using namespace drogon_model::blog;
using namespace jwt;

class jwtService {
public:
    static string generateFromUser(const User& user);

    static optional<int> getUserIdFromJwt(const string& token);

    static optional<int> getCurrentUserIdFromRequest(const HttpRequestPtr &req);

    static std::optional<drogon_model::blog::User> getCurrentUserFromRequest(const HttpRequestPtr &req, const function<void(optional<User>)>& callback);

private:
    static const string secret;
    static const int duration;
    static const verifier<default_clock, traits::kazuho_picojson> _verifier;
};
