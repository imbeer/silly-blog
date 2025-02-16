cmake_minimum_required(VERSION 3.5)
project(blog_backend CXX)

include(CheckIncludeFileCXX)

check_include_file_cxx(any HAS_ANY)
check_include_file_cxx(string_view HAS_STRING_VIEW)
check_include_file_cxx(coroutine HAS_COROUTINE)
if (NOT "${CMAKE_CXX_STANDARD}" STREQUAL "")
    # Do nothing
elseif (HAS_ANY AND HAS_STRING_VIEW AND HAS_COROUTINE)
    set(CMAKE_CXX_STANDARD 20)
elseif (HAS_ANY AND HAS_STRING_VIEW)
    set(CMAKE_CXX_STANDARD 17)
else ()
    set(CMAKE_CXX_STANDARD 14)
endif ()

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(PROJECT_SOURCES
    main.cc
    controllers/PostController.cc
    controllers/PostController.h
    controllers/UserController.cc
    controllers/UserController.h
    controllers/CommentController.cc
    controllers/CommentController.h
    controllers/LikeController.cc
    controllers/LikeController.h
    controllers/ImageController.cc
    controllers/ImageController.h
    models/Comment.cc
    models/Comment.h
    models/Image.cc
    models/Image.h
    models/Like.cc
    models/Like.h
    models/Post.cc
    models/Post.h
    models/User.cc
    models/User.h
    filters/LoginFilter.cc
    filters/LoginFilter.h
    filters/PostRightFilter.cc
    filters/PostRightFilter.h
    filters/CommentRightFilter.h
    filters/CommentRightFilter.cc
    utils/jwtservice.cc
    utils/jwtservice.h
    utils/parseservice.h
    utils/httpservice.cc
    utils/httpservice.h
)


add_executable(${PROJECT_NAME} ${PROJECT_SOURCES}
)

# ##############################################################################
# If you include the drogon source code locally in your project, use this method
# to add drogon 
# add_subdirectory(drogon) 
# target_link_libraries(${PROJECT_NAME} PRIVATE drogon)
#
# and comment out the following lines
add_subdirectory(3rdparty/jwt-cpp)
add_subdirectory(3rdparty/libbcrypt)

find_package(Drogon CONFIG REQUIRED)
target_link_libraries(${PROJECT_NAME} PRIVATE
    Drogon::Drogon
    jwt-cpp
    bcrypt
)

# ##############################################################################

if (CMAKE_CXX_STANDARD LESS 17)
    message(FATAL_ERROR "c++17 or higher is required")
elseif (CMAKE_CXX_STANDARD LESS 20)
    message(STATUS "use c++17")
else ()
    message(STATUS "use c++20")
endif ()

# aux_source_directory(controllers CTL_SRC)
# aux_source_directory(filters FILTER_SRC)
# aux_source_directory(plugins PLUGIN_SRC)
# aux_source_directory(models MODEL_SRC)

# drogon_create_views(${PROJECT_NAME} ${CMAKE_CURRENT_SOURCE_DIR}/views
#                     ${CMAKE_CURRENT_BINARY_DIR})
# use the following line to create views with namespaces.
# drogon_create_views(${PROJECT_NAME} ${CMAKE_CURRENT_SOURCE_DIR}/views
#                     ${CMAKE_CURRENT_BINARY_DIR} TRUE)
# use the following line to create views with namespace CHANGE_ME prefixed
# and path namespaces.
# drogon_create_views(${PROJECT_NAME} ${CMAKE_CURRENT_SOURCE_DIR}/views
#                     ${CMAKE_CURRENT_BINARY_DIR} TRUE CHANGE_ME)

# target_include_directories(${PROJECT_NAME}
#                            PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
#                                    ${CMAKE_CURRENT_SOURCE_DIR}/models)
# target_sources(${PROJECT_NAME}
#                PRIVATE
#                ${SRC_DIR}
#                ${CTL_SRC}
#                ${FILTER_SRC}
#                ${PLUGIN_SRC}
#                ${MODEL_SRC})
# ##############################################################################
# uncomment the following line for dynamically loading views 
# set_property(TARGET ${PROJECT_NAME} PROPERTY ENABLE_EXPORTS ON)

# ##############################################################################

add_subdirectory(test)
