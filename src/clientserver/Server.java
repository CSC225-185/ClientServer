package clientserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.WindowEvent;

public class Server extends Application {
    
    private TextArea outputTextArea = new TextArea();
    // Number a client
    private int clientNo = 0;

    @Override                                                                   // Override the start method in the Application class
    public void start(Stage primaryStage) {
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                System.exit(0);
            }
        });

        Scene scene = new Scene(new ScrollPane(outputTextArea), 500, 200);                          // Create a scene containing a border pane with specified dimensions

        primaryStage.setTitle("MultiThreadedServer");                                // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage
        
        outputTextArea.appendText("Project 7 - Client/Server Technology\n");

        new Thread( () -> {
            try {

                // Create a socket to connect to the server
                ServerSocket serverSocket = new ServerSocket(8000);

                //ServerSocket serverSocket = new ServerSocket(PORT);
                outputTextArea.appendText("MultiThreadServer started at " + new Date() + '\n');

                while (true) {

                    // Listen for a connection request
                    Socket socket = serverSocket.accept();

                    // Increment clientNo
                    clientNo++;
                    
                    Platform.runLater( () -> {
                        // Display the client number
                        outputTextArea.appendText("\nStarting thread for client " + clientNo + " at " + new Date() + '\n');
                        
                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        outputTextArea.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                        outputTextArea.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                    });
                    
                    // Create and start a new thread for the connection
                    new Thread(new HandleAClient(socket)).start();

                }
            } catch (IOException ex) {
                System.err.println(ex);
            } 
        }).start();           
    }

    class HandleAClient implements Runnable {
    
        private Socket socket; // A connected socket
        
        /** Construct a thread */
        public HandleAClient(Socket socket) {
            this.socket = socket;        
        }
        
        /** Run a thread */
        @Override
        public void run() {
        
            try {
System.out.println("test001");            
                // Create primitive data type input and output streams
                DataInputStream primFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream primToClient = new DataOutputStream(socket.getOutputStream());
System.out.println("test002");                
                // Create object type input and output streams                
                ObjectInputStream objFromClient;
                ObjectOutputStream objToClient;
                
System.out.println("test003");                
                // Continuously serve the client
                while (true) {
                    
                    objFromClient = new ObjectInputStream(socket.getInputStream());
                    objToClient = new ObjectOutputStream(socket.getOutputStream());
                
                    Object object = null;

                    try {
                        object = objFromClient.readObject();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[] fromClientObj = object.toString().split(":");

                    Loan loan = new Loan(
                            Double.parseDouble(fromClientObj[0]), 
                            Integer.parseInt(fromClientObj[1]), 
                            Double.parseDouble(fromClientObj[2])
                    );

                    Platform.runLater(() -> {
                        outputTextArea.appendText(String.format("\nAnnual Interest Rate: %.2f\n", loan.getAnnualInterestRate()));
                        outputTextArea.appendText(String.format("Number of Years: %d\n", loan.getNumberOfYears()));
                        outputTextArea.appendText(String.format("Loan Amount: %.2f\n", loan.getLoanAmount()));
                        outputTextArea.appendText(String.format("monthlyPayment: %.2f\n", loan.getMonthlyPayment())); 	// Get the monthly payment from the object, format it to look good and display it in the textbox
                        outputTextArea.appendText(String.format("totalPayment: %.2f\n", loan.getTotalPayment())); 
                        
                    });

                    String toClientObj = loan.getMonthlyPayment() + ":" + loan.getTotalPayment();
                    
                    objToClient.writeObject(toClientObj);
                    objToClient.flush();                    
                
                }
                            
            } catch (IOException ex) {
                outputTextArea.appendText(ex.toString() + '\n');
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);            
            }
        
        }
    
    }
    
    public static void main (String[] args) {
    
        launch(args);
    
    }
    
}