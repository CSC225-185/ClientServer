package server;

/*
 * CSC-225 - Advanced JAVA Programming
 * Project 7 - Client Server Technology Lab
 * Class Description : This is the server program class for the server-client based loan calculator program
 * Author            : Original Author Unknown - Code supplied in class
 * Modified by       : Jacob Johncox, Benjamin Kleynhans, and Jake Neels
 * Date              : November 16
 * Filename          : Server.java
 */

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
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static javafx.application.Application.launch;

/**
 * <h1>
 * CSC-225 - Advanced JAVA Programming - Client Server Technology Lab
 * </h1>
 * <p>
 * This is the server program class for the server-client based loan calculator
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

public class Server extends Application {

    
    private final TextArea outputTextArea = new TextArea();                     // Output area definition
    private int clientNo = 0;                                                   // Used to assign a number to a client

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

        Scene scene = new Scene(new ScrollPane(outputTextArea), 500, 200);      // Create a scene containing a border pane with specified dimensions

        primaryStage.setTitle("MultiThreadedServer");                           // Set the stage title
        primaryStage.setScene(scene);                                           // Place the scene in the stage
        primaryStage.show();                                                    // Display the stage

        outputTextArea.appendText("Project 7 - Client/Server Technology\n");

        new Thread(() -> {
            try {
                
                ServerSocket serverSocket = new ServerSocket(8000);             // Create a socket to connect to the server

                outputTextArea.appendText("MultiThreadServer started at " 
                                          + new Date() + '\n');                 //ServerSocket serverSocket = new ServerSocket(PORT);
                
                while (true) {

                    Socket socket = serverSocket.accept();                      // Listen for a connection request

                    clientNo++;                                                 // Increment clientNo
                    
                    Platform.runLater(() -> {

                        outputTextArea.appendText("\nStarting thread for client " 
                                                  + clientNo + " at " 
                                                  + new Date() + '\n');         // Display the client number

                        InetAddress inetAddress = socket.getInetAddress();      // Find the client's host name, and IP address
                        outputTextArea.appendText("Client " + clientNo 
                                                  + "'s host name is " 
                                                  + inetAddress.getHostName() 
                                                  + "\n");
                        outputTextArea.appendText("Client " + clientNo 
                                                  + "'s IP Address is " 
                                                  + inetAddress.getHostAddress() 
                                                  + "\n");
                    });

                    new Thread(new HandleAClient(socket)).start();              // Create and start a new thread for the connection
                    
                }
            } catch (IOException ex) {                                          // Catch IO Exceptions
                System.err.println(ex);
            }
        }).start();
    }
    
    /**
     * Performs calculation tasks for each client
     */

    class HandleAClient implements Runnable {

        private final Socket socket;                                            // A connected socket

        public HandleAClient(Socket socket) {                                   // Construct a thread
            this.socket = socket;
        }

        @Override
        public void run() {                                                     // Launch a thread

            try {

                ObjectInputStream objFromClient;                                // Create object type input and output streams
                ObjectOutputStream objToClient;

                while (true) {                                                  // Continuously serve the client

                    objFromClient = new ObjectInputStream(socket.getInputStream());// Create input stream
                    objToClient = new ObjectOutputStream(socket.getOutputStream());// Create output stream

                    Object object = null;

                    try {
                        object = objFromClient.readObject();                    // Receive object from client
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[] fromClientObj = object.toString().split(":");      // Split client object into array

                    Loan loan = new Loan(                                       // Create new loan object
                            Double.parseDouble(fromClientObj[0]),
                            Integer.parseInt(fromClientObj[1]),
                            Double.parseDouble(fromClientObj[2])
                    );

                    Platform.runLater(() -> {                                   // create a new thread
                        outputTextArea.appendText(
                                String.format("\nAnnual Interest Rate: %.2f\n",
                                              loan.getAnnualInterestRate())
                        );                                                      // Get and format annual interest rate from object
                        outputTextArea.appendText(
                                String.format("Number of Years: %d\n",
                                              loan.getNumberOfYears())
                        );                                                      // Get and format monthly payment from object
                        outputTextArea.appendText(
                                String.format("Loan Amount: %.2f\n",
                                              loan.getLoanAmount())
                        );                                                      // Get and format loan amount from object
                        outputTextArea.appendText(
                                String.format("monthlyPayment: %.2f\n",
                                              loan.getMonthlyPayment())
                        );                                                      // Get and format monthly paymnet from object
                        outputTextArea.appendText(
                                String.format("totalPayment: %.2f\n", loan.getTotalPayment())
                        );                                                      // Get and format total payment from object

                    });

                    String toClientObj = loan.getMonthlyPayment() + ":" 
                                         + loan.getTotalPayment();              // Construct information string to send to client

                    objToClient.writeObject(toClientObj);                       // Transmit information string
                    objToClient.flush();                                        // Flush buffers

                }

            } catch (IOException ex) {                                          // Catch IO Exceptions
                outputTextArea.appendText(ex.toString() + '\n');
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
    /**
     * Main method used to launch the program.  Required for compatibility
     * 
     * @param args Input arguments mostly used by legacy implementations     
     */

    public static void main(String[] args) {

        launch(args);                                                           // Launch program

    }

}
