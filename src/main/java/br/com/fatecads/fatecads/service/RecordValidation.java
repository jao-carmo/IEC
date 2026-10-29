package br.com.fatecads.fatecads.service;

/** Shared server-side rules for academic registration forms. */
final class RecordValidation {
    private RecordValidation() {}
    static void text(String value, int max, String field) {
        if (value == null || value.isBlank() || value.length() > max)
            throw new IllegalArgumentException(field + " is required and must contain at most " + max + " characters.");
    }
    static void workload(Integer hours) {
        if (hours == null || hours < 1 || hours > 50000)
            throw new IllegalArgumentException("Workload must be between 1 and 50,000 hours.");
    }
}
