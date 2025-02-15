package org.example.controller;

import org.example.model.Reminder;
import org.example.model.Task;
import org.example.utils.JSONHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TaskManager {
    private List<Task> tasks;
    private List<String> categories;       // Dynamic list for categories
    private List<String> priorityLevels;   // Dynamic list for priority levels
    private List<Reminder> reminders;


    public TaskManager() {
        tasks = new ArrayList<>();
        categories = new ArrayList<>();
        priorityLevels = new ArrayList<>();

        // Load all data (tasks, categories, priorities) from the JSON file
        loadData();

        // Initialize defaults if lists are empty
        initializeDefaults();

    }

    /**
     * Generates a unique ID for reminders.
     */
    public int generateReminderId() {
        int maxId = tasks.stream()
                .flatMap(task -> task.getReminders() != null ? task.getReminders().stream() : Stream.empty()) // ✅ FIX: Handle null safely
                .mapToInt(Reminder::getId)
                .max()
                .orElse(0);
        return maxId + 1;
    }


    private void initializeDefaults() {
        if (categories.isEmpty()) {
            categories.add("Personal");
            categories.add("Other");
        }

        if (priorityLevels.isEmpty()) {
            priorityLevels.add("Default"); // Ensure Default priority is always present
            priorityLevels.add("High");
            priorityLevels.add("Medium");
            priorityLevels.add("Low");
        }
    }

    // Task Management
    public void addTask(Task task) {
        tasks.add(task);
        saveData(); // Save all data to the JSON file
    }

    public void updateTask(int id, String title, String description, String category, String priority, LocalDate deadline, String status) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setTitle(title);
                task.setDescription(description);
                task.setCategory(category);
                task.setPriority(priority);
                task.setDeadline(deadline);
                task.setStatus(status);
                saveData(); // Save all data to the JSON file
                break;
            }
        }
    }

    public void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
        saveData(); // Save all data to the JSON file
    }

    public List<Task> searchTasks(String category, String priority) {
        return tasks.stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(category) || task.getPriority().equalsIgnoreCase(priority))
                .toList();
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    // Category Management
    public List<String> getCategories() {
        if (categories.isEmpty()) {
            initializeDefaults(); // Ensure defaults if none are set
        }
        return new ArrayList<>(categories);
    }

    public boolean addCategory(String category) {
        if (category != null && !categories.contains(category)) {
            categories.add(category);
            saveData(); // Save all data to the JSON file
            return true;
        }
        return false;
    }

    public void deleteCategory(String category) {
        if (!"Other".equals(category) && categories.contains(category)) {
            categories.remove(category);

            // Remove tasks associated with this category
            tasks.removeIf(task -> task.getCategory().equals(category));

            saveData(); // Save all data to the JSON file
        }
    }

    // Priority Management
    public List<String> getPriorityLevels() {
        if (priorityLevels.isEmpty()) {
            initializeDefaults(); // Ensure Default is always present
        }
        return new ArrayList<>(priorityLevels);
    }

    public boolean addPriorityLevel(String priority) {
        if (priority != null && !priorityLevels.contains(priority)) {
            priorityLevels.add(priority);
            saveData(); // Save all data to the JSON file
            return true;
        }
        return false;
    }

    public void deletePriorityLevel(String priority) {
        // Check if the priority is not one of the default priorities and exists in the priorityLevels list
        if (!"Default".equalsIgnoreCase(priority)) {

            // Remove the priority from the list
            priorityLevels.remove(priority);

            // Update tasks with the deleted priority to "Default" instead of deleting them
            for (Task task : tasks) {
                if (task.getPriority().equals(priority)) {
                    task.setPriority("Default");
                }
            }

            // Save all changes to the JSON file
            saveData();
        } else {
            // Optional: Log or handle the case where an invalid priority deletion is attempted
            System.out.println("Cannot delete the default or non-existent priority: " + priority);
        }
    }



    public boolean renameCategory(String oldCategory, String newCategory) {
        if (oldCategory == null || newCategory == null || oldCategory.equals(newCategory)) {
            return false; // Invalid input or no change
        }

        if (!categories.contains(oldCategory)) {
            return false; // Old category does not exist
        }

        if (categories.contains(newCategory)) {
            return false; // New category already exists
        }

        // Rename the category
        categories.remove(oldCategory);
        categories.add(newCategory);

        // Update all tasks with the old category
        for (Task task : tasks) {
            if (task.getCategory().equals(oldCategory)) {
                task.setCategory(newCategory);
            }
        }

        saveData(); // Save updated data
        return true;
    }


    // JSON Data Handling
    public void loadData() {
        try {
            JSONHandler.DataWrapper data = JSONHandler.loadData();
            this.tasks = data.getTasks();
            this.categories = data.getCategories();
            this.priorityLevels = data.getPriorities();
        } catch (Exception e) {
            this.tasks = new ArrayList<>();
            this.categories = new ArrayList<>();
            this.priorityLevels = new ArrayList<>();
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            JSONHandler.saveData(new JSONHandler.DataWrapper(tasks, categories, priorityLevels, reminders));

        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Generate unique ID for a new task
    public int generateTaskId() {
        return tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
    }
}
