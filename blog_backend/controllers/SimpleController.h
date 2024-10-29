#pragma once
#include <drogon/HttpController.h>

using namespace drogon;

class SimpleController : public HttpController<SimpleController> {
public:
    METHOD_LIST_BEGIN
    // Define routes and methods for each endpoint
    ADD_METHOD_TO(SimpleController::getItem, "/item", Get);
    ADD_METHOD_TO(SimpleController::createItem, "/item", Post);
    ADD_METHOD_TO(SimpleController::updateItem, "/item", Put);
    ADD_METHOD_TO(SimpleController::deleteItem, "/item", Delete);
    METHOD_LIST_END

    // Handlers for each method
    void getItem(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void createItem(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void updateItem(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void deleteItem(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
