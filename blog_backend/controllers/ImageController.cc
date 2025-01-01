#include "ImageController.h"

#include "../utils/httpservice.h"

void ImageController::get(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    const int& imageId)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    m_imageMapper.findByPrimaryKey(
        imageId,
        [callbackPtr](const drogon_model::blog::Image &image)
        {
            httpService::sendImageByPath(image.getValueOfUrl(), callbackPtr);
        },
        [callbackPtr](const DrogonDbException& e)
        {
            httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
        });
}
void ImageController::getByLink(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback,
    const string &link)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
    httpService::sendImageByPath(link, callbackPtr);
}

void ImageController::upload(
    const HttpRequestPtr &req,
    function<void(const HttpResponsePtr &)> &&callback)
{
    auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));

    const optional<string> path = httpService::saveImageFromReq(/*callbackPtr, */req);
    if (!path.has_value()) {
        httpService::sendEmptyResponse(callbackPtr, k500InternalServerError);
        return;
    }

    drogon_model::blog::Image image;
    image.setUrl(path.value());

    m_imageMapper.insert(
        image,
        [callbackPtr](const drogon_model::blog::Image &image)
        {
            auto resp = HttpResponse::newHttpJsonResponse(image.toJson()["image_id"]);
            resp->setStatusCode(drogon::k201Created);
            (*callbackPtr)(resp);
        },
        [callbackPtr](const DrogonDbException &e)
        {
            httpService::sendEmptyResponse(callbackPtr, k500InternalServerError);
        });
}

// void ImageController::remove(
//     const HttpRequestPtr &req,
//     function<void(const HttpResponsePtr &)> &&callback)
// {
//     auto callbackPtr = make_shared<function<void(const HttpResponsePtr &)>>(std::move(callback));
//     const int imageId = parseService::getImageIdFromRequest(*req);


//     try {
//         m_imageMapper.deleteByPrimaryKey(imageId);
//         httpService::sendEmptyResponse(callbackPtr, k204NoContent);
//     } catch (const exception &e) {
//         httpService::sendEmptyResponse(callbackPtr, k400BadRequest);
//     }
// }
