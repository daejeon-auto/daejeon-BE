
# Api
    default-path: https://pcs-daejeon.herokuapp.com/
    
    default structure: {
        "data": String | Array,
        "hasError": boolean
    }

## POST

---

+ Get Post List
  ```
  Path: /posts
  Method: GET
  
  data: {
    postList: List [
        postId     : long / number
        description: string
        created    : LocalDateTime
        liked      : int
    ],
    totalPost: long,
    totalPage: long
  }

+ Write Post
    ```
    Path: /post/write
    Method: Post
    
    Body: {
        description: String(max: 50, min 5)
    }
  
    data: "success" | error...
    ```
  
+ like Post
    ```
    Path: /post/like/add/{postId}
    postId: Long | number  
  
    Method: Get

    data: "success" | error...
    ```
  
+ reject Post
  ```
  Path: /admin/post/reject/{postId}
  postId: Long | number
    
  Method: Post
  data: "success" | error...
  ```
  
+ accept Post
  ```
  Path: /admin/post/accept/{postId}
  postId: Long | number
    
  Method: Post
  data: "success" | error...
  ```

+ report Post
  ```
  Path: /post/report/{postId}
  postId: Long | number
  
  Method: Post
  data: "success" | error...
  ```
  
## ACCOUNT

  --- 
+ sign-up
  ```
  Path: /sign-up
  
  Method: Post
  Body: {
    loginId      : String
    password     : String
    name         : String
    birthDay     : String
    phoneNumber  : String
    authType     : String(DIRECT, INDIRECT)
    studentNumber: String
  }
  ```
+ login
  ```
  Path: /login
  
  Method: Post
  Body: {
    loginId : String
    password: String
  }
  data: "success" | error...
  ```

+ logout
  ```
  Path: /logout
  
  Method: Post
  data: null | error
  ```