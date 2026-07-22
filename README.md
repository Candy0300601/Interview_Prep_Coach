# AI Interview Prep Coach

Paste a job description, get AI-generated interview questions tailored to
it, answer them, get instant AI feedback with a score — and over time,
get a coaching report that spots your *recurring* weaknesses across every
session you've ever done.


## Tech stack
- Spring Boot 3.3, Java 17
- Spring Data JPA + PostgreSQL
- Spring Security + OAuth2 (Google login)
- Spring Kafka (async question generation)
- Spring AI (Ollama — runs against a local model, no API key/costs)
- Docker Compose (Postgres + Kafka + Zookeeper + the app itself)
- React frontend (Vite)

## The unique feature: cross-session progress tracking
Every individual answer gets scored and tagged with specific weaknesses
(vague, no example, missing metrics, etc.) by the AI. That alone isn't
new — most "AI feedback" tools stop there.

What's different here: `GET /api/progress/report` looks across **every
answer you've ever given, in every session**, and asks the AI to find the
pattern — not "here's today's feedback" but "here's what you keep doing
wrong, and where it shows up." That's the difference between a Q&A toy
and something that behaves like a real coach who's worked with you for a
while.

---

## How it all fits together

```
POST /api/sessions (job title + description)
        │
        ▼
  saved as GENERATING ──► Kafka topic "session-created"
        │                          │
        │                          ▼
        │                 QuestionGenerationConsumer
        │                 (calls AI, generates 6 questions)
        │                          │
        ▼                          ▼
  frontend polls          questions saved, session
  GET /api/sessions/{id}  flips to READY
        │
        ▼
  user answers a question
        │
        ▼
  POST /api/questions/{id}/answers
        │
        ▼
  AI critiques synchronously (score + feedback + weakness tags)
  returned immediately - no Kafka here, since the whole point
  is instant feedback


  ... after answering across many sessions over time ...


  GET /api/progress/report
        │
        ▼
  pulls every past answer for the user, counts weakness tags,
  and asks the AI to write a narrative about the recurring pattern
```

**Why question generation is async but answer critique is not:**
Generating questions from a job description is a "fire and forget" step —
the user doesn't need to stare at a spinner for it, so it happens in the
background via Kafka. Critiquing an answer is the opposite: the user just
submitted an answer and is actively waiting to see how they did, so it's
a normal synchronous request/response call.

---

## Project structure
```
src/main/java/com/interviewcoach/
├── config/         SecurityConfig, KafkaConfig
├── controller/      SessionController, AnswerController, ProgressController
├── dto/             Request/response objects, Kafka event
├── entity/          AppUser, InterviewSession, Question, Answer, enums
├── repository/      Spring Data JPA repositories
├── security/        CustomOAuth2UserService, CurrentUser helper
├── service/         SessionService, AnswerService, ProgressService,
│                    AiInterviewService, QuestionGenerationConsumer
└── exception/       GlobalExceptionHandler + custom exceptions
```

---



## API reference

All `/api/**` endpoints require login. Log in first by visiting
`http://localhost:8080/oauth2/authorization/google` in a browser (this is
a session-cookie-based login, so use a browser or a client that keeps
cookies — plain `curl` won't carry the session unless you pass the cookie
jar explicitly).

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/sessions` | Submit a job title + description, starts async question generation |
| GET | `/api/sessions/mine` | List all your past sessions |
| GET | `/api/sessions/{id}` | Get one session with its questions (and answers, if given) |
| POST | `/api/questions/{id}/answers` | Submit an answer, get instant AI score + feedback |
| GET | `/api/progress/report` | **The unique feature** — cross-session weakness pattern report |

### Example: create a session
```json
POST /api/sessions
{
  "jobTitle": "Backend Engineer",
  "jobDescription": "We're looking for a backend engineer experienced with Java, Spring Boot, and distributed systems..."
}
```
Response (immediately, status will be GENERATING):
```json
{
  "id": 1,
  "jobTitle": "Backend Engineer",
  "status": "GENERATING",
  "questions": []
}
```
Poll `GET /api/sessions/1` a few seconds later — status becomes `READY`
and `questions` is populated with 6 questions.

### Example: submit an answer
```json
POST /api/questions/4/answers
{
  "answerText": "I once had a disagreement with a teammate about architecture, so we talked it out and picked the better option."
}
```
Response:
```json
{
  "id": 10,
  "answerText": "...",
  "score": 5,
  "aiFeedback": "This is a good start but lacks specifics - what was the actual disagreement about, and what was the measurable outcome?",
  "weaknessTags": ["VAGUE_ANSWER", "LACKS_METRICS"]
}
```

### Example: progress report
```json
GET /api/progress/report
```
```json
{
  "totalAnswersAnalyzed": 14,
  "averageScore": 6.2,
  "weaknessTagCounts": { "VAGUE_ANSWER": 6, "LACKS_METRICS": 5, "TOO_SHORT": 2 },
  "topWeaknesses": ["VAGUE_ANSWER", "LACKS_METRICS", "TOO_SHORT"],
  "narrativeInsight": "You consistently give vague answers to behavioral questions, especially around conflict and teamwork - you tend to describe what happened but skip the concrete outcome or measurable impact. Adding a specific number or result would make these answers noticeably stronger."
}
```

