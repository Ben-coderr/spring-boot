package com.example.school_managment.controllers;

import com.example.school_managment.models.Assignment;
import com.example.school_managment.services.AssignmentService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssignmentController implements Initializable {
    @FXML private TableView<Assignment> assignmentTable;
    @FXML private TableColumn<Assignment, String> nameColumn;
    @FXML private TableColumn<Assignment, String> classNameColumn;
    @FXML private TableColumn<Assignment, String> teacherColumn;
    @FXML private TableColumn<Assignment, String> dateColumn;
    @FXML private TableColumn<Assignment, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private AssignmentService assignmentService;
    private ObservableList<Assignment> assignmentsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            assignmentService = new AssignmentService();
            assignmentsList = assignmentService.getAllAssignments();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize AssignmentService: " + e.getMessage());
            assignmentsList = FXCollections.observableArrayList();
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0);
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Assignment, Void>, TableCell<Assignment, Void>> cellFactory = param -> new TableCell<>() {
            private final Button viewBtn = new Button();
            private final Button deleteBtn = new Button();

            {
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
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    showAssignmentDetails(assignment);
                });

                deleteBtn.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    deleteAssignment(assignment);
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
            return assignmentTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) assignmentsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, assignmentsList.size());
        assignmentTable.setItems(FXCollections.observableArrayList(assignmentsList.subList(fromIndex, toIndex)));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Assignment> filteredList = FXCollections.observableArrayList();
            for (Assignment assignment : assignmentService.getAllAssignments()) {
                if (assignment.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                        assignment.getClassName().toLowerCase().contains(newValue.toLowerCase()) ||
                        assignment.getTeacherName().toLowerCase().contains(newValue.toLowerCase()) ||
                        assignment.getDate().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredList.add(assignment);
                }
            }
            assignmentsList.setAll(filteredList);
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddAssignmentDialog());
    }

    private void showAddAssignmentDialog() {
        Dialog<Assignment> dialog = new Dialog<>();
        dialog.setTitle("Add New Assignment");
        dialog.setHeaderText("Enter assignment details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField classNameField = new TextField();
        TextField teacherField = new TextField();
        TextField dateField = new TextField();
        Label dateErrorLabel = new Label();
        dateErrorLabel.setStyle("-fx-text-fill: red;");
        dateErrorLabel.setVisible(false);

        grid.add(new Label("Subject_Name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classNameField, 1, 1);
        grid.add(new Label("Teacher:"), 0, 2);
        grid.add(teacherField, 1, 2);
        grid.add(new Label("Date (YYYY-MM-DD):"), 0, 3);
        grid.add(dateField, 1, 3);
        grid.add(dateErrorLabel, 2, 3);

        dialog.getDialogPane().setContent(grid);
        nameField.requestFocus();

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key navigation
        setupEnterKeyNavigation(nameField, classNameField);
        setupEnterKeyNavigation(classNameField, teacherField);
        setupEnterKeyNavigation(teacherField, dateField);

        dateField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        // Validation listeners
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(dialog, addButtonType, nameField, classNameField, teacherField, dateField, dateErrorLabel));
        classNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(dialog, addButtonType, nameField, classNameField, teacherField, dateField, dateErrorLabel));
        teacherField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(dialog, addButtonType, nameField, classNameField, teacherField, dateField, dateErrorLabel));
        dateField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (!newVal.isEmpty()) LocalDate.parse(newVal);
                dateErrorLabel.setVisible(false);
            } catch (DateTimeParseException e) {
                dateErrorLabel.setText("Invalid date format");
                dateErrorLabel.setVisible(true);
            }
            validateForm(dialog, addButtonType, nameField, classNameField, teacherField, dateField, dateErrorLabel);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Assignment(
                        nameField.getText(),
                        classNameField.getText(),
                        teacherField.getText(),
                        dateField.getText()
                );
            }
            return null;
        });

        Optional<Assignment> result = dialog.showAndWait();
        result.ifPresent(assignment -> {
            assignmentService.addAssignment(assignment);
            assignmentsList.setAll(assignmentService.getAllAssignments());
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

    private void validateForm(Dialog<Assignment> dialog, ButtonType addButtonType,
                              TextField nameField, TextField classNameField,
                              TextField teacherField, TextField dateField,
                              Label dateErrorLabel) {
        boolean fieldsFilled = !nameField.getText().trim().isEmpty() &&
                !classNameField.getText().trim().isEmpty() &&
                !teacherField.getText().trim().isEmpty() &&
                !dateField.getText().trim().isEmpty();

        boolean dateValid;
        try {
            LocalDate.parse(dateField.getText());
            dateValid = true;
        } catch (DateTimeParseException e) {
            dateValid = false;
        }

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!(fieldsFilled && dateValid));
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "class", "teacher", "date");
            dialog.setTitle("Filter Assignments");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Assignments");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");

                Optional<String> valueResult = inputDialog.showAndWait();
                valueResult.ifPresent(value -> {
                    ObservableList<Assignment> filteredList = FXCollections.observableArrayList();
                    for (Assignment assignment : assignmentService.getAllAssignments()) {
                        String fieldValue = switch (field) {
                            case "class" -> assignment.getClassName();
                            case "teacher" -> assignment.getTeacherName();
                            case "date" -> assignment.getDate();
                            default -> assignment.getName();
                        };
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(assignment);
                        }
                    }
                    assignmentsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "class", "teacher", "date");
            dialog.setTitle("Sort Assignments");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(field -> {
                assignmentsList.sort(Comparator.comparing(assignment -> {
                    switch (field) {
                        case "class": return assignment.getClassName();
                        case "teacher": return assignment.getTeacherName();
                        case "date": return assignment.getDate();
                        default: return assignment.getName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showAssignmentDetails(Assignment assignment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assignment Details");
        alert.setHeaderText("Assignment Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.addRow(0, new Label("Name:"), new Label(assignment.getName()));
        grid.addRow(1, new Label("Class:"), new Label(assignment.getClassName()));
        grid.addRow(2, new Label("Teacher:"), new Label(assignment.getTeacherName()));
        grid.addRow(3, new Label("Date:"), new Label(assignment.getDate()));

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteAssignment(Assignment assignment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Assignment");
        confirmAlert.setContentText("Are you sure you want to delete " + assignment.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            assignmentService.deleteAssignment(assignment);
            assignmentsList.setAll(assignmentService.getAllAssignments());

            int pageCount = calculatePageCount();
            pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
            int currentPage = Math.min(pagination.getCurrentPageIndex(), pageCount - 1);
            pagination.setCurrentPageIndex(Math.max(0, currentPage));

            updateTable(pagination.getCurrentPageIndex());
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
    @FXML private void loadStudents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page"); }
    @FXML private void loadSubjects() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page"); }
    @FXML private void loadClasses() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page"); }
    @FXML private void loadLessons() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page"); }
    @FXML private void loadExams() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page"); }
    @FXML private void loadTeachers() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page"); }
    @FXML private void loadResults() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Results page"); }
    @FXML private void loadAttendance() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Attendance page"); }
    @FXML private void loadEvents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Events page"); }
    @FXML private void loadMessages() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Messages page"); }
    @FXML private void loadAnnouncements() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Announcements page"); }
    @FXML private void loadProfile() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Profile page"); }
    @FXML private void loadSettings() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Settings page"); }

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