import { useEffect, useState } from "react";
import { api, AuthError } from "./api";
import "./App.css";

import LoginScreen from "./components/LoginScreen";
import Sidebar from "./components/Sidebar";
import NewSessionForm from "./components/NewSessionForm";
import SessionsList from "./components/SessionsList";
import SessionDetail from "./components/SessionDetail";
import ProgressReport from "./components/ProgressReport";

export default function App() {
  const [user, setUser] = useState(null);
  const [authChecked, setAuthChecked] = useState(false);
  const [view, setView] = useState("new"); // "new" | "sessions" | "session-detail" | "progress"
  const [activeSessionId, setActiveSessionId] = useState(null);

  useEffect(() => {
    api.me()
      .then(setUser)
      .catch((err) => {
        if (!(err instanceof AuthError)) console.error(err);
      })
      .finally(() => setAuthChecked(true));
  }, []);

  const handleNavigate = (target) => {
    setActiveSessionId(null);
    setView(target);
  };

  const handleSessionCreated = (sessionId) => {
    setActiveSessionId(sessionId);
    setView("session-detail");
  };

  if (!authChecked) {
    return <div className="loading-screen">Loading…</div>;
  }

  if (!user) {
    return <LoginScreen />;
  }

  return (
    <div className="app">
      <Sidebar user={user} view={view} onNavigate={handleNavigate} />
      <main className="app__content">
        {view === "new" && <NewSessionForm onCreated={handleSessionCreated} />}
        {view === "sessions" && (
          <SessionsList
            onSelect={(id) => {
              setActiveSessionId(id);
              setView("session-detail");
            }}
          />
        )}
        {view === "session-detail" && (
          <SessionDetail sessionId={activeSessionId} onBack={() => setView("sessions")} />
        )}
        {view === "progress" && <ProgressReport />}
      </main>
    </div>
  );
}
