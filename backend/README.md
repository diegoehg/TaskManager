# TaskManager Backend API Documentation

## POST `/api/auth/signup`
This endpoint receives a new user credentials and it creates it.

### Request Body Structure
```json
{
  "email": "string",
  "password": "string"
}
```

#### Example Request
```
POST /api/auth/signup
{
  "email": "john-doe@example.com",
  "password": "S0m3&Ex4mpl3"
}
```


### Response Structure
The response will have the following structure:
```json
{
  "status": "SUCCESS" | "FAILED",
  "message": "string",
  "data": null
}
```

#### (Success - 201 Created):
```json
{
  "status": "SUCCESS",
  "message": "User registered successfully!",
  "data": null
}
```

#### (Invalid Data - 400 Bad Request):
```json
{
  "status": "FAILED",
  "message": "Invalid password. It must meet the following criteria: At least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character.",
  "data": null
}
```


## POST /api/auth/login
This endpoint allows users to authenticate with the system by providing their credentials.

### Request Structure
```json
{
  "email": "string",
  "password": "string"
}
```

#### Example Request
```json
{
  "email": "john-doe@example.com",
  "password": "S0m3&Ex4mpl3"
}
```

### Response Structure
```json
{
    "status": "SUCCESS",
    "message": "string",
    "data": {
        "access_token":"JWT_TOKEN",
        "token_type":"Bearer",
        "expires_in":3600,
        "refresh_token":"refresh_token"
    }
}
```

#### (Success - 200 OK):
```json
{
    "status": "SUCCESS",
    "message": "Authentication successful",
    "data": {
        "access_token":"JWT_TOKEN",
        "token_type":"Bearer",
        "expires_in":3600,
        "refresh_token":"refresh_token"
    }
}
```

#### Failed Authentication (401 Unathorized):
```json
{
  "status": "FAILED",
  "message": "Authentication not authorized",
  "data": null
}
```

#### Error Response (400 Bad Request):
```json
{
  "status": "FAILED",
  "message": "string",
  "data": null
}
```


## POST /api/auth/logout
This endpoint allows users to log out of the system, invalidating their current session.

### Request Structure
The request should include a valid JWT token in the Authorization header, using the Bearer scheme. For example:
```
Authorization: Bearer your-jwt-token
```

### Response Structure
```json
{
  "status": "SUCCESS" | "FAILED",
  "message": "string",
  "data": null
}
```

#### Succesful Logout:
```json
{
  "status": "SUCCESS",
  "message": "Logout successful",
  "data": null
}
```


#### Failed Logout (401 Unauthorized):
```json
{
  "status": "FAILED",
  "message": "Invalid or missing JWT token",
  "data": null
}
```


## GET `/api/tasks`
This endpoint retrieves a list of tasks. It supports pagination and filtering by status.

### Response Structure
The endpoint returns a JSON object with the following structure:
```json
{
  "status": "SUCCESS" | "FAILED",
  "message": "string",
  "data": {
    "content": [
      {
        "id": "long",
        "description": "string",
        "dueDate": "date",
        "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
      }
    ],
    "pageable": {
      "sort": {
        "sorted": "boolean",
        "unsorted": "boolean",
        "empty": "boolean"
      },
      "offset": "int",
      "pageNumber": "int",
      "pageSize": "int",
      "paged": "boolean",
      "unpaged": "boolean"
    },
    "last": "boolean",
    "totalPages": "int",
    "totalElements": "int",
    "size": "int",
    "number": "int",
    "sort": {
      "sorted": "boolean",
      "unsorted": "boolean",
      "empty": "boolean"
    },
    "first": "boolean",
    "numberOfElements": "int",
    "empty": "boolean"
  }
}
```

### Usage
1. Get All Tasks with Pagination

   Endpoint: `/api/tasks`

   Method: GET

   Parameters:

  | Parameter | Type | Required | Default | Description                               |
  | --------- | ---- | -------- | ------- | ----------------------------------------- |
  | page      | int  | No       | 0       | The page number (zero-based) to retrieve. |
  | size      | int  | No       | 10      | The number of tasks to return per page.   |

   Example Request: `GET /api/tasks?page=1&size=20`


2. Get Tasks Filtered by Status

   Endpoint: /api/tasks

   Method: GET

   Parameters:

   | Parameter | Type   | Required | Default | Description                                                                                                                 |
   | --------- | ------ | -------- | ------- |-----------------------------------------------------------------------------------------------------------------------------|
   | status    | string | No       | null    | A comma-separated list of task statuses to filter by (e.g., PENDING,IN_PROGRESS). If not provided, all tasks are returned.  |

   Example Request: `GET /api/tasks?status=PENDING,COMPLETED`


3. Get Tasks with Pagination and Status Filtering

   Endpoint: /api/tasks

   Method: GET

   Parameters:

   | Parameter | Type   | Required | Default | Description                                                                                                                |
   | --------- | ------ | -------- | ------- |----------------------------------------------------------------------------------------------------------------------------|
   | page      | int    | No       | 0       | The page number (zero-based) to retrieve.                                                                                  |
   | size      | int    | No       | 10      | The number of tasks to return per page.                                                                                    |
   | status    | string | No       | null    | A comma-separated list of task statuses to filter by (e.g., PENDING,IN_PROGRESS). If not provided, all tasks are returned. |

   Example Request: `GET /api/tasks?page=0&size=5&status=IN_PROGRESS`

4. Get Tasks Filtered by Due Date Range

   Endpoint: /api/tasks

   Method: GET

   Parameters:

   | Parameter     | Type            | Required | Default | Description                                                  |
   |---------------|-----------------| -------- | ------- |--------------------------------------------------------------|
   | dueDateAfter  | ISO date string | No       | 0       | ISO date (YYYY-MM-DD) after which to filter tasks due date.  |
   | dueDateBefore | ISO date string | No       | 10      | ISO date (YYYY-MM-DD) before which to filter tasks due date. |

   Example Request: `GET /api/tasks?dueDateAfter=2024-06-07&dueDateBefore=2024-12-31`


5. Get Tasks Sorted by Due Date

   Endpoint: /api/tasks

   Method: GET

   Parameters:

   | Parameter | Type   | Required | Default | Description                                                                                                                            |
   | --------- | ------ | -------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------- |
   | sort      | string | No       | ASC     | The sorting direction for due date. Possible values are "ASC" (ascending) or "DESC" (descending). Defaults to "ASC" if not provided. |

   Example Request: `GET /api/tasks?sort=DESC`


## GET `/api/tasks/:id`
This endpoint retrieves a specific task

### Parameters:

| Parameter | Type   | Required | Description    |
| --------- | ------ | -------- | --------------------  |
| id        | long   | Yes      | The ID of the task to retrieve. |

### Example Request
`GET /api/tasks/1`

### Response Structure

#### (Success - 200 OK):
```json
{
  "status": "SUCCESS",
  "message": "Task retrieved successfully",
  "data": {
    "id": "long",
    "description": "string",
    "dueDate": "date",
    "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
  }
}
```

#### (Not Found - 404):
```json
{
   "status": "FAILED",
   "message": "Task not found with ID: {id}",
   "data": null
}
```

## POST `/api/tasks`
Creates a New Task with the details provided in the request body. The response will include the newly created task with 
its generated ID.

### Request Body
```json
{
  "description": "string",
  "dueDate": "date (YYYY-MM-DD)",
  "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
}
```

#### Example Request
```
POST /api/tasks
{
    "description": "Finish project report",
    "dueDate": "2024-12-25",
    "status": "PENDING"
}
```

### Response Structure

#### (Success - 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "Task created successfully",
  "data": {
    "id": "long",
    "description": "string",
    "dueDate": "date",
    "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
  }
}
```


## PUT `/api/tasks/:id`
Updates an existing Task with the details included in the request body. The response will include the recently
updated task.

### Parameters
| Parameter | Type   | Required | Description                            |
| --------- | ------ | -------- | -------------------------------------- |
| id        | long   | Yes      | The ID of the task to update.         |

### Request Body
You can include `id` field although is going to be ignored.
```json
{
   "description": "string",
   "dueDate": "date (YYYY-MM-DD)",
   "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
}
```

#### Example Request
```
PUT /api/tasks
{
   "description": "Finish project report (updated)",
   "dueDate": "2025-01-05",
   "status": "IN_PROGRESS"
}
```

### Response Structure

#### (Success - 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Task updated successfully",
  "data": {
  "id": "long",
  "description": "string",
  "dueDate": "date",
  "status": "PENDING" | "IN_PROGRESS" | "COMPLETED"
}
```

#### (Not Found - 404)
```json
{
   "status": "FAILED",
   "message": "Task not found with ID: {id}",
   "data": null
}
```

#### (Bad Request - 400)
```json
{
   "status": "FAILED",
   "message": "Malformed task request body.",
   "data": null
}
```


## DELETE `/api/tasks/:id`
Delete a Task identified by the introduced ID.

### Parameters
| Parameter | Type | Required | Description |
| --------- | ------ | -------- | ---------------------- |
| id | long | Yes | The ID of the task to delete. |

### Example Request
`DELETE /api/tasks/1`

### Response Structure

#### (Success - 200 OK)
```json
{
   "status": "SUCCESS",
   "message": "Task deleted successfully",
   "data": null
}
```

#### (Not Found - 404)
```json
{
  "status": "FAILED",
  "message": "Task not found with ID: {id}",
  "data": null
}
```


## Error Handling

### Bad Request (400)
If the request body is malformed, data cannot be parsed or it has missing required fields, the endpoint will return a
JSON response with the following structure:
```json
{
   "status": "FAILED",
   "message": "string",
   "data": null
}
```

```json
{
   "status": "FAILED",
   "message": "Malformed task request body.",
   "data": null
}
```

### Internal Server Error (500)
If an internal server error occurs, the endpoint will return a JSON response with the following structure:
```json
{
  "status": "FAILED",
  "message": "string",
  "data": null
}
```

Example:
```json
{
  "status": "FAILED",
  "message": "Error retrieving tasks",
  "data": null
}
```