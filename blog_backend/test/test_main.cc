#define DROGON_TEST_MAIN
#include <drogon/drogon_test.h>
#include <drogon/drogon.h>
// #include "../utils/jwtservice.h"
// #include "../models/User.h"

DROGON_TEST(GetPostsTest)
{
    using namespace drogon;
    using namespace std;

    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");
    const std::string author = "mcgeechristopher";
    const int offset = 0;
    const int limit = 20;
    const auto request = HttpRequest::newHttpRequest();
    request->setPath("/posts");
    request->setParameter("author", author);
    request->setParameter("offset", std::to_string(offset));
    request->setParameter("limit", std::to_string(limit));

    client->sendRequest(
        request,
        [TEST_CTX, author, offset, limit](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                cout << "Request URL: /posts?author=" << author
                     << "&offset=" << offset << "&limit=" << limit << endl;
                cout << "Status Code: " << response->getStatusCode() << endl;
                cout << "Response Body: " << response->getBody() << endl;

                CHECK(response->getStatusCode() == k200OK);
            } else {
                cerr << "Request failed with error: " << (int)result << endl;
                FAIL("Request failed");
            }
        });
}

DROGON_TEST(NewPostsTest)
{
    // todo: WTF WITH THIS TEST
    using namespace drogon;
    using namespace std;

    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");

    Json::Value postData;
    postData["post"]["user_id"] = 205;
    postData["post"]["text_content"] = "This is a test post.";
    // cout << "jwt is: " << jsonWebToken << endl;
    string jsonWebToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMDYifQ.xC3afYlIgVBwESPQWnNTOdQrd116i2OAngDigY62cfk";

    auto postCreationRequest = HttpRequest::newHttpJsonRequest(postData);
    postCreationRequest->setMethod(Post);
    postCreationRequest->setPath("/posts");
    postCreationRequest->addHeader("Authorization", "Bearer " + jsonWebToken);

    client->sendRequest(
        postCreationRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k201Created);
            } else {
                cerr << "Request failed with error: " << (int)result << endl;
                FAIL("Request failed");
            }
        });
}

DROGON_TEST(NewUserTest)
{
    using namespace drogon;
    using namespace std;

    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");

    Json::Value userData;
    userData["user"]["username"] = "cool_test_username_2";
    userData["user"]["password"] = "0000";
    userData["user"]["email"] = "cool_test_email2@gmail.com";

    auto userRegistrationRequest = HttpRequest::newHttpJsonRequest(userData);
    userRegistrationRequest->setMethod(Post);
    userRegistrationRequest->setPath("/users");

    client->sendRequest(
        userRegistrationRequest,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k201Created);
                auto json = response->getJsonObject();
                string jsonWebToken = (*json)["token"].asString();
                cout << "jwt is: " << jsonWebToken << endl;
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
