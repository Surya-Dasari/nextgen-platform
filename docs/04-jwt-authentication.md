# JWT Authentication

## 1. Why JWT?

- Stateless authentication
- Scalable
- Secure

---

## 2. JWT Flow

1. Login request
2. Auth Service validates user
3. JWT generated
4. Token returned to UI

---

## 3. Behavior Change

Before:
- Text response

After:
- Token response

API Gateway forwards token.
