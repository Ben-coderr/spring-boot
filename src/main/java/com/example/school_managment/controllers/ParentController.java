package com.example.school_managment.controllers;

import com.example.school_managment.models.Parent;
import com.example.school_managment.services.ParentService;
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

public class ParentController implements Initializable {
    @FXML private TableView<Parent> parentTable;
    @FXML private TableColumn<Parent, String> nameColumn;
    @FXML private TableColumn<Parent, String> infoColumn;
    @FXML private TableColumn<Parent, String> studentNamesColumn;
    @FXML private TableColumn<Parent, String> phoneColumn;
    @FXML private TableColumn<Parent, String> addressColumn;
    @FXML private TableColumn<Parent, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button filterButton;
    @FXML private Button sortButton;
    @FXML private Pagination pagination;

    private ParentService parentService;
    private ObservableList<Parent> parentsList;
    private final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            parentService = new ParentService();
            parentsList = parentService.getAllParents();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize ParentService: " + e.getMessage());
            parentsList = FXCollections.observableArrayList();
        }

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("info"));
        studentNamesColumn.setCellValueFactory(new PropertyValueFactory<>("studentNames"));
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
        Callback<TableColumn<Parent, Void>, TableCell<Parent, Void>> cellFactory = param -> new TableCell<>() {
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
                    Parent parent = getTableView().getItems().get(getIndex());
                    showParentDetails(parent);
                });

                deleteBtn.setOnAction(event -> {
                    Parent parent = getTableView().getItems().get(getIndex());
                    deleteParent(parent);
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
            return parentTable;
        });
    }

    private int calculatePageCount() {
        return (int) Math.ceil((double) parentsList.size() / ROWS_PER_PAGE);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, parentsList.size());

        if (fromIndex >= parentsList.size()) {
            parentTable.setItems(FXCollections.observableArrayList());
        } else {
            parentTable.setItems(FXCollections.observableArrayList(parentsList.subList(fromIndex, toIndex)));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                parentsList.setAll(parentService.getAllParents());
            } else {
                ObservableList<Parent> filteredList = FXCollections.observableArrayList();
                for (Parent parent : parentService.getAllParents()) {
                    if (parent.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            parent.getInfo().toLowerCase().contains(newValue.toLowerCase()) ||
                            parent.getStudentNames().toLowerCase().contains(newValue.toLowerCase()) ||
                            parent.getPhone().toLowerCase().contains(newValue.toLowerCase()) ||
                            parent.getAddress().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(parent);
                    }
                }
                parentsList.setAll(filteredList);
            }
            pagination.setPageCount(calculatePageCount());
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupAddButton() {
        addButton.setOnAction(event -> showAddParentDialog());
    }

    private void showAddParentDialog() {
        Dialog<Parent> dialog = new Dialog<>();
        dialog.setTitle("Add New Parent");
        dialog.setHeaderText("Enter parent details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField infoField = new TextField();
        TextField studentNamesField = new TextField();
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
        grid.add(new Label("Email (Info):"), 0, 1);
        grid.add(infoField, 1, 1);
        grid.add(emailErrorLabel, 2, 1);
        grid.add(new Label("Student Names:"), 0, 2);
        grid.add(studentNamesField, 1, 2);
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
        setupEnterKeyNavigation(nameField, infoField);
        setupEnterKeyNavigation(infoField, studentNamesField);
        setupEnterKeyNavigation(studentNamesField, phoneField);
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
            validateForm(dialog, addButtonType, nameField, infoField, emailErrorLabel,
                    studentNamesField, phoneField, phoneErrorLabel, addressField);
        });

        infoField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidEmail = isValidEmail(newValue);
            emailErrorLabel.setVisible(!isValidEmail && !newValue.isEmpty());
            emailErrorLabel.setText(isValidEmail ? "" : "Invalid email format");
            validateForm(dialog, addButtonType, nameField, infoField, emailErrorLabel,
                    studentNamesField, phoneField, phoneErrorLabel, addressField);
        });

        studentNamesField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, infoField, emailErrorLabel,
                    studentNamesField, phoneField, phoneErrorLabel, addressField);
        });

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValidPhone = isValidPhone(newValue);
            phoneErrorLabel.setVisible(!isValidPhone && !newValue.isEmpty());
            phoneErrorLabel.setText(isValidPhone ? "" : "Phone must contain only numbers");
            validateForm(dialog, addButtonType, nameField, infoField, emailErrorLabel,
                    studentNamesField, phoneField, phoneErrorLabel, addressField);
        });

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm(dialog, addButtonType, nameField, infoField, emailErrorLabel,
                    studentNamesField, phoneField, phoneErrorLabel, addressField);
        });

        // Convert the result to a parent when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                String info = infoField.getText();
                String studentNames = studentNamesField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();

                return new Parent(name, info, studentNames, phone, address);
            }
            return null;
        });

        Optional<Parent> result = dialog.showAndWait();
        result.ifPresent(parent -> {
            parentService.addParent(parent);
            parentsList.setAll(parentService.getAllParents());
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

    private void validateForm(Dialog<Parent> dialog, ButtonType addButtonType,
                              TextField nameField, TextField infoField, Label emailErrorLabel,
                              TextField studentNamesField, TextField phoneField, Label phoneErrorLabel,
                              TextField addressField) {
        boolean areFieldsFilled = !nameField.getText().trim().isEmpty() &&
                !infoField.getText().trim().isEmpty() &&
                !studentNamesField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !addressField.getText().trim().isEmpty();

        boolean isEmailValid = isValidEmail(infoField.getText());
        boolean isPhoneValid = isValidPhone(phoneField.getText());

        boolean isValid = areFieldsFilled && isEmailValid && isPhoneValid;

        dialog.getDialogPane().lookupButton(addButtonType).setDisable(!isValid);
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("studentNames", "name", "info", "studentNames", "phone", "address");
            dialog.setTitle("Filter Parents");
            dialog.setHeaderText("Select field to filter by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Filter Parents");
                inputDialog.setHeaderText("Enter value to filter by in " + field + ":");
                inputDialog.setContentText(field + ":");
                Optional<String> filterValue = inputDialog.showAndWait();

                filterValue.ifPresent(value -> {
                    ObservableList<Parent> filteredList = FXCollections.observableArrayList();
                    for (Parent parent : parentService.getAllParents()) {
                        String fieldValue = "";
                        switch (field) {
                            case "name": fieldValue = parent.getName(); break;
                            case "info": fieldValue = parent.getInfo(); break;
                            case "studentNames": fieldValue = parent.getStudentNames(); break;
                            case "phone": fieldValue = parent.getPhone(); break;
                            case "address": fieldValue = parent.getAddress(); break;
                        }
                        if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                            filteredList.add(parent);
                        }
                    }
                    parentsList.setAll(filteredList);
                    pagination.setPageCount(calculatePageCount());
                    updateTable(0);
                    pagination.setCurrentPageIndex(0);
                });
            });
        });
    }

    private void setupSortButton() {
        sortButton.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "info", "studentNames", "phone", "address");
            dialog.setTitle("Sort Parents");
            dialog.setHeaderText("Select field to sort by:");
            dialog.setContentText("Field:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(field -> {
                parentsList.sort(Comparator.comparing(parent -> {
                    switch (field) {
                        case "info": return parent.getInfo();
                        case "studentNames": return parent.getStudentNames();
                        case "phone": return parent.getPhone();
                        case "address": return parent.getAddress();
                        default: return parent.getName();
                    }
                }));
                updateTable(pagination.getCurrentPageIndex());
            });
        });
    }

    private void showParentDetails(Parent parent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Parent Details");
        alert.setHeaderText("Parent Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(parent.getName()), 1, 0);
        grid.add(new Label("Email (Info):"), 0, 1);
        grid.add(new Label(parent.getInfo()), 1, 1);
        grid.add(new Label("Student Names:"), 0, 2);
        grid.add(new Label(parent.getStudentNames()), 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(new Label(parent.getPhone()), 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(new Label(parent.getAddress()), 1, 4);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void deleteParent(Parent parent) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Parent");
        confirmAlert.setContentText("Are you sure you want to delete " + parent.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            parentService.deleteParent(parent);
            parentsList.setAll(parentService.getAllParents());

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