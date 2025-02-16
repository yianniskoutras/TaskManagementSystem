package org.example.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.controller.TaskManager;
import org.example.model.Reminder;
import org.example.model.Task;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final TaskManager taskManager = new TaskManager();


    @FXML
    private ListView<String> categoryListView; // For displaying categories

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label delayedTasksLabel;
    @FXML
    private Label upcomingTasksLabel;

    @FXML
    private ListView<Task> taskListView; // For displaying tasks


    @FXML
    private ListView<String> priorityListView;


    @FXML
    private ListView<Reminder> reminderListView;

    @FXML
    private ComboBox<String> categoryFilterComboBox;

    @FXML
    private Button searchButton;


    /**
     * Initialize the UI and load data.
     */
    public void initialize() {
        // Load all data from JSON
        taskManager.loadData();

        checkAndMarkOverdueTasks();

        // Populate UI components
        categoryListView.getItems().setAll(taskManager.getCategories());

        populatePriorities();
        styleScrollBar(taskListView);
        styleScrollBar(categoryListView);
        styleScrollBar(priorityListView);

        customizeCategoryListView();

        taskListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Main HBox to hold the task details and the status icon
                    HBox mainBox = new HBox(); // Default spacing
                    mainBox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
                    mainBox.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #cce7ff;");

                    // VBox for task details
                    VBox taskBox = new VBox(5); // 5px spacing between elements

                    // Title
                    HBox titleBox = new HBox(5); // 5px spacing
                    Label titleLabel = new Label("Title: ");
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label titleValue = new Label(task.getTitle());
                    titleValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;"); // Light blue
                    titleBox.getChildren().addAll(titleLabel, titleValue);

                    // Description
                    HBox descriptionBox = new HBox(5);
                    Label descriptionLabel = new Label("Description: ");
                    descriptionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label descriptionValue = new Label(task.getDescription());
                    descriptionValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;"); // Light blue
                    descriptionBox.getChildren().addAll(descriptionLabel, descriptionValue);

                    // Category
                    HBox categoryBox = new HBox(5);
                    Label categoryLabel = new Label("Category: ");
                    categoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label categoryValue = new Label(task.getCategory());
                    categoryValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;"); // Light blue
                    categoryBox.getChildren().addAll(categoryLabel, categoryValue);

                    // Priority
                    HBox priorityBox = new HBox(5);
                    Label priorityLabel = new Label("Priority: ");
                    priorityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label priorityValue = new Label(task.getPriority());
                    priorityValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;"); // Light blue
                    priorityBox.getChildren().addAll(priorityLabel, priorityValue);

                    // Status
                    HBox statusBox = new HBox(5);
                    Label statusStaticLabel = new Label("Status: ");
                    statusStaticLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label statusValueLabel = new Label(task.getStatus());
                    if ("Completed".equalsIgnoreCase(task.getStatus())) {
                        statusValueLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;"); // Green for "Completed"
                    } else if ("Delayed".equalsIgnoreCase(task.getStatus())) {
                        statusValueLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;"); // Red for "Delayed"
                    } else if ("Postponed".equalsIgnoreCase(task.getStatus())) {
                        statusValueLabel.setStyle("-fx-text-fill: #ff9100; -fx-font-size: 14px;"); // Orange for "Postponed"
                    }
                        else{
                            statusValueLabel.setStyle("-fx-text-fill: #6D7ED5FF; -fx-font-size: 14px;"); // Default light blue
                        }

                    statusBox.getChildren().addAll(statusStaticLabel, statusValueLabel);

                    // Deadline
                    HBox deadlineBox = new HBox(5);
                    Label deadlineLabel = new Label("Deadline: ");
                    deadlineLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;"); // Bold blue
                    Label deadlineValue = new Label(task.getDeadline().toString());
                    deadlineValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;"); // Light blue
                    deadlineBox.getChildren().addAll(deadlineLabel, deadlineValue);

                    // Add all details to the VBox
                    taskBox.getChildren().addAll(titleBox, descriptionBox, categoryBox, priorityBox, statusBox, deadlineBox);

                    // Spacer for controlled spacing
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    spacer.setMaxWidth(240); // Adjust this value to control left movement of icons

                    // Label for status icon
                    Label statusIcon = new Label();
                    statusIcon.setStyle("-fx-font-size: 30px; -fx-padding: 0;"); // Larger font size
                    switch (task.getStatus().toLowerCase()) {
                        case "completed":
                            statusIcon.setText("✅");
                            statusIcon.setStyle("-fx-text-fill: green; -fx-font-size: 24px;"); // Green icon
                            break;
                        case "in progress":
                            statusIcon.setText("⏳");
                            statusIcon.setStyle("-fx-text-fill: #6D7ED5FF; -fx-font-size: 38px;"); // Blue icon
                            break;
                        case "delayed":
                            statusIcon.setText("❗");
                            statusIcon.setStyle("-fx-text-fill: red; -fx-font-size: 28px;"); // Red icon
                            break;
                        case "postponed":
                            statusIcon.setText("⏱");
                            statusIcon.setStyle("-fx-text-fill: #ff9100; -fx-font-size: 28px;"); // Red icon
                            break;
                        default:
                            statusIcon.setText("ℹ️");
                            statusIcon.setStyle("-fx-text-fill: gray; -fx-font-size: 36px;"); // Gray icon
                            break;
                    }

                    // Add the task box, spacer, and icon to the main HBox
                    mainBox.getChildren().addAll(taskBox, spacer, statusIcon);

                    // Set the HBox as the graphic for this cell
                    setGraphic(mainBox);
                }
            }
        });






        // Populate the ListView with tasks
        taskListView.getItems().setAll(taskManager.getAllTasks());


        // Populate filter ComboBox: add "All" plus all categories
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("All");
        filterOptions.addAll(taskManager.getCategories());
        categoryFilterComboBox.getItems().setAll(filterOptions);
        categoryFilterComboBox.setValue("All");

// Add listener to filter tasks based on selected category
        categoryFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == null || newVal.equalsIgnoreCase("All")) {
                taskListView.getItems().setAll(taskManager.getAllTasks());
            } else {
                taskListView.getItems().setAll(taskManager.getAllTasks().stream()
                        .filter(task -> task.getCategory().equalsIgnoreCase(newVal))
                        .toList());
            }
        });



        // Enhanced reminder cell factory
        reminderListView.setCellFactory(listView -> new ListCell<Reminder>() {
            @Override
            protected void updateItem(Reminder reminder, boolean empty) {
                super.updateItem(reminder, empty);
                if (empty || reminder == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Container with a gradient background and rounded borders
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(10));
                    container.setStyle("-fx-background-color: linear-gradient(to right, #f0f8ff, #e6f7ff); " +
                            "-fx-border-color: #003d99; -fx-border-radius: 5; -fx-background-radius: 5;");

                    VBox details = new VBox(5);
                    Task associatedTask = taskManager.getAllTasks().stream()
                            .filter(task -> task.getId() == reminder.getTaskId())
                            .findFirst().orElse(null);
                    String taskTitle = (associatedTask != null) ? associatedTask.getTitle() : "Unknown";
                    // Task ID row
                    HBox taskRow = new HBox(5);
                    Label taskLabel = new Label("Task: ");
                    taskLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;");
                    Label taskTitleValue = new Label(taskTitle);
                    taskTitleValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;");
                    taskRow.getChildren().addAll(taskLabel, taskTitleValue);

                    // Type row
                    HBox typeRow = new HBox(5);
                    Label typeLabel = new Label("Type: ");
                    typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;");
                    Label typeValue = new Label(reminder.getType());
                    typeValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;");
                    typeRow.getChildren().addAll(typeLabel, typeValue);

                    // Date row
                    HBox dateRow = new HBox(5);
                    Label dateLabel = new Label("Date: ");
                    dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;");
                    Label dateValue = new Label(reminder.getReminderDate().toString());
                    dateValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;");
                    dateRow.getChildren().addAll(dateLabel, dateValue);

                    details.getChildren().addAll(taskRow, typeRow, dateRow);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label icon = new Label("⏰");
                    icon.setStyle("-fx-font-size: 28px; -fx-text-fill: #003d99;");

                    container.getChildren().addAll(details, spacer, icon);
                    setGraphic(container);
                }
            }
        });

        refreshRemindersList();

        // Update task-related statistics
        updateTaskCounts();

        searchButton.setOnAction(e -> showSearchPopup());
        // Schedule the popup to show after the application is loaded
        Platform.runLater(this::showDelayedTasksPopup);
    }


    private void checkAndMarkOverdueTasks() {
        LocalDate today = LocalDate.now();
        boolean taskUpdated = false;

        for (Task task : taskManager.getAllTasks()) {
            // If deadline is before today and status is not already "Delayed", "Completed", or "Postponed"
            if (task.getDeadline().isBefore(today) &&
                    !task.getStatus().equalsIgnoreCase("Delayed") &&
                    !task.getStatus().equalsIgnoreCase("Completed")) {

                task.setStatus("Delayed"); // Mark as delayed
                taskUpdated = true;
            }
        }

        // Save updates if any task status changed
        if (taskUpdated) {
            taskManager.saveData(); // Ensure this method exists in TaskManager
        }
    }



    private void populatePriorities() {
        priorityListView.getItems().setAll(taskManager.getPriorityLevels());

        priorityListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);

                if (empty || priority == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a styled HBox for each priority
                    HBox priorityBox = new HBox(10); // 10px spacing
                    priorityBox.setAlignment(Pos.CENTER_LEFT);
                    priorityBox.setPadding(new Insets(10));
                    priorityBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-radius: 5; -fx-border-color: #cce7ff;");

                    // Label for priority text
                    Label priorityLabel = new Label(priority);
                    priorityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    // Set unique styles/colors for priorities
                    switch (priority.toLowerCase()) {
                        case "high":
                            priorityLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
                            break;
                        case "medium":
                            priorityLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 14px;");
                            break;
                        case "low":
                            priorityLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
                            break;
                        default:
                            priorityLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-style: italic;");
                            break;
                    }

                    // Optional: Add an icon for each priority
                    Label icon = new Label(); // Placeholder for an icon
                    icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #003d99;");
                    switch (priority.toLowerCase()) {
                        case "high":
                            icon.setText("\uD83D\uDD25"); // High priority icon
                            break;
                        case "medium":
                            icon.setText("🔶"); // Medium priority icon
                            break;
                        case "low":
                            icon.setText("\uD83D\uDFE2"); // Low priority icon
                            break;
                        default:
                            icon.setText("ℹ️"); // Default icon
                            break;
                    }

                    // Add components to the priority box
                    priorityBox.getChildren().addAll(icon, priorityLabel);

                    // Apply hover effect for better interactivity
                    priorityBox.setOnMouseEntered(e -> priorityBox.setStyle("-fx-background-color: #cce7ff; -fx-border-radius: 5; -fx-border-color: #003d99;"));
                    priorityBox.setOnMouseExited(e -> priorityBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-radius: 5; -fx-border-color: #cce7ff;"));

                    // Set the HBox as the graphic for this cell
                    setGraphic(priorityBox);
                }
            }
        });
    }





    private void customizeCategoryListView() {
        categoryListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create an HBox to hold the category label and icon (if needed)
                    HBox categoryBox = new HBox(10); // 10px spacing
                    categoryBox.setAlignment(Pos.CENTER_LEFT);
                    categoryBox.setPadding(new Insets(10));
                    categoryBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-radius: 5; -fx-border-color: #cce7ff;");

                    // Label for the category name
                    Label categoryLabel = new Label(category);
                    categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #003d99;");

                    // Optional: Add a small icon or note for "Other"
                    if ("Other".equals(category)) categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: grey;");
                    categoryBox.getChildren().add(categoryLabel);

                    // Hover effect
                    categoryBox.setOnMouseEntered(e -> categoryBox.setStyle("-fx-background-color: #cce7ff; -fx-border-radius: 5; -fx-border-color: #003d99;"));
                    categoryBox.setOnMouseExited(e -> categoryBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-radius: 5; -fx-border-color: #cce7ff;"));

                    // Set the HBox as the graphic for this cell
                    setGraphic(categoryBox);
                }
            }
        });
    }



    /**
     * Update task-related labels with counts.
     */
    private void updateTaskCounts() {
        totalTasksLabel.setText("Total Tasks: " + taskManager.getAllTasks().size());
        totalTasksLabel.setWrapText(false);
        completedTasksLabel.setText("Completed: " +
                taskManager.getAllTasks().stream().filter(task -> "Completed".equals(task.getStatus())).count());
        delayedTasksLabel.setText("Delayed: " +
                taskManager.getAllTasks().stream().filter(task -> "Delayed".equals(task.getStatus())).count());
        upcomingTasksLabel.setText("Upcoming: " +
                taskManager.getAllTasks().stream().filter(task ->
                        task.getDeadline().isAfter(LocalDate.now()) &&
                                task.getDeadline().isBefore(LocalDate.now().plusDays(7)) &&
                                !"Completed".equals(task.getStatus())).count());
    }

    /**
     * Show a popup for delayed tasks, if any.
     */
    private void showDelayedTasksPopup() {
        List<Task> delayedTasks = taskManager.getAllTasks().stream()
                .filter(task -> "Delayed".equals(task.getStatus()))
                .toList(); // Collect delayed tasks

        if (!delayedTasks.isEmpty()) {
            // Create a new popup Stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Delayed Tasks");

            // Ensure the popup appears in front of the main app
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(taskListView.getScene().getWindow()); // Set owner stage

            // Title Section
            Label titleLabel = new Label("Delayed Tasks");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #003d99;");

            Label subTitleLabel = new Label("You have " + delayedTasks.size() + " delayed task(s):");
            subTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;");

            VBox titleBox = new VBox(5, titleLabel, subTitleLabel);
            titleBox.setAlignment(Pos.CENTER);

            // ListView to display delayed tasks
            ListView<VBox> delayedTasksListView = new ListView<>();
            delayedTasks.stream()
                    .map(task -> {
                        // Create a styled VBox for each task
                        VBox taskBox = new VBox(5); // 5px spacing between elements
                        taskBox.setPadding(new Insets(10));
                        taskBox.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #003d99; -fx-border-radius: 5;");

                        // Task title
                        Label taskTitle = new Label("Title: ");
                        taskTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003d99;");
                        Label taskTitleValue = new Label(task.getTitle());
                        taskTitleValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #6D7ED5FF;");

                        HBox titleRow = new HBox(5, taskTitle, taskTitleValue);
                        titleRow.setAlignment(Pos.CENTER_LEFT);

                        // Task category
                        Label taskCategory = new Label("Category: ");
                        taskCategory.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #003d99;");
                        Label taskCategoryValue = new Label(task.getCategory());
                        taskCategoryValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #6D7ED5FF;");

                        HBox categoryRow = new HBox(5, taskCategory, taskCategoryValue);
                        categoryRow.setAlignment(Pos.CENTER_LEFT);

                        // Task deadline
                        Label taskDeadline = new Label("Deadline: ");
                        taskDeadline.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #003d99;");
                        Label taskDeadlineValue = new Label(task.getDeadline().toString());
                        taskDeadlineValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #6D7ED5FF;"); // Red for deadlines

                        HBox deadlineRow = new HBox(5, taskDeadline, taskDeadlineValue);
                        deadlineRow.setAlignment(Pos.CENTER_LEFT);

                        // Add rows to VBox
                        taskBox.getChildren().addAll(titleRow, categoryRow, deadlineRow);

                        return taskBox;
                    })
                    .forEach(delayedTasksListView.getItems()::add);

            delayedTasksListView.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #003d99;"); // Matches light blue
            delayedTasksListView.setPrefHeight(200); // Adjust height if necessary

            // "OK" Button
            Button okButton = new Button("OK");
            okButton.setStyle("-fx-background-color: #003d99; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5 15 5 15;");
            okButton.setOnAction(e -> popupStage.close());

            // Layout (VBox)
            VBox layout = new VBox(15); // 15px spacing
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.CENTER);
            layout.getChildren().addAll(titleBox, delayedTasksListView, okButton);
            layout.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #003d99; -fx-border-width: 2px; -fx-border-radius: 10px;");

            // Set the scene and show the popup
            Scene popupScene = new Scene(layout, 450, 350); // Adjust dimensions as needed
            popupStage.setScene(popupScene);
            popupStage.show();
        }
    }








    /**
     * Add a new task to the system.
     */
    @FXML
    private void handleAddTask() {
        // Prompt the user for task details using our custom dialogs.
        String title = promptInput("Add New Task", "Enter Task Title", "Title:");
        if (title == null || title.isBlank()) return;

        String description = promptInput("Add New Task", "Enter Task Description", "Description:");
        if (description == null || description.isBlank()) return;

        String category = promptChoice("Add New Task", "Select Task Category", "Category:", taskManager.getCategories());
        if (category == null) return;

        String priority = promptChoice("Add New Task", "Select Task Priority", "Priority:", taskManager.getPriorityLevels());
        if (priority == null) return;

        // Create a dialog for selecting the deadline using a styled DatePicker.
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(7));

        Dialog<LocalDate> dateDialog = new Dialog<>();
        dateDialog.setTitle("Select Deadline");
        dateDialog.setHeaderText("Choose the deadline for the task:");
        URL cssURL2 = getClass().getResource("/styles/dialogstyles.css");
        if (cssURL2 != null) {
            dateDialog.getDialogPane().getStylesheets().add(cssURL2.toExternalForm());
        }
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dateDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        Label deadlineLabel = new Label("Deadline:");
        deadlineLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
        grid.add(deadlineLabel, 0, 0);
        grid.add(datePicker, 1, 0);

        dateDialog.getDialogPane().setContent(grid);

        dateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return datePicker.getValue();
            }
            return null;
        });

        LocalDate deadline = dateDialog.showAndWait().orElse(null);
        if (deadline == null) return;

        // Create and add the task.
        Task newTask = new Task(taskManager.generateTaskId(), title, description, category, priority, deadline);
        taskManager.addTask(newTask);

        // Update UI.
        taskListView.getItems().setAll(taskManager.getAllTasks());
        updateTaskCounts();
    }


    /**
     * Delete a task from the system.
     */
    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("No Task Selected", "Please select a task to delete.");
            return;
        }

        taskManager.deleteTask(selectedTask.getId());

        // Update the UI
        taskListView.getItems().setAll(taskManager.getAllTasks());
        updateTaskCounts();
        showInformation("Task Deleted", "The selected task has been deleted successfully.");
    }

    /**
     * Add a new category.
     */
    @FXML
    private void handleAddCategory() {
        String category = promptInput("Add Category", "Enter the new category name:", "Category:");
        if (category == null || category.isBlank()) return;

        if (taskManager.addCategory(category)) {
            categoryListView.getItems().setAll(taskManager.getCategories());
            // Update the filter ComboBox:
            List<String> filterOptions = new ArrayList<>();
            filterOptions.add("All");
            filterOptions.addAll(taskManager.getCategories());
            categoryFilterComboBox.getItems().setAll(filterOptions);
            categoryFilterComboBox.setValue("All");
        } else {
            showWarning("Duplicate Category", "This category already exists.");
        }
    }

    /**
     * Delete a category and associated tasks.
     */
    @FXML
    private void handleDeleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showWarning("No Category Selected", "Please select a category to delete.");
            return;
        }

        if ("Other".equals(selectedCategory)) {
            // Warn the user that "Other" cannot be deleted
            showWarning("Restricted Deletion", "The 'Other' category cannot be deleted.");
        } else {
            taskManager.deleteCategory(selectedCategory);
            showInformation("Success", "The category '" + selectedCategory + "' has been deleted successfully.");
        }

        // Update the UI
        categoryListView.getItems().setAll(taskManager.getCategories());
        taskListView.getItems().setAll(taskManager.getAllTasks());
        updateTaskCounts();

        // Refresh the filter ComboBox with an "All" option
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("All");
        filterOptions.addAll(taskManager.getCategories());
        categoryFilterComboBox.getItems().setAll(filterOptions);
        categoryFilterComboBox.setValue("All");
        refreshRemindersList();
    }


    /**
     * Add a new priority.
     */
    @FXML
    private void handleAddPriority() {
        // Prompt the user for a new priority name
        String priority = promptInput("Add Priority", "Enter the new priority name:", "Priority:");
        if (priority == null || priority.isBlank()) {
            return; // Exit if no valid input is provided
        }

        // Add the priority to the TaskManager
        if (taskManager.addPriorityLevel(priority)) {
            // Update the UI: refresh the priorityListView
            priorityListView.getItems().setAll(taskManager.getPriorityLevels());
            showInformation("Success", "The priority '" + priority + "' has been added successfully.");
        } else {
            showWarning("Duplicate Priority", "This priority already exists.");
        }
    }


    /**
     * Delete a priority.
     */
    @FXML
    private void handleDeletePriority() {
        // Get the selected priority from the ListView
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();

        if (selectedPriority == null) {
            // Show a warning if no priority is selected
            showWarning("No Priority Selected", "Please select a priority to delete.");
            return;
        }

        // Check if the selected priority is restricted (e.g., default priorities)
        if ("Default".equals(selectedPriority)) {
            showWarning("Restricted Deletion", "The '" + selectedPriority + "' priority cannot be deleted.");
            return;
        }

        // Delete the selected priority from the TaskManager
        taskManager.deletePriorityLevel(selectedPriority);

        // Show a success message
        showInformation("Success", "The priority '" + selectedPriority + "' has been deleted successfully.");

        // Update the Priority ListView
        priorityListView.getItems().setAll(taskManager.getPriorityLevels());

        // Optionally, update tasks and related UI components
        taskListView.getItems().setAll(taskManager.getAllTasks());
        updateTaskCounts();
    }



    @FXML
    private void handleEditTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("No Task Selected", "Please select a task to edit.");
            return;
        }

        // Create a custom dialog
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit the task details below: ");
        dialog.getDialogPane().setStyle("-fx-background-color: #f0f8ff;"); // Set dialog background color

        // Create labels and input fields
        Label titleLabel = new Label("Title:");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        TextField titleField = new TextField(selectedTask.getTitle());
        titleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        TextArea descriptionField = new TextArea(selectedTask.getDescription());
        descriptionField.setWrapText(true);
        descriptionField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        Label categoryLabel = new Label("Category:");
        categoryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        ChoiceBox<String> categoryField = new ChoiceBox<>();
        categoryField.getItems().setAll(taskManager.getCategories());
        categoryField.setValue(selectedTask.getCategory());
        categoryField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        Label priorityLabel = new Label("Priority:");
        priorityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        ChoiceBox<String> priorityField = new ChoiceBox<>();
        priorityField.getItems().setAll(taskManager.getPriorityLevels());
        priorityField.setValue(selectedTask.getPriority());
        priorityField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        Label deadlineLabel = new Label("Deadline:");
        deadlineLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        DatePicker deadlineField = new DatePicker(selectedTask.getDeadline());
        deadlineField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #003d99; -fx-font-size: 14px;");
        ChoiceBox<String> statusField = new ChoiceBox<>();
        statusField.getItems().addAll("Open", "In Progress", "Postponed", "Completed");
        statusField.setValue(selectedTask.getStatus());
        statusField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cce7ff; -fx-border-radius: 5;");

        // Layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: #f0f8ff;"); // Match background to app theme

        // Add components to the grid
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(priorityLabel, 0, 3);
        grid.add(priorityField, 1, 3);
        grid.add(deadlineLabel, 0, 4);
        grid.add(deadlineField, 1, 4);
        grid.add(statusLabel, 0, 5);
        grid.add(statusField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Add buttons to the dialog
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Style the buttons
        dialog.getDialogPane().lookupButton(saveButtonType).setStyle(
                "-fx-background-color: #003d99; -fx-text-fill: white; -fx-border-radius: 5; -fx-font-size: 14px;");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
                "-fx-background-color: #cce7ff; -fx-text-fill: #003d99; -fx-border-radius: 5; -fx-font-size: 14px;");

        // Handle Save button action
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Update the task with the new values
                selectedTask.setTitle(titleField.getText());
                selectedTask.setDescription(descriptionField.getText());
                selectedTask.setCategory(categoryField.getValue());
                selectedTask.setPriority(priorityField.getValue());
                selectedTask.setDeadline(deadlineField.getValue());

                // Check if the deadline is before today
                LocalDate selectedDeadline = deadlineField.getValue();
                if (selectedDeadline.isBefore(LocalDate.now())) {
                    // Only set status to "Delayed" if it's not "Completed" or "Postponed"
                    String currentStatus = statusField.getValue();
                    if (!"Completed".equalsIgnoreCase(currentStatus) && !"Postponed".equalsIgnoreCase(currentStatus)) {
                        selectedTask.setStatus("Delayed");
                    } else {
                        selectedTask.setStatus(currentStatus);
                    }
                } else {
                    selectedTask.setStatus(statusField.getValue());
                }
                return selectedTask;
            }
            return null;
        });

        // Show the dialog and wait for user input
        dialog.showAndWait().ifPresent(editedTask -> {
            if ("Completed".equalsIgnoreCase(editedTask.getStatus())) {
                // Remove all reminders from this task.
                if (editedTask.getReminders() != null) {
                    editedTask.getReminders().clear();
                }
            }
            taskManager.updateTask(
                    editedTask.getId(),
                    editedTask.getTitle(),
                    editedTask.getDescription(),
                    editedTask.getCategory(),
                    editedTask.getPriority(),
                    editedTask.getDeadline(),
                    editedTask.getStatus()
            );

            // Refresh UI
            taskListView.refresh();
            updateTaskCounts();
            refreshRemindersList();
            showInformation("Task Updated", "The task has been successfully updated.");
        });
    }




    @FXML
    private void handleEditCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showWarning("No Category Selected", "Please select a category to rename.");
            return;
        }
        if ("Other".equals(selectedCategory)) {
            // Warn the user that "Other" cannot be deleted
            showWarning("Restricted Modification", "The 'Other' category cannot be renamed.\nYou should add another category instead.");
        }

        else {
            String newCategoryName = promptInput("Rename Category", "Enter a new name for the category:", selectedCategory);
            if (newCategoryName != null && !newCategoryName.isBlank()) {
                taskManager.renameCategory(selectedCategory, newCategoryName);
                categoryListView.getItems().setAll(taskManager.getCategories());
                taskListView.getItems().setAll(taskManager.getAllTasks());
                showInformation("Category Updated", "The category has been successfully renamed.");
            }
        }
    }


    @FXML
    private void handleEditPriority() {
        // Get the selected priority from the ListView.
        String oldPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (oldPriority == null) {
            showWarning("No Priority Selected", "Please select a priority to rename.");
            return;
        }
        if ("Default".equalsIgnoreCase(oldPriority)) {
            showWarning("Invalid Priority", "The 'Default' priority cannot be renamed.");
            return;
        }

        // Prompt for the new priority name.
        String newPriority = promptInput("Rename Priority", "Enter new name for the priority:", oldPriority);
        if (newPriority == null || newPriority.isBlank()) {
            return;
        }

        // Attempt to rename the priority using TaskManager's renamePriority method.
        if (taskManager.renamePriority(oldPriority, newPriority)) {
            showInformation("Priority Updated", "The priority has been successfully renamed.");
            // Refresh the UI: update the Priority ListView and task list.
            priorityListView.getItems().setAll(taskManager.getPriorityLevels());
            taskListView.getItems().setAll(taskManager.getAllTasks());
            updateTaskCounts();
        } else {
            showWarning("Rename Failed", "The new priority name may already exist or is invalid.");
        }
    }



    /**
     * Refresh reminders from all tasks.
     */
    private void refreshRemindersList() {
        reminderListView.getItems().clear();
        // Only add reminders from tasks that are still present.
        taskManager.getAllTasks().forEach(task -> {
            if (task.getReminders() != null && !task.getReminders().isEmpty()) {
                reminderListView.getItems().addAll(task.getReminders());
            }
        });
    }



    /**
     * Add a new reminder for a selected task.
     */
    @FXML
    private void handleAddReminder() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showWarning("No Task Selected", "Please select a task to set a reminder.");
            return;
        }
        if ("Completed".equalsIgnoreCase(selectedTask.getStatus())) {
            showWarning("Invalid Task", "Cannot add a reminder for a completed task.");
            return;
        }

        // Prompt user to select reminder type using a styled ChoiceDialog.
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("1 day", List.of("1 day", "1 week", "1 month", "Custom"));
        typeDialog.setTitle("Set Reminder");

        // Create and set a custom header label in blue.
        Label typeHeader = new Label("Select Reminder Type");
        typeHeader.setStyle("-fx-text-fill: #003d99; -fx-font-size: 16px; -fx-font-weight: bold;");
        typeDialog.getDialogPane().setHeader(typeHeader);

        // Remove the default content text.
        typeDialog.setContentText(null);

        // (Optional) Load external CSS for consistent styling.
        URL dialogCssURL = getClass().getResource("/styles/dialogstyles.css");
        if (dialogCssURL != null) {
            typeDialog.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
        } else {
            System.err.println("dialogstyles.css not found!");
        }

        String reminderType = typeDialog.showAndWait().orElse(null);
        if (reminderType == null) return;

        LocalDate reminderDate = null;
        if ("Custom".equalsIgnoreCase(reminderType)) {
            // Create a styled DatePicker dialog.
            DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));

            Dialog<LocalDate> dateDialog = new Dialog<>();
            dateDialog.setTitle("Select Custom Reminder Date");

            // Set a custom header for the date dialog.
            Label dateHeader = new Label("Select Custom Reminder Date");
            dateHeader.setStyle("-fx-text-fill: #003d99; -fx-font-size: 16px; -fx-font-weight: bold;");
            dateDialog.getDialogPane().setHeader(dateHeader);

            // Load the same external CSS to keep styling consistent.
            if (dialogCssURL != null) {
                dateDialog.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
            }

            // Set button types.
            dateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Create a VBox with a label and the DatePicker.
            VBox dateContent = new VBox(10);
            dateContent.setAlignment(Pos.CENTER);
            Label dateLabel = new Label("Reminder Date:");
            dateLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
            dateContent.getChildren().addAll(dateLabel, datePicker);
            dateDialog.getDialogPane().setContent(dateContent);

            // Convert the result.
            dateDialog.setResultConverter(button -> button == ButtonType.OK ? datePicker.getValue() : null);
            reminderDate = dateDialog.showAndWait().orElse(null);
            if (reminderDate == null) return; // Ensure a valid date is selected
        } else {
            // Set automatic reminder date based on type.
            switch (reminderType) {
                case "1 day":
                    reminderDate = selectedTask.getDeadline().minusDays(1);
                    break;
                case "1 week":
                    reminderDate = selectedTask.getDeadline().minusWeeks(1);
                    break;
                case "1 month":
                    reminderDate = selectedTask.getDeadline().minusMonths(1);
                    break;
            }
        }

        // Check that the reminder date is before the task's deadline.
        if (reminderDate.compareTo(selectedTask.getDeadline()) >= 0) {
            showWarning("Invalid Date", "The reminder date must be before the task deadline.");
            return;
        }

        // Check for duplicate reminders (same date for same task)
        if (selectedTask.getReminders() != null) {
            for (Reminder existingReminder : selectedTask.getReminders()) {
                if (existingReminder.getReminderDate().equals(reminderDate)) {
                    showWarning("Duplicate Reminder", "A reminder for this date already exists for the selected task.");
                    return;
                }
            }
        }

        // Ensure the task has a reminders list before adding.
        if (selectedTask.getReminders() == null) {
            selectedTask.setReminders(new ArrayList<>());
        }

        // Create and add reminder with the computed date.
        Reminder newReminder = new Reminder(taskManager.generateReminderId(), selectedTask.getId(), reminderType, reminderDate);
        selectedTask.getReminders().add(newReminder);

        // Save updated task with reminders.
        taskManager.saveData();

        // Update UI.
        refreshRemindersList();
        showInformation("Reminder Set", "Reminder added for task: " + selectedTask.getTitle() + " on " + reminderDate);
    }






    /**
     * Edit an existing reminder.
     */
    @FXML
    private void handleEditReminder() {
        Reminder selectedReminder = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showWarning("No Reminder Selected", "Please select a reminder to edit.");
            return;
        }

        // Get associated task
        Task associatedTask = taskManager.getAllTasks().stream()
                .filter(task -> task.getId() == selectedReminder.getTaskId())
                .findFirst().orElse(null);

        if (associatedTask == null) {
            showWarning("Error", "Associated task not found.");
            return;
        }

        // Prompt user for new reminder type with consistent styling
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>(selectedReminder.getType(), List.of("1 day", "1 week", "1 month", "Custom"));
        typeDialog.setTitle("Edit Reminder");

// Load external CSS files for dialog and tab styles
        URL dialogCssURL = getClass().getResource("/styles/dialogstyles.css");
        if (dialogCssURL != null) {
            typeDialog.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
        }

        URL tabCssURL = getClass().getResource("/styles/tabstyles.css");

        if (tabCssURL != null) {
            typeDialog.getDialogPane().getStylesheets().add(tabCssURL.toExternalForm());
        }

// Set a custom header to use blue text
        Label headerLabel = new Label("Select New Reminder Type");
        headerLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 16px; -fx-font-weight: bold;");
        typeDialog.getDialogPane().setHeader(headerLabel);

        typeDialog.setContentText("Reminder Type:");

// Show the dialog and retrieve the result
        String newType = typeDialog.showAndWait().orElse(null);

        if (newType == null) return;

        LocalDate newDate;
        if ("Custom".equalsIgnoreCase(newType)) {
            // Ask user for a custom date
            // Create a DatePicker with the current reminder date and apply inline styling.
            DatePicker datePicker = new DatePicker(selectedReminder.getReminderDate());
            datePicker.setStyle("-fx-background-color: #ffffff; -fx-border-color: #003d99; -fx-border-radius: 5; -fx-text-fill: #003d99;");

// Create the dialog.
            Dialog<LocalDate> dateDialog = new Dialog<>();
            dateDialog.setTitle("Select Custom Reminder Date");

            if (dialogCssURL != null) {
                dateDialog.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
            } else {
                System.err.println("dialogstyles.css not found!");
            }

            if (tabCssURL != null) {
                dateDialog.getDialogPane().getStylesheets().add(tabCssURL.toExternalForm());
            } else {
                System.err.println("tabstyles.css not found!");
            }

// Set a custom header using a Label styled in blue.
            Label headerLabel1 = new Label("Select Custom Reminder Date");
            headerLabel1.setStyle("-fx-text-fill: #003d99; -fx-font-size: 16px; -fx-font-weight: bold;");
            dateDialog.getDialogPane().setHeader(headerLabel1);

// Set button types.
            dateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

// Create a VBox for the dialog content, styled to match your UI.
            VBox content = new VBox(10);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));
            Label label = new Label("Reminder Date:");
            label.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
            content.getChildren().addAll(label, datePicker);
            dateDialog.getDialogPane().setContent(content);

// Convert the result when the user clicks OK.
            dateDialog.setResultConverter(button -> button == ButtonType.OK ? datePicker.getValue() : null);

// Show the dialog and assign the result.
            newDate = dateDialog.showAndWait().orElse(null);

            if (newDate == null) return;
        } else {
            // Automatically calculate new reminder date based on type.
            switch (newType.toLowerCase()) {
                case "1 day":
                    newDate = associatedTask.getDeadline().minusDays(1);
                    break;
                case "1 week":
                    newDate = associatedTask.getDeadline().minusWeeks(1);
                    break;
                case "1 month":
                    newDate = associatedTask.getDeadline().minusMonths(1);
                    break;
                default:
                    newDate = associatedTask.getDeadline().minusDays(1);
            }
        }

        // After calculating newDate
        if (newDate.compareTo(associatedTask.getDeadline()) >= 0) {
            showWarning("Invalid Date", "The reminder date must be before the task deadline.");
            return;
        }


        // Update reminder
        selectedReminder.setType(newType);
        selectedReminder.setReminderDate(newDate);
        taskManager.saveData(); // Save changes

        // Refresh UI
        refreshRemindersList();
        showInformation("Reminder Updated", "The reminder has been updated successfully.");
    }



    /**
     * Delete an existing reminder.
     */
    @FXML
    private void handleDeleteReminder() {
        Reminder selectedReminder = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showWarning("No Reminder Selected", "Please select a reminder to delete.");
            return;
        }

        // Find the associated task and remove the reminder
        Task associatedTask = taskManager.getAllTasks().stream()
                .filter(task -> task.getId() == selectedReminder.getTaskId())
                .findFirst().orElse(null);

        if (associatedTask != null) {
            associatedTask.getReminders().removeIf(r -> r.getId() == selectedReminder.getId());
            taskManager.saveData(); // Save changes
        }

        // Refresh UI
        refreshRemindersList();
        showInformation("Reminder Deleted", "The reminder has been deleted successfully.");
    }




    /**
     * Utility method to prompt the user for input.
     */
    private String promptInput(String dialogTitle, String headerText, String labelText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.setHeaderText(headerText);

        // Optionally add an external CSS file for further styling
        URL cssURL = getClass().getResource("/styles/dialogstyles.css");
        if (cssURL != null) {
            dialog.getDialogPane().getStylesheets().add(cssURL.toExternalForm());
        }

        // Create a label with blue text.
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Create a text field with blue text and a blue border.
        TextField textField = new TextField();
        textField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #003d99; " +
                "-fx-border-radius: 5; -fx-padding: 5; -fx-text-fill: #003d99;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Platform.runLater(textField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }





    /**
     * Utility method to prompt the user for a choice.
     */
    private String promptChoice(String dialogTitle, String headerText, String labelText, List<String> choices) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.setHeaderText(headerText);

        URL cssURL = getClass().getResource("/styles/dialogstyles.css");
        if (cssURL != null) {
            dialog.getDialogPane().getStylesheets().add(cssURL.toExternalForm());
        }

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(choices);
        comboBox.setValue(choices.get(0));
        comboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #003d99; " +
                "-fx-border-radius: 5; -fx-text-fill: #003d99;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(label, 0, 0);
        grid.add(comboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return comboBox.getValue();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }



    /**
     * Show an informational alert.
     */
    private void showInformation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show a warning alert.
     */
    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void styleScrollBar(ListView<?> listView) {
        // Wait until the ListView is fully initialized
        Platform.runLater(() -> {
            // Lookup the vertical scrollbar
            ScrollBar verticalScrollBar = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            if (verticalScrollBar != null) {
                verticalScrollBar.setStyle(
                        "-fx-background-color: #6D7ED5FF;" +   // Background of the scrollbar track
                                "-fx-border-color: #003d99;" +      // Border color
                                "-fx-border-radius: 5;" +           // Rounded corners
                                "-fx-border-width: 2;"              // Border thickness
                );

                // Style the thumb (the draggable part)
                verticalScrollBar.lookupAll(".thumb").forEach(thumb ->
                        thumb.setStyle(
                                "-fx-background-color: #003d99;" +   // Thumb color
                                        "-fx-border-radius: 5;" +           // Rounded corners for thumb
                                        "-fx-padding: 2;"                   // Optional padding for the thumb
                        )
                );
            }

            // Lookup the horizontal scrollbar (if any)
            ScrollBar horizontalScrollBar = (ScrollBar) listView.lookup(".scroll-bar:horizontal");
            if (horizontalScrollBar != null) {
                horizontalScrollBar.setStyle(
                        "-fx-background-color: #6D7ED5FF;" +   // Background of the scrollbar track
                                "-fx-border-color: #003d99;" +      // Border color
                                "-fx-border-radius: 5;" +           // Rounded corners
                                "-fx-border-width: 2;"              // Border thickness
                );

                // Style the thumb (the draggable part)
                horizontalScrollBar.lookupAll(".thumb").forEach(thumb ->
                        thumb.setStyle(
                                "-fx-background-color: #99CCFFFF;" +   // Thumb color
                                        "-fx-border-radius: 5;" +           // Rounded corners for thumb
                                        "-fx-padding: 2;"                   // Optional padding for the thumb
                        )
                );
            }
        });
    }

    private void showSearchPopup() {
        // Create a dialog for search input with a custom result type.
        Dialog<SearchCriteria> dialog = new Dialog<>();
        dialog.setTitle("Search Tasks");
        Label headerLabel = new Label("Enter search criteria:");
        headerLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
        dialog.getDialogPane().setHeader(headerLabel);


        // Load the external CSS file for dialog styling.
        URL dialogCssURL = getClass().getResource("/styles/dialogstyles.css");
        if (dialogCssURL != null) {
            dialog.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
        } else {
            System.err.println("dialogstyles.css not found!");
        }
        // Also load the tab styles to ensure ComboBoxes get proper styling.

        URL tabCssURL = getClass().getResource("/styles/tabstyles.css");
        if (tabCssURL != null) {
            dialog.getDialogPane().getStylesheets().add(tabCssURL.toExternalForm());
        } else {
            System.err.println("tabstyles.css not found!");
        }

        // Set the button types.
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        // Create controls for the search criteria.
        TextField titleField = new TextField();
        titleField.setPromptText("Title keyword...");

        ComboBox<String> categoryCombo = new ComboBox<>();
        List<String> catOptions = new ArrayList<>();
        catOptions.add("All");
        catOptions.addAll(taskManager.getCategories());
        categoryCombo.getItems().setAll(catOptions);
        categoryCombo.setValue("All");
        // Ensure it uses the "combo-box" style (from tabstyles.css)
        if (!categoryCombo.getStyleClass().contains("combo-box")) {
            categoryCombo.getStyleClass().add("combo-box");
        }

        ComboBox<String> priorityCombo = new ComboBox<>();
        List<String> priOptions = new ArrayList<>();
        priOptions.add("All");
        priOptions.addAll(taskManager.getPriorityLevels());
        priorityCombo.getItems().setAll(priOptions);
        priorityCombo.setValue("All");
        if (!priorityCombo.getStyleClass().contains("combo-box")) {
            priorityCombo.getStyleClass().add("combo-box");
        }

        // Layout the controls in a grid.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label titleLabel = new Label("Title:");
        titleLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);

        Label categoryLabel = new Label("Category:");
        categoryLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
        grid.add(categoryLabel, 0, 1);
        grid.add(categoryCombo, 1, 1);

        Label priorityLabel = new Label("Priority:");
        priorityLabel.setStyle("-fx-text-fill: #003d99; -fx-font-size: 14px; -fx-font-weight: bold;");
        grid.add(priorityLabel, 0, 2);
        grid.add(priorityCombo, 1, 2);


        dialog.getDialogPane().setContent(grid);

        // Convert the result to a SearchCriteria object when the Search button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return new SearchCriteria(titleField.getText(), categoryCombo.getValue(), priorityCombo.getValue());
            }
            return null;
        });

        // Process the search criteria.
        dialog.showAndWait().ifPresent(criteria -> {
            List<Task> filteredTasks = taskManager.getAllTasks().stream().filter(task -> {
                boolean matchesTitle = criteria.title == null || criteria.title.isBlank() ||
                        task.getTitle().toLowerCase().contains(criteria.title.toLowerCase());
                boolean matchesCategory = "All".equalsIgnoreCase(criteria.category) ||
                        task.getCategory().equalsIgnoreCase(criteria.category);
                boolean matchesPriority = "All".equalsIgnoreCase(criteria.priority) ||
                        task.getPriority().equalsIgnoreCase(criteria.priority);
                return matchesTitle && matchesCategory && matchesPriority;
            }).toList();

            taskListView.getItems().setAll(filteredTasks);
            if (filteredTasks.isEmpty()) {
                showInformation("No Results", "No tasks match your search criteria.");
            }
        });
    }

    // Helper class to encapsulate search criteria.
    private static class SearchCriteria {
        String title;
        String category;
        String priority;

        SearchCriteria(String title, String category, String priority) {
            this.title = title;
            this.category = category;
            this.priority = priority;
        }
    }

    @FXML
    private void handleHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Usage Instructions");
        helpAlert.setHeaderText("How to Use MediaLab Assistant");
        helpAlert.setContentText("• Use the Task Management tab to add, edit, or delete tasks. Set up reminders for your tasks.\n\n"
                + "• Manage categories and priorities in their respective tabs.\n\n"
                + "• Manage reminders for tasks in the Reminders tab.\n\n"
                + "• Use the search functionality (accessible via the search button of the Task Management tab) to filter tasks by title, category, or priority.");

        // Load your external CSS for consistent styling (if available)
        URL dialogCssURL = getClass().getResource("/styles/dialogstyles.css");
        if (dialogCssURL != null) {
            helpAlert.getDialogPane().getStylesheets().add(dialogCssURL.toExternalForm());
        }

        helpAlert.showAndWait();
    }



}