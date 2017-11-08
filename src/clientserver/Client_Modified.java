package clientserver;

//**************************************************************
//* Class Name:    Client_Modified
//* Author:        Christine Forde
//* Abstract:      Used to test a client process
//*                that communicates with a server process
//*
//*-------------------------------------------------------------
//* Classes:
//* Methods: main
//* Run Server1.java first
//**************************************************************
import java.io.*;
import java.net.*;
import java.util.*;

import java.io.EOFException;
import java.io.IOException;
import javax.swing.JFrame;

public class Client_Modified extends JFrame
{
	private Socket connectToServer;
	private BufferedReader stdin;
	private BufferedReader isFromServer;
	private PrintWriter osToServer;


   //---------------------------------------------------------
  public void runClient()
  {
  try
      {
         // create a client socket Use this to generate the error
         // message:  "Client died with exception: "
         //Socket serverSock = new Socket("localhost",8000);
         //Note the server is on port 9000 not 8000.



        //Step 1: create a client socket to connnect to the server

         connectToServer = new Socket("localhost",8000); //old logic
        

         stdin = new BufferedReader
                         (new InputStreamReader(System.in), 1);

         //Step 2: use buffer reader stream to get data from server
          isFromServer = new BufferedReader
                                          (new InputStreamReader
                                  (connectToServer.getInputStream()));

	 	 //Step 2: create buffer writer to send data to server
          osToServer = new PrintWriter
                           (connectToServer.getOutputStream(), true);

	 	 //Step 3:  continuously send data to server and get back
	 	 //some stuff

         boolean cont = true;
         while(cont == true)
         {
            System.out.println("Client: enter a radius, -1 to quit ");
            double radius =Double.parseDouble(stdin.readLine());

	    		// send data to server
            osToServer.println(radius);

	    		//get data from server
            StringTokenizer st = new StringTokenizer
                                      (isFromServer.readLine());

            // convert to double
            double area = new Double(st.nextToken()).doubleValue();

	    		if (area < 0)
            {
               System.out.println("****Client is exiting*****");
               cont = false;
            }
            else
            {
               System.out.println(" the area of a circle with radius "+
               radius +" is: " + area);
            }
         }
         System.out.println("****Goodbye - client*****");
      }


      catch(IOException e1)
      {
         System.err.println("Client died with exception: " + e1.toString());

         System.exit(0);
      }//end catch

      finally
      {
	    //Close the connection
        closeConnection();
	  }// end finally

   }//end runClient


   private void closeConnection ()
       {
		 try
		 {
		  osToServer.close();    //Close output stream
		  isFromServer.close();  // Close input stream
		  connectToServer.close(); //Close socket
		 } //end try

         catch (IOException ioException)
         {
		   ioException.printStackTrace();
	     } //end catch
	   } //end closeConnection

//--------------------------------------------------------

   public static void main(String[] args)
   {
	 //declare a client1 object
	 Client_Modified client1 = new Client_Modified();
	 client1.runClient();
   } //end main

   //---------------------------------------------------------

}// end class
