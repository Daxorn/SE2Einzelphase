package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    //Define necessary variables
    Socket clientSocket;
    Button btnSend;
    Button btnCalc;
    EditText userInput;
    TextView answerFromSrv;
    TextView result;
    String serverOutput;
    public static final int SERVER_PORT = 53212;
    public static final String SERVER_URL = "se2-isys.aau.at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCalc = findViewById(R.id.btn_Calc);
        result = findViewById(R.id.res_View);
        btnSend = findViewById(R.id.btn_Send);
        userInput = findViewById(R.id.inpt_ImmNumb);
        answerFromSrv = findViewById(R.id.srvr_Ans_View);
    }

    //Calculating Function
    public void calculation(View view) {

        String stringInput = convertToString(userInput);
        StringBuilder primeNumbers = new StringBuilder();
        for(int index = 0; index < stringInput.length(); index++){
            if(isPrime(Character.getNumericValue(stringInput.charAt(index)))){
                primeNumbers.append(stringInput.charAt(index));
            }
        }

       result.setText(primeNumbers.toString());
    }
    //----------------------------------------------------------------------------------------------
    //Sending Function
    public void sendToServer(View view) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create connection to server
                    clientSocket = new Socket(SERVER_URL, SERVER_PORT);
                    //-------------------------------------------------

                    //Create necessary streams inFromServer/outToServer
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    //-------------------------------------------------

                    //Send and receive information
                    outToServer.writeBytes(convertToString(userInput) + '\n');
                    serverOutput = inFromServer.readLine();
                    //-------------------------------------

                    //Set answer on view
                    answerFromSrv.setText(serverOutput);
                    //---------------------------------

                    //Close all streams
                    inFromServer.close();
                    outToServer.close();
                    clientSocket.close();
                    //-------------------

                } catch (UnknownHostException e) {
                    serverOutput = "! Unknown Host !";
                    e.printStackTrace();
                } catch (IOException e) {
                    serverOutput = "! Server Error !";
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    //-------------------------------------------------------------------------------------

    public String convertToString(EditText userInput){
        return userInput.getText().toString();
    }
    static boolean isPrime(int inputNumber) {
        boolean isItPrime = true;

        if (inputNumber <= 1) {
            isItPrime = false;

            return isItPrime;
        } else {
            for (int i = 2; i <= inputNumber / 2; i++) {
                if ((inputNumber % i) == 0) {
                    isItPrime = false;

                    break;
                }
            }

            return isItPrime;
        }
    }
}