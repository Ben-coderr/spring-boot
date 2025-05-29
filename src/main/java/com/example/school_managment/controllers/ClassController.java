package com.example.school_managment.controllers;

import com.example.school_managment.models.Class;
import com.example.school_managment.services.ClassService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClassController implements Initializable {
    @FXML private TableView<Class> classTable;
    @FXML private TableColumn<Class, String> nameColumn;
    @FXML private TableColumn<Class, Integer> capacityColumn;
    @FXML private TableColumn<Class, Integer> gradeColumn;
    @FXML private TableColumn<Class, String> supervisorColumn;
    @FXML private TableColumn<Class, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private ClassService classService;
    private ObservableList<Class> classesList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            classService = new ClassService();
            classesList = classService.getAllClasses();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize ClassService: " + e.getMessage());
            classesList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Class, Void>, TableCell<Class, Void>> cellFactory = param -> new TableCell<>() {
            private final Button viewBtn = new Button();
            private final Button deleteBtn = new Button();

            {
                // Set icons for buttons with error handling
                try {
                    ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/view.png")));
                    viewIcon.setFitHeight(20);
                    viewIcon.setFitWidth(20);
                    viewBtn.setGraphic(viewIcon);
                    viewBtn.getStyleClass().add("view-button");
                } catch (Exception e) {
                    viewBtn.setText("View");
                }

                try {
                    ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/Trash Icon.png")));
                    deleteIcon.setFitHeight(20);
                    deleteIcon.setFitWidth(20);
                    deleteBtn.setGraphic(deleteIcon);
                    deleteBtn.getStyleClass().add("delete-button");
                } catch (Exception e) {
                    deleteBtn.setText("Delete");
                }

                viewBtn.setOnAction(event -> {
                    Class classObj = getTableView().getItems().get(getIndex());
                    showClassDetails(classObj);
                });

                deleteBtn.setOnAction(event -> {
                    Class classObj = getTableView().getItems().get(getIndex());
                    deleteClass(classObj);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, viewBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void setupPagination() {
        int pageCount = calculatePageCount();
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            updateTable(pageIndex);
            return classTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) classesList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, classesList.size());

        if (fromIndex >= classesList.size()) {
            classTable.setItems(FXCollections.observableArrayList());
        } else {
            classTable.setItems(FXCollections.observableArrayList(classesList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                classesList.setAll(classService.getAllClasses());
            } else {
                ObservableList<Class> filteredList = FXCollections.observableArrayList();
                for (Class classObj : classService.getAllClasses()) {
                    if (classObj.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            String.valueOf(classObj.getCapacity()).contains(newValue) ||
                            String.valueOf(classObj.getGrade()).contains(newValue) ||
                            classObj.getSupervisor().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(classObj);
                    }
                }
                classesList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        Dialog<Class> dialog = new Dialog<>();
        dialog.setTitle("Add New Class");
        dialog.setHeaderText("Enter class details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField capacityField = new TextField();
        TextField gradeField = new TextField();
        TextField supervisorField = new TextField();

        // Set error labels for validations
        Label capacityErrorLabel = new Label();
        capacityErrorLabel.setStyle("-fx-text-fill: red;");
        capacityErrorLabel.setVisible(false);

        Label gradeErrorLabel = new Label();
        gradeErrorLabel.setStyle("-fx-text-fill: red;");
        gradeErrorLabel.setVisible(false);

        grid.add(new Label("Class Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Capacity:"), 0, 1);
        grid.add(capacityField, 1, 1);
        grid.add(capacityErrorLabel, 2, 1);
        grid.add(new Label("Grade:"), 0, 2);
        grid.add(gradeField, 1, 2);
        grid.add(gradeErrorLabel, 2, 2);
        grid.add(new Label("Supervisor:"), 0, 3);
        grid.add(supervisorField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Enable/Disable add button depending on whether fields are filled
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key navigation
        setupEnterKeyNavigation(nameField, capacityField);
        setupEnterKeyNavigation(capacityField, gradeField);
        setupEnterKeyNavigation(gradeField, supervisorField);

        // Make the supervisor field trigger the Add button when Enter is pressed
        supervisorField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        // Validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, capacityField, capacityErrorLabel, gradeField, gradeErrorLabel, supervisorField);
        });

        capacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidCapacity = isValidCapacity(newValue);
            capacityErrorLabel.setVisible(!isValidCapacity && !newValue.isEmpty());
            capacityErrorLabel.setText(isValidCapacity ? "" : "Capacity must contain only numbers");
            validateForm(dialog, addButtonType, nameField, capacityField, capacityErrorLabel, gradeField, gradeErrorLabel, supervisorField);
        });

        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidGrade = isValidGrade(newValue);
            gradeErrorLabel.setVisible(!isValidGrade && !newValue.isEmpty());
            gradeErrorLabel.setText(isValidGrade ? "" : "Grade must be a number <= 20");
            validateForm(dialog, addButtonType, nameField, capacityField, capacityErrorLabel, gradeField, gradeErrorLabel, supervisorField);
        });

        supervisorField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, capacityField, capacityErrorLabel, gradeField, gradeErrorLabel, supervisorField);
        });

        // Convert the result to a class when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                int capacity = Integer.parseInt(capacityField.getText());
                int grade = Integer.parseInt(gradeField.getText());
                String supervisor = supervisorField.getText();
                return new Class(name, capacity, grade, supervisor);
            }
            return null;
        });

        Optional<Class> result = dialog.showAndWait();
        result.ifPresent(classObj -> {
            classService.addClass(classObj);
            classesList.setAll(classService.getAllClasses());
            pagination.setPageCount(calculatePageCount());
            updateTable(pagination.getCurrentPageIndex());
        });
    }

    private void setupEnterKeyNavigation(TextField currentField, TextField nextField) {
        currentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                nextField.requestFocus();
            }
        });
    }

    // Capacity validation: must contain only numbers
    private boolean isValidCapacity(String capacity) {
        return capacity.isEmpty() || capacity.matches("^[0-9]+$");
    }

    // Grade validation: must be a number <= 20
    private boolean isValidGrade(String grade) {
        if (grade.isEmpty()) return true;
        if (!grade.matches("^[0-9]+$")) return false;
        try {
            int gradeValue = Integer.parseInt(grade);
            return gradeValue <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateForm(Dialog<Class> dialog, ButtonType addButtonType,
                              TextField nameField, TextField capacityField, Label capacityErrorLabel,
                              TextField gradeField, Label gradeErrorLabel, TextField supervisorField) {
        boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                !capacityField.getText().trim().isEmpty() &&
                !gradeField.getText().trim().isEmpty() &&
                !supervisorField.getText().trim().isEmpty();

        boolean isCapacityValid = isValidCapacity(capacityField.getText());
        boolean isGradeValid = isValidGrade(gradeField.getText());

        boolean isValid = areFieldsFilled && isCapacityValid && isGradeValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "capacity", "grade", "supervisor");
            dialog.setTitle("Filter Classes");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Classes");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Class> filteredList = FXCollections.observableArrayList();
                    for (Class classObj : classService.getAllClasses()) {
                        String fieldValue = "";
                        switch (field) {
                            case "name": fieldValue = classObj.getName(); break;
                            case "capacity": fieldValue = String.valueOf(classObj.getCapacity()); break;
                            case "grade": fieldValue = String.valueOf(classObj.getGrade()); break;
                            case "supervisor": fieldValue = classObj.getSupervisor(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(classObj);
                        }
                    }
                    classesList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "capacity", "grade", "supervisor");
            dialog.setTitle("Sort Classes");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                classesList.sort(Comparator.comparing(classObj -> {
                    switch (field) {
                        case "capacity": return String.valueOf(classObj.getCapacity());
                        case "grade": return String.valueOf(classObj.getGrade());
                        case "supervisor": return classObj.getSupervisor();
                        default: return classObj.getName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showClassDetails(Class classObj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Class Details");
        alert.setHeaderText("Class Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Class Name:"), 0, 0);
        grid.add(new Label(classObj.getName()), 1, 0);
        grid.add(new Label("Capacity:"), 0, 1);
        grid.add(new Label(String.valueOf(classObj.getCapacity())), 1, 1);
        grid.add(new Label("Grade:"), 0, 2);
        grid.add(new Label(String.valueOf(classObj.getGrade())), 1, 2);
        grid.add(new Label("Supervisor:"), 0, 3);
        grid.add(new Label(classObj.getSupervisor()), 1, 3);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteClass(Class classObj) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Class");
        confirmAlert.setContentText("Are you sure you want to delete " + classObj.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            classService.deleteClass(classObj);
            classesList.setAll(classService.getAllClasses());

            int pageCount = calculatePageCount();
            pagination.setPageCount(pageCount == 0 ? 1 : pageCount);

            int currentPage = Math.min(pagination.getCurrentPageIndex(), pageCount - 1);
            currentPage = Math.max(0, currentPage);
            pagination.setCurrentPageIndex(currentPage);

            updateTable(currentPage);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    @FXML private void loadHome() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page"); }
    @FXML private void loadTeachers() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page"); }
    @FXML private void loadStudents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page"); }
    @FXML private void loadSubjects() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page"); }
    @FXML private void loadLessons() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page"); }
    @FXML private void loadExams() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page"); }
    @FXML private void loadAssignments() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Assignments page"); }
    @FXML private void loadResults() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Results page"); }
    @FXML private void loadAttendance() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Attendance page"); }
    @FXML private void loadEvents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Events page"); }
    @FXML private void loadMessages() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Messages page"); }
    @FXML private void loadAnnouncements() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Announcements page"); }
    @FXML private void loadProfile() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Profile page"); }
    @FXML private void loadSettings() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Settings page"); }
    @FXML private void logout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Logout");
        confirmAlert.setHeaderText("Logout");
        confirmAlert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
}