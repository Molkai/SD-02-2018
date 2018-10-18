/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

import java.io.*;
import java.net.*;
import java.util.*;

class Resource {

    public static void main(String argv[]) throws Exception {

        try{
            ServerSocket welcomeSocket = new ServerSocket(6523);

            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.csocket.getInputStream()));
                int clockpid = Integer.parseInt(inFromClient.readLine());
                String message = inFromClient.readLine();
                System.out.printf("Mensagem com clock global \"%d\" e texto: \"%s\" chegou!\n", clockpid, message);
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                StringBuilder sendMessage = new StringBuilder();
                sendMessage = sendMessage.append(inFromUser.readLine() + '\n');
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(sendMessage.toString());
                connectionSocket.close();
            }
        } catch(IOException a) {
              a.printStackTrace();
        }
    }

}
