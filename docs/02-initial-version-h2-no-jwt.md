# Initial Version – H2 Database & No JWT

## 1. Initial Setup

The first version used:
- H2 in-memory database
- No JWT authentication
- Plain text login responses

---

## 2. H2 Characteristics

- Data stored in memory
- Lost on restart
- Suitable only for learning

---

## 3. Authentication Behavior

Login returned:
- "Login successful"
- "Invalid credentials"

No token, no session.

---

## 4. Limitations

- No persistence
- Not production-ready
- Restart loses users
