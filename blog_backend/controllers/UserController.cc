#include "UserController.h"
#include "../utils/jwtservice.h"
#include "../utils/httpservice.h"
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

void UserController::search(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    const string &bioprompt, const string &usernameprompt,
    const int limit, const int offset)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    const auto bioCriteria = Criteria(
        drogon_model::blog::User::Cols::_bio,
        CompareOperator::Like,
        "%" + bioprompt + "%");
    const auto nameCriteria = Criteria(
        drogon_model::blog::User::Cols::_username,
        CompareOperator::Like,
        "%" + usernameprompt + "%");
    Json::Value responseBody;

    try {
        const auto users = m_userMapper.limit(limit).offset(offset).findBy(bioCriteria && nameCriteria);
        for (const auto &user : users) {
            Json::Value userJson;
            userJson["username"] = user.getValueOfUsername();
            userJson["user_id"] = user.getValueOfUserId();
            userJson["image_link"] = user.getValueOfImageLink();
            userJson["is_admin"] = user.getValueOfIsAdmin() ? 1 : 0;
            responseBody.append(userJson);
        }
    } catch (const exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
    auto response = HttpResponse::newHttpJsonResponse(responseBody);
    response->setStatusCode(HttpStatusCode::k200OK);
    (*callbackPtr)(response);
}

void UserController::get(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback,
    const int id)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    try {
        const auto user = m_userMapper.findByPrimaryKey(id);
        Json::Value userJson;
        userJson["username"] = user.getValueOfUsername();
        userJson["user_id"] = user.getValueOfUserId();
        userJson["image_link"] = user.getValueOfImageLink();
        auto response = HttpResponse::newHttpJsonResponse(userJson);
        response->setStatusCode(HttpStatusCode::k200OK);
        (*callbackPtr)(response);
    } catch (const exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

void UserController::login(
    User &&newUser,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    m_userMapper.findOne(
        Criteria(User::Cols::_email, CompareOperator::EQ, newUser.getValueOfEmail()),
        [=](const User &user) {
            auto passhash = user.getValueOfPassword();

            size_t end = passhash.find_last_not_of(' ');
            if (end != string::npos)
                passhash.erase(end + 1);

            if (BCrypt::validatePassword(newUser.getValueOfPassword(), passhash)) {
                auto json = Json::Value();
                json["user"] = user.toJson();
                json["user"].removeMember("password");
                json["token"] = jwtService::generateFromUser(user);
                auto resp = HttpResponse::newHttpJsonResponse(json);
                resp->setStatusCode(HttpStatusCode::k200OK);
                (*callbackPtr)(resp);
            } else {
                auto resp = HttpResponse::newHttpResponse();
                resp->setStatusCode(HttpStatusCode::k401Unauthorized);
                (*callbackPtr)(resp);
            }
        },
        [callbackPtr](const DrogonDbException &e) {
            LOG_ERROR << e.base().what();
            auto resp = HttpResponse::newHttpResponse();
            resp->setStatusCode(HttpStatusCode::k400BadRequest);
            (*callbackPtr)(resp);
        });
}

void UserController::create(
    User &&newUser,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    if (!isInputUserAllowed(newUser)) {
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        (*callbackPtr)(resp);
        return;
    }
    newUser.setPassword(BCrypt::generateHash(newUser.getValueOfPassword()));
    m_userMapper.insert(
        newUser,
        [callbackPtr](const User &user) {
            auto json = Json::Value();
            json["user"] = user.toJson();
            json["user"].removeMember("password");
            json["token"] = jwtService::generateFromUser(user);
            auto response = HttpResponse::newHttpJsonResponse(json);
            response->setStatusCode(HttpStatusCode::k201Created);
            (*callbackPtr)(response);
        },
        [callbackPtr](const drogon::orm::DrogonDbException &e) {
            LOG_ERROR << e.base().what();
            auto response = HttpResponse::newHttpResponse();
            response->setStatusCode(HttpStatusCode::k400BadRequest);
            (*callbackPtr)(response);
        });
}

void UserController::updateImage(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    try {
        addImage(req, jwtService::getCurrentUserIdFromRequest(req).value_or(-1));
        httpService::sendEmptyResponse(callbackPtr, k201Created);
    } catch (const exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

void UserController::updateData(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    User &&editUser)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    auto currentUser = jwtService::getCurrentUserFromRequest(req).value();
    currentUser.setBio(editUser.getValueOfBio());

    if (editUser.getUsername()) {
        currentUser.setUsername(editUser.getValueOfUsername());
    }
    if (editUser.getEmail()) {
        currentUser.setEmail(editUser.getValueOfEmail());
    }
    if (editUser.getPassword()) {
        currentUser.setPassword(BCrypt::generateHash(editUser.getValueOfPassword()));
    }
    try {
        m_userMapper.update(currentUser);
        httpService::sendEmptyResponse(callbackPtr, k200OK);
    } catch (const exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}
void UserController::remove(
    const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    auto user = jwtService::getCurrentUserFromRequest(req);
    vector<int> postIds;

    try {
        auto result = app().getDbClient()->execSqlSync("select post_id from post where user_id = $1", user->getValueOfUserId());
        for (const auto &row : result) {
            postIds.push_back(row["post_id"].as<int>());
        }

        const auto postCriteria = Criteria(
            drogon_model::blog::Post::Cols::_post_id,
            CompareOperator::In,
            postIds);
        const auto imageCriteria = Criteria(
            drogon_model::blog::Image::Cols::_post_id,
            CompareOperator::In,
            postIds);
        const auto commentFromPostsCriteria = Criteria(
            drogon_model::blog::Comment::Cols::_post_id,
            CompareOperator::In,
            postIds);
        const auto commentFromUserCriteria = Criteria(
            drogon_model::blog::Comment::Cols::_user_id,
            CompareOperator::EQ,
            user->getValueOfUserId());
        const auto likesFromPostsCriteria = Criteria(
            drogon_model::blog::Like::Cols::_post_id,
            CompareOperator::In,
            postIds);
        const auto likesFromUserCriteria = Criteria(
            drogon_model::blog::Like::Cols::_user_id,
            CompareOperator::EQ,
            user->getValueOfUserId());

        m_imageMapper.deleteBy(imageCriteria);
        m_commentMapper.deleteBy(commentFromPostsCriteria);
        m_commentMapper.deleteBy(commentFromUserCriteria);
        m_likeMapper.deleteBy(likesFromUserCriteria);
        m_likeMapper.deleteBy(likesFromPostsCriteria);
        m_postMapper.deleteBy(postCriteria);
        m_userMapper.deleteByPrimaryKey(user->getValueOfUserId());
        httpService::sendEmptyResponse(callbackPtr, k204NoContent);
    } catch (const exception &e) {
        httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
    }
}

bool UserController::isInputUserAllowed(const User &user)
{
    if (!user.getEmail() || !user.getUsername() || !user.getPassword()) {
        return false;
    }
    auto usernameCriteria = Criteria(User::Cols::_username, CompareOperator::EQ, user.getValueOfUsername());
    auto emailCriteria = Criteria(User::Cols::_email, CompareOperator::EQ, user.getValueOfEmail());
    return m_userMapper.findBy(usernameCriteria || emailCriteria).empty();
}

void UserController::addImage(
    const HttpRequestPtr &req,
    const int &userId)
{
    auto user = m_userMapper.findByPrimaryKey(userId);
    auto imageLink = httpService::saveImageFromReq(req);
    if (!imageLink.has_value()) {
        throw runtime_error("Unable to upload image");
    }
    user.setImageLink(imageLink.value());
    m_userMapper.update(user);
}
