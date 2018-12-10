import java.io.*;
import java.net.*;
import java.util.*;

class Central implements Runnable {
    public Socket csocket;

    public Central (Socket connectionSocket){
        this.csocket = connectionSocket;
    }

    public static void main(String argv[]) throws Exception {

        new Thread(receive).start();
        new Thread(send).start();

    }

    private static Runnable receive = new Runnable() {
        public void run() {
            try{
                ServerSocket welcomeSocket = new ServerSocket(6530);

                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();

                    Central c = new Central(connectionSocket);
                    Thread t = new Thread(c);
                    t.start();
                }
            }
            catch(IOException a) {
                a.printStackTrace();
            }
        }
    };

    public void run(){
        try{
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.csocket.getInputStream()));
            String bestResource = inFromClient.readLine();
            String bestPid = inFromClient.readLine();
            System.out.println("Nó eleito: " + bestPid + '\n' + "Seu recurso: " + bestResource + '\n');
        }
        catch(IOException a) {
                a.printStackTrace();
        }
    }

  private static Runnable send = new Runnable() {
        public void run() {
            try{
                int i;
                boolean flag;

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                String ip = inFromUser.readLine();
                for(i = 0; i < 10; i++){
                    Socket clientSocket = new Socket(ip, 6520+i);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("5" + '\n' + ip + '\n');
                    clientSocket.close();
                }
                while(true){
                    StringBuilder sendMessage = new StringBuilder();
                    sendMessage = sendMessage.append("4" + '\n');
                    String eleicao = inFromUser.readLine();
                    String[] nosString = eleicao.split(" ");
                    for(i = 0; i < nosString.length; i++){
                        Socket clientSocket = new Socket(ip, 6520+Integer.parseInt(nosString[i]));
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(sendMessage.toString());
                        clientSocket.close();
                    }
                }
            }
            catch(IOException a) {
                a.printStackTrace();
            }
        }
    };

}
