# Interview Coach — Frontend

React (Vite) frontend for the Interview Prep Coach backend.

## Run it

```bash
npm install
npm run dev
```

Opens at `http://localhost:5173`. The backend must be running at
`http://localhost:8080` (see the root `README.md`).

## How login works here (important — read this before debugging auth issues)

This app uses **session-cookie-based OAuth2 login**, not a token you copy
around. That means:

1. Clicking "Sign in with Google" does a full-page redirect to
   `http://localhost:8080/oauth2/authorization/google` — NOT a fetch call.
   OAuth2 login has to happen as a real browser navigation.
2. After Google login succeeds, the backend redirects the browser back to
   `http://localhost:5173` (configured in `SecurityConfig.java` via
   `defaultSuccessUrl`). At this point you're logged in — the backend set
   a session cookie in your browser for `localhost:8080`.
3. Every API call after that (`src/api.js`) uses `credentials: "include"`
   so that cookie gets sent along with requests to the backend, even
   though the frontend and backend are different origins/ports.
4. The backend's `SecurityConfig` has CORS configured specifically to
   allow `http://localhost:5173` with credentials — if you change the
   frontend's port, update `FRONTEND_URL` in `SecurityConfig.java` too.

If you ever see 401s right after logging in, it's almost always one of:
missing `credentials: "include"`, a mismatched CORS origin, or the browser
blocking third-party cookies (shouldn't be an issue here since both are
`localhost`, just different ports).

## Structure
- `src/api.js` — all backend calls in one place
- `src/App.jsx` — top-level view routing + auth check
- `src/components/` — one component per screen/piece of UI
- `src/App.css` — all styling (no CSS framework)
