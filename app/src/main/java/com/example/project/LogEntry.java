package com.example.project;

import java.util.Objects;

public class LogEntry {
    private String buttonName;
    private String input;
    private String formattedTimestamp;



    public LogEntry(String buttonName, String formattedTimestamp, String input) {
        this.buttonName = buttonName;
        this.input = input;
        this.formattedTimestamp = formattedTimestamp;
    }

    public LogEntry() {
    }

    @Override // לצורכי בדיקה
    public String toString() {
        return "LogEntry{" +
                "buttonName='" + buttonName + '\'' +
                ", timestamp=" + input +
                ", formattedTimestamp='" + formattedTimestamp;
    }

    @Override // בודק אם עצם שווה לעצם
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry logEntry = (LogEntry) o;
        return Objects.equals(buttonName, logEntry.buttonName) && Objects.equals(input, logEntry.input) && Objects.equals(formattedTimestamp, logEntry.formattedTimestamp);
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }
}
