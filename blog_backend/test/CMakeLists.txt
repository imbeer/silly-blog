cmake_minimum_required(VERSION 3.5)
project(blog_backend_test CXX)

set(TEST_PROJECT_SOURCES
    test_main.cc
    # ../utils/jwtservice.cc
    # ../utils/jwtservice.h
)

add_executable(${PROJECT_NAME} ${TEST_PROJECT_SOURCES})

include_directories(${CMAKE_SOURCE_DIR}/3rdparty/jwt-cpp/include)
include_directories(${CMAKE_SOURCE_DIR}/3rdparty/libbcrypt/include)
# add_subdirectory(../3rdparty/libbcrypt)

# ##############################################################################
# If you include the drogon source code locally in your project, use this method
# to add drogon 
# target_link_libraries(${PROJECT_NAME} PRIVATE drogon)
#
# and comment out the following lines
target_link_libraries(${PROJECT_NAME} PRIVATE
    Drogon::Drogon
    jwt-cpp
    bcrypt
)

ParseAndAddDrogonTests(${PROJECT_NAME})
