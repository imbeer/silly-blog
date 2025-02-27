#ifndef IMAGECONTROLLER_H
#define IMAGECONTROLLER_H

#include <drogon/HttpController.h>

#include "../models/Image.h"

using namespace drogon;
using namespace drogon::orm;
using namespace std;

class ImageController : public HttpController<ImageController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(ImageController::get, "/image?image_id={1}", drogon::Get);
        ADD_METHOD_TO(ImageController::getByLink, "/image/{1}", drogon::Get);
        ADD_METHOD_TO(ImageController::upload, "/image/upload", drogon::Post/*, "LoginFilter"*/);
    METHOD_LIST_END

    void get(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback,
        const int& imageId);
    void getByLink(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback,
        const string &link);
    void upload(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    // void remove(
    //     const HttpRequestPtr &req,
    //     function<void(const HttpResponsePtr &)> &&callback);

private:
    Mapper<drogon_model::blog::Image> m_imageMapper = Mapper<drogon_model::blog::Image>(app().getDbClient());
};

#endif // IMAGECONTROLLER_H
