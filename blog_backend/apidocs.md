# Silly Blog backend api

### Endpoints
#### Userdata:

- GET: /users?bioprompt={text}&usernameprompt={text}&limit={int}&offset={int}

Search users by "biography text" and "username"

Resp: 200 if ok, 400 if wrong request


- GET: /user?id={int}

Gets data about one user by id

Resp: 200 if ok, 400 if wrong request

- POST: /register

Obviously, endpoint for registering (creating new one) users

requires:
```
"user": {
    "email": text,
    "password": text,
    "username": text,
}
```

returns: 
```
"user": {
    "email": text,
    "username": text,
    "image_link": link in system (text),
    "is_admin": bool,
    "bio": text
}
"token": jwt (text)
```
Resp: 201 if ok, 400 if wrong request

- POST: /login

Same as /register, but logs in existing user.
Does not require "username" in input json.

Resp: 200 if ok, 401 if wrong password, 400 if wrong email

- PUT: /user/image

Updates profile picture of current user.

requires:
```     
   jwt in authorisation:
   Authorisation: Bearer jwt      

   jpeg picture in request body
```

Resp: 201 if ok, 400 if something wrong, 401 if wrong jwt

- PUT: /user/update
updates all data in current user profile except image link

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"user": {
    "email": text,
    "username": text,
    "password": text,
    "bio": text
}
```

Resp: 200 if ok 400 if something wrong, 401 if wrong jwt


- DELETE: /user/remove
removes current user profile, posts, comments and likes

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt  
```

Resp: 204 if deleted, 401 if wrong jwt, 400 if already deleted or something wrong

#### Postdata:

- GET: /posts?author={text}&prompt={text}&offset={int}&limit={int}

All posts of author (can be empty -> all authors) &&
All posts with prompt text inside (can be empty -> no restrictions)

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   
```

returns:
```
[
    {
        "post_id": int,
        "user_id": int,
        "author": text (username),
        "text_content": text,
        "likes": int,
        "isLiked": bool,
        "images": [int, int, int (image ids)]
        "time": timestamp
        "canBeEdited" bool
    },
... sorted by time
]
```

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt  
```

Resp: 200 if ok, 401 if wrong jwt, 500 if you are cringe, should not be possible 

- POST: /posts

Creates post by current user

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"post":
{
    "text_content": text,
    "images": [int, int, int (image ids)]
}
```

Resp: 201 if ok, 400 if not, 401 if wrong jwt


- PUT: /posts

Edits post by id. Current user must be owner or admin.
requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"post":
{
    "post_id": int,
    "text_content": text,
    "images": [int, int, int (image ids)]
}
```

Resp: 200 if ok, 400 if not, 403 if not available.

- DELETE: /posts

Deletes post by id. Also deletes images, comments and likes. Current user must be owner or admin.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"post":
{
    "post_id"": int,
}
```

Resp: 204 if ok, 400 if not, 403 if not available.

#### Commentdata:

- GET: /post?id={int}&author={text}&offset={int}&limit={int}

Gets comments from post_id by author (username). If author empty, then gets comments from all users.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   
```

returns:
```
[
    {
        "comment_id": int,
        "user_id": int, 
        "text_content": int
        "time": timestamp   
        "canBeEdited": bool
    }
]
```

Resp: 200 if ok, else 400.

- POST: /post/comment

Creates comment under post.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"comment":
{
    "post_id"": int,
    "text_content": text (comment itself)
}
```

returns:
```
[
    {
        "comment_id": int,
        "user_id": int, 
        "text_content": int
        "time": timestamp
    }
]
```

Resp: 201 if ok, 401 if wrong jwt, 400 if wrong data

- PUT: /post/comment

Edits comment by id. Current user must be owner or admin.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"comment":
{
    "comment_id"": int,
    "text_content": text (comment itself)
}
```

returns:
```
[
    {
        "comment_id": int,
        "user_id": int, 
        "text_content": int
        "time": timestamp
    }
]
```

Resp: 200 if ok. 400 if not. 403 if not available to edit.

- Delete: 

Deletes comment by id. Current user must be owner or admin.

```
jwt in authorisation:
Authorisation: Bearer jwt   

"comment":
{
    "comment_id":: int,
}
```

Resp: 204 if ok. 400 if not. 403 if not available to edit.


#### Likedata:

- GET: /post/like?post_id={int} 

Gets likes from one post.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   
```

returns:
```
{
    "post_id": int,
    "count": int (how many likes), 
    "is_liked": bool (by current user),
}
```

Resp: 200 if ok, 400 if not, 401 if wrong jwt.

- PUT: /post/like

Adds like to post from current user if not liked yet.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"post": 
{
    "post_id": int
}
```

Resp: 201 if ok, 400 if wrong id, 401 if wrong jwt, 409 if was already liked, 500 if you are cringe, should not be able


- DELETE: /post/like 

'Unlikes' post, if was liked already.

requires:
```
jwt in authorisation:
Authorisation: Bearer jwt   

"post": 
{
    "post_id": int
}
```

Resp: 204 if ok, 409 if wasnt liked, 400 if wrong id, 401 if wrong jwt, 500 - fuck you in particular (idk how you did this)

#### Imagedata:

- GET: /image?image_id={1}

Gets image by id.
Resp: 200 + file in response body. 400 if there is no such id.

- POST: /image/upload

Uploads image

requires: image in req body.

returns: 
```
int (image_id)
```

Resp: 201 if ok, 500 if not.



### Deploy:
For now it works on docker compose where postgres data directory and image uploads directory are mounted as volumes.
However this isn't good because images and database data will disappear when I'll change machine, but I dont have time to fix it and use amazon s3 or something to store images. This is still an educational project and I know how to make it better, but I dont have enough money to experiment with different api's.

instructions for deployment:

1. install docker
2. install git
3. clone repo
4. cd silly blog
5. docker compose up -d
6. profit

Also I dont want to mess with dns yet, so Ill use raw ips. 
Jwt secret is in the "local_config/config.json" file. Re-login to account when token expires not implemented yet.
