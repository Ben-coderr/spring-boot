package com.example.school_managment.controllers;

import com.example.school_managment.models.Student;
import com.example.school_managment.services.StudentService;
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
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentController implements Initializable {
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, Integer> gradeColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, String> addressColumn;
    @FXML private TableColumn<Student, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private StudentService studentService;
    private ObservableList<Student> studentsList;
    private final int ROWS_PER_PAGE = 10;
    private Student lastDeletedStudent = null; // Track the last deleted student

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            studentService = new StudentService();
            studentsList = studentService.getAllStudents();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize StudentService: " + e.getMessage());
            studentsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
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
        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = param -> new TableCell<>() {
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
                    Student student = getTableView().getItems().get(getIndex());
                    showStudentDetails(student);
                });

                deleteBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    deleteStudent(student);
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
            return studentTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) studentsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, studentsList.size());

        // Ensure fromIndex is not negative
        fromIndex = Math.max(0, fromIndex);

        if (fromIndex < studentsList.size()) {
            studentTable.setItems(FXCollections.observableArrayList(studentsList.subList(fromIndex, toIndex)));
        } else {
            studentTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                studentsList.setAll(studentService.getAllStudents());
            } else {
                ObservableList<Student> filteredList = FXCollections.observableArrayList();
                for (Student student : studentService.getAllStudents()) {
                    if (student.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            student.getId().toLowerCase().contains(newValue.toLowerCase()) ||
                            String.valueOf(student.getGrade()).contains(newValue) ||
                            student.getPhone().toLowerCase().contains(newValue.toLowerCase()) ||
                            student.getAddress().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(student);
                    }
                }
                studentsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddStudentDialog());
    }

    private void showAddStudentDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student details");

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
        TextField gradeField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();

        // Error labels
        Label gradeErrorLabel = new Label();
        gradeErrorLabel.setStyle("-fx-text-fill: red;");
        gradeErrorLabel.setVisible(false);

        Label phoneErrorLabel = new Label();
        phoneErrorLabel.setStyle("-fx-text-fill: red;");
        phoneErrorLabel.setVisible(false);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Grade:"), 0, 2);
        grid.add(gradeField, 1, 2);
        grid.add(gradeErrorLabel, 2, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(phoneErrorLabel, 2, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Enable/Disable add button depending on whether fields are filled
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key press to move to next field
        setupEnterKeyNavigation(nameField, idField);
        setupEnterKeyNavigation(idField, gradeField);
        setupEnterKeyNavigation(gradeField, phoneField);
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
            validateStudentForm(dialog, addButtonType, nameField, idField, gradeField, gradeErrorLabel,
                    phoneField, phoneErrorLabel, addressField);
        });

        idField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateStudentForm(dialog, addButtonType, nameField, idField, gradeField, gradeErrorLabel,
                    phoneField, phoneErrorLabel, addressField);
        });

        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidGrade = isValidGrade(newValue);
            gradeErrorLabel.setVisible(!isValidGrade && !newValue.isEmpty());
            gradeErrorLabel.setText(!newValue.isEmpty() && !isNumeric(newValue) ? "Grade must be a number" :
                    !isValidGrade ? "Grade must be less than 20" : "");
            validateStudentForm(dialog, addButtonType, nameField, idField, gradeField, gradeErrorLabel,
                    phoneField, phoneErrorLabel, addressField);
        });

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidPhone = isValidPhone(newValue);
            phoneErrorLabel.setVisible(!isValidPhone && !newValue.isEmpty());
            phoneErrorLabel.setText(isValidPhone ? "" : "Phone must contain only numbers");
            validateStudentForm(dialog, addButtonType, nameField, idField, gradeField, gradeErrorLabel,
                    phoneField, phoneErrorLabel, addressField);
        });

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateStudentForm(dialog, addButtonType, nameField, idField, gradeField, gradeErrorLabel,
                    phoneField, phoneErrorLabel, addressField);
        });

        // Convert the result to a student when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    String id = idField.getText();
                    int grade = Integer.parseInt(gradeField.getText());
                    String phone = phoneField.getText();
                    String address = addressField.getText();

                    return new Student(name, id, grade, phone, address);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid grade number.");
                    return null;
                }
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            studentService.addStudent(student);
            studentsList.setAll(studentService.getAllStudents());
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

    // Grade validation method
    private boolean isValidGrade(String grade) {
        if (!isNumeric(grade)) return false;
        try {
            int gradeValue = Integer.parseInt(grade);
            return gradeValue < 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if string is numeric
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("^[0-9]+$");
    }

    // Phone validation method
    private boolean isValidPhone(String phone) {
        return phone.isEmpty() || phone.matches("^[0-9]+$");
    }

    private void validateStudentForm(Dialog<Student> dialog, ButtonType addButtonType,
                                     TextField nameField, TextField idField,
                                     TextField gradeField, Label gradeErrorLabel,
                                     TextField phoneField, Label phoneErrorLabel,
                                     TextField addressField) {
        boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                !idField.getText().trim().isEmpty() &&
                !gradeField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !addressField.getText().trim().isEmpty();

        boolean isGradeValid = isValidGrade(gradeField.getText());
        boolean isPhoneValid = isValidPhone(phoneField.getText());

        boolean isValid = areFieldsFilled && isGradeValid && isPhoneValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Filter Students");
            dialog.setHeaderText("Enter grade to filter by:");
            dialog.setContentText("Grade:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(grade -> {
                try {
                    int filterGrade = Integer.parseInt(grade);
                    ObservableList<Student> filteredList = FXCollections.observableArrayList();
                    for (Student student : studentService.getAllStudents()) {
                        if (student.getGrade() == filterGrade) {
                            filteredList.add(student);
                        }
                    }
                    studentsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid grade number.");
                }
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "id", "grade");
            dialog.setTitle("Sort Students");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                studentsList.sort(Comparator.comparing(student -> {
                    switch (field) {
                        case "id": return student.getId();
                        case "grade": return String.valueOf(student.getGrade());
                        default: return student.getName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showStudentDetails(Student student) {
        // Basic information dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Student Details");
        dialog.setHeaderText("Basic Student Information");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(student.getName()), 1, 0);
        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(new Label(student.getId()), 1, 1);
        grid.add(new Label("Grade:"), 0, 2);
        grid.add(new Label(String.valueOf(student.getGrade())), 1, 2);

        Button moreInfoButton = new Button("More Info");
        grid.add(moreInfoButton, 1, 3);

        VBox contentVBox = new VBox(10, grid);
        dialog.getDialogPane().setContent(contentVBox);

        // Handle Enter key for showing more info
        dialog.getDialogPane().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                moreInfoButton.fire();
            }
        });

        // More information button action
        moreInfoButton.setOnAction(event -> {
            // Change dialog content to show all information
            GridPane expandedGrid = new GridPane();
            expandedGrid.setHgap(10);
            expandedGrid.setVgap(10);
            expandedGrid.setPadding(new Insets(20, 150, 10, 10));

            expandedGrid.add(new Label("Name:"), 0, 0);
            expandedGrid.add(new Label(student.getName()), 1, 0);
            expandedGrid.add(new Label("Student ID:"), 0, 1);
            expandedGrid.add(new Label(student.getId()), 1, 1);
            expandedGrid.add(new Label("Grade:"), 0, 2);
            expandedGrid.add(new Label(String.valueOf(student.getGrade())), 1, 2);
            expandedGrid.add(new Label("Phone:"), 0, 3);
            expandedGrid.add(new Label(student.getPhone()), 1, 3);
            expandedGrid.add(new Label("Address:"), 0, 4);
            expandedGrid.add(new Label(student.getAddress()), 1, 4);

            dialog.setHeaderText("Complete Student Information");
            contentVBox.getChildren().clear();
            contentVBox.getChildren().add(expandedGrid);

            // Remove the button since all info is now shown
            moreInfoButton.setVisible(false);
        });

        dialog.showAndWait();
    }

    private void deleteStudent(Student student) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Student");
        confirmAlert.setContentText("Are you sure you want to delete " + student.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the student from the service
            studentService.deleteStudent(student);

            // Update the full list
            studentsList.setAll(studentService.getAllStudents());

            // Calculate the new page count
            int newPageCount = calculatePageCount();

            // Adjust the current page index if needed
            int currentPageIndex = pagination.getCurrentPageIndex();
            if (currentPageIndex >= newPageCount && newPageCount > 0) {
                currentPageIndex = newPageCount - 1;
            } else if (newPageCount == 0) {
                currentPageIndex = 0;
            }

            // Update pagination and table
            pagination.setPageCount(Math.max(newPageCount, 1));
            pagination.setCurrentPageIndex(currentPageIndex);
            updateTable(currentPageIndex);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods (for consistency, though not used in student-view.fxml)
    @FXML
    private void loadHome() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page");
    }

    @FXML
    private void loadTeachers() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page");
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