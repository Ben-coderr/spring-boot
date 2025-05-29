package com.example.school_managment.controllers;

import com.example.school_managment.models.Exam;
import com.example.school_managment.services.ExamService;
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

public class ExamController implements Initializable {
    @FXML private TableView<Exam> examTable;
    @FXML private TableColumn<Exam, String> subjectNameColumn;
    @FXML private TableColumn<Exam, String> classNameColumn;
    @FXML private TableColumn<Exam, String> teacherColumn;
    @FXML private TableColumn<Exam, String> dateColumn;
    @FXML private TableColumn<Exam, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private ExamService examService;
    private ObservableList<Exam> examsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            examService = new ExamService();
            examsList = examService.getAllExams();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize ExamService: " + e.getMessage());
            examsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>> cellFactory = param -> new TableCell<>() {
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
                    Exam exam = getTableView().getItems().get(getIndex());
                    showExamDetails(exam);
                });

                deleteBtn.setOnAction(event -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    deleteExam(exam);
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
            return examTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) examsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, examsList.size());

        if (fromIndex >= examsList.size()) {
            examTable.setItems(FXCollections.observableArrayList());
        } else {
            examTable.setItems(FXCollections.observableArrayList(examsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                examsList.setAll(examService.getAllExams());
            } else {
                ObservableList<Exam> filteredList = FXCollections.observableArrayList();
                for (Exam exam : examService.getAllExams()) {
                    if (exam.getSubjectName().toLowerCase().contains(newValue.toLowerCase()) ||
                            exam.getClassName().toLowerCase().contains(newValue.toLowerCase()) ||
                            exam.getTeacher().toLowerCase().contains(newValue.toLowerCase()) ||
                            exam.getDate().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(exam);
                    }
                }
                examsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddExamDialog());
    }

    private void showAddExamDialog() {
        Dialog<Exam> dialog = new Dialog<>();
        dialog.setTitle("Add New Exam");
        dialog.setHeaderText("Enter exam details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField subjectNameField = new TextField();
        TextField classNameField = new TextField();
        TextField teacherField = new TextField();
        TextField dateField = new TextField();

        Label dateErrorLabel = new Label();
        dateErrorLabel.setStyle("-fx-text-fill: red;");
        dateErrorLabel.setVisible(false);

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(subjectNameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classNameField, 1, 1);
        grid.add(new Label("Teacher:"), 0, 2);
        grid.add(teacherField, 1, 2);
        grid.add(new Label("Date (YYYY-MM-DD):"), 0, 3);
        grid.add(dateField, 1, 3);
        grid.add(dateErrorLabel, 2, 3);

        dialog.getDialogPane().setContent(grid);

        subjectNameField.requestFocus();

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        setupEnterKeyNavigation(subjectNameField, classNameField);
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

        subjectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField, dateField, dateErrorLabel);
        });

        classNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField, dateField, dateErrorLabel);
        });

        teacherField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField, dateField, dateErrorLabel);
        });

        dateField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidDate = isValidDate(newValue);
            dateErrorLabel.setVisible(!isValidDate && !newValue.isEmpty());
            dateErrorLabel.setText(isValidDate ? "" : "Invalid date format (YYYY-MM-DD)");
            validateForm(dialog, addButtonType, subjectNameField, classNameField, teacherField, dateField, dateErrorLabel);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String subjectName = subjectNameField.getText();
                String className = classNameField.getText();
                String teacher = teacherField.getText();
                String date = dateField.getText();

                return new Exam(subjectName, className, teacher, date);
            }
            return null;
        });

        Optional<Exam> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            examService.addExam(exam);
            examsList.setAll(examService.getAllExams());
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

    private boolean isValidDate(String date) {
        return date.isEmpty() || date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    private void validateForm(Dialog<Exam> dialog, ButtonType addButtonType,
                              TextField subjectNameField, TextField classNameField,
                              TextField teacherField, TextField dateField, Label dateErrorLabel) {
        boolean areFieldsFilled = !subjectNameField.getText().trim().isEmpty() &&
                !classNameField.getText().trim().isEmpty() &&
                !teacherField.getText().trim().isEmpty() &&
                !dateField.getText().trim().isEmpty();

        boolean isDateValid = isValidDate(dateField.getText());

        boolean isValid = areFieldsFilled && isDateValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "className", "teacher", "date");
            dialog.setTitle("Filter Exams");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Exams");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Exam> filteredList = FXCollections.observableArrayList();
                    for (Exam exam : examService.getAllExams()) {
                        String fieldValue = "";
                        switch (field) {
                            case "subjectName": fieldValue = exam.getSubjectName(); break;
                            case "className": fieldValue = exam.getClassName(); break;
                            case "teacher": fieldValue = exam.getTeacher(); break;
                            case "date": fieldValue = exam.getDate(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(exam);
                        }
                    }
                    examsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "className", "teacher", "date");
            dialog.setTitle("Sort Exams");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                examsList.sort(Comparator.comparing(exam -> {
                    switch (field) {
                        case "className": return exam.getClassName();
                        case "teacher": return exam.getTeacher();
                        case "date": return exam.getDate();
                        default: return exam.getSubjectName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showExamDetails(Exam exam) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exam Details");
        alert.setHeaderText("Exam Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(new Label(exam.getSubjectName()), 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(new Label(exam.getClassName()), 1, 1);
        grid.add(new Label("Teacher:"), 0, 2);
        grid.add(new Label(exam.getTeacher()), 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(new Label(exam.getDate()), 1, 3);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteExam(Exam exam) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Exam");
        confirmAlert.setContentText("Are you sure you want to delete the " + exam.getSubjectName() + " exam?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            examService.deleteExam(exam);
            examsList.setAll(examService.getAllExams());

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

    // Navigation methods (same as ParentController)
    @FXML private void loadHome() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page");
    }

    @FXML private void loadStudents() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page");
    }

    @FXML private void loadTeachers() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page");
    }

    @FXML private void loadSubjects() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page");
    }

    @FXML private void loadClasses() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page");
    }

    @FXML private void loadLessons() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page");
    }

    @FXML private void loadExams() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page");
    }

    @FXML private void loadAssignments() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Assignments page");
    }

    @FXML private void loadResults() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Results page");
    }

    @FXML private void loadAttendance() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Attendance page");
    }

    @FXML private void loadEvents() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Events page");
    }

    @FXML private void loadMessages() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Messages page");
    }

    @FXML private void loadAnnouncements() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Announcements page");
    }

    @FXML private void loadProfile() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Profile page");
    }

    @FXML private void loadSettings() {
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Settings page");
    }

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