import random
from faker import Faker

fake = Faker()

USERNUM = 200
POSTNUM = 5
COMMENTNUM = 10

with open("migrations.sql", "w", encoding="utf-8") as f:
    queries = []

    user_ids = []
    for i in range(1, 1 + USERNUM):
        username = fake.user_name()[:32]
        email = fake.email()
        password = fake.sha256()
        text_content = fake.text(max_nb_chars=250)
        is_admin = False
        image_link = fake.image_url()

        query = f"INSERT INTO \"user\" (image_link, username, password, bio, email, is_admin) VALUES ('{image_link}', '{username}', '{password}', '{text_content}', '{email}', {is_admin});"
        queries.append(query)
        user_ids.append(i)

    post_ids = []
    post_id_counter = 1
    for user_id in user_ids:
        num_posts = POSTNUM
        for _ in range(num_posts):
            post_content = fake.text(max_nb_chars=320)
            time = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')

            query = f"INSERT INTO \"post\" (user_id, text_content, time) VALUES ({user_id}, '{post_content}', '{time}');"
            queries.append(query)
            post_ids.append(post_id_counter)
            post_id_counter += 1

    comment_id_counter = 1
    for post_id in post_ids:
        num_comments = COMMENTNUM
        for _ in range(num_comments):
            commenter_id = random.choice(user_ids)
            comment_content = fake.text(max_nb_chars=320)
            time = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')

            query = f"INSERT INTO \"comment\" (post_id, user_id, text_content, time) VALUES ({post_id}, {commenter_id}, '{comment_content}', '{time}');"
            queries.append(query)
            comment_id_counter += 1

    image_id_counter = 1
    for post_id in post_ids:
        num_images = random.randint(0, 3)
        for _ in range(num_images):
            image_url = fake.image_url()
            query = f"INSERT INTO \"image\" (post_id, url) VALUES ({post_id}, '{image_url}');"
            queries.append(query)
            image_id_counter += 1


    for post_id in post_ids:
        random_user_id = random.randint(1, len(user_ids) - 1)
        query = f"INSERT INTO \"like\" (post_id, user_id) VALUES ({post_id}, {random_user_id});";
        queries.append(query)

    for query in queries:
        f.write(query + "\n")

print("Data generation complete. Check 'migrations.sql' for the generated queries.")
