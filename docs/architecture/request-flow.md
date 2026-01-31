# Request Flow

## Signup Flow
1. User submits signup form in UI
2. UI calls POST /signup (apiservice)
3. apiservice forwards to userservice
4. userservice stores user in PostgreSQL
5. Success returned to UI

## Login Flow
1. User submits login form
2. UI calls POST /login (apiservice)
3. apiservice forwards to authservice
4. authservice validates via userservice
5. authservice returns JWT
6. UI redirects to welcome.html
