import { useEffect, useState } from "react";
import { api } from "../api";

export default function ProgressReport() {
  const [report, setReport] = useState(null);
  const [error, setError] = useState(null);
  const [exporting, setExporting] = useState(false);
  const [exportError, setExportError] = useState(null);

  useEffect(() => {
    api.getProgressReport().then(setReport).catch((err) => setError(err.message));
  }, []);

  const handleExport = async () => {
    setExporting(true);
    setExportError(null);
    try {
      const result = await api.exportProgressReport();
      window.open(result.downloadUrl, "_blank");
    } catch (err) {
      setExportError(err.message);
    } finally {
      setExporting(false);
    }
  };

  if (error) return <div className="panel"><p className="form__error">{error}</p></div>;
  if (!report) return <div className="panel"><p className="panel__subtitle">Loading…</p></div>;

  const maxCount = Math.max(1, ...Object.values(report.weaknessTagCounts));

  return (
    <div className="panel">
      <div className="panel__header-row">
        <div>
          <h2 className="panel__title">Your progress report</h2>
          <p className="panel__subtitle">
            Aggregated across every answer you've ever given, in every session.
          </p>
        </div>
        <button className="button button--secondary" onClick={handleExport} disabled={exporting}>
          {exporting ? "Preparing PDF…" : "Export as PDF"}
        </button>
      </div>
      {exportError && <p className="form__error">{exportError}</p>}

      <div className="stat-row">
        <div className="stat">
          <span className="stat__number">{report.totalAnswersAnalyzed}</span>
          <span className="stat__label">Answers analyzed</span>
        </div>
        <div className="stat">
          <span className="stat__number">{report.averageScore || "–"}</span>
          <span className="stat__label">Average score</span>
        </div>
      </div>

      <div className="insight-card">
        <span className="insight-card__label">Coach's note</span>
        <p>{report.narrativeInsight}</p>
      </div>

      {Object.keys(report.weaknessTagCounts).length > 0 && (
        <>
          <h3 className="panel__section-title">Weakness frequency</h3>
          <div className="bar-chart">
            {Object.entries(report.weaknessTagCounts)
              .sort(([, a], [, b]) => b - a)
              .map(([tag, count]) => (
                <div key={tag} className="bar-row">
                  <span className="bar-row__label">{formatTag(tag)}</span>
                  <div className="bar-row__track">
                    <div
                      className="bar-row__fill"
                      style={{ width: `${(count / maxCount) * 100}%` }}
                    />
                  </div>
                  <span className="bar-row__count">{count}</span>
                </div>
              ))}
          </div>
        </>
      )}
    </div>
  );
}

function formatTag(tag) {
  return tag.replace(/_/g, " ").toLowerCase().replace(/^\w/, (c) => c.toUpperCase());
}
