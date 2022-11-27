
# Api
    default-path: https://pcs-daejeon.herokuapp.com/
    
    default structure: {
        data    : String | Array,
        hasError: boolean
    }

## POST

---

+ Get Post List
  ```
  Path: /posts
  Method: POST
  
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
    Method: POST
    
    Body: {
        description: String(max: 50, min 5)
    }
  
    data: "success" | error...
    ```
  
+ like Post
    ```
    Path: /post/like/add/{postId}
    postId: Long | number  
  
    Method: POST

    data: "success" | error...
    ```

+ report post
  ```
  Path: /post/report/{postId}
  postId: Long | number
  
  Method: POST

  Body: {
    reason: String
  }
    
  data: "success" | error...
  ```
  
+ reject Post
  ```
  Path: /admin/post/reject/{postId}
  postId: Long | number
    
  Method: POST
  data: "success" | error...
  ```
  
+ accept Post
  ```
  Path: /admin/post/accept/{postId}
  postId: Long | number
    
  Method: POST
  data: "success" | error...
  ```

+ report Post
  ```
  Path: /post/report/{postId}
  postId: Long | number
  
  Method: POST
  data: "success" | error...
  ```
  
## ACCOUNT

  --- 
+ sign-up
  ```
  Path: /sign-up
  
  Method: POST
  Body: {
    loginId      : String
    password     : String
    name         : String
    birthDay     : String
    phoneNumber  : String
    authType     : String (DIRECT, INDIRECT)
    studentNumber: String
    referCode    : String | null (only INDIRECT user)
  }
  ```
+ login
  ```
  Path: /login
  
  Method: POST
  Body: (form type){
    loginId : String
    password: String
  }
  
  data: {
    result : String
    message: String 
    data   : Object
  }
  
  Error message
    id or password not exist: 아이디 혹은 비밀번호가 틀렸음 
    account is disabled     : 계정이 비활성화 됨
    account is pending      : 걔정이 승인 대기 상태임
  ```

+ logout
  ```
  Path: /logout
  
  Method: POST
  data: null | error
  ```

+ generate code
  ```
  Path: /code/generate
  
  Methid: Post
  
  data: code: String | error
  ```
+ get code list
  ```
  Path: /code/list
  
  Methid: Post
  
  data: codes: List<String> | error
  ```