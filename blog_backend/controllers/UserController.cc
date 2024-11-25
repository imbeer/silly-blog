#include "UserController.h"
#include "../utils/jwtservice.h"
#include "../3rdparty/libbcrypt/include/bcrypt/BCrypt.hpp"

namespace drogon {
template<>
inline drogon_model::blog::User fromRequest(const HttpRequest &req) {
    auto json = req.getJsonObject();
    auto userJson = (*json)["user"];
    auto user = drogon_model::blog::User(userJson);
    return user;
}
}

void UserController::create(
    User &&pNewUser,
    std::function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    if (!isInputUserAllowed(pNewUser)) {
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(resp);
        return;
    }

    auto newUser = pNewUser;
    auto encryptedPass = BCrypt::generateHash(newUser.getValueOfPassword());
    newUser.setPassword(encryptedPass);

    m_userMapper.insert(
        newUser,
        [callbackPtr](const User &user) {
            auto json = Json::Value();
            json["user"] = user.toJson();
            json["token"] = jwtService::generateFromUser(user);
            auto response = HttpResponse::newHttpJsonResponse(json);
            response->setStatusCode(HttpStatusCode::k201Created);
            std::cout << "new user created" << endl;
            (*callbackPtr)(response);
        },
        [callbackPtr](const drogon::orm::DrogonDbException &e) {
            LOG_ERROR << e.base().what();
            std::cerr << "did not create new user" << endl;
            auto response = HttpResponse::newHttpResponse();
            response->setStatusCode(HttpStatusCode::k400BadRequest);
            (*callbackPtr)(response);
        });
}

bool UserController::isInputUserAllowed(const User &user)
{
    if (user.getEmail() == nullptr || user.getUsername() == nullptr || user.getPassword() == nullptr) {
        return false;
    }

    auto usernameCriteria = Criteria(User::Cols::_username, CompareOperator::EQ, user.getValueOfUsername());
    auto emailCriteria = Criteria(User::Cols::_email, CompareOperator::EQ, user.getValueOfEmail());

    return m_userMapper.findBy(usernameCriteria || emailCriteria).empty();
}
