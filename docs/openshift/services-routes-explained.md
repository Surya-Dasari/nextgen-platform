# Services & Routes Explained

## Service
- Stable internal endpoint
- Must match container ports
- Named ports required for routes

## Route
- External exposure
- Depends on service
- Returns 503 if backend mismatch

## Enterprise Lesson
Networking contracts must remain stable.
