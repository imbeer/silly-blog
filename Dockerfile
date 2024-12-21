FROM ubuntu:22.04 as build

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    git \
    libjsoncpp-dev \
    uuid-dev \
    openssl \
    libssl-dev \
    zlib1g-dev \
    libbrotli-dev \
    libsqlite3-dev \
    libboost-all-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /tmp
RUN git clone --branch v1.8.0 --depth 1 https://github.com/drogonframework/drogon.git && \
    cd drogon && \
    mkdir build && cd build && \
    cmake .. -DCMAKE_INSTALL_PREFIX=/usr && \
    make -j$(nproc) && \
    make install

WORKDIR /app

ARG GIT_REPO_URL=https://github.com/imbeer/silly-blog
ARG GIT_BRANCH=backend
RUN git clone --branch ${GIT_BRANCH} ${GIT_REPO_URL} . && \
    git submodule update --init --recursive

RUN cd blog_backend &&
    mkdir -p build && \
    cd build && \
    cmake .. && \
    make -j$(nproc)

FROM ubuntu:22.04

RUN apt-get update && apt-get install -y --no-install-recommends \
    libssl-dev \
    libboost-all-dev \
    uuid-dev \
    libcurl4 \
    zlib1g && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8080

CMD ["./blog_backend"]

