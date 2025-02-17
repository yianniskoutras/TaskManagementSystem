package org.example.controller;

import org.example.model.Reminder;
import org.example.model.Task;
import org.example.utils.JSONHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages tasks, categories, priority levels, and reminders.
 * Provides functionalities for task CRUD operations, category and priority management,
 * and data persistence using JSON.
 */
public class TaskManager {
    private List<Task> tasks;
    private List<String> categories;       // Dynamic list for categories
    private List<String> priorityLevels;   // Dynamic list for priority levels
    private List<Reminder> reminders;


    /**
     * Constructs a new TaskManager instance.
     * Initializes lists, loads data from JSON, and ensures default categories and priority levels.
     */
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
     * @return A new unique reminder ID.
     */
    public int generateReminderId() {
        int maxId = tasks.stream()
                .flatMap(task -> task.getReminders() != null ? task.getReminders().stream() : Stream.empty()) // ✅ FIX: Handle null safely
                .mapToInt(Reminder::getId)
                .max()
                .orElse(0);
        return maxId + 1;
    }


    /**
     * Initializes default categories and priority levels
     * if they are empty.
     */
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

    // --------------------------------
    // TASK MANAGEMENT
    // --------------------------------

    /**
     * Adds a new task to the task list.
     * @param task The task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
        saveData(); // Save all data to the JSON file
    }

    /**
     * Updates an existing task with new details.
     * @param id The ID of the task to update.
     * @param title The new title.
     * @param description The new description.
     * @param category The new category.
     * @param priority The new priority level.
     * @param deadline The new deadline.
     * @param status The new status.
     */
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

    /**
     * Deletes a task based on its ID.
     * @param id The ID of the task to delete.
     */
    public void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
        saveData(); // Save all data to the JSON file
    }


    /**
     * Retrieves a list of all tasks.
     * @return A list of all tasks.
     */
    public List<Task> getAllTasks() {
        return tasks;
    }

    // --------------------------------
    // CATEGORY MANAGEMENT
    // --------------------------------

    /**
     * Retrieves all available categories.
     * @return A list of category names.
     */
    public List<String> getCategories() {
        if (categories.isEmpty()) {
            initializeDefaults(); // Ensure defaults if none are set
        }
        return new ArrayList<>(categories);
    }

    /**
     * Adds a new category if it doesn't already exist.
     * @param category The category name to add.
     * @return True if the category was added, false if it already exists.
     */
    public boolean addCategory(String category) {
        if (category != null && !categories.contains(category)) {
            categories.add(category);
            saveData(); // Save all data to the JSON file
            return true;
        }
        return false;
    }

    /**
     * Deletes a category and removes associated tasks.
     * @param category The category name to delete.
     */
    public void deleteCategory(String category) {
        if (!"Other".equals(category) && categories.contains(category)) {
            // Remove tasks associated with this category and collect their IDs.
            List<Integer> removedTaskIds = tasks.stream()
                    .filter(task -> task.getCategory().equals(category))
                    .map(Task::getId)
                    .toList();
            categories.remove(category);
            // Remove tasks that belong to the category.
            tasks.removeIf(task -> task.getCategory().equals(category));
            saveData();
            saveData(); // Save updated data to JSON.
        }
    }


    // --------------------------------
    // PRIORITY MANAGEMENT
    // --------------------------------

    /**
     * Retrieves all available priority levels.
     * @return A list of priority levels.
     */
    public List<String> getPriorityLevels() {
        if (priorityLevels.isEmpty()) {
            initializeDefaults(); // Ensure Default is always present
        }
        return new ArrayList<>(priorityLevels);
    }

    /**
     * Adds a new priority level.
     * @param priority The priority level to add.
     * @return True if successfully added, false if it already exists.
     */
    public boolean addPriorityLevel(String priority) {
        if (priority != null && !priorityLevels.contains(priority)) {
            priorityLevels.add(priority);
            saveData(); // Save all data to the JSON file
            return true;
        }
        return false;
    }

    /**
     * Deletes a priority level and updates tasks using it to "Default".
     * @param priority The priority level to delete.
     */
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


    /**
     * Renames an existing category in the task list.
     * If the old category does not exist or the new category already exists, the operation fails.
     *
     * @param oldCategory The current name of the category to be renamed.
     * @param newCategory The new name to be assigned to the category.
     * @return {@code true} if the category was successfully renamed, {@code false} otherwise.
     */
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

    /**
     * Renames an existing priority level in the task list.
     * If the old priority does not exist or the new priority already exists, the operation fails.
     * The default priority level cannot be renamed.
     *
     * @param oldPriority The current name of the priority level to be renamed.
     * @param newPriority The new name to be assigned to the priority level.
     * @return {@code true} if the priority was successfully renamed, {@code false} otherwise.
     */
    public boolean renamePriority(String oldPriority, String newPriority) {
        if (oldPriority == null || newPriority == null || oldPriority.equals(newPriority)) {
            return false; // No change or invalid input.
        }
        if ("Default".equalsIgnoreCase(oldPriority)) {
            return false; // "Default" cannot be renamed.
        }
        if (!priorityLevels.contains(oldPriority) || priorityLevels.contains(newPriority)) {
            return false; // Old priority not found or new priority already exists.
        }
        // Rename the priority
        priorityLevels.remove(oldPriority);
        priorityLevels.add(newPriority);
        // Update all tasks that use the old priority
        for (Task task : tasks) {
            if (task.getPriority().equalsIgnoreCase(oldPriority)) {
                task.setPriority(newPriority);
            }
        }
        saveData(); // Save changes to JSON
        return true;
    }



    // --------------------------------
    // DATA MANAGEMENT
    // --------------------------------

    /**
     * Loads task data from a JSON file.
     */
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

    /**
     * Saves task data to a JSON file.
     */
    public void saveData() {
        try {
            JSONHandler.saveData(new JSONHandler.DataWrapper(tasks, categories, priorityLevels, reminders));

        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Generates a unique ID for a new task.
     * @return A new unique task ID.
     */
    public int generateTaskId() {
        return tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
    }
}
