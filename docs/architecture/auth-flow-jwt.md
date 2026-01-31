# JWT Authentication Flow

## Before JWT
- Plain text "Login successful"
- No session or token
- No authorization layer

## After JWT
- authservice issues JWT on login
- Token contains:
  - subject (username)
  - issuedAt
  - expiration
- UI stores token (future use)

## Benefits
- Stateless authentication
- Scalable
- Enterprise standard
