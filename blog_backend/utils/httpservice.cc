#include "httpservice.h"
#include <random>

namespace httpService
{

optional<string> saveImageFromReq(
    const shared_ptr<function<void(const HttpResponsePtr &)>> &callback,
    const HttpRequest &req)
{
    MultiPartParser fileUpload;

    if (fileUpload.parse(req) != 0 || fileUpload.getFiles().size() != 1)
    {
        auto resp = HttpResponse::newHttpResponse();
        resp->setBody("Must only be one file");
        resp->setStatusCode(k400BadRequest);
        (*callback)(resp);
        return nullopt;
    }

    auto &file = fileUpload.getFiles()[0];
    auto md5 = file.getMd5();
    auto resp = HttpResponse::newHttpResponse();
    resp->setBody("The server has calculated the file's MD5 hash to be " + md5);

    string filename = httpService::generateUniqueFileName();

    try {
        file.saveAs(filename);
        resp->setStatusCode(drogon::k201Created);
    } catch (const exception &e) {
        resp->setStatusCode(drogon::k500InternalServerError);
    }

    (*callback)(resp);
    return optional<string>(filename);
}

string generateUniqueFileName()
{
    const auto now = chrono::system_clock::now();
    const auto time = chrono::system_clock::to_time_t(now);
    std::stringstream stringStream;
    stringStream << std::put_time(localtime(&time), "%Y%m%d%H%M%S");

    random_device randomDevice;
    uniform_int_distribution<int> dist(0, 15);
    stringStream << "_";
    for (int i = 0; i < 8; ++i) {
        stringStream << hex << dist(randomDevice);
    }

    return stringStream.str();
}

}
