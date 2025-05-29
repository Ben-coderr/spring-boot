package com.example.school_managment.controllers;

import com.example.school_managment.models.Lesson;
import com.example.school_managment.services.LessonService;
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

public class LessonController implements Initializable {
    @FXML private TableView<Lesson> lessonTable;
    @FXML private TableColumn<Lesson, String> subjectNameColumn;
    @FXML private TableColumn<Lesson, String> classNameColumn;
    @FXML private TableColumn<Lesson, String> teacherColumn;
    @FXML private TableColumn<Lesson, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private LessonService lessonService;
    private ObservableList<Lesson> lessonsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            lessonService = new LessonService();
            lessonsList = lessonService.getAllLessons();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize LessonService: " + e.getMessage());
            lessonsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Lesson, Void>, TableCell<Lesson, Void>> cellFactory = param -> new TableCell<>() {
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
                    Lesson lesson = getTableView().getItems().get(getIndex());
                    showLessonDetails(lesson);
                });

                deleteBtn.setOnAction(event -> {
                    Lesson lesson = getTableView().getItems().get(getIndex());
                    deleteLesson(lesson);
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
            return lessonTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) lessonsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, lessonsList.size());

        if (fromIndex >= lessonsList.size()) {
            lessonTable.setItems(FXCollections.observableArrayList());
        } else {
            lessonTable.setItems(FXCollections.observableArrayList(lessonsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                lessonsList.setAll(lessonService.getAllLessons());
            } else {
                ObservableList<Lesson> filteredList = FXCollections.observableArrayList();
                for (Lesson lesson : lessonService.getAllLessons()) {
                    if (lesson.getSubjectName().toLowerCase().contains(newValue.toLowerCase()) ||
                            lesson.getClassName().toLowerCase().contains(newValue.toLowerCase()) ||
                            lesson.getTeacher().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(lesson);
                    }
                }
                lessonsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddLessonDialog());
    }

    private void showAddLessonDialog() {
        Dialog<Lesson> dialog = new Dialog<>();
        dialog.setTitle("Add New Lesson");
        dialog.setHeaderText("Enter lesson details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField subjectNameField = new TextField();
        TextField classNameField = new TextField();
        TextField teacherField = new TextField();

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(subjectNameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classNameField, 1, 1);
        grid.add(new Label("Teacher:"), 0, 2);
        grid.add(teacherField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the subject name field by default
        subjectNameField.requestFocus();

        // Enable/Disable add button depending on whether fields are filled
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Setup Enter key navigation
        setupEnterKeyNavigation(subjectNameField, classNameField);
        setupEnterKeyNavigation(classNameField, teacherField);

        // Make the teacher field trigger the Add button when Enter is pressed
        teacherField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        // Validation
        subjectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField);
        });

        classNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField);
        });

        teacherField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField);
        });

        // Convert the result to a lesson when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String subjectName = subjectNameField.getText();
                String className = classNameField.getText();
                String teacher = teacherField.getText();
                return new Lesson(subjectName, className, teacher);
            }
            return null;
        });

        Optional<Lesson> result = dialog.showAndWait();
        result.ifPresent(lesson -> {
            lessonService.addLesson(lesson);
            lessonsList.setAll(lessonService.getAllLessons());
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

    private void validateForm(Dialog<Lesson> dialog, ButtonType addButtonType,
                              TextField subjectNameField, TextField classNameField, TextField teacherField) {
        boolean areFieldsFilled = !subjectNameField.getText().trim().isEmpty() &&
                !classNameField.getText().trim().isEmpty() &&
                !teacherField.getText().trim().isEmpty();

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!areFieldsFilled);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "className", "teacher");
            dialog.setTitle("Filter Lessons");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Lessons");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Lesson> filteredList = FXCollections.observableArrayList();
                    for (Lesson lesson : lessonService.getAllLessons()) {
                        String fieldValue = "";
                        switch (field) {
                            case "subjectName": fieldValue = lesson.getSubjectName(); break;
                            case "className": fieldValue = lesson.getClassName(); break;
                            case "teacher": fieldValue = lesson.getTeacher(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(lesson);
                        }
                    }
                    lessonsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "className", "teacher");
            dialog.setTitle("Sort Lessons");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                lessonsList.sort(Comparator.comparing(lesson -> {
                    switch (field) {
                        case "className": return lesson.getClassName();
                        case "teacher": return lesson.getTeacher();
                        default: return lesson.getSubjectName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showLessonDetails(Lesson lesson) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lesson Details");
        alert.setHeaderText("Lesson Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(new Label(lesson.getSubjectName()), 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(new Label(lesson.getClassName()), 1, 1);
        grid.add(new Label("Teacher:"), 0, 2);
        grid.add(new Label(lesson.getTeacher()), 1, 2);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteLesson(Lesson lesson) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Lesson");
        confirmAlert.setContentText("Are you sure you want to delete the lesson for " + lesson.getSubjectName() + " in " + lesson.getClassName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            lessonService.deleteLesson(lesson);
            lessonsList.setAll(lessonService.getAllLessons());

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
    @FXML private void loadClasses() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page"); }
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