package com.example.school_managment.controllers;

import com.example.school_managment.models.Result;
import com.example.school_managment.services.ResultService;
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
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class ResultController implements Initializable {
    @FXML private TableView<Result> resultTable;
    @FXML private TableColumn<Result, String> subjectNameColumn;
    @FXML private TableColumn<Result, String> studentColumn;
    @FXML private TableColumn<Result, Integer> scoreColumn;
    @FXML private TableColumn<Result, String> teacherColumn;
    @FXML private TableColumn<Result, String> classColumn;
    @FXML private TableColumn<Result, LocalDate> dateColumn;
    @FXML private TableColumn<Result, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private ResultService resultService;
    private ObservableList<Result> resultsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            resultService = new ResultService();
            resultsList = resultService.getAllResults();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize ResultService: " + e.getMessage());
            resultsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
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
        Callback<TableColumn<Result, Void>, TableCell<Result, Void>> cellFactory = param -> new TableCell<>() {
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
                    Result result = getTableView().getItems().get(getIndex());
                    showResultDetails(result);
                });

                deleteBtn.setOnAction(event -> {
                    Result result = getTableView().getItems().get(getIndex());
                    deleteResult(result);
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
            return resultTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) resultsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, resultsList.size());

        if (fromIndex >= resultsList.size()) {
            resultTable.setItems(FXCollections.observableArrayList());
        } else {
            resultTable.setItems(FXCollections.observableArrayList(resultsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                resultsList.setAll(resultService.getAllResults());
            } else {
                ObservableList<Result> filteredList = FXCollections.observableArrayList();
                for (Result result : resultService.getAllResults()) {
                    if (result.getSubjectName().toLowerCase().contains(newValue.toLowerCase()) ||
                            result.getStudent().toLowerCase().contains(newValue.toLowerCase()) ||
                            String.valueOf(result.getScore()).contains(newValue) ||
                            result.getTeacher().toLowerCase().contains(newValue.toLowerCase()) ||
                            result.getClassName().toLowerCase().contains(newValue.toLowerCase()) ||
                            result.getDate().toString().contains(newValue)) {
                        filteredList.add(result);
                    }
                }
                resultsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddResultDialog());
    }

    private void showAddResultDialog() {
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Add New Result");
        dialog.setHeaderText("Enter result details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField subjectNameField = new TextField();
        TextField studentField = new TextField();
        TextField scoreField = new TextField();
        TextField teacherField = new TextField();
        TextField classField = new TextField();
        DatePicker dateField = new DatePicker();

        Label scoreErrorLabel = new Label();
        scoreErrorLabel.setStyle("-fx-text-fill: red;");
        scoreErrorLabel.setVisible(false);

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(subjectNameField, 1, 0);
        grid.add(new Label("Student:"), 0, 1);
        grid.add(studentField, 1, 1);
        grid.add(new Label("Score:"), 0, 2);
        grid.add(scoreField, 1, 2);
        grid.add(scoreErrorLabel, 2, 2);
        grid.add(new Label("Teacher:"), 0, 3);
        grid.add(teacherField, 1, 3);
        grid.add(new Label("Class:"), 0, 4);
        grid.add(classField, 1, 4);
        grid.add(new Label("Date:"), 0, 5);
        grid.add(dateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        subjectNameField.requestFocus();

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        setupEnterKeyNavigation(subjectNameField, studentField);
        setupEnterKeyNavigation(studentField, scoreField);
        setupEnterKeyNavigation(scoreField, teacherField);
        setupEnterKeyNavigation(teacherField, classField);
        setupEnterKeyNavigation(classField, dateField);

        dateField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        subjectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        studentField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidScore = isValidScore(newValue);
            scoreErrorLabel.setVisible(!isValidScore && !newValue.isEmpty());
            scoreErrorLabel.setText(isValidScore ? "" : "Score must be a number between 0 and 100");
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        teacherField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        classField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, subjectNameField, studentField, scoreField, scoreErrorLabel,
                    teacherField, classField, dateField);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String subjectName = subjectNameField.getText();
                String student = studentField.getText();
                int score = Integer.parseInt(scoreField.getText());
                String teacher = teacherField.getText();
                String className = classField.getText();
                LocalDate date = dateField.getValue();

                return new Result(subjectName, student, score, teacher, className, date);
            }
            return null;
        });

        Optional<Result> result = dialog.showAndWait();
        result.ifPresent(newResult -> {
            resultService.addResult(newResult);
            resultsList.setAll(resultService.getAllResults());
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

    private void setupEnterKeyNavigation(TextField currentField, DatePicker nextField) {
        currentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                nextField.requestFocus();
            }
        });
    }

    private boolean isValidScore(String score) {
        if (score.isEmpty()) return true;
        try {
            int value = Integer.parseInt(score);
            return value >= 0 && value <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateForm(Dialog<Result> dialog, ButtonType addButtonType,
                              TextField subjectNameField, TextField studentField, TextField scoreField, Label scoreErrorLabel,
                              TextField teacherField, TextField classField, DatePicker dateField) {
        boolean areFieldsFilled = !subjectNameField.getText().trim().isEmpty() &&
                !studentField.getText().trim().isEmpty() &&
                !scoreField.getText().trim().isEmpty() &&
                !teacherField.getText().trim().isEmpty() &&
                !classField.getText().trim().isEmpty() &&
                dateField.getValue() != null;

        boolean isScoreValid = isValidScore(scoreField.getText());

        boolean isValid = areFieldsFilled && isScoreValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "student", "score", "teacher", "className", "date");
            dialog.setTitle("Filter Results");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> selectedField = dialog.showAndWait();

            selectedField.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Results");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Result> filteredList = FXCollections.observableArrayList();
                    for (Result res : resultService.getAllResults()) {
                        String fieldValue = "";
                        switch (field) {
                            case "subjectName": fieldValue = res.getSubjectName(); break;
                            case "student": fieldValue = res.getStudent(); break;
                            case "score": fieldValue = String.valueOf(res.getScore()); break;
                            case "teacher": fieldValue = res.getTeacher(); break;
                            case "className": fieldValue = res.getClassName(); break;
                            case "date": fieldValue = res.getDate().toString(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(res);
                        }
                    }
                    resultsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("subjectName", "subjectName", "student", "score", "teacher", "className", "date");
            dialog.setTitle("Sort Results");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> selectedField = dialog.showAndWait();

            selectedField.ifPresent(field -> {
                resultsList.sort(Comparator.comparing(res -> {
                    switch (field) {
                        case "student": return res.getStudent();
                        case "score": return String.valueOf(res.getScore());
                        case "teacher": return res.getTeacher();
                        case "className": return res.getClassName();
                        case "date": return res.getDate().toString();
                        default: return res.getSubjectName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showResultDetails(Result result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result Details");
        alert.setHeaderText("Result Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(new Label(result.getSubjectName()), 1, 0);
        grid.add(new Label("Student:"), 0, 1);
        grid.add(new Label(result.getStudent()), 1, 1);
        grid.add(new Label("Score:"), 0, 2);
        grid.add(new Label(String.valueOf(result.getScore())), 1, 2);
        grid.add(new Label("Teacher:"), 0, 3);
        grid.add(new Label(result.getTeacher()), 1, 3);
        grid.add(new Label("Class:"), 0, 4);
        grid.add(new Label(result.getClassName()), 1, 4);
        grid.add(new Label("Date:"), 0, 5);
        grid.add(new Label(result.getDate().toString()), 1, 5);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteResult(Result result) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Result");
        confirmAlert.setContentText("Are you sure you want to delete the result for " + result.getSubjectName() + " of " + result.getStudent() + "?");

        Optional<ButtonType> resultOpt = confirmAlert.showAndWait();
        if (resultOpt.isPresent() && resultOpt.get() == ButtonType.OK) {
            resultService.deleteResult(result);
            resultsList.setAll(resultService.getAllResults());

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
    @FXML private void loadHome() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page"); }
    @FXML private void loadStudents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page"); }
    @FXML private void loadTeachers() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page"); }
    @FXML private void loadParents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Parents page"); }
    @FXML private void loadSubjects() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page"); }
    @FXML private void loadClasses() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page"); }
    @FXML private void loadLessons() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page"); }
    @FXML private void loadExams() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page"); }
    @FXML private void loadAssignments() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Assignments page"); }
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