package clientserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Client extends Application {
    
    // Name of server to connect to
    private static final String SERVERNAME = "localhost";
    // Port used for socket connections
    private static final int PORT = 8000;
    // Text area for displaying contents
    private static TextArea outputTextArea = new TextArea();
    // IO Streams
    private static DataOutputStream toServer;
    private static DataInputStream fromServer;
    
    private final Insets DEFAULT_INSETS = new Insets(7.5, 6, 7.5, 6);
  
    @Override                                                                   // Override the start method in the Application class
    public void start(Stage primaryStage) {
        
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
        
        TextField interestTxtF = new TextField();        
        TextField periodTxtF = new TextField();        
        TextField amountTxtF = new TextField();
        
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
        
        //outputTextArea
        //        .setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(outputTextArea);
        
        borderPane.setCenter(gridPane);
        borderPane.setRight(vBox);
        borderPane.setBottom(hBox);
        
        Scene scene = new Scene(borderPane, 450, 400);                          // Create a scene containing a border pane with specified dimensions
        
        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            System.out.println("CLOSING");
        });
        
        primaryStage.setTitle("Client");                                // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage
    }
    
    public static void connectToServer() {
    
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(SERVERNAME, PORT);
            // Socket socket = new Socket("130.254.204.36", 8000);
            // Socket socket = new Socket("drake.Armstrong.edu", 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(
              socket.getInputStream());

            // Create an output stream to send data to the server
            toServer =
              new DataOutputStream(socket.getOutputStream());
          }
          catch (IOException ex) {
            outputTextArea.appendText(ex.toString() + '\n');
          }
    }
    
    public static void main (String[] args) {
    
        connectToServer();
        launch(args);
    
    }

//// Text field for receiving radius
//  private JTextField jtf = new JTextField();
//  // Name of server to connect to
//  private final String SERVERNAME = "localhost";
//  // Port used for socket connections
//  private final int PORT = 8000;
//
//  // Text area to display contents
//  private JTextArea jta = new JTextArea();
//
//  // IO streams
//  private DataOutputStream toServer;
//  private DataInputStream fromServer;
//
//  public static void main(String[] args) {
//    new Client();
//  }
//
//  public Client() {
//    // Panel p to hold the label and text field
//    JPanel p = new JPanel();
//    p.setLayout(new BorderLayout());
//    p.add(new JLabel("Enter radius"), BorderLayout.WEST);
//    p.add(jtf, BorderLayout.CENTER);
//    jtf.setHorizontalAlignment(JTextField.RIGHT);
//
//    setLayout(new BorderLayout());
//    add(p, BorderLayout.NORTH);
//    add(new JScrollPane(jta), BorderLayout.CENTER);
//
//    jtf.addActionListener(new ButtonListener()); // Register listener
//
//    setTitle("Client");
//    setSize(500, 300);
//    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    setVisible(true); // It is necessary to show the frame here!
//
//    try {
//      // Create a socket to connect to the server
//      Socket socket = new Socket(SERVERNAME, PORT);
//      // Socket socket = new Socket("130.254.204.36", 8000);
//      // Socket socket = new Socket("drake.Armstrong.edu", 8000);
//
//      // Create an input stream to receive data from the server
//      fromServer = new DataInputStream(
//        socket.getInputStream());
//
//      // Create an output stream to send data to the server
//      toServer =
//        new DataOutputStream(socket.getOutputStream());
//    }
//    catch (IOException ex) {
//      jta.append(ex.toString() + '\n');
//    }
//  }
//
//  private class ButtonListener implements ActionListener {
//    public void actionPerformed(ActionEvent e) {
//      try {
//        // Get the radius from the text field
//        double radius = Double.parseDouble(jtf.getText().trim());
//
//        // Send the radius to the server
//        toServer.writeDouble(radius);
//        toServer.flush();
//
//        // Get area from the server
//        double area = fromServer.readDouble();
//
//        // Display to the text area
//        jta.append("Radius is " + radius + "\n");
//        jta.append("Area received from the server is "
//          + area + '\n');
//      }
//      catch (IOException ex) {
//        System.err.println(ex);
//      }
//    }
//  }
}
