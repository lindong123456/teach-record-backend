package com.teachrecord.school;

/**
 * Singapore school levels: Primary 1–6, Secondary 1–5, Junior College Year 1–2 (A-Level / IB typical path).
 * Codes match MOE common shorthand: P1…P6, S1…S5, JC1, JC2. Stored and serialized as the enum name.
 */
public enum SgGradeLevel {
    P1,
    P2,
    P3,
    P4,
    P5,
    P6,
    S1,
    S2,
    S3,
    S4,
    S5,
    JC1,
    JC2;

    public static SgGradeLevel fromCode(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String t = raw.trim().toUpperCase();
        if (t.startsWith("SEC") && t.length() >= 4) {
            t = "S" + t.substring(3);
        }
        try {
            return valueOf(t);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static SgGradeLevel fromCodeRequired(String raw) {
        SgGradeLevel g = fromCode(raw);
        if (g == null) {
            throw new IllegalArgumentException("invalid grade level: " + raw);
        }
        return g;
    }
}
