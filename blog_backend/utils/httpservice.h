#pragma once

#include <drogon/HttpResponse.h>

using namespace drogon;
using namespace std;

namespace httpService
{
inline void sendEmptyResponse(
    const std::shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
    const HttpStatusCode &code)
{
    (*callback)(HttpResponse::newHttpResponse(code, CT_NONE));
}
}
