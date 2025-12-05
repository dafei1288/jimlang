# JimLang Web App Example

Start server:

- Build: `mvn -q -DskipTests package`
- Run: `bin\\jimlang.cmd examples\\web_app\\app.jim`

Endpoints:
- `GET /` (home)
- `GET /api/ping` → text
- `POST /api/echo` (JSON body) → JSON echo
- `GET /users/:id` → path param
- `GET /static/*` → serves files from `examples/web_app/static`
- `GET /file` → single file
- `GET /download` → attachment
- `GET /login` then `GET /me` → cookie demo

Example (PowerShell):
- `curl http://localhost:8088/api/ping`
- `curl -Method POST http://localhost:8088/api/echo -ContentType 'application/json' -Body '{"foo":1}'`