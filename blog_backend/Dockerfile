# Use the pre-built Drogon image as the base
FROM drogonframework/drogon:latest
WORKDIR /app/blog_backend

#ARG GIT_REPO_URL=https://github.com/imbeer/silly-blog
#ARG GIT_BRANCH=backend
#RUN git clone --branch ${GIT_BRANCH} ${GIT_REPO_URL} . && \
#    cd blog_backend && \
#    cd 3rdparty && \
#    git clone https://github.com/Thalhammer/jwt-cpp && \
#    git clone https://github.com/trusch/libbcrypt

COPY . /app/blog_backend

RUN mkdir -p build && \
    cd build && \
    cmake .. && \
    make -j$(nproc)

EXPOSE 8080

CMD ["./build/blog_backend"]

