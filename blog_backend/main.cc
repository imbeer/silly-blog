#include <drogon/drogon.h>
int main() {

    drogon::app()
        .loadConfigFile("/app/blog_backend/local_config/config.json")
        // .addListener("0.0.0.0", 8080)
        // .addDbClient(dbConfig)
        .run();
    return 0;
}
