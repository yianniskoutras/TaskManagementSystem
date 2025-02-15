package org.example.controller;

import org.example.model.Reminder;
import org.example.model.Task;
import org.example.utils.JSONHandler;

import java.time.LocalDate;

public class ReminderManager {
    private final TaskManager taskManager; // Reference to task manager

    public ReminderManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public boolean addReminder(Task task, String type, LocalDate customDate) {
        if (task == null) return false;

        LocalDate reminderDate;

        // Determine reminder date based on type
        switch (type.toLowerCase()) {
            case "1 day":
                reminderDate = task.getDeadline().minusDays(1);
                break;
            case "1 week":
                reminderDate = task.getDeadline().minusWeeks(1);
                break;
            case "1 month":
                reminderDate = task.getDeadline().minusMonths(1);
                break;
            case "custom":
                reminderDate = customDate;
                break;
            default:
                return false; // Invalid type
        }

        // Ensure the reminderDate is valid
        if (reminderDate == null || reminderDate.isBefore(LocalDate.now())) {
            System.out.println("Invalid reminder date!");
            return false;
        }

        // Generate a unique reminder ID
        int newReminderId = taskManager.generateReminderId();

        // Create and add the reminder
        Reminder newReminder = new Reminder(newReminderId, task.getId(), type, reminderDate);
        task.getReminders().add(newReminder);
        taskManager.saveData(); // Save updated task list to JSON

        return true;
    }


    public boolean deleteReminder(Task task, int reminderId) {
        task.removeReminder(reminderId);
        taskManager.saveData();  // Save all tasks
        return true;
    }
}
