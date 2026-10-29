package br.com.fatecads.fatecads.entity;

public enum AcademicStatus {
    ENROLLED("In progress"), COMPLETED("Completed");
    private final String label;
    AcademicStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
