# TaskManager Backend API Documentation

## `/api/tasks`
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

### Endpoints
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


### Error Handling

#### Internal Server Error (500)
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