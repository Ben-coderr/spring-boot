package com.example.school_managment.controllers;

import com.example.school_managment.models.Subject;
import com.example.school_managment.services.SubjectService;
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

public class SubjectController implements Initializable {
    @FXML private TableView<Subject> subjectTable;
    @FXML private TableColumn<Subject, String> nameColumn;
    @FXML private TableColumn<Subject, String> teachersColumn;
    @FXML private TableColumn<Subject, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private SubjectService subjectService;
    private ObservableList<Subject> subjectsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            subjectService = new SubjectService();
            subjectsList = subjectService.getAllSubjects();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize SubjectService: " + e.getMessage());
            subjectsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teachersColumn.setCellValueFactory(new PropertyValueFactory<>("teachers"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Subject, Void>, TableCell<Subject, Void>> cellFactory = param -> new TableCell<>() {
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
                    Subject subject = getTableView().getItems().get(getIndex());
                    showSubjectDetails(subject);
                });

                deleteBtn.setOnAction(event -> {
                    Subject subject = getTableView().getItems().get(getIndex());
                    deleteSubject(subject);
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
            return subjectTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) subjectsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, subjectsList.size());

        if (fromIndex >= subjectsList.size()) {
            subjectTable.setItems(FXCollections.observableArrayList());
        } else {
            subjectTable.setItems(FXCollections.observableArrayList(subjectsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                subjectsList.setAll(subjectService.getAllSubjects());
            } else {
                ObservableList<Subject> filteredList = FXCollections.observableArrayList();
                for (Subject subject : subjectService.getAllSubjects()) {
                    if (subject.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            subject.getTeachers().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(subject);
                    }
                }
                subjectsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddSubjectDialog());
    }

    private void showAddSubjectDialog() {
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add New Subject");
        dialog.setHeaderText("Enter subject details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField teachersField = new TextField();

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Teachers:"), 0, 1);
        grid.add(teachersField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Enable/Disable add button depending on whether fields are filled
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key navigation
        setupEnterKeyNavigation(nameField, teachersField);

        // Make the teachers field trigger the Add button when Enter is pressed
        teachersField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        // Validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                    !teachersField.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(addButtonType).setDisable(!areFieldsFilled);
        });

        teachersField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                    !teachersField.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(addButtonType).setDisable(!areFieldsFilled);
        });

        // Convert the result to a subject when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                String teachers = teachersField.getText();
                return new Subject(name, teachers);
            }
            return null;
        });

        Optional<Subject> result = dialog.showAndWait();
        result.ifPresent(subject -> {
            subjectService.addSubject(subject);
            subjectsList.setAll(subjectService.getAllSubjects());
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

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "teachers");
            dialog.setTitle("Filter Subjects");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Subjects");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Subject> filteredList = FXCollections.observableArrayList();
                    for (Subject subject : subjectService.getAllSubjects()) {
                        String fieldValue = field.equals("name") ? subject.getName() : subject.getTeachers();
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(subject);
                        }
                    }
                    subjectsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "teachers");
            dialog.setTitle("Sort Subjects");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                subjectsList.sort(Comparator.comparing(subject -> {
                    return field.equals("name") ? subject.getName() : subject.getTeachers();
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showSubjectDetails(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subject Details");
        alert.setHeaderText("Subject Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(new Label(subject.getName()), 1, 0);
        grid.add(new Label("Teachers:"), 0, 1);
        grid.add(new Label(subject.getTeachers()), 1, 1);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteSubject(Subject subject) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Subject");
        confirmAlert.setContentText("Are you sure you want to delete " + subject.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            subjectService.deleteSubject(subject);
            subjectsList.setAll(subjectService.getAllSubjects());

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

    // Navigation methods (similar to TeacherController)
    @FXML private void loadHome() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page"); }
    @FXML private void loadTeachers() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page"); }
    @FXML private void loadStudents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page"); }
    @FXML private void loadClasses() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page"); }
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