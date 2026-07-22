const BASE_URL = "http://localhost:8080";

/**
 * Every call includes credentials so the session cookie from Google OAuth
 * login gets sent along - without this, the backend sees every request
 * as "not logged in" even right after a successful login.
 */
async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (res.status === 401) {
    throw new AuthError("Not logged in");
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || `Request failed: ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export class AuthError extends Error {}

export const api = {
  me: () => request("/api/me"),
  loginUrl: `${BASE_URL}/oauth2/authorization/google`,

  createSession: (jobTitle, jobDescription) =>
    request("/api/sessions", {
      method: "POST",
      body: JSON.stringify({ jobTitle, jobDescription }),
    }),
  getMySessions: () => request("/api/sessions/mine"),
  getSession: (id) => request(`/api/sessions/${id}`),

  submitAnswer: (questionId, answerText) =>
    request(`/api/questions/${questionId}/answers`, {
      method: "POST",
      body: JSON.stringify({ answerText }),
    }),

  getProgressReport: () => request("/api/progress/report"),
  exportProgressReport: () => request("/api/progress/report/export"),
  exportProgressReport: () => request("/api/progress/report/export"),
};
