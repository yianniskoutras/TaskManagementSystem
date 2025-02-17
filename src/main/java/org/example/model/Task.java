package org.example.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Task {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate deadline;
    private String status;
    private List<Reminder> reminders;

    // No-argument constructor
    public Task() {
        this.reminders = new ArrayList<>();
    }

    public Task(int id, String title, String description, String category, String priority, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        // Automatically set status based on the deadline
        if (deadline.isBefore(LocalDate.now())) {
            this.status = "Delayed"; // Set to "Delayed" if the deadline has already passed
        } else {
            this.status = "Open"; // Default to "Open"
        }
        this.reminders = new ArrayList<>();
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Reminder> getReminders() {
        if (reminders == null) {  //  FIX: If null, initialize it to an empty list
            reminders = new ArrayList<>();
        }
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) { this.reminders = reminders; }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}
