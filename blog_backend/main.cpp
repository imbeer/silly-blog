#include <drogon/drogon.h>
int main() {

    // drogon::orm::DbConfig dbConfig = drogon::orm::PostgresConfig(
    //     "127.0.0.1",
    //     5432,
    //     "blog",
    //     "postgres",
    //     1440,
    //     1,
    //     "default",
    //     false,
    //     "utf8",
    //     -1.0,
    //     false);

    drogon::app()
        .loadConfigFile("/home/tod/Projects/BLOG/blog_backend/config.json")
        // .addListener("0.0.0.0", 8080)
        // .addDbClient(dbConfig)
        .run();
    return 0;
}
