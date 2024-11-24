#define DROGON_TEST_MAIN
#include <drogon/drogon_test.h>
#include <drogon/drogon.h>

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
                // Print the URL with parameters and the response data
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
    using namespace drogon;
    using namespace std;

    auto client = HttpClient::newHttpClient("http://127.0.0.1:8080");
    Json::Value postData;

    postData["post"]["user_id"] = 1;
    postData["post"]["text_content"] = "This is a test post.";

    auto request = HttpRequest::newHttpJsonRequest(postData);
    request->setMethod(Post);
    request->setPath("/posts");

    client->sendRequest(
        request,
        [TEST_CTX](ReqResult result, const HttpResponsePtr &response) {
            if (result == ReqResult::Ok && response) {
                CHECK(response->getStatusCode() == k201Created);
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
