| Exception Class             | HTTP Status | Error Code | Meaning                                                        |
| --------------------------- | ----------- | -------------------- | -------------------------------------------------------------- |
| `InvalidArgumentException`  | `400`       | `400`                | Client sent invalid or malformed parameters.                   |
| `ValidationException`       | `422`       | `422`                | Request was well-formed but failed domain/business validation. |
| `ResourceNotFoundException` | `404`       | `404`                | Requested entity not found in the system.                      |
| `ResourceConflictException` | `409`       | `409`                | Conflict with existing resource (e.g., duplicate email).       |
| `UnauthorizedException`     | `401`       | `401`                | Authentication required or failed.                             |
| `InternalServerException`   | `500`       | `500`                | Unexpected server-side error (fallback).                       |


***

### API error responses follow this structure:

example:

```json
{
  "timestamp": "2025-08-26T15:23:01",
  "status": 400, 
  "error": "Bad Request",  
  "code": "400", 
  "message": "Validation failed",
  "path": "/api/users",
  "fieldErrors": [ 
    { "field": "email", "rejectedValue": "invalid@", "message": "must be a valid email" }
  ]
}
```

**Notes:**
- timestamp: ISO-8601 datetime when the error occurred
- status: numeric HTTP status code
- error: human-readable text (e.g., "Bad Request")
- code: application error code (currently same as status)
- message: description of the error
- path: the request path where the error occurred
- fieldErrors: (optional) field-specific validation errors, optional list

***
