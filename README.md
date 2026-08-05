# FreeSlot — Full Setup Guide (Beginner Friendly)

This is now a **single Spring Boot project** — backend and frontend live together,
and Spring Boot serves both. No separate frontend server needed anymore.

---

## 1. What you need to install

1. **Java 17 (JDK)** — https://adoptium.net — check with `java -version`
2. **MySQL Community Server** (+ MySQL Workbench) — https://dev.mysql.com/downloads/mysql
3. **VS Code** — https://code.visualstudio.com
4. **VS Code Extensions**: "Extension Pack for Java", "Spring Boot Extension Pack"
5. **Postman** — https://www.postman.com/downloads
6. **Git** — https://git-scm.com

You do **not** need to install Maven separately — this project includes a Maven
Wrapper (`mvnw` / `mvnw.cmd`), which downloads the correct Maven version for you
automatically the first time you run it (as long as you have internet access then).

---

## 2. Project structure

```
FreeSlot/
├── src/main/java/com/freeslot/backend/
│   ├── controller/      -> REST endpoints (signup, login, routine, friends, free-slot)
│   ├── dto/              -> request/response shapes for the API
│   ├── entity/           -> @Entity classes = database tables (User, Routine, Friend)
│   ├── repository/       -> Spring Data JPA interfaces (talk to MySQL)
│   ├── service/          -> business logic, incl. the free-slot algorithm
│   ├── security/         -> JWT + Spring Security (JwtUtil, JwtAuthFilter,
│   │                        CustomUserDetailsService, SecurityConfig)
│   ├── exception/        -> centralized error handling (kept separate from
│   │                        security/ so error-response logic doesn't get lost
│   │                        inside the auth code)
│   └── FreeSlotApplication.java   -> entry point (has `main`)
│
├── src/main/resources/
│   ├── static/            -> FRONTEND lives here now (HTML/CSS/JS)
│   │   ├── index.html, signup.html, dashboard.html, routine.html,
│   │   │   friends.html, freeslot.html
│   │   ├── css/style.css
│   │   ├── js/ (api.js, auth.js, dashboard.js, routine.js, friends.js, freeslot.js)
│   │   └── images/         -> (empty for now, put any icons/images here)
│   ├── templates/          -> only needed if you later add Thymeleaf; unused for now
│   └── application.properties
│
├── database/schema.sql    -> run once in MySQL Workbench to create tables
├── pom.xml
├── mvnw / mvnw.cmd        -> Maven wrapper scripts
└── .mvn/wrapper/          -> wrapper config (auto-downloads Maven on first run)
```

**Why the frontend moved into `static/`:** Spring Boot automatically serves any file
placed in `src/main/resources/static/` at the root URL. So once the backend is
running, your whole app — pages and API — lives at `http://localhost:8080`. No CORS
headaches, no separate Live Server, no second terminal window.

**Note on `exception/`:** your requested structure didn't list an `exception`
package, but I kept it as its own folder rather than merging it into `security/`
— error handling (validation errors, "not found", "bad request") isn't really a
security concern, and keeping it separate means you can find `GlobalExceptionHandler`
without having to hunt through JWT code. Let me know if you'd rather fold it
somewhere else.

---

## 3. Set up the database

1. Open MySQL Workbench, connect with your root password.
2. Run `database/schema.sql` — creates `freeslot_db` and the three tables.

---

## 4. Configure the backend

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=yourpassword
```

---

## 5. Run everything (one command, one app)

Open the `FreeSlot` folder in VS Code (this is the project root now — open it directly).

**Option A — from the terminal, using the included wrapper:**
```bash
./mvnw spring-boot:run        # Mac/Linux
mvnw.cmd spring-boot:run      # Windows
```

**Option B — from VS Code:**
Open `FreeSlotApplication.java` and click the "Run" link above `public static void main`.

Either way, once you see `Started FreeSlotApplication in X seconds`, open your browser to:

```
http://localhost:8080/index.html
```

That's your whole app — login, signup, dashboard, everything — served by the same
Spring Boot process that runs your API.

---

## 6. Test the API with Postman

Same as before — see the endpoint list below. Point Postman at `http://localhost:8080`.

1. `POST /signup` → `{ "studentId": "...", "name": "...", "email": "...", "password": "...", "semester": "..." }` → returns a token
2. `POST /login` → `{ "email": "...", "password": "..." }`
3. For everything else, set Postman's Authorization tab to Bearer Token with the token above.
4. `GET /user`, `PUT /user`
5. `POST /routine`, `GET /routine`, `PUT /routine/{id}`, `DELETE /routine/{id}`
6. `POST /friends/add` with `{ "friendStudentId": "..." }`, `GET /friends`, `DELETE /friends/remove/{friendId}`
7. `GET /free-slot?friendIds=2,3`

---

## 7. How the Free Slot algorithm works

Each user's week is Saturday–Thursday × periods 1–8. For the chosen group (you +
selected friends), the backend builds a busy/free grid per person from their
`routine` rows, then walks every day/period slot and keeps the ones where
*everyone* in the group is free. See `service/FreeSlotService.java`.

---

## 8. Git & GitHub workflow

```bash
git clone <your-repo-url>
cd FreeSlot
git checkout -b develop
git push -u origin develop

git checkout develop
git pull
git checkout -b feature/routine-crud
# ...changes...
git add .
git commit -m "Add routine CRUD endpoints"
git push -u origin feature/routine-crud
# Open PR: feature/routine-crud -> develop
```

Add a `.gitignore` excluding `target/` and `.mvn/wrapper/maven-wrapper.jar`
(the jar re-downloads automatically, no need to commit it).

---

## 9. Common beginner problems

| Problem | Fix |
|---|---|
| `./mvnw: Permission denied` | Run `chmod +x mvnw` once (Mac/Linux) |
| Backend won't start: "Communications link failure" | MySQL isn't running, or wrong credentials in `application.properties` |
| Blank page at `localhost:8080/index.html` | Backend didn't fully start — check the terminal for errors |
| 401/403 after login | Token expired (24h) or missing — check browser dev tools → Application → Local Storage for `freeslot_token` |
| Free Slot page shows nothing | Both you and the selected friend(s) need routine entries added first |

You're all set — one project, one command, one URL.
