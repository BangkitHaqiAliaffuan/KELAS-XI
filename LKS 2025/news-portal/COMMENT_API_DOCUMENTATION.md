# News Portal API - Comment System Documentation

## Overview
Complete CRUD operations for Comments with ownership-based authorization and relationship management.

## Comment System Features
- ✅ Create comments on posts (authenticated users only)
- ✅ Read comments with user information
- ✅ Update own comments only
- ✅ Delete own comments only
- ✅ Display comments in post details
- ✅ Show comment totals in post details

---

## Comment Endpoints

### 1. Get All Comments
**GET** `/api/comments`

**Headers:**
```
Accept: application/json
Content-Type: application/json
```

**Response:**
```json
{
    "data": [
        {
            "id": 1,
            "comment_content": "Great article!",
            "created_at": "2024-01-15 10:30:00",
            "user": {
                "id": 1,
                "name": "John Doe",
                "email": "john@example.com"
            },
            "post": {
                "id": 1,
                "title": "Laravel 11 Features"
            }
        }
    ]
}
```

---

### 2. Get Single Comment
**GET** `/api/comments/{id}`

**Headers:**
```
Accept: application/json
Content-Type: application/json
```

**Response:**
```json
{
    "data": {
        "id": 1,
        "comment_content": "Great article!",
        "created_at": "2024-01-15 10:30:00",
        "user": {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com"
        },
        "post": {
            "id": 1,
            "title": "Laravel 11 Features"
        }
    }
}
```

---

### 3. Create Comment (Protected)
**POST** `/api/comments`

**Headers:**
```
Accept: application/json
Content-Type: application/json
Authorization: Bearer {your_token}
```

**Request Body:**
```json
{
    "post_id": 1,
    "comment_content": "This is a very informative article. Thank you for sharing!"
}
```

**Success Response (201):**
```json
{
    "data": {
        "id": 2,
        "comment_content": "This is a very informative article. Thank you for sharing!",
        "created_at": "2024-01-15 14:20:00",
        "user": {
            "id": 2,
            "name": "Jane Smith",
            "email": "jane@example.com"
        },
        "post": {
            "id": 1,
            "title": "Laravel 11 Features"
        }
    }
}
```

**Validation Errors (422):**
```json
{
    "message": "The given data was invalid.",
    "errors": {
        "post_id": ["The post id field is required."],
        "comment_content": ["The comment content field is required."]
    }
}
```

---

### 4. Update Comment (Protected - Own Comments Only)
**PUT** `/api/comments/{id}`

**Headers:**
```
Accept: application/json
Content-Type: application/json
Authorization: Bearer {your_token}
```

**Request Body:**
```json
{
    "comment_content": "Updated comment content here."
}
```

**Success Response (200):**
```json
{
    "data": {
        "id": 1,
        "comment_content": "Updated comment content here.",
        "created_at": "2024-01-15 10:30:00",
        "user": {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com"
        },
        "post": {
            "id": 1,
            "title": "Laravel 11 Features"
        }
    }
}
```

**Unauthorized (403):**
```json
{
    "message": "You are not authorized to update this comment."
}
```

---

### 5. Delete Comment (Protected - Own Comments Only)
**DELETE** `/api/comments/{id}`

**Headers:**
```
Accept: application/json
Content-Type: application/json
Authorization: Bearer {your_token}
```

**Success Response (200):**
```json
{
    "message": "Comment deleted successfully"
}
```

**Unauthorized (403):**
```json
{
    "message": "You are not authorized to delete this comment."
}
```

---

## Post Details with Comments

### Get Post with Comments
**GET** `/api/posts/{id}`

**Headers:**
```
Accept: application/json
Content-Type: application/json
```

**Response:**
```json
{
    "data": {
        "id": 1,
        "title": "Laravel 11 Features",
        "news_content": "Laravel 11 brings many exciting features...",
        "created_at": "2024-01-15 09:00:00",
        "writer": {
            "id": 1,
            "name": "Admin User",
            "email": "admin@example.com"
        },
        "comments": [
            {
                "id": 1,
                "comment_content": "Great article!",
                "created_at": "2024-01-15 10:30:00",
                "user": {
                    "id": 2,
                    "name": "John Doe",
                    "email": "john@example.com"
                }
            },
            {
                "id": 2,
                "comment_content": "Very helpful, thanks!",
                "created_at": "2024-01-15 11:15:00",
                "user": {
                    "id": 3,
                    "name": "Jane Smith",
                    "email": "jane@example.com"
                }
            }
        ],
        "comment_total": 2
    }
}
```

---

## Testing Steps

### 1. Setup Authentication
1. Register a user: `POST /api/register`
2. Login to get token: `POST /api/login`
3. Use token in Authorization header for protected endpoints

### 2. Create Test Data
1. Create a post: `POST /api/posts` (requires authentication)
2. Create comments on the post: `POST /api/comments`

### 3. Test Comment Operations
1. **Create Comment**: POST to `/api/comments` with post_id and comment_content
2. **Read Comments**: GET `/api/comments` and `/api/comments/{id}`
3. **Update Own Comment**: PUT `/api/comments/{id}` (only works for comment owner)
4. **Delete Own Comment**: DELETE `/api/comments/{id}` (only works for comment owner)
5. **View Post with Comments**: GET `/api/posts/{id}` to see comments included

### 4. Test Authorization
1. Try to update/delete another user's comment (should fail with 403)
2. Try to create comment without authentication (should fail with 401)

---

## Database Relationships

### Comment Model Relationships:
- **belongsTo User**: Each comment belongs to one user (commenter)
- **belongsTo Post**: Each comment belongs to one post

### User Model Relationships:
- **hasMany Comments**: User can have many comments

### Post Model Relationships:
- **hasMany Comments**: Post can have many comments
- **belongsTo User (as writer)**: Post belongs to one user (author)

---

## Authorization Rules

### Comment Authorization:
- **Create**: Must be authenticated user
- **Read**: Public access (no authentication required)
- **Update**: Only comment owner can update
- **Delete**: Only comment owner can delete

### Middleware Applied:
- `PemilikKomentar`: Validates comment ownership for Update/Delete operations
- `auth:sanctum`: Validates authentication token for protected routes

---

## Error Handling

### Common HTTP Status Codes:
- **200**: Success (GET, PUT, DELETE)
- **201**: Created (POST)
- **401**: Unauthenticated (missing/invalid token)
- **403**: Unauthorized (not comment owner)
- **404**: Not Found (comment/post doesn't exist)
- **422**: Validation Error (invalid input data)

### Error Response Format:
```json
{
    "message": "Error description",
    "errors": {
        "field_name": ["Specific validation error"]
    }
}
```
