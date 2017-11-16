package client;

/*
 * CSC-225 - Advanced JAVA Programming
 * Project 7 - Client Server Technology Lab
 * Class Description : This is the client program class for the server-client based loan calculator program
 * Author            : Original Author Unknown - Code supplied in class
 * Modified by       : Jacob Johncox, Benjamin Kleynhans, and Jake Neels
 * Date              : November 16, 2017
 * Filename          : Client.java
 */

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * <h1>
 * CSC-225 - Advanced JAVA Programming - Client Server Technology Lab
 * </h1>
 * <p>
 * This is the client program class for the server-client based loan calculator
 * program
 * </p>
 *
 * @author Jacob Johncox (M00864758)
 * @author Benjamin Kleynhans (M00858194)
 * @author Jake Neels (M00696306)
 *
 * @version 1.0
 * @since November 16, 2017
 */
public class Client extends Application {

    // IO streams
    private DataOutputStream primToServer = null;
    private DataInputStream primFromServer = null;
    private ObjectOutputStream objToServer = null;
    private ObjectInputStream objFromServer = null;

    private Socket socket = null;

    private TextArea outputTextArea = new TextArea();

    private final Insets DEFAULT_INSETS = new Insets(7.5, 6, 7.5, 6);

    private TextField interestTxtF = new TextField();
    private TextField periodTxtF = new TextField();
    private TextField amountTxtF = new TextField();

    
    /**
     * Creates the primary stage display object
     * 
     * @param primaryStage JavaFX Stage
     */
    
    @Override                                                                   // Override the start method in the Application class
    public void start(Stage primaryStage) {

        primaryStage.setOnCloseRequest((WindowEvent we) -> {                    // Exit the program closing all ports and stopping all threads
            System.out.println("Stage is closing");
            System.exit(0);
        });

        BorderPane borderPane = new BorderPane();                               // Create BorderPane layout element

        GridPane gridPane = new GridPane();                                     // Create GridPane layout element

        HBox hBox = new HBox();                                                 // Create HBox layout elements
        VBox vBox = new VBox();                                                 // Create VBox layout elements

        Button submitBtn = new Button("Submit");                                // Create submit button

        Label interestLbl = new Label("Annual Interest Rate");                  // Create label for interest
        Label periodLbl = new Label("Number Of Years");                         // Create label for period of loan
        Label amountLbl = new Label("Loan Amount");                             // Create label for loan amount

        GridPane.setHalignment(interestLbl, HPos.LEFT);                         // Set GridPane alignment
        gridPane.setPadding(DEFAULT_INSETS);                                    // Set GridPane padding

        vBox.setMinHeight(Region.USE_PREF_SIZE);                                // Set vBox min height
        vBox.setMinWidth(Region.USE_PREF_SIZE);                                 // Set vBox max height
        vBox.setPadding(DEFAULT_INSETS);                                        // Define space formatting for vertical box
        vBox.setAlignment(Pos.CENTER);                                          // Set vBox alignment

        hBox.setMinHeight(Region.USE_PREF_SIZE);                                // Set hBox min height
        hBox.setPadding(DEFAULT_INSETS);                                        // Set hBox padding
        hBox.setAlignment(Pos.TOP_LEFT);                                        // Set hBox alignment

        submitBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);               // Set button max size
        submitBtn.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);               // Set button min size

        interestLbl.setMinWidth(Region.USE_PREF_SIZE);                          // Set interest label preferred size
        interestLbl.setPadding(DEFAULT_INSETS);                                 // Set interest label padding

        periodLbl.setMinWidth(Region.USE_PREF_SIZE);                            // Set loan period label min width
        periodLbl.setPadding(DEFAULT_INSETS);                                   // Set loan period label padding

        amountLbl.setMinWidth(Region.USE_PREF_SIZE);                            // Set loan amount label min width
        amountLbl.setPadding(DEFAULT_INSETS);                                   // Set loan amount label padding

        interestTxtF.setMinWidth(Region.USE_PREF_SIZE);                         // Set textfield sizes and alignments
        interestTxtF.setAlignment(Pos.CENTER_RIGHT);
        periodTxtF.setMinWidth(Region.USE_PREF_SIZE);
        periodTxtF.setAlignment(Pos.CENTER_RIGHT);
        amountTxtF.setMinWidth(Region.USE_PREF_SIZE);
        amountTxtF.setAlignment(Pos.CENTER_RIGHT);

        GridPane.setConstraints(interestLbl, 0, 0);                             // Define label and textfield locations in gridpane
        GridPane.setConstraints(interestTxtF, 1, 0);
        GridPane.setConstraints(periodLbl, 0, 1);
        GridPane.setConstraints(periodTxtF, 1, 1);
        GridPane.setConstraints(amountLbl, 0, 2);
        GridPane.setConstraints(amountTxtF, 1, 2);

        gridPane.getChildren().addAll(                                          // Place labels and textfields in gridpane
                interestLbl,
                interestTxtF,
                periodLbl,
                periodTxtF,
                amountLbl,
                amountTxtF
        );

        vBox.getChildren().add(submitBtn);                                      // Place submit button in vBox

        hBox.getChildren().add(new ScrollPane(outputTextArea));                 // Place scrollpane in hBox

        borderPane.setCenter(gridPane);                                         // Place gridpane in borderpane
        borderPane.setRight(vBox);                                              // Place vBox in borderpane
        borderPane.setBottom(hBox);                                             // place hBox in borderpane

        outputTextArea.appendText("Project 7 - Client/Server Technology\n");    // Append the title to the output area

        try {                                                                   // Try to create a new socket
            socket = new Socket("localhost", 8000);
        } catch (IOException ex) {
            outputTextArea.appendText(ex.toString() + '\n');
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        submitBtn.setOnAction(e -> transportData(socket));

        Scene scene = new Scene(borderPane, 450, 400);                          // Create a scene containing a border pane with specified dimensions

        primaryStage.setTitle("Client");                                        // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage

    }

    /**
     * Send the data across the network to the server
     * 
     * @param socket Socket created for data transport
     */
    
    private void transportData(Socket socket) {

        try {

            primFromServer = new DataInputStream(socket.getInputStream());      // Create primitive data type input and output streams
            primToServer = new DataOutputStream(socket.getOutputStream());
            
            objToServer = new ObjectOutputStream(socket.getOutputStream());     // Create object type input and output streams

            String interestRate = interestTxtF.getText().trim();                // Get and format input strings for transmission
            String period = periodTxtF.getText().trim();
            String amount = amountTxtF.getText().trim();

            String toServerObj = interestRate + ":" + period + ":" + amount;    // Compile transmission string

            if (!interestRate.isEmpty() && !period.isEmpty() && !amount.isEmpty()) {
                try {
                    objToServer.writeObject(toServerObj);                       // Transmit transmission string to client
                    objToServer.flush();                                        // Flush stream

                    objFromServer = new ObjectInputStream(socket.getInputStream()); // Define new read object for data from server

                    Object object = null;

                    try {
                        object = objFromServer.readObject();                    // Get input from server
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[] fromServerObj = object.toString().split(":");      // Split transmission string into array

                    outputTextArea.appendText(
                            String.format("\nAnnual Interest Rate: %.2f\n", 
                                          Double.parseDouble(interestRate))
                    );                                                          // Get and format annual interest rate from object
                    outputTextArea.appendText(
                            String.format("Number of Years: %d\n", 
                                          Integer.parseInt(period))
                    );                                                          // Get and format monthly payment from object
                    outputTextArea.appendText(
                            String.format("Loan Amount: %.2f\n", 
                                          Double.parseDouble(amount))
                    );                                                          // Get and format loan amount from object
                    outputTextArea.appendText(
                            String.format("monthlyPayment: %.2f\n", 
                                          Double.parseDouble(fromServerObj[0]))
                    );                                                          // Get and format monthly paymnet from object
                    outputTextArea.appendText(
                            String.format("totalPayment: %.2f\n", 
                                          Double.parseDouble(fromServerObj[1]))
                    );                                                          // Get and format total payment from object

                } catch (IOException ex) {
                    outputTextArea.appendText(ex.toString() + '\n');
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {                                                            // Inform the user they didn't provide the correct input

                outputTextArea.appendText("\n!!! Please enter all the required values before pressing submit !!!\n");

            }
        } catch (IOException ex) {                                              // Catch IO Exceptions
            outputTextArea.appendText(ex.toString() + '\n');
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Main method used to launch the program.  Required for compatibility
     * 
     * @param args Input arguments mostly used by legacy implementations
     * @throws IOException Exception catch required for input-output due to network transport
     */    

    public static void main(String[] args) throws IOException {

        launch(args);                                                           // Launch program

    }
}
