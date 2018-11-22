/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

import java.io.*;
import java.net.*;
import java.util.*;

class Eleicao implements Runnable {
    public Socket csocket;
    private int resource = /*Algum numero*/;
    private int pid = /*Entre 0 e 9*/;
    private int[] adj;
    private Node no;

    public Eleicao (Socket connectionSocket){
        this.csocket = connectionSocket;
    }

    public static void main(String argv[]) throws Exception {

        adj = new int[/*Numero de nos adjacentes a ele*/] {/*pid dos nos adjacentes*/};
        new Thread(receive).start();
        new Thread(send).start();

    }

    private static Runnable receive = new Runnable() {
        public void run() {
            try{
                ServerSocket welcomeSocket = new ServerSocket(/*Entre 6520 e 6529*/);

                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();

                    Mutual s = new Mutual(connectionSocket);
                    Thread t = new Thread(s);
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
            int i;

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.csocket.getInputStream()));
            String e = inFromClient.readLine();
            if(e.equals("2") == true){
                no.receiveResp();
                if(checkQuant() == true){
                    if(no.getProcess() == -1){
                        StringBuilder sendMessage = new StringBuilder();
                        sendMessage = sendMessage.append("4" + '\n' + Integer.toString(no.getResource()) + '\n');
                        sendMessage = sendMessage.append(Integer.toString(no.getId()) + '\n' + Integer.toString(pid) + '\n');
                        for(i = 0; i < adj.length(); i++){
                            Socket clientSocket = new Socket(/*IP*/, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(sendMessage.toString());
                            clientSocket.close();
                        }
                    } else {
                        StringBuilder toFather = new StringBuilder();
                        toFather = toFather.append("1" + '\n' + Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n');
                        Socket clientSocket = new Socket(/*IP*/, 6520+no.getProcess());
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(toFather.toString());
                        clientSocket.close();
                    }
                }
            }
            else if(e.equals("1") == true){
                int rResource = Integer.parseInt(inFromClient.readLine());
                int rPid = Integer.parseInt(inFromClient.readLine());
                no.receiveSon(rResource, rPid);
                no.receiveResp();
                if(checkQuant() == true){
                    if(no.getProcess() == -1){
                        StringBuilder sendMessage = new StringBuilder();
                        sendMessage = sendMessage.append("4" + '\n' + Integer.toString(no.getResource()) + '\n');
                        sendMessage = sendMessage.append(Integer.toString(no.getId()) + '\n' + Integer.toString(pid) + '\n');
                        for(i = 0; i < adj.length(); i++){
                            Socket clientSocket = new Socket(/*IP*/, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(sendMessage.toString());
                            clientSocket.close();
                        }
                    } else {
                        StringBuilder toFather = new StringBuilder();
                        toFather = toFather.append("1" + '\n' + Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n');
                        Socket clientSocket = new Socket(/*IP*/, 6520+no.getProcess());
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(toFather.toString());
                        clientSocket.close();
                    }
                }
            }
            else if(e.equals("0") == true){
                int rPid = Integer.parseInt(inFromClient.readLine());
                if(checkPai(rPid) == true){
                    StringBuilder toNode = new StringBuilder();
                    toNode = toNode.append("2" + '\n');
                    Socket clientSocket = new Socket(/*IP*/, 6520+rPid);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes(toNode.toString());
                    clientSocket.close();
                } else {
                    StringBuilder toNode = new StringBuilder();
                    toNode = toNode.append("0" + '\n' + Integer.toString(pid) + '\n');
                    for(i = 0; i < adj.length(); i++){
                        if(adj[i] != no.getProcess()){
                            Socket clientSocket = new Socket(/*IP*/, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(toNode.toString());
                            clientSocket.close();
                        }
                    }
                }
            }
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

                while(true){
                    StringBuilder sendMessage = new StringBuilder();
                    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                    String eleicao = inFromUser.readLine();
                    if(checkPai(-1) == false){
                        sendMessage = sendMessage.append("0" + '\n' + Integer.toString(pid) + '\n');
                        System.out.printf("Iniciando Eleição\n\n");
                        for(i = 0; i < adj.length(); i++){
                            Socket clientSocket = new Socket(/*IP*/, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(sendMessage.toString());
                            clientSocket.close();
                        }
                    }else{
                        System.out.printf("Já esta ocorrendo uma eleição\n\n");
                    }
                }
            }
            catch(IOException a) {
                a.printStackTrace();
            }
        }
    };

     public static synchronized boolean checkQuant(){
        if(no.getQuant() == adj.length()){
            no.resetQuant();
            return true;
        }
        return false;
    }

    public static synchronized boolean checkPai(int pai){
        if(no == null){
            no = new Node(pai, resource, pid);
            return false;
        }
        return true;
    }
}
