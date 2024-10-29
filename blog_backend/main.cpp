#include <drogon/drogon.h>
int main() {
    drogon::app().addListener("0.0.0.0", 8080)
        .run();
    return 0;
}
