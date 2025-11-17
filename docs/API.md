# API Documentation

**Last Updated**: 2025-11-17
**Base URL**: Configured in `local.properties`
**Authentication**: Bearer Token (Telegram OAuth)

---

## Overview

The Bite-Size Reader backend API provides endpoints for authentication, content submission, and summary retrieval.

---

## Authentication

### Login with Telegram

**POST** `/api/auth/telegram`

**Request**:
```json
{
  "telegram_user_id": "123456789",
  "first_name": "John",
  "last_name": "Doe",
  "username": "johndoe",
  "photo_url": "https://t.me/i/userpic/...",
  "auth_date": 1234567890,
  "hash": "abc123..."
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "telegram_user_id": "123456789",
      "username": "johndoe",
      "first_name": "John",
      "last_name": "Doe"
    },
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

**Headers**:
```
Authorization: Bearer {token}
```

---

## Summaries

### Get Summaries

**GET** `/api/summaries`

**Query Parameters**:
- `limit` (int): Number of results (default: 20)
- `offset` (int): Pagination offset (default: 0)
- `read_status` (string): Filter by read status ("read", "unread", null for all)
- `topic_tags` (string[]): Filter by topic tags

**Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "request_id": 1,
      "title": "Understanding Kotlin Multiplatform",
      "url": "https://example.com/article",
      "domain": "example.com",
      "tldr": "Quick summary of the article...",
      "summary_250": "Detailed 250-word summary...",
      "summary_1000": "Comprehensive 1000-word summary...",
      "key_ideas": ["Idea 1", "Idea 2"],
      "topic_tags": ["kotlin", "mobile"],
      "answered_questions": ["What is KMP?"],
      "seo_keywords": ["kotlin", "multiplatform"],
      "reading_time_min": 5,
      "lang": "en",
      "entities": {
        "people": ["John Doe"],
        "organizations": ["JetBrains"],
        "locations": ["Prague"]
      },
      "key_stats": [
        {
          "label": "Code sharing",
          "value": 90,
          "unit": "%",
          "source_excerpt": "..."
        }
      ],
      "readability": {
        "method": "flesch_kincaid",
        "score": 8.5,
        "level": "College"
      },
      "is_read": false,
      "is_favorite": false,
      "created_at": "2025-01-15T10:30:00Z",
      "updated_at": null
    }
  ]
}
```

### Get Summary by ID

**GET** `/api/summaries/{id}`

**Response**: Same as individual summary object above

### Submit URL

**POST** `/api/requests`

**Request**:
```json
{
  "url": "https://example.com/article",
  "priority": "normal"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "url": "https://example.com/article",
    "status": "pending",
    "created_at": "2025-01-15T10:30:00Z"
  }
}
```

**Processing Statuses**:
- `pending`: Queued for processing
- `processing`: Currently being processed
- `completed`: Summary ready
- `failed`: Processing failed

### Mark as Read

**PATCH** `/api/summaries/{id}/read`

**Request**:
```json
{
  "is_read": true
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "is_read": true
  }
}
```

---

## Search

### Search Summaries

**GET** `/api/search`

**Query Parameters**:
- `q` (string): Search query
- `limit` (int): Results limit
- `offset` (int): Pagination offset

**Response**: Array of summaries matching query

---

## Error Responses

### Standard Error Format

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid URL format",
    "field": "url"
  }
}
```

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Invalid input |
| `UNAUTHORIZED` | 401 | Missing/invalid token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `RATE_LIMIT` | 429 | Too many requests |
| `SERVER_ERROR` | 500 | Internal server error |

---

## Rate Limiting

- **Limit**: 100 requests per minute
- **Headers**:
  - `X-RateLimit-Limit`: Total requests allowed
  - `X-RateLimit-Remaining`: Requests remaining
  - `X-RateLimit-Reset`: Unix timestamp when limit resets

---

## Pagination

All list endpoints support pagination:

```
GET /api/summaries?limit=20&offset=40
```

**Response includes**:
- `limit`: Requested limit
- `offset`: Current offset
- `total`: Total count (if available)

---

## Code Examples

### Kotlin (Ktor Client)

```kotlin
// Get summaries
val summaries = client.get("/api/summaries") {
    parameter("limit", 20)
    parameter("offset", 0)
    bearerAuth(token)
}.body<SummariesResponse>()

// Submit URL
val request = client.post("/api/requests") {
    bearerAuth(token)
    contentType(ContentType.Application.Json)
    setBody(SubmitURLRequest(url = "https://example.com"))
}.body<RequestResponse>()
```

---

## Resources

- [Backend Repository](https://github.com/po4yka/bite-size-reader-server)
- [API Swagger Docs](http://localhost:8000/docs)
- [Postman Collection](link-to-collection)

---

**Maintained By**: Development Team
