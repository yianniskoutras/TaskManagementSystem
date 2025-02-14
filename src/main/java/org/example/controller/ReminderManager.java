package org.example.controller;

import org.example.model.Reminder;
import org.example.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages reminders for tasks.
 */
public class ReminderManager {
    private List<Reminder> reminders;
    private int nextReminderId = 1; // Counter for generating unique reminder IDs

    public ReminderManager() {
        reminders = new ArrayList<>();
        // Optionally, load reminders from JSON (see JSONHandler update below)
    }

    private int generateReminderId() {
        return nextReminderId++;
    }

    /**
     * Adds a reminder for the specified task.
     * The reminder date is determined by the type:
     * - "1 day": one day before the deadline.
     * - "1 week": one week before the deadline.
     * - "1 month": one month before the deadline.
     * - "Custom": uses the provided customDate.
     *
     * Returns true if the reminder is successfully added.
     */
    public boolean addReminder(Task task, String type, LocalDate customDate) {
        // Do not allow reminders for completed tasks.
        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            return false;
        }
        LocalDate deadline = task.getDeadline();
        LocalDate reminderDate = null;
        switch (type.toLowerCase()) {
            case "1 day":
                reminderDate = deadline.minusDays(1);
                break;
            case "1 week":
                reminderDate = deadline.minusWeeks(1);
                break;
            case "1 month":
                reminderDate = deadline.minusMonths(1);
                break;
            case "custom":
                if (customDate == null) {
                    return false;
                }
                reminderDate = customDate;
                break;
            default:
                return false;
        }
        // Ensure the reminder date is before the deadline and not in the past.
        if (reminderDate.isAfter(deadline) || reminderDate.isBefore(LocalDate.now())) {
            return false;
        }
        Reminder reminder = new Reminder(generateReminderId(), task.getId(), type, reminderDate);
        reminders.add(reminder);
        return true;
    }

    /**
     * Updates an existing reminder.
     */
    public boolean updateReminder(int reminderId, String newType, LocalDate customDate, Task task) {
        for (Reminder r : reminders) {
            if (r.getId() == reminderId) {
                LocalDate deadline = task.getDeadline();
                LocalDate newDate = null;
                switch (newType.toLowerCase()) {
                    case "1 day":
                        newDate = deadline.minusDays(1);
                        break;
                    case "1 week":
                        newDate = deadline.minusWeeks(1);
                        break;
                    case "1 month":
                        newDate = deadline.minusMonths(1);
                        break;
                    case "custom":
                        if (customDate == null) {
                            return false;
                        }
                        newDate = customDate;
                        break;
                    default:
                        return false;
                }
                if (newDate.isAfter(deadline) || newDate.isBefore(LocalDate.now())) {
                    return false;
                }
                r.setType(newType);
                r.setReminderDate(newDate);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a reminder by its ID.
     */
    public boolean deleteReminder(int reminderId) {
        return reminders.removeIf(r -> r.getId() == reminderId);
    }

    /**
     * Deletes all reminders associated with a given task.
     */
    public void deleteRemindersForTask(int taskId) {
        reminders.removeIf(r -> r.getTaskId() == taskId);
    }

    /**
     * Returns a list of all active reminders.
     */
    public List<Reminder> getAllReminders() {
        return new ArrayList<>(reminders);
    }
}
