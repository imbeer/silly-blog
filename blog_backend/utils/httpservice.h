#pragma once

#include <drogon/HttpResponse.h>
#include <drogon/MultiPart.h>

using namespace drogon;
using namespace std;

namespace httpService
{

optional<string> saveImageFromReq(
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
    const HttpRequest &req);


string generateUniqueFileName();

inline void sendImageByPath(
    const string &path,
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback)
{
    (*callback)(HttpResponse::newFileResponse("./uploads" + path, "", CT_IMAGE_JPG));
}

inline void sendEmptyResponse(
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
    const HttpStatusCode &code)
{
    (*callback)(HttpResponse::newHttpResponse(code, CT_NONE));
}
}
