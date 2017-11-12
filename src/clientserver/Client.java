package clientserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

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

    @Override                                                                   // Override the start method in the Application class
    public void start(Stage primaryStage) {

        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Stage is closing");
            System.exit(0);
        });

        BorderPane borderPane = new BorderPane();

        GridPane gridPane = new GridPane();

        HBox hBox = new HBox();
        VBox vBox = new VBox();

        hBox.setStyle("-fx-background-color: red");
        vBox.setStyle("-fx-background-color: blue");
        gridPane.setStyle("-fx-background-color: orange");
        borderPane.setStyle("-fx-background-color: purple");

        Button submitBtn = new Button("Submit");

        Label interestLbl = new Label("Annual Interest Rate");
        Label periodLbl = new Label("Number Of Years");
        Label amountLbl = new Label("Loan Amount");

        GridPane.setHalignment(interestLbl, HPos.LEFT);
        gridPane.setPadding(DEFAULT_INSETS);

        vBox.setMinHeight(Region.USE_PREF_SIZE);
        vBox.setMinWidth(Region.USE_PREF_SIZE);
        vBox.setPadding(DEFAULT_INSETS);                            // Define space formatting for vertical box
        vBox.setAlignment(Pos.CENTER);

        hBox.setMinHeight(Region.USE_PREF_SIZE);
        hBox.setPadding(DEFAULT_INSETS);
        hBox.setAlignment(Pos.TOP_LEFT);

        submitBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        submitBtn.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

        interestLbl.setMinWidth(Region.USE_PREF_SIZE);
        interestLbl.setPadding(DEFAULT_INSETS);

        periodLbl.setMinWidth(Region.USE_PREF_SIZE);
        periodLbl.setPadding(DEFAULT_INSETS);

        amountLbl.setMinWidth(Region.USE_PREF_SIZE);
        amountLbl.setPadding(DEFAULT_INSETS);

        interestTxtF.setMinWidth(Region.USE_PREF_SIZE);
        interestTxtF.setAlignment(Pos.CENTER_RIGHT);
        periodTxtF.setMinWidth(Region.USE_PREF_SIZE);
        periodTxtF.setAlignment(Pos.CENTER_RIGHT);
        amountTxtF.setMinWidth(Region.USE_PREF_SIZE);
        amountTxtF.setAlignment(Pos.CENTER_RIGHT);

        GridPane.setConstraints(interestLbl, 0, 0);
        GridPane.setConstraints(interestTxtF, 1, 0);
        GridPane.setConstraints(periodLbl, 0, 1);
        GridPane.setConstraints(periodTxtF, 1, 1);
        GridPane.setConstraints(amountLbl, 0, 2);
        GridPane.setConstraints(amountTxtF, 1, 2);

        gridPane.getChildren().addAll(
                interestLbl,
                interestTxtF,
                periodLbl,
                periodTxtF,
                amountLbl,
                amountTxtF
        );

        vBox.getChildren().add(submitBtn);

        hBox.getChildren().add(new ScrollPane(outputTextArea));

        borderPane.setCenter(gridPane);
        borderPane.setRight(vBox);
        borderPane.setBottom(hBox);

//        new Thread(() -> {
//            submitBtn.setOnAction(new ButtonListener());
//        });
        outputTextArea.appendText("Project 7 - Client/Server Technology\n");

        try {
            socket = new Socket("localhost", 8000);
        } catch (IOException ex) {
            outputTextArea.appendText(ex.toString() + '\n');
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        submitBtn.setOnAction(e -> submitData(socket));

        Scene scene = new Scene(borderPane, 450, 400);                          // Create a scene containing a border pane with specified dimensions

        primaryStage.setTitle("Client");                                // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage

    }
//
//    private class ButtonListener implements EventHandler<ActionEvent> {

    private void submitData(Socket socket) {
//
//        @Override
//        public void handle(ActionEvent e) {
//            new Thread(() -> {

        try {
//                    Socket socket = new Socket("localhost", 8000);

            // Create primitive data type input and output streams
            primFromServer = new DataInputStream(socket.getInputStream());
            primToServer = new DataOutputStream(socket.getOutputStream());

            // Create object type input and output streams
            objToServer = new ObjectOutputStream(socket.getOutputStream());


            String interestRate = interestTxtF.getText().trim();
            String period = periodTxtF.getText().trim();
            String amount = amountTxtF.getText().trim();

            String toServerObj = interestRate + ":" + period + ":" + amount;


            if (!interestRate.isEmpty() && !period.isEmpty() && !amount.isEmpty()) {
                try {
                    objToServer.writeObject(toServerObj);
                    objToServer.flush();

                    objFromServer = new ObjectInputStream(socket.getInputStream());

                    Object object = null;

                    try {
                        object = objFromServer.readObject();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[] fromServerObj = object.toString().split(":");

                    outputTextArea.appendText(String.format("\nAnnual Interest Rate: %.2f\n", Double.parseDouble(interestRate)));
                    outputTextArea.appendText(String.format("Number of Years: %d\n", Integer.parseInt(period)));
                    outputTextArea.appendText(String.format("Loan Amount: %.2f\n", Double.parseDouble(amount)));
                    outputTextArea.appendText(String.format("monthlyPayment: %.2f\n", Double.parseDouble(fromServerObj[0]))); 	// Get the monthly payment from the object, format it to look good and display it in the textbox
                    outputTextArea.appendText(String.format("totalPayment: %.2f\n", Double.parseDouble(fromServerObj[1])));

                } catch (IOException ex) {
                    outputTextArea.appendText(ex.toString() + '\n');
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
            
                outputTextArea.appendText("\n!!! Please enter all the required values before pressing submit !!!\n");
            
            }
        } catch (IOException ex) {
            outputTextArea.appendText(ex.toString() + '\n');
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException {

        launch(args);

    }
}
