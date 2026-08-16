# Keystone Frontend

React SPA (Vite) — Login, Work Order List, Work Order Detail with lifecycle status changes,
plus quick time-log and part-usage entry on the detail screen.

## Where this goes in your repo

```
C:\projects\keystone\
├── backend\
└── frontend\   <-- put this folder here
```

## Setup

```bash
cd frontend
npm install
npm run dev
```

Opens at `http://localhost:5173`.

## Before it will work — backend CORS is required

Your backend must allow requests from `http://localhost:5173`, or every request will fail
in the browser with a CORS error (curl/Postman won't show this — it's a browser-only restriction).

Add this to `SecurityConfig.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

And add `.cors(Customizer.withDefaults())` to the `securityFilterChain` builder chain,
alongside the existing `.csrf(...)` / `.sessionManagement(...)` calls.

## What's built

| Screen | Route | Notes |
|---|---|---|
| Login | `/login` | Posts to `/api/v1/auth/login`, stores JWT in localStorage |
| Work Order List | `/work-orders` | Paginated table, role-scoped automatically by the backend |
| Work Order Detail | `/work-orders/:id` | Status history, assign technician, status transition buttons (only shows legal next-states), time log entry, part usage entry |

## Known limitations (by design, given project scope)

- **No technician picker** — assigning a technician requires pasting their UUID directly,
  since there's no `GET /api/users` endpoint yet to populate a dropdown. Get a technician's
  ID via SQL (`SELECT id FROM users WHERE role = 'TECHNICIAN'`) for now.
- **No registration screen** — accounts are created via SQL insert until the backend's
  register endpoint exists.
- **No role-specific dashboards** — every logged-in role sees the same list/detail screens;
  the backend already scopes *which* work orders each role can see, so this was intentionally
  kept to one shared view rather than building 4 separate dashboards (see project's revised
  work division doc, Section 5).
- **Client-side transition rules are a mirror, not the source of truth** — `StatusBadge.jsx`
  duplicates the backend's `ALLOWED_TRANSITIONS` map just to decide which buttons to *show*.
  The backend still independently validates and will reject anything illegal with `409`,
  even if this mirror ever drifts out of sync.

## Test login

Use whatever test users exist in your `keystone_dev` database, e.g.:
```
Email: manager@keystone.com
Password: Test1234!
```
