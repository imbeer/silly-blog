#define DROGON_TEST_MAIN
#include <drogon/drogon_test.h>
#include <drogon/drogon.h>

using namespace drogon;
using namespace std;

//todo: Like test, comment test


DROGON_TEST(GetPostsTest)
{
    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");

    const std::string author = "mcgeechristopher";
    string currentUser = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMDYifQ.xC3afYlIgVBwESPQWnNTOdQrd116i2OAngDigY62cfk";

    const int offset = 0;
    const int limit = 20;

    const auto request = HttpRequest::newHttpRequest();
    request->setPath("/posts");
    request->setMethod(drogon::Get);
    request->setParameter("author", author);
    request->setParameter("offset", std::to_string(offset));
    request->setParameter("limit", std::to_string(limit));
    request->addHeader("Authorization", "Bearer " + currentUser);

    client->sendRequest(
        request,
        [TEST_CTX, author, offset, limit](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k200OK);
                cout << response->getBody() << endl;
            } else {
                cerr << "Request failed with error: " << (int)result << endl;
                FAIL("Request failed");
            }
        });
}

DROGON_TEST(NewPostsTest)
{
    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");
    string ownerJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMDYifQ.xC3afYlIgVBwESPQWnNTOdQrd116i2OAngDigY62cfk";
    string adminJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMTEifQ.a3R-w1k-ljSqVvsp8OkFWrfZOPV96etxnYvCyJ408n8";
    string nonOwnerJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMDgifQ.BS8gjxz-_ujhRb5LER11Op0YVCM6ds8fgIC16pbPtuo";

    Json::Value newPostJson;
    newPostJson["post"]["text_content"] = "This is a test post.";

    auto postCreationRequest = HttpRequest::newHttpJsonRequest(newPostJson);
    postCreationRequest->setMethod(Post);
    postCreationRequest->setPath("/posts");
    postCreationRequest->addHeader("Authorization", "Bearer " + ownerJwt);

    int postId = 0;

    auto [result, response] = client->sendRequest(postCreationRequest);
    if (result == ReqResult::Ok && response) {
        CHECK(response->getStatusCode() == k201Created);
        postId = (*response->getJsonObject())["post"]["post_id"].asInt();
    } else {
        cerr << "Not created, error is: " << (int)result << endl;
        FAIL("Request failed");
        return;
    }

    auto editPostJson = Json::Value();
    editPostJson["post"]["post_id"] = postId;
    editPostJson["post"]["text_content"] = "edited text content";

    auto postEditFromOwnerRequest = HttpRequest::newHttpJsonRequest(editPostJson);
    postEditFromOwnerRequest->setMethod(Put);
    postEditFromOwnerRequest->setPath("/posts");
    postEditFromOwnerRequest->addHeader("Authorization", "Bearer " + ownerJwt);

    auto postEditFromAdminRequest = HttpRequest::newHttpJsonRequest(editPostJson);
    postEditFromAdminRequest->setMethod(Put);
    postEditFromAdminRequest->setPath("/posts");
    postEditFromAdminRequest->addHeader("Authorization", "Bearer " + adminJwt);

    auto postEditFromNonOwnerRequest = HttpRequest::newHttpJsonRequest(editPostJson);
    postEditFromNonOwnerRequest->setMethod(Put);
    postEditFromNonOwnerRequest->setPath("/posts");
    postEditFromNonOwnerRequest->addHeader("Authorization", "Bearer " + nonOwnerJwt);

    client->sendRequest(
        postEditFromOwnerRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k200OK);
            } else {
                cerr << "Not edited by owner, error is: " << (int)result << endl;
                FAIL("Request failed");
            }
        });

    client->sendRequest(
        postEditFromAdminRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k200OK);
            } else {
                cerr << "Not edited by admin, error is: " << (int)result << endl;
                FAIL("Request failed");
            }
        });

    client->sendRequest(
        postEditFromNonOwnerRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k403Forbidden);
            } else {
                cerr << "Something with non-owner: " << (int)result << endl;
                FAIL("Request failed");
            }
        });

    auto deletePostJson = Json::Value();
    deletePostJson["post"]["post_id"] = postId;

    auto postDeleteFromOwnerRequest = HttpRequest::newHttpJsonRequest(deletePostJson);
    postDeleteFromOwnerRequest->setMethod(Delete);
    postDeleteFromOwnerRequest->setPath("/posts");
    postDeleteFromOwnerRequest->addHeader("Authorization", "Bearer " + ownerJwt);

    auto postDeleteFromAdminRequest = HttpRequest::newHttpJsonRequest(deletePostJson);
    postDeleteFromAdminRequest->setMethod(Delete);
    postDeleteFromAdminRequest->setPath("/posts");
    postDeleteFromAdminRequest->addHeader("Authorization", "Bearer " + adminJwt);

    auto postDeleteFromNonOwnerRequest = HttpRequest::newHttpJsonRequest(deletePostJson);
    postDeleteFromNonOwnerRequest->setMethod(Delete);
    postDeleteFromNonOwnerRequest->setPath("/posts");
    postDeleteFromNonOwnerRequest->addHeader("Authorization", "Bearer " + nonOwnerJwt);

    client->sendRequest(
        postDeleteFromNonOwnerRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k403Forbidden);
            } else {
                cerr << "Something with non-owner deletion: " << (int)result << endl;
                FAIL("Request failed");
            }
        });

    client->sendRequest(
        postDeleteFromOwnerRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k204NoContent);
            } else {
                cerr << "Not deleted by owner: " << (int)result << endl;
                FAIL("Request failed");
            }
        });
}

// DROGON_TEST(NewUserTest)
// {
//     using namespace drogon;
//     using namespace std;

//     auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");

//     Json::Value userData;
//     userData["user"]["username"] = "admin";
//     userData["user"]["password"] = "admin";
//     userData["user"]["email"] = "admin@gmail.com";

//     auto userRegistrationRequest = HttpRequest::newHttpJsonRequest(userData);
//     userRegistrationRequest->setMethod(Post);
//     userRegistrationRequest->setPath("/users");

//     client->sendRequest(
//         userRegistrationRequest,
//         [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
//             if (result == ReqResult::Ok && response) {
//                 CHECK(response->getStatusCode() == k201Created);
//                 auto json = response->getJsonObject();
//                 string jsonWebToken = (*json)["token"].asString();
//                 cout << "jwt of admin is: " << jsonWebToken << endl;
//             } else {
//                 cerr << "Request failed with error: " << (int)result << endl;
//                 FAIL("Request failed");
//             }
//         });
// }

DROGON_TEST(LoginUserTest)
{
    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");

    Json::Value userData;
    userData["user"]["password"] = "0000";
    userData["user"]["email"] = "cool_test_email3@gmail.com";

    auto userRegistrationRequest = HttpRequest::newHttpJsonRequest(userData);
    userRegistrationRequest->setMethod(Post);
    userRegistrationRequest->setPath("/users/login");

    client->sendRequest(
        userRegistrationRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k200OK);
                auto json = response->getJsonObject();
                string loginJwt = (*json)["token"].asString();
                CHECK((*json)["user"]["username"] == "cool_test_username_3");
                cout << "jwt is: " << loginJwt << endl;
            } else {
                cerr << "Request failed with error: " << (int)result << endl;
                FAIL("Request failed");
            }
        });
}

int main(int argc, char** argv) 
{
    using namespace drogon;

    std::promise<void> p1;
    std::future<void> f1 = p1.get_future();

    std::thread thr([&]() {
        app().getLoop()->queueInLoop([&p1]() { p1.set_value(); });
        app().run();
    });

    f1.get();
    int status = test::run(argc, argv);

    app().getLoop()->queueInLoop([]() { app().quit(); });
    thr.join();
    return status;
}
