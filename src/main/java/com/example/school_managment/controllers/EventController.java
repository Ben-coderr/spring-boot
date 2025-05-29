package com.example.school_managment.controllers;

import com.example.school_managment.models.Event;
import com.example.school_managment.services.EventService;
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
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventController implements Initializable {
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> classColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private TableColumn<Event, LocalTime> startTimeColumn;
    @FXML private TableColumn<Event, LocalTime> endTimeColumn;
    @FXML private TableColumn<Event, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private EventService eventService;
    private ObservableList<Event> eventsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            eventService = new EventService();
            eventsList = eventService.getAllEvents();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize EventService: " + e.getMessage());
            eventsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        setupActionsColumn();
        setupPagination();
        setupSearch();
        setupAddButton();
        setupFilterButton();
        setupSortButton();

        updateTable(0); // Load first page
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = param -> new TableCell<>() {
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
                    Event eventObj = getTableView().getItems().get(getIndex());
                    showEventDetails(eventObj);
                });

                deleteBtn.setOnAction(event -> {
                    Event eventObj = getTableView().getItems().get(getIndex());
                    deleteEvent(eventObj);
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
            return eventTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) eventsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, eventsList.size());

        if (fromIndex >= eventsList.size()) {
            eventTable.setItems(FXCollections.observableArrayList());
        } else {
            eventTable.setItems(FXCollections.observableArrayList(eventsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                eventsList.setAll(eventService.getAllEvents());
            } else {
                ObservableList<Event> filteredList = FXCollections.observableArrayList();
                for (Event event : eventService.getAllEvents()) {
                    if (event.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                            event.getClassName().toLowerCase().contains(newValue.toLowerCase()) ||
                            event.getDate().toString().contains(newValue) ||
                            event.getStartTime().toString().contains(newValue) ||
                            event.getEndTime().toString().contains(newValue)) {
                        filteredList.add(event);
                    }
                }
                eventsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddEventDialog());
    }

    private void showAddEventDialog() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Add New Event");
        dialog.setHeaderText("Enter event details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextField classField = new TextField();
        DatePicker dateField = new DatePicker();
        TextField startTimeField = new TextField();
        TextField endTimeField = new TextField();

        Label timeFormatLabel = new Label("(HH:MM)");
        timeFormatLabel.setStyle("-fx-text-fill: gray;");
        Label startTimeErrorLabel = new Label();
        startTimeErrorLabel.setStyle("-fx-text-fill: red;");
        startTimeErrorLabel.setVisible(false);
        Label endTimeErrorLabel = new Label();
        endTimeErrorLabel.setStyle("-fx-text-fill: red;");
        endTimeErrorLabel.setVisible(false);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateField, 1, 2);
        grid.add(new Label("Start Time:"), 0, 3);
        grid.add(startTimeField, 1, 3);
        grid.add(timeFormatLabel, 2, 3);
        grid.add(startTimeErrorLabel, 1, 4);
        grid.add(new Label("End Time:"), 0, 5);
        grid.add(endTimeField, 1, 5);
        grid.add(new Label("(HH:MM)"), 2, 5);
        grid.add(endTimeErrorLabel, 1, 6);

        dialog.getDialogPane().setContent(grid);

        titleField.requestFocus();

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        setupEnterKeyNavigation(titleField, classField);
        setupEnterKeyNavigation(classField, dateField);
        setupEnterKeyNavigation(dateField, startTimeField);
        setupEnterKeyNavigation(startTimeField, endTimeField);

        endTimeField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
                if (!addButton.isDisabled()) {
                    addButton.fire();
                }
            }
        });

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, titleField, classField, dateField, startTimeField, startTimeErrorLabel, endTimeField, endTimeErrorLabel);
        });

        classField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, titleField, classField, dateField, startTimeField, startTimeErrorLabel, endTimeField, endTimeErrorLabel);
        });

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, titleField, classField, dateField, startTimeField, startTimeErrorLabel, endTimeField, endTimeErrorLabel);
        });

        startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidTime = isValidTime(newValue);
            startTimeErrorLabel.setVisible(!isValidTime && !newValue.isEmpty());
            startTimeErrorLabel.setText(isValidTime ? "" : "Invalid time format (HH:MM)");
            validateForm(dialog, addButtonType, titleField, classField, dateField, startTimeField, startTimeErrorLabel, endTimeField, endTimeErrorLabel);
        });

        endTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidTime = isValidTime(newValue);
            endTimeErrorLabel.setVisible(!isValidTime && !newValue.isEmpty());
            endTimeErrorLabel.setText(isValidTime ? "" : "Invalid time format (HH:MM)");
            validateForm(dialog, addButtonType, titleField, classField, dateField, startTimeField, startTimeErrorLabel, endTimeField, endTimeErrorLabel);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText();
                String className = classField.getText();
                LocalDate date = dateField.getValue();
                LocalTime startTime = LocalTime.parse(startTimeField.getText());
                LocalTime endTime = LocalTime.parse(endTimeField.getText());

                return new Event(title, className, date, startTime, endTime);
            }
            return null;
        });

        Optional<Event> event = dialog.showAndWait();
        event.ifPresent(newEvent -> {
            eventService.addEvent(newEvent);
            eventsList.setAll(eventService.getAllEvents());
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

    private void setupEnterKeyNavigation(DatePicker currentField, TextField nextField) {
        currentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                nextField.requestFocus();
            }
        });
    }

    private boolean isValidTime(String time) {
        if (time.isEmpty()) return true;
        try {
            LocalTime.parse(time); // Expected format: HH:MM
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateForm(Dialog<Event> dialog, ButtonType addButtonType,
                              TextField titleField, TextField classField, DatePicker dateField,
                              TextField startTimeField, Label startTimeErrorLabel,
                              TextField endTimeField, Label endTimeErrorLabel) {
        boolean areFieldsFilled = !titleField.getText().trim().isEmpty() &&
                !classField.getText().trim().isEmpty() &&
                dateField.getValue() != null &&
                !startTimeField.getText().trim().isEmpty() &&
                !endTimeField.getText().trim().isEmpty();

        boolean isStartTimeValid = isValidTime(startTimeField.getText());
        boolean isEndTimeValid = isValidTime(endTimeField.getText());

        boolean isValid = areFieldsFilled && isStartTimeValid && isEndTimeValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("title", "title", "className", "date", "startTime", "endTime");
            dialog.setTitle("Filter Events");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> selectedField = dialog.showAndWait();

            selectedField.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Events");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Event> filteredList = FXCollections.observableArrayList();
                    for (Event evt : eventService.getAllEvents()) {
                        String fieldValue = "";
                        switch (field) {
                            case "title": fieldValue = evt.getTitle(); break;
                            case "className": fieldValue = evt.getClassName(); break;
                            case "date": fieldValue = evt.getDate().toString(); break;
                            case "startTime": fieldValue = evt.getStartTime().toString(); break;
                            case "endTime": fieldValue = evt.getEndTime().toString(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(evt);
                        }
                    }
                    eventsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("title", "title", "className", "date", "startTime", "endTime");
            dialog.setTitle("Sort Events");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> selectedField = dialog.showAndWait();

            selectedField.ifPresent(field -> {
                eventsList.sort(Comparator.comparing(evt -> {
                    switch (field) {
                        case "className": return evt.getClassName();
                        case "date": return evt.getDate().toString();
                        case "startTime": return evt.getStartTime().toString();
                        case "endTime": return evt.getEndTime().toString();
                        default: return evt.getTitle();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showEventDetails(Event event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Event Details");
        alert.setHeaderText("Event Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(new Label(event.getTitle()), 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(new Label(event.getClassName()), 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(new Label(event.getDate().toString()), 1, 2);
        grid.add(new Label("Start Time:"), 0, 3);
        grid.add(new Label(event.getStartTime().toString()), 1, 3);
        grid.add(new Label("End Time:"), 0, 4);
        grid.add(new Label(event.getEndTime().toString()), 1, 4);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteEvent(Event event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Event");
        confirmAlert.setContentText("Are you sure you want to delete the event " + event.getTitle() + " for class " + event.getClassName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventService.deleteEvent(event);
            eventsList.setAll(eventService.getAllEvents());

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

    // Navigation methods (same as ResultController)
    @FXML private void loadHome() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Home page"); }
    @FXML private void loadStudents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Students page"); }
    @FXML private void loadTeachers() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Teachers page"); }
    @FXML private void loadParents() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Parents page"); }
    @FXML private void loadSubjects() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Subjects page"); }
    @FXML private void loadClasses() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Classes page"); }
    @FXML private void loadLessons() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Lessons page"); }
    @FXML private void loadExams() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Exams page"); }
    @FXML private void loadAssignments() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Assignments page"); }
    @FXML private void loadResults() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Results page"); }
    @FXML private void loadAttendance() { showAlert(Alert.AlertType.INFORMATION, "Navigation", "Navigating to Attendance page"); }
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