import { useState } from "react";
import { api } from "../api";

const CATEGORY_LABEL = {
  BEHAVIORAL: "Behavioral",
  TECHNICAL: "Technical",
  SITUATIONAL: "Situational",
  CULTURE_FIT: "Culture fit",
};

export default function QuestionCard({ question, index }) {
  const [answerText, setAnswerText] = useState("");
  const [result, setResult] = useState(question.answer || null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    if (!answerText.trim()) return;
    setSubmitting(true);
    setError(null);
    try {
      const answer = await api.submitAnswer(question.id, answerText.trim());
      setResult(answer);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="question-card">
      <div className="question-card__header">
        <span className={`category-badge category-badge--${question.category.toLowerCase()}`}>
          {CATEGORY_LABEL[question.category]}
        </span>
        <span className="question-card__index">Q{index + 1}</span>
      </div>

      <p className="question-card__text">{question.text}</p>

      {!result ? (
        <>
          <textarea
            rows={4}
            value={answerText}
            onChange={(e) => setAnswerText(e.target.value)}
            placeholder="Type your answer as you would say it out loud…"
          />
          {error && <p className="form__error">{error}</p>}
          <button
            className="button button--secondary"
            onClick={handleSubmit}
            disabled={submitting || !answerText.trim()}
          >
            {submitting ? "Getting feedback…" : "Submit answer"}
          </button>
        </>
      ) : (
        <div className="critique">
          <div className="critique__score">
            <span className="critique__score-number">{result.score}</span>
            <span className="critique__score-max">/10</span>
          </div>
          <div className="critique__body">
            <p className="critique__feedback">{result.aiFeedback}</p>
            {result.weaknessTags && result.weaknessTags.length > 0 && (
              <div className="critique__tags">
                {result.weaknessTags.map((tag) => (
                  <span key={tag} className="tag-chip">{formatTag(tag)}</span>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

function formatTag(tag) {
  return tag.replace(/_/g, " ").toLowerCase().replace(/^\w/, (c) => c.toUpperCase());
}
