#pragma once

#include <drogon/HttpController.h>
#include <drogon/orm/Mapper.h>
#include "../models/User.h"
#include "../models/Post.h"
#include "../models/Like.h"
#include "../models/Comment.h"
#include "../models/Image.h"
using namespace drogon;
using namespace drogon::orm;
using namespace drogon_model::blog;

class UserController: public drogon::HttpController<UserController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(UserController::search, "/user?bioprompt={1}&usernameprompt={2}&limit={3}&offset={4}", drogon::Get);
        ADD_METHOD_TO(UserController::get, "/user/{1}", drogon::Get);
        ADD_METHOD_TO(UserController::create, "/register", drogon::Post); // todo: change endpoints in tests
        ADD_METHOD_TO(UserController::login, "/login", drogon::Post);
        ADD_METHOD_TO(UserController::updateImage, "/user/image", drogon::Put, "LoginFilter");
        ADD_METHOD_TO(UserController::updateData, "/user/update", drogon::Put, "LoginFilter");
        ADD_METHOD_TO(UserController::remove, "/user/remove", drogon::Delete, "LoginFilter");
    METHOD_LIST_END

    void search(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        const std::string &bioprompt, const std::string &usernameprompt,
        const int limit, const int offset);
    void get(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        const int id);
    void login(
        User &&newUser,
        std::function<void(const HttpResponsePtr &)> &&callback);
    void create(
        User &&newUser,
        std::function<void(const HttpResponsePtr &)> &&callback);
    void updateImage(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback);
    void updateData(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback,
        User &&editUser);
    void remove(
        const HttpRequestPtr &req,
        std::function<void(const HttpResponsePtr &)> &&callback);

private:
    bool isInputUserAllowed(const User &user);
    void addImage(
        const HttpRequestPtr &req,
        const int &userId);

private:
    Mapper<User> m_userMapper = Mapper<User>(app().getDbClient());
    Mapper<drogon_model::blog::Post> m_postMapper = Mapper<drogon_model::blog::Post>(app().getDbClient());
    Mapper<drogon_model::blog::Like> m_likeMapper = Mapper<drogon_model::blog::Like>(app().getDbClient());
    Mapper<drogon_model::blog::Comment> m_commentMapper = Mapper<drogon_model::blog::Comment>(app().getDbClient());
    Mapper<drogon_model::blog::Image> m_imageMapper = Mapper<drogon_model::blog::Image>(app().getDbClient());

};
