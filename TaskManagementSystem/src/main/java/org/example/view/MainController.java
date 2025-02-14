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
import org.example.controller.ReminderManager;
import org.example.model.Reminder;
import org.example.model.Task;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    private final TaskManager taskManager = new TaskManager();

    private final ReminderManager reminderManager = new ReminderManager();

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
    private TextField searchField; // For search functionality

    @FXML
    private ListView<String> priorityListView;

    @FXML
    private ListView<Task> searchResultsView; // For displaying search results

    @FXML
    private ListView<Reminder> reminderListView;


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
                        statusValueLabel.setStyle("-fx-text-fill: #ff9100; -fx-font-size: 14px;"); // Orange for "Postpoend"
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
        reminderListView.getItems().setAll(reminderManager.getAllReminders());

        // Update task-related statistics
        updateTaskCounts();

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
                    !task.getStatus().equalsIgnoreCase("Completed") &&
                    !task.getStatus().equalsIgnoreCase("Postponed")) {

                task.setStatus("Delayed"); // Mark as delayed
                taskUpdated = true;
            }
        }

        // Save updates if any task status changed
        if (taskUpdated) {
            taskManager.saveData(); // Ensure this method exists in TaskManager
        }
    }





    // Populate categories in the ListView
    private void populateCategories() {
        categoryListView.getItems().setAll(taskManager.getCategories());
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
                            priorityLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px; -fx-font-style: italic;");
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
        // Prompt the user for task details
        String title = promptInput("Add New Task", "Enter Task Title", "Title:");
        if (title == null || title.isBlank()) return;

        String description = promptInput("Add New Task", "Enter Task Description", "Description:");
        if (description == null || description.isBlank()) return;

        String category = promptChoice("Add New Task", "Select Task Category", "Category:", taskManager.getCategories());
        if (category == null) return;

        String priority = promptChoice("Add New Task", "Select Task Priority", "Priority:", taskManager.getPriorityLevels());
        if (priority == null) return;

        // Create a dialog for selecting the deadline using a DatePicker
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(7)); // Default to 7 days from now

        Dialog<LocalDate> dateDialog = new Dialog<>();
        dateDialog.setTitle("Select Deadline");
        dateDialog.setHeaderText("Choose the deadline for the task:");

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dateDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Deadline:"), 0, 0);
        grid.add(datePicker, 1, 0);

        dateDialog.getDialogPane().setContent(grid);

        // Convert the result to a LocalDate when the Confirm button is clicked
        dateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return datePicker.getValue();
            }
            return null;
        });

        LocalDate deadline = dateDialog.showAndWait().orElse(null);
        if (deadline == null) return;

        // Create and add the task
        Task newTask = new Task(taskManager.generateTaskId(), title, description, category, priority, deadline);
        taskManager.addTask(newTask);

        // Update UI
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


    /**
     * Search tasks based on a keyword.
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            showWarning("Empty Search", "Please enter a keyword to search.");
            return;
        }

        searchResultsView.getItems().clear();
        taskManager.getAllTasks().stream()
                .filter(task -> task.getTitle().contains(keyword) || task.getDescription().contains(keyword))
                .forEach(searchResultsView.getItems()::add);

        if (searchResultsView.getItems().isEmpty()) {
            showInformation("No Results", "No tasks match your search criteria.");
        }
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
            String newCategoryName = promptInput("Rename Category", "Enter new name for the category:", selectedCategory);
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
        showInformation("Edit Priority", "Priority editing functionality is not yet implemented.");
    }


    /**
     * Add a new reminder for a selected task.
     */
    @FXML
    private void handleAddReminder() {
        // Select task from Task Management tab
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("No Task Selected", "Please select a task to set a reminder.");
            return;
        }

        if ("Completed".equalsIgnoreCase(selectedTask.getStatus())) {
            showWarning("Invalid Task", "Cannot add a reminder for a completed task.");
            return;
        }

        // Prompt user to select reminder type
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("1 day", List.of("1 day", "1 week", "1 month", "Custom"));
        typeDialog.setTitle("Set Reminder");
        typeDialog.setHeaderText("Select Reminder Type");
        typeDialog.setContentText("Reminder Type:");

        String reminderType = typeDialog.showAndWait().orElse(null);
        if (reminderType == null) return;

        LocalDate customDate = null;
        if ("Custom".equalsIgnoreCase(reminderType)) {
            // Prompt for a custom reminder date
            DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
            Dialog<LocalDate> dateDialog = new Dialog<>();
            dateDialog.setTitle("Select Custom Reminder Date");
            dateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            grid.add(new Label("Reminder Date:"), 0, 0);
            grid.add(datePicker, 1, 0);

            dateDialog.getDialogPane().setContent(grid);
            dateDialog.setResultConverter(dialogButton -> (dialogButton == ButtonType.OK) ? datePicker.getValue() : null);
            customDate = dateDialog.showAndWait().orElse(null);

            if (customDate == null) return;
        }

        // Add the reminder
        boolean success = reminderManager.addReminder(selectedTask, reminderType, customDate);
        if (success) {
            showInformation("Reminder Set", "Reminder added for task: " + selectedTask.getTitle());
            reminderListView.getItems().setAll(reminderManager.getAllReminders()); // Update Reminders tab
        } else {
            showWarning("Failed", "Failed to add reminder. Ensure the date is valid.");
        }
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

        // Prompt user for new reminder type
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>(selectedReminder.getType(), List.of("1 day", "1 week", "1 month", "Custom"));
        typeDialog.setTitle("Edit Reminder");
        typeDialog.setHeaderText("Select New Reminder Type");
        typeDialog.setContentText("Reminder Type:");
        String newType = typeDialog.showAndWait().orElse(null);
        if (newType == null) return;

        LocalDate newCustomDate = null;
        if ("Custom".equalsIgnoreCase(newType)) {
            DatePicker datePicker = new DatePicker(selectedReminder.getReminderDate());
            Dialog<LocalDate> dateDialog = new Dialog<>();
            dateDialog.setTitle("Select Custom Reminder Date");
            dateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dateDialog.getDialogPane().setContent(new VBox(10, new Label("Reminder Date:"), datePicker));
            dateDialog.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? datePicker.getValue() : null);
            newCustomDate = dateDialog.showAndWait().orElse(null);
            if (newCustomDate == null) return;
        }

        boolean success = reminderManager.updateReminder(selectedReminder.getId(), newType, newCustomDate, associatedTask);
        if (success) {
            showInformation("Reminder Updated", "The reminder has been updated successfully.");
            reminderListView.getItems().setAll(reminderManager.getAllReminders());
        } else {
            showWarning("Failed", "Failed to update the reminder. Please check the input.");
        }
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
        boolean success = reminderManager.deleteReminder(selectedReminder.getId());
        if (success) {
            showInformation("Reminder Deleted", "The reminder has been deleted successfully.");
            reminderListView.getItems().setAll(reminderManager.getAllReminders());
        } else {
            showWarning("Failed", "Failed to delete the reminder.");
        }
    }



    /**
     * Utility method to prompt the user for input.
     */
    private String promptInput(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait().orElse(null);
    }

    /**
     * Utility method to prompt the user for a choice.
     */
    private String promptChoice(String title, String header, String content, List<String> choices) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
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


}