export default function Sidebar({ user, view, onNavigate }) {
  return (
    <aside className="sidebar">
      <div className="sidebar__brand">
        <span className="sidebar__mark">IC</span>
        <span className="sidebar__title">Interview Coach</span>
      </div>

      <nav className="sidebar__nav">
        <button
          className={view === "new" ? "active" : ""}
          onClick={() => onNavigate("new")}
        >
          + New session
        </button>
        <button
          className={view === "sessions" ? "active" : ""}
          onClick={() => onNavigate("sessions")}
        >
          My sessions
        </button>
        <button
          className={view === "progress" ? "active" : ""}
          onClick={() => onNavigate("progress")}
        >
          Progress report
        </button>
      </nav>

      <div className="sidebar__user">
        <div className="sidebar__avatar">{(user.name || user.email || "?")[0].toUpperCase()}</div>
        <div className="sidebar__user-info">
          <span className="sidebar__user-name">{user.name || "You"}</span>
          <span className="sidebar__user-email">{user.email}</span>
        </div>
      </div>
    </aside>
  );
}
