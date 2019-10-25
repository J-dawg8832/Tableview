package edu.capella.it4749;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class U08a1_tableview extends Application {
    Logger log = Logger.getLogger(U08a1_tableview.class.getName());
    
    //Setting up the entity manager to go to the CourseRegistrationService for the info
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("CourseRegistrationService");
    EntityManager em = emf.createEntityManager();
    CourseRegistrationService service = new CourseRegistrationService(em);
    
    //Setting up the application
    GridPane grid = new GridPane();
    
    //ObservableList for the two TableViews
    private ObservableList observableCourses;
    private ObservableList observableRegi;
    
    //Controls for the two Tableviews
    TableView<Course> coursesTable = new TableView<>();
    TableView<CourseRegistration> regisTable = new TableView<>();
    
    //Creating the Labels
    Label confirmPromptLabel = new Label("");
    Label registeredCoursePromptLabel = new Label("You are currently registered for");
    Label creditHourPromptLabel = new Label("Current Total Credit Hours");
    Label registeredCoursesLabel = new Label("");
    Label creditHoursLabel = new Label("0");
    Label dataFilePathLabel = new Label("");
    
    Label applicationLabel = new Label("Registration Application");
    Label learnerIdLabel = new Label("Learner: ");
    TextField learnerIDField = new TextField();
    
    
    Course choice;
    final int MAX_CREDIT_LOAD = 9;
    int totalCredit = 0;
    
    @Override
    public void start(Stage primaryStage) {
        
        //column constraints
        ColumnConstraints column0 = new ColumnConstraints();
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        
        column0.setPercentWidth(45);
        column1.setPercentWidth(10);
        column2.setPercentWidth(45);
        
        //Adds the columns to the grid
        grid.getColumnConstraints().addAll(column0, column1, column2);
        
        RowConstraints row0 = new RowConstraints();
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        RowConstraints row5 = new RowConstraints();
        
        //configure row heights
        row0.setPercentHeight(5);
        row1.setPercentHeight(10);
        row2.setPercentHeight(25);
        row3.setPercentHeight(10);
        row4.setPercentHeight(3);
        row5.setPercentHeight(35);
        
        grid.getRowConstraints().addAll(row0, row1, row2, row3, row4, row5);
        
        grid.setAlignment(Pos.CENTER);
        
        grid.setHgap(5);
        grid.setVgap(5);
        
        //Sets the Margins
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        //new application styling
        Font applicationNameFont = new Font("Monospaced", 24);
        applicationLabel.setFont(applicationNameFont);
        applicationLabel.setStyle("-fx-font-weight: bold");
        applicationLabel.setTextFill(Color.DARKBLUE);
        
        //Adds the application label to the grid
        grid.add(applicationLabel, 0, 0);
        grid.add(learnerIdLabel, 1, 0);
        GridPane.setHalignment(learnerIdLabel, HPos.RIGHT);
        grid.add(learnerIDField, 2, 0);
        
        //add columns to TableView for available Courses
        //Setting the columns of the table to read like is
        TableColumn<Course, String> courseCodeCol = new TableColumn<>("Course Code");
        TableColumn<Course, Integer> creditHourCol = new TableColumn<>("Credits");
        courseCodeCol.setPrefWidth(110);
        creditHourCol.setPrefWidth(110);
        //adds the columns to the courses table
        coursesTable.getColumns().setAll(courseCodeCol, creditHourCol);
        
        //This line gets the course code from the database and displays it on the table
        courseCodeCol.setCellValueFactory( (CellDataFeatures<Course, String> c) ->
        new ReadOnlyObjectWrapper(c.getValue().getCourseCode()));
        //This code gets the credit hours of the course from the database and displays it on the table
        creditHourCol.setCellValueFactory( (CellDataFeatures<Course, Integer> c) ->
        new ReadOnlyObjectWrapper(c.getValue().getCreditHours()));
        
        grid.add(confirmPromptLabel, 2, 2);
        GridPane.setHalignment(confirmPromptLabel, HPos.LEFT);
        GridPane.setValignment(confirmPromptLabel, VPos.TOP);
        
        grid.add(registeredCoursePromptLabel, 0, 4);
        GridPane.setHalignment(registeredCoursePromptLabel, HPos.LEFT);
        GridPane.setValignment(registeredCoursePromptLabel, VPos.TOP);
        
        grid.add(creditHourPromptLabel, 2, 4);
        GridPane.setHalignment(creditHourPromptLabel, HPos.LEFT);
        GridPane.setValignment(creditHourPromptLabel, VPos.TOP);
        
        //Adds the two tables to the grid
        grid.add(coursesTable, 0, 2);
        grid.add(regisTable, 0, 5);
        
        //TableView for Registered Courses for the user
        //Adds the column Registered Courses to the Table
        TableColumn<CourseRegistration, String> registeredCourseCol = new TableColumn<>("Registered Courses");
        //Sets preferred width
        registeredCourseCol.setPrefWidth(140);
        //adds columns to the table
        regisTable.getColumns().setAll(registeredCourseCol);
        
        //Gets the registered courses from the database and displays them on the table
        registeredCourseCol.setCellValueFactory( (CellDataFeatures<CourseRegistration, String> r) ->
        new ReadOnlyObjectWrapper(r.getValue().getCourseCode()));
        
        GridPane.setHalignment(creditHoursLabel, HPos.LEFT);
        GridPane.setValignment(creditHoursLabel, VPos.TOP);
        creditHoursLabel.setStyle("-fx-background-color: #fff600");
        
        Scene scene = new Scene(grid, 700, 700, Color.RED);
        
        primaryStage.setTitle("JavaFX Register for Courses");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //Async call to get list of available courses
        try{
            Future<List<Course>> availableCourses = service.getAllCoursesAsync();
            confirmPromptLabel.setText("Getting course list...");
            observableCourses = FXCollections.observableArrayList(availableCourses.get());
            coursesTable.setItems(observableCourses);           
            confirmPromptLabel.setText("Course data retrieved...");
        }
        catch(Exception ex) {
            log.log(Level.SEVERE, "Error accessing the course list: {0}", ex.getMessage());
        }
        
        //Mouse Click Event bringing the lists of available courses to the registered courses when clicked upon
        coursesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                boolean alreadyRegistered = false;
                boolean overCreditLimit = false;
                boolean learnerIDBlank = false;
                
                int index = coursesTable.getSelectionModel().getSelectedIndex();
                if(index > -1) {
                    Course c = (Course) observableCourses.get(index);
                    
                    Future<List<CourseRegistration>> registeredCourses = service.getAllCourseRegistrationsAsync(learnerIDField.getText());
                    
                    if(learnerIDField.getText().length() == 0) {
                        learnerIDBlank = true;
                        confirmPromptLabel.setText("Learner ID cannot be blank");
                    }
                    if(! learnerIDBlank ) {
                        if(totalCredit + c.getCreditHours() > MAX_CREDIT_LOAD) {
                            overCreditLimit = true;
                            confirmPromptLabel.setText("Cannot register for more than 9 credit hours");
                        }
                        try{
                            for(CourseRegistration cr : registeredCourses.get()) {
                                if(c.getCourseCode().equals(cr.getCourseCode())) {
                                    alreadyRegistered = true;
                                    confirmPromptLabel.setText("Already Registered For " + cr.getCourseCode());
                                    break;
                                }
                            }
                        }
                        catch(Exception ex) {
                            log.log(Level.WARNING, "Cannot recieve the proper field... {0}");
                        }
                    }
                    
                    if(! learnerIDBlank && ! overCreditLimit && ! alreadyRegistered) {
                        try{
                            Future<CourseRegistration> reg = service.createCourseRegistrationAsync(learnerIDField.getText(), c.getCourseCode());
                            reg.get();
                            
                            registeredCourses = service.getAllCourseRegistrationsAsync(learnerIDField.getText());
                            
                            observableRegi = FXCollections.observableArrayList(registeredCourses.get());
                            regisTable.setItems(observableRegi);
                            totalCredit += c.getCreditHours();
                            creditHoursLabel.setText(Integer.toString(totalCredit));
                            confirmPromptLabel.setText("Registration confirmed");
                        }
                        catch(Exception ex) {
                            log.log(Level.WARNING, "Error with the aforementioned text fields: {0}");
                        }
                    }
                }
            }
        });
    }

    public void stop() {
        em.close();
        emf.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //created the fileHandler and simpleformatter to create logs into the proper log file
        try {
            FileHandler fileHandler = new FileHandler("registration.log");
            SimpleFormatter simple = new SimpleFormatter();
            //set the simpleformatter to the filehandler
            fileHandler.setFormatter(simple);
            //caught any exceptions that needed to be caught 
        } catch(IOException ex) {
            System.err.println("Error opening the Log File...");
            System.exit(1);
        }
        Logger.getLogger("").getHandlers()[0].setLevel(Level.WARNING);
        launch(args);
    }
    
}
