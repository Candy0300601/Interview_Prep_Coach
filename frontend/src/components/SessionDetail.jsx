import { useEffect, useRef, useState } from "react";
import { api } from "../api";
import QuestionCard from "./QuestionCard";

export default function SessionDetail({ sessionId, onBack }) {
  const [session, setSession] = useState(null);
  const [error, setError] = useState(null);
  const pollRef = useRef(null);

  useEffect(() => {
    let cancelled = false;

    const fetchSession = async () => {
      try {
        const data = await api.getSession(sessionId);
        if (cancelled) return;
        setSession(data);

        if (data.status === "GENERATING") {
          pollRef.current = setTimeout(fetchSession, 2500);
        }
      } catch (err) {
        if (!cancelled) setError(err.message);
      }
    };

    fetchSession();

    return () => {
      cancelled = true;
      if (pollRef.current) clearTimeout(pollRef.current);
    };
  }, [sessionId]);

  if (error) return <div className="panel"><p className="form__error">{error}</p></div>;
  if (!session) return <div className="panel"><p className="panel__subtitle">Loading…</p></div>;

  return (
    <div className="panel">
      <button className="back-link" onClick={onBack}>&larr; Back to sessions</button>
      <h2 className="panel__title">{session.jobTitle}</h2>

      {session.status === "GENERATING" && (
        <div className="generating-state">
          <SignalPulse />
          <p>Generating your questions — this usually takes 10-20 seconds.</p>
        </div>
      )}

      {session.status === "FAILED" && (
        <p className="form__error">
          Question generation failed. Make sure Ollama is running (`ollama serve`)
          and try creating a new session.
        </p>
      )}

      {session.status === "READY" && (
        <div className="question-list">
          {session.questions.map((q, i) => (
            <QuestionCard key={q.id} question={q} index={i} />
          ))}
        </div>
      )}
    </div>
  );
}

function SignalPulse() {
  return (
    <span className="signal" aria-hidden="true">
      {[0, 1, 2].map((i) => (
        <span key={i} className="signal__bar" style={{ animationDelay: `${i * 0.12}s` }} />
      ))}
    </span>
  );
}
