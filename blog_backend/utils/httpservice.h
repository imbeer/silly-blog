#pragma once

#include <drogon/HttpResponse.h>
#include <drogon/MultiPart.h>

using namespace drogon;
using namespace std;

namespace httpService
{

optional<string> saveImageFromReq(const HttpRequestPtr &req);

string generateUUID();

inline void sendImageByPath(
    const string &path,
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback)
{
    try {
        (*callback)(HttpResponse::newFileResponse("./uploads/" + path, "", CT_IMAGE_JPG));
    } catch (const exception &e) {
        (*callback)(HttpResponse::newHttpResponse(drogon::k500InternalServerError, CT_NONE));
    }
}

inline void sendEmptyResponse(
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
    const HttpStatusCode &code)
{
    (*callback)(HttpResponse::newHttpResponse(code, CT_NONE));
}
}
