package idatt1002_2021_k1_08;

import idatt1002_2021_k1_08.datamodel.FileHandler;
import idatt1002_2021_k1_08.datamodel.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class TaskController {

    @FXML
    Button add_task_button;
    @FXML
    Button edit_task_button;
    @FXML
    Button delete_task_button;
    @FXML
    Button newCategory;
    @FXML
    Button complete_task_button;
    @FXML
    Button show_completed_tasks_button;

    @FXML
    TextArea task_information_TextArea;

    @FXML
    private ListView<Task> tasksView;

    @FXML
    Image logoImage = new Image(new FileInputStream("images/CiterLogo.png"));

    @FXML
    ImageView logoImageView;
    @FXML
    MenuButton menuButton;
    @FXML
    MenuItem helpItem;
    @FXML
    TextField usernameTextField;
    @FXML
    DatePicker datePicker;
    @FXML
    AnchorPane taskDisplayAnchor;
    @FXML
    ComboBox<String> categoryList;

    @FXML TextField taskNameTextField;
    @FXML  TextField endDateTextField;
    @FXML  TextField startDateTextField;
    @FXML TextField priorityTextField;
    @FXML TextField categoryTextField;
    @FXML TextArea notesTextArea;
    ArrayList<TextField> textfieldList = new ArrayList<>();

    @FXML Button getAllDatesButton;
    @FXML ComboBox priorityComboBox;

    public TaskController() throws FileNotFoundException {
    }

    public void initialize() {
        textfieldList.add(taskNameTextField);
        textfieldList.add(endDateTextField);
        textfieldList.add(startDateTextField);
        textfieldList.add(priorityTextField);
        textfieldList.add(categoryTextField);
        logoImageView.setImage(logoImage);
        addItemsInPriorityComboBox();
        menuButton = new MenuButton("Options", null, helpItem);
        for(TextField textField : textfieldList){
            textField.setEditable(false);
        }
        notesTextArea.setEditable(false);
        categoryList.setItems(FileHandler.getCategories());
        tasksView.getSelectionModel().selectFirst();
        tasksView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task task, Task t1) {
                if(t1 != null){

                    Task task1 = tasksView.getSelectionModel().getSelectedItem();

                    taskNameTextField.setText(task1.getTaskName());
                    startDateTextField.setText(task1.getStartDate().toString());
                    endDateTextField.setText(task1.getEndDate().toString());
                    priorityTextField.setText(task1.getPriority());
                    categoryTextField.setText(task1.getCategory());
                    notesTextArea.setText(task1.getDescription());

                }
            }
        });

        ObservableList<Task> listOfTasks = FileHandler.getInstance().getTasks();

        //Should fix a sorting method here that displays a sortedList (by date f.eksample)
//        taskFilteredList = new FilteredList<Task>(listOfTasks,sortedByDate);

        SortedList<Task> sortedList = new SortedList<Task>(listOfTasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return(task1.getEndDate().compareTo(task2.getEndDate()));
            }
        });

        ObservableList<Task> boo = FXCollections.observableArrayList();
        for(Task t:sortedList){
            if(!t.isCompleted()){
                boo.add(t);
            }
        }

        tasksView.setItems(boo);
        tasksView.getSelectionModel().selectFirst();
    }

    private void addItemsInPriorityComboBox(){
        priorityComboBox.getItems().add("All");
        priorityComboBox.getItems().add("Low");
        priorityComboBox.getItems().add("Medium");
        priorityComboBox.getItems().add("High");
        priorityComboBox.setValue("All");
    }

    @FXML
    public void changeSceneToAddTask() throws IOException{
        CiterClient.setRoot("addTask");
    }

    private void clearText(){
        for (int i = 0; i < textfieldList.size(); i++) {
            textfieldList.get(i).setText(null);
        }
        notesTextArea.setText(null);
    }
    @FXML
    public void handleKeyPressed(KeyEvent e){
        Task taskSelected = tasksView.getSelectionModel().getSelectedItem();
        if(taskSelected != null){
            if(e.getCode().equals(e)){
                deleteTask(taskSelected);
            }
        }
    }

    public void handleEditButton(){
        for(TextField textField : textfieldList){
            textField.setEditable(true);
        }
        notesTextArea.setEditable(true);
        delete_task_button.setText("Save");

    }
    public void saveEditedTask(){
        LocalDate startLocalDate = LocalDate.parse(startDateTextField.getText());
        LocalDate endLocalDate = LocalDate.parse(endDateTextField.getText());
        Task task1 = tasksView.getSelectionModel().getSelectedItem();

        task1.setTaskName(taskNameTextField.getText());
        task1.setStartDate(startLocalDate);
        task1.setEndDate(endLocalDate);
        task1.setPriority(priorityTextField.getText());
        task1.setCategory(categoryTextField.getText());
        task1.setDescription(notesTextArea.getText());
        for(TextField textField : textfieldList){
            textField.setEditable(false);
        }
        notesTextArea.setEditable(false);
        delete_task_button.setText("Delete Task");
    }

    /**
     * Directs user to add new category
     * @throws IOException
     */
    @FXML
    public void handleNewCategoryButton(){
            CategoryController.displayNewCategoryTextInput();
        }
    /**
     *
     * @param delete
     * Handles deletebutton
     */
    @FXML
    public void handleDeleteButton(ActionEvent delete){
        Task selectedTask = tasksView.getSelectionModel().getSelectedItem();
        if(selectedTask != null){
            if(delete.getSource().equals(delete_task_button)){
                deleteTask(selectedTask);
            }
        }
    }

    /**
     *
     * @param task
     * Deletes a task with method from FileHandler class
     */
    public void deleteTask(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Delete Task: " + task.getTaskName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            FileHandler.getInstance().deleteTask(task); //Deletes from list in FileHandler class
        }
        clearText();

    }

    @FXML
    public void handleCompleteButton(ActionEvent complete){
        Task selectedTask = tasksView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            if (complete.getSource().equals(complete_task_button)) {
                completeTask(selectedTask);
            }
        }
    }

    private void completeTask(Task task){
        task.setCompleted(true);
    }

    @FXML
    public void viewCompletedTasks(){
        ObservableList<Task> tasks = FileHandler.getInstance().getTasks();
        ObservableList<Task> boo = FXCollections.observableArrayList();
        for(Task t:tasks){
            if(t.isCompleted()){
                boo.add(t);
            }
        }
        displayTasks(boo);
    }

    public void tasksOnChosenDate(){
        LocalDate date = datePicker.getValue();
        ObservableList<Task> tasksOnDate = FXCollections.observableArrayList();
        for (int i = 0; i < FileHandler.getInstance().getTasks().size(); i++) {
            if (FileHandler.getInstance().getTasks().get(i).getEndDate().equals(date)){
                tasksOnDate.add(FileHandler.getInstance().getTasks().get(i));
            }
        }
        tasksView.setItems(tasksOnDate);
        System.out.println(tasksOnDate);
    }

    public void viewByPriority(){
        ObservableList<Task> tasksOfPriority = FXCollections.observableArrayList();
        String priority = (String) priorityComboBox.getValue();
        if (priority.equals("All")){
            tasksView.setItems(FileHandler.getInstance().getTasks());
        }
        else{
            for (int i = 0; i < FileHandler.getInstance().getTasks().size(); i++) {
                if (FileHandler.getInstance().getTasks().get(i).getPriority().equals(priority)){
                    tasksOfPriority.add(FileHandler.getInstance().getTasks().get(i));
                }
            }
           displayTasks(tasksOfPriority);
            System.out.println(priority);
        }
    }

    public void viewAllTasks(){
        ObservableList<Task> boo = FXCollections.observableArrayList();
        for(Task t:FileHandler.getInstance().getTasks()){
            if(!t.isCompleted()){
                boo.add(t);
            }
        }
        displayTasks(boo);
    }


    public void displayTasks(ObservableList<Task> tasks){
        datePicker.setValue(null);
        tasksView.setItems(tasks);
    }
}

