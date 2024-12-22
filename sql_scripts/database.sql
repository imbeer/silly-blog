CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    image_link VARCHAR(2048),
    username VARCHAR(32) UNIQUE,
    password CHAR(64) NOT NULL,
    bio VARCHAR(256), 
    email VARCHAR(32) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE "post" (
    post_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    text_content TEXT,
    time TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id)
);

CREATE TABLE "like" (
    post_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES "post"(post_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id)
);

CREATE TABLE "comment" (
    comment_id SERIAL PRIMARY KEY,
    post_id INTEGER,
    user_id INTEGER,
    text_content TEXT NOT NULL,
    time TIMESTAMP NOT NULL,
    FOREIGN KEY (post_id) REFERENCES "post"(post_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id)
);

CREATE TABLE "image" (
    image_id SERIAL PRIMARY KEY,
    post_id INTEGER,
    url VARCHAR(2048) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES "post"(post_id)
);

