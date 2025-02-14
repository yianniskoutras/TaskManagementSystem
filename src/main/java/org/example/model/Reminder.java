package org.example.model;

import java.time.LocalDate;

/**
 * Represents a reminder for a task.
 */
public class Reminder {
    private int id;              // Unique reminder ID
    private int taskId;          // Associated task's ID
    private String type;         // Reminder type (e.g., "1 day", "1 week", "1 month", "Custom")
    private LocalDate reminderDate; // The actual reminder date

    public Reminder(int id, int taskId, String type, LocalDate reminderDate) {
        this.id = id;
        this.taskId = taskId;
        this.type = type;
        this.reminderDate = reminderDate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }


    public int getTaskId() {
        return taskId;
    }

    public String getType() {
        return type;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    @Override
    public String toString() {
        return "Reminder for Task " + taskId + " on " + reminderDate + " (" + type + ")";
    }
}
