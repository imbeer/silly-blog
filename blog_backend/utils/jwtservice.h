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
    static auto generateFromUser(const User& user) -> string;

    static auto getUserIdFromJwt(const string& token) -> optional<int>;

    static auto getCurrentUserIdFromRequest(const HttpRequestPtr &req) -> optional<int>;

    static auto getCurrentUserFromRequest(const HttpRequestPtr &req, const function<void(optional<User>)>& callback) -> void;

private:
    static const string secret;
    static const int duration;
    static const verifier<default_clock, traits::kazuho_picojson> _verifier;
};
