# Use the pre-built Drogon image as the base
FROM drogonframework/drogon:latest
WORKDIR /app

ARG GIT_REPO_URL=https://github.com/imbeer/silly-blog
ARG GIT_BRANCH=backend
RUN git clone --branch ${GIT_BRANCH} ${GIT_REPO_URL} . && \
    git submodule update --init --recursive

RUN cd blog_backend && \
    mkdir -p build && \
    cd build && \
    cmake .. && \
    make -j$(nproc)

EXPOSE 8080

CMD ["./blog_backend/build/blog_backend"]

