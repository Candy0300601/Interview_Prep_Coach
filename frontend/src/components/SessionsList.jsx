import { useEffect, useState } from "react";
import { api } from "../api";

const STATUS_LABEL = {
  GENERATING: "Generating…",
  READY: "Ready",
  FAILED: "Failed",
};

export default function SessionsList({ onSelect }) {
  const [sessions, setSessions] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.getMySessions().then(setSessions).catch((err) => setError(err.message));
  }, []);

  if (error) return <div className="panel"><p className="form__error">{error}</p></div>;
  if (!sessions) return <div className="panel"><p className="panel__subtitle">Loading…</p></div>;

  return (
    <div className="panel">
      <h2 className="panel__title">My sessions</h2>
      {sessions.length === 0 && (
        <p className="panel__subtitle">
          No sessions yet — start one from "New session" in the sidebar.
        </p>
      )}
      <ul className="session-list">
        {sessions.map((s) => (
          <li key={s.id} onClick={() => onSelect(s.id)}>
            <div>
              <span className="session-list__title">{s.jobTitle}</span>
              <span className="session-list__date">
                {new Date(s.createdAt).toLocaleDateString()}
              </span>
            </div>
            <span className={`status-pill status-pill--${s.status.toLowerCase()}`}>
              {STATUS_LABEL[s.status]}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
