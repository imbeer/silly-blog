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
    METHOD_LIST_END

    void upload(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);
    void get(
        const HttpRequestPtr &req,
        function<void(const HttpResponsePtr &)> &&callback);

private:
    Mapper<drogon_model::blog::Image> m_imageMapper = Mapper<drogon_model::blog::Image>(app().getDbClient());
};

#endif // IMAGECONTROLLER_H
