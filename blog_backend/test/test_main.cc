#define DROGON_TEST_MAIN
#include <drogon/drogon_test.h>
#include <drogon/drogon.h>

DROGON_TEST(BasicTest)
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

                // Verify the status code is 200 OK
                CHECK(response->getStatusCode() == k200OK);
            } else {
                // If there's an error, print the error and fail the test
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

    // Start the main loop on another thread
    std::thread thr([&]() {
        // Queues the promise to be fulfilled after starting the loop
        app().getLoop()->queueInLoop([&p1]() { p1.set_value(); });
        app().run();
    });

    // The future is only satisfied after the event loop started
    f1.get();
    int status = test::run(argc, argv);

    // Ask the event loop to shutdown and wait
    app().getLoop()->queueInLoop([]() { app().quit(); });
    thr.join();
    return status;
}
