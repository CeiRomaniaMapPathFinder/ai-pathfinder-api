# ai-pathfinder-api

Spring Boot API for the Romania map pathfinder. Frontend: [`ai-pathfinder-web`](https://github.com/CeiRomaniaMapPathFinder/ai-pathfinder-web).

## Setup

```bash
./mvnw spring-boot:run
```

http://localhost:8080

## Commands

| Command | Does |
| --- | --- |
| `./mvnw spring-boot:run` | Run locally, port 8080 |
| `./mvnw -B verify` | Build and test |
| `./mvnw -B package -DskipTests` | Build the jar only |

CI runs `./mvnw -B verify` and a Docker build.

## Environment

| Variable | Set where | Default |
| --- | --- | --- |
| `APP_CORS_ALLOWED_ORIGINS` | Dokploy, comma separated | `http://localhost:3000` |
| `APP_COMMIT` | Docker build arg `GIT_SHA` | `unknown` |

Local dev needs no config — the CORS default already allows the frontend on port 3000.

## Docker

```bash
docker build --build-arg GIT_SHA=$(git rev-parse HEAD) -t pathfinder-api:local .
docker run --rm -p 8080:8080 pathfinder-api:local
```

## Deploy

Push to `main` → image to GHCR → Dokploy redeploy → waits for `/api/health` to report the new commit. A failed deploy rolls back automatically.

`/api/health` returns `{"status":"UP","commit":"<sha>"}`. Don't rename `commit` — `deploy.yaml` parses it.
