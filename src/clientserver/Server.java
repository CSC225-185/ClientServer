package clientserver;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class Server extends Application {
    // Text area for displaying contents
    private TextArea textArea = new TextArea();
    // Port used for socket connections
    private final int PORT = 8000;

    @Override                                                                   // Override the start method in the Application class
    public void start(Stage primaryStage) {
        
        BorderPane borderPane = new BorderPane();
        
        borderPane.setCenter(textArea);
        
        Scene scene = new Scene(borderPane, 400, 250);                          // Create a scene containing a border pane with specified dimensions
        
        primaryStage.setTitle("Server");                                // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage

//        // Place text area on the frame
//        setLayout(new BorderLayout());
//        add(new JScrollPane(serverTextArea), BorderLayout.CENTER);
//
//        setTitle("Server");
//        setSize(500, 300);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setVisible(true); // It is necessary to show the frame here!
//
//        try {
//            // Create a server socket
//            ServerSocket serverSocket = new ServerSocket(PORT);
//            serverTextArea.append("Server started at " + new Date() + '\n');
//
//            // Listen for a connection request
//            Socket socket = serverSocket.accept();
//
//            // Create data input and output streams
//            DataInputStream inputFromClient = new DataInputStream(
//                    socket.getInputStream());
//            DataOutputStream outputToClient = new DataOutputStream(
//                    socket.getOutputStream());
//
//            while (true) {
//                // Receive radius from the client
//                double radius = inputFromClient.readDouble();
//
//                // Compute area
//                double area = radius * radius * Math.PI;
//
//                // Send area back to the client
//                outputToClient.writeDouble(area);
//
//                serverTextArea.append("Radius received from client: " + radius + '\n');
//                serverTextArea.append("Area found: " + area + '\n');
//            }
//        } catch (IOException ex) {
//            System.err.println(ex);
//        }
    }
}
