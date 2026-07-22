package com.candy.InterviewCoachAI.entity;

/**
 * Tags the AI attaches to a single answer's critique. On their own they're
 * just labels — the interesting part is aggregating them across ALL of a
 * user's answers over time (see ProgressService) to surface recurring
 * weaknesses, which is the project's signature feature.
 */
public enum WeaknessTag {
    VAGUE_ANSWER,        // lacks specificity, too general
    MISSING_EXAMPLES,    // no concrete example given
    NO_STAR_STRUCTURE,   // behavioral answer isn't structured (Situation/Task/Action/Result)
    TOO_SHORT,
    TOO_LONG,
    LACKS_METRICS,       // no quantifiable impact ("improved performance" vs "cut load time 40%")
    NEGATIVE_TONE,       // badmouths past employer/team, comes off poorly
    WEAK_TECHNICAL_DEPTH,
    NONE                 // answer had no notable weakness
}
