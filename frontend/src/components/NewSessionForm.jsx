import { useState } from "react";
import { api } from "../api";

export default function NewSessionForm({ onCreated }) {
  const [jobTitle, setJobTitle] = useState("");
  const [jobDescription, setJobDescription] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!jobTitle.trim() || !jobDescription.trim()) return;

    setSubmitting(true);
    setError(null);
    try {
      const session = await api.createSession(jobTitle.trim(), jobDescription.trim());
      onCreated(session.id);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="panel">
      <h2 className="panel__title">Start a new practice session</h2>
      <p className="panel__subtitle">
        Paste in a real job description. Questions are generated in the
        background — usually ready within 10-20 seconds depending on your
        model.
      </p>

      <form onSubmit={handleSubmit} className="form">
        <label>
          Job title
          <input
            type="text"
            value={jobTitle}
            onChange={(e) => setJobTitle(e.target.value)}
            placeholder="e.g. Backend Engineer"
          />
        </label>

        <label>
          Job description
          <textarea
            rows={8}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the full job description here…"
          />
        </label>

        {error && <p className="form__error">{error}</p>}

        <button type="submit" className="button button--primary" disabled={submitting}>
          {submitting ? "Creating session…" : "Generate questions"}
        </button>
      </form>
    </div>
  );
}
