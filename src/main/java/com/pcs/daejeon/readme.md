
# Api
    default-path: 
    
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
  
  data: List [
      postId     : long / number
      description: string
      created    : LocalDateTime
      liked      : int
  ]

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
  Path: /post/reject/{postId}
  postId: Long | number
    
  Method: Post
  data: "success" | error...
  ```
  
+ accept Post
  ```
  Path: /post/accept/{postId}
  postId: Long | number
    
  Method: Post
  data: "success" | error...
  ```

