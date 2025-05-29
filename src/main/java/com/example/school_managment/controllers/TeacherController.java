package com.example.school_managment.controllers;

import com.example.school_managment.models.Teacher;
import com.example.school_managment.services.TeacherService;
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

public class TeacherController implements Initializable {
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> nameColumn;
    @FXML private TableColumn<Teacher, String> idColumn;
    @FXML private TableColumn<Teacher, String> emailColumn;
    @FXML private TableColumn<Teacher, String> subjectsColumn;
    @FXML private TableColumn<Teacher, String> classesColumn;
    @FXML private TableColumn<Teacher, String> phoneColumn;
    @FXML private TableColumn<Teacher, String> addressColumn;
    @FXML private TableColumn<Teacher, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private TeacherService teacherService;
    private ObservableList<Teacher> teachersList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            teacherService = new TeacherService();
            teachersList = teacherService.getAllTeachers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize TeacherService: " + e.getMessage());
            teachersList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        subjectsColumn.setCellValueFactory(new PropertyValueFactory<>("subjects"));
        classesColumn.setCellValueFactory(new PropertyValueFactory<>("classes"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Teacher, Void>, TableCell<Teacher, Void>> cellFactory = param -> new TableCell<>() {
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
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    showTeacherDetails(teacher);
                });

                deleteBtn.setOnAction(event -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    deleteTeacher(teacher);
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
            return teacherTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) teachersList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, teachersList.size());

        if (fromIndex >= teachersList.size()) {
            teacherTable.setItems(FXCollections.observableArrayList());
        } else {
            teacherTable.setItems(FXCollections.observableArrayList(teachersList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                teachersList.setAll(teacherService.getAllTeachers());
            } else {
                ObservableList<Teacher> filteredList = FXCollections.observableArrayList();
                for (Teacher teacher : teacherService.getAllTeachers()) {
                    if (teacher.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getId().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getEmail().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getSubjects().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getClasses().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getPhone().toLowerCase().contains(newValue.toLowerCase()) ||
                            teacher.getAddress().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(teacher);
                    }
                }
                teachersList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddTeacherDialog());
    }

    private void showAddTeacherDialog() {
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Add New Teacher");
        dialog.setHeaderText("Enter teacher details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField idField = new TextField();
        TextField emailField = new TextField();
        TextField subjectsField = new TextField();
        TextField classesField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();

        // Set Field error labels
        Label emailErrorLabel = new Label();
        emailErrorLabel.setStyle("-fx-text-fill: red;");
        emailErrorLabel.setVisible(false);

        Label phoneErrorLabel = new Label();
        phoneErrorLabel.setStyle("-fx-text-fill: red;");
        phoneErrorLabel.setVisible(false);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Teacher ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(emailErrorLabel, 2, 2);
        grid.add(new Label("Subjects:"), 0, 3);
        grid.add(subjectsField, 1, 3);
        grid.add(new Label("Classes:"), 0, 4);
        grid.add(classesField, 1, 4);
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(phoneErrorLabel, 2, 5);
        grid.add(new Label("Address:"), 0, 6);
        grid.add(addressField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Enable/Disable add button depending on whether fields are filled
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key press to move to next field
        setupEnterKeyNavigation(nameField, idField);
        setupEnterKeyNavigation(idField, emailField);
        setupEnterKeyNavigation(emailField, subjectsField);
        setupEnterKeyNavigation(subjectsField, classesField);
        setupEnterKeyNavigation(classesField, phoneField);
        setupEnterKeyNavigation(phoneField, addressField);

        // Make the address field trigger the Add button when Enter is pressed
        addressField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        // Validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        idField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidEmail = isValidEmail(newValue);
            emailErrorLabel.setVisible(!isValidEmail && !newValue.isEmpty());
            emailErrorLabel.setText(isValidEmail ? "" : "Invalid email format");
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        subjectsField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        classesField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidPhone = isValidPhone(newValue);
            phoneErrorLabel.setVisible(!isValidPhone && !newValue.isEmpty());
            phoneErrorLabel.setText(isValidPhone ? "" : "Phone must contain only numbers");
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, idField, emailField, emailErrorLabel,
                    subjectsField, classesField, phoneField, phoneErrorLabel, addressField);
        });

        // Convert the result to a teacher when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                String id = idField.getText();
                String email = emailField.getText();
                String subjects = subjectsField.getText();
                String classes = classesField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();

                return new Teacher(name, id, email, subjects, classes, phone, address);
            }
            return null;
        });

        Optional<Teacher> result = dialog.showAndWait();
        result.ifPresent(teacher -> {
            teacherService.addTeacher(teacher);
            teachersList.setAll(teacherService.getAllTeachers());
            pagination.setPageCount(calculatePageCount());
            updateTable(pagination.getCurrentPageIndex());
        });
    }

    // Helper method to setup Enter key navigation between fields
    private void setupEnterKeyNavigation(TextField currentField, TextField nextField) {
        currentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                nextField.requestFocus();
            }
        });
    }

    // Email validation method with simpler regex
    private boolean isValidEmail(String email) {
        return email.isEmpty() || email.matches("^.+@.+\\..+$");
    }

    // Phone validation method
    private boolean isValidPhone(String phone) {
        return phone.isEmpty() || phone.matches("^[0-9]+$");
    }

    private void validateForm(Dialog<Teacher> dialog, ButtonType addButtonType,
                              TextField nameField, TextField idField,
                              TextField emailField, Label emailErrorLabel,
                              TextField subjectsField, TextField classesField,
                              TextField phoneField, Label phoneErrorLabel,
                              TextField addressField) {
        boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                !idField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !subjectsField.getText().trim().isEmpty() &&
                !classesField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !addressField.getText().trim().isEmpty();

        boolean isEmailValid = isValidEmail(emailField.getText());
        boolean isPhoneValid = isValidPhone(phoneField.getText());

        boolean isValid = areFieldsFilled && isEmailValid && isPhoneValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjects", "name", "id", "email", "subjects", "classes", "phone", "address");
            dialog.setTitle("Filter Teachers");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Teachers");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Teacher> filteredList = FXCollections.observableArrayList();
                    for (Teacher teacher : teacherService.getAllTeachers()) {
                        String fieldValue = "";
                        switch (field) {
                            case "name": fieldValue = teacher.getName(); break;
                            case "id": fieldValue = teacher.getId(); break;
                            case "email": fieldValue = teacher.getEmail(); break;
                            case "subjects": fieldValue = teacher.getSubjects(); break;
                            case "classes": fieldValue = teacher.getClasses(); break;
                            case "phone": fieldValue = teacher.getPhone(); break;
                            case "address": fieldValue = teacher.getAddress(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(teacher);
                        }
                    }
                    teachersList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "id", "email", "subjects", "classes", "phone", "address");
            dialog.setTitle("Sort Teachers");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                teachersList.sort(Comparator.comparing(teacher -> {
                    switch (field) {
                        case "id": return teacher.getId();
                        case "email": return teacher.getEmail();
                        case "subjects": return teacher.getSubjects();
                        case "classes": return teacher.getClasses();
                        case "phone": return teacher.getPhone();
                        case "address": return teacher.getAddress();
                        default: return teacher.getName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showTeacherDetails(Teacher teacher) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Teacher Details");
        alert.setHeaderText("Teacher Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(teacher.getName()), 1, 0);
        grid.add(new Label("Teacher ID:"), 0, 1);
        grid.add(new Label(teacher.getId()), 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(teacher.getEmail()), 1, 2);
        grid.add(new Label("Subjects:"), 0, 3);
        grid.add(new Label(teacher.getSubjects()), 1, 3);
        grid.add(new Label("Classes:"), 0, 4);
        grid.add(new Label(teacher.getClasses()), 1, 4);
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(new Label(teacher.getPhone()), 1, 5);
        grid.add(new Label("Address:"), 0, 6);
        grid.add(new Label(teacher.getAddress()), 1, 6);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteTeacher(Teacher teacher) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Teacher");
        confirmAlert.setContentText("Are you sure you want to delete " + teacher.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            teacherService.deleteTeacher(teacher);
            teachersList.setAll(teacherService.getAllTeachers());

            // Recalculate page count
            int pageCount = calculatePageCount();
            pagination.setPageCount(pageCount == 0 ? 1 : pageCount);

            // Ensure current page index is valid
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
    @FXML
    private void loadHome() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page");
    }

    @FXML
    private void loadStudents() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page");
    }

    @FXML
    private void loadSubjects() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page");
    }

    @FXML
    private void loadClasses() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page");
    }

    @FXML
    private void loadLessons() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page");
    }

    @FXML
    private void loadExams() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page");
    }

    @FXML
    private void loadAssignments() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Assignments page");
    }

    @FXML
    private void loadResults() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Results page");
    }

    @FXML
    private void loadAttendance() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Attendance page");
    }

    @FXML
    private void loadEvents() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Events page");
    }

    @FXML
    private void loadMessages() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Messages page");
    }

    @FXML
    private void loadAnnouncements() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Announcements page");
    }

    @FXML
    private void loadProfile() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Profile page");
    }

    @FXML
    private void loadSettings() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Settings page");
    }

    @FXML
    private void logout() {
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