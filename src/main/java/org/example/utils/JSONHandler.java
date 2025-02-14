package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Task;
import org.example.model.Reminder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {
    private static final String FILE_PATH = "src/main/resources/medialab/tasks.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Wrapper class to hold tasks, categories, priorities, and reminders.
     */
    public static class DataWrapper {
        private List<Task> tasks;
        private List<String> categories;
        private List<String> priorities;
        private List<Reminder> reminders;

        // Default constructor for deserialization
        public DataWrapper() {
            tasks = new ArrayList<>();
            categories = new ArrayList<>();
            priorities = new ArrayList<>();
            reminders = new ArrayList<>();
        }

        // Constructor for initialization
        public DataWrapper(List<Task> tasks, List<String> categories, List<String> priorities, List<Reminder> reminders) {
            this.tasks = tasks;
            this.categories = categories;
            this.priorities = priorities;
            this.reminders = reminders;
        }

        // Getters and setters
        public List<Task> getTasks() {
            return tasks;
        }
        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }
        public List<String> getCategories() {
            return categories;
        }
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        public List<String> getPriorities() {
            return priorities;
        }
        public void setPriorities(List<String> priorities) {
            this.priorities = priorities;
        }
        public List<Reminder> getReminders() {
            return reminders;
        }
        public void setReminders(List<Reminder> reminders) {
            this.reminders = reminders;
        }
    }

    /**
     * Saves tasks, categories, priorities, and reminders to the JSON file.
     *
     * @param dataWrapper The wrapper containing all application data.
     * @throws IOException if the file cannot be written.
     */
    public static void saveData(DataWrapper dataWrapper) throws IOException {
        ensureFileExists();
        mapper.writeValue(new File(FILE_PATH), dataWrapper);
    }

    /**
     * Loads tasks, categories, priorities, and reminders from the JSON file.
     *
     * @return A DataWrapper containing the loaded data.
     */
    public static DataWrapper loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, DataWrapper.class);
            } catch (IOException e) {
                System.err.println("Error reading data from file: " + e.getMessage());
            }
        }
        return new DataWrapper();
    }

    /**
     * Ensures that the file path and its parent directories exist.
     *
     * @throws IOException if the directories cannot be created.
     */
    private static void ensureFileExists() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Failed to create parent directories for " + FILE_PATH);
                }
            }
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + FILE_PATH);
            }
        }
    }
}
