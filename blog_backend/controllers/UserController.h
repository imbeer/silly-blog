#pragma once

#include <drogon/HttpController.h>
#include <drogon/orm/Mapper.h>
#include "../models/User.h"
using namespace drogon;
using namespace drogon::orm;
using namespace drogon_model::blog;

class UserController: public drogon::HttpController<UserController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(UserController::create, "/users", drogon::Post);
    METHOD_LIST_END

    // void get(
    //     const HttpRequestPtr &req,
    //     std::function<void(const HttpResponsePtr &)> &&callback);
    void create(
        User &&pNewUser,
        std::function<void(const HttpResponsePtr &)> &&callback);

private:
    bool isInputUserAllowed(const User &user);

private:

    Mapper<User> m_userMapper = Mapper<User>(app().getDbClient());
};
