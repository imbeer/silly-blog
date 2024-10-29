#include "SimpleController.h"

void SimpleController::getItem(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback) {
    auto response = HttpResponse::newHttpResponse();
    response->setBody("GET request: Returning item details");
    callback(response);
}

void SimpleController::createItem(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback) {
    auto response = HttpResponse::newHttpResponse();
    response->setBody("POST request: Creating new item");
    callback(response);
}

void SimpleController::updateItem(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback) {
    auto response = HttpResponse::newHttpResponse();
    response->setBody("PUT request: Updating item details");
    callback(response);
}

void SimpleController::deleteItem(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback) {
    auto response = HttpResponse::newHttpResponse();
    response->setBody("DELETE request: Deleting item");
    callback(response);
}
