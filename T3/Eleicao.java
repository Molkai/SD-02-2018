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
                    StringBuilder toFather = new StringBuilder();
                    toFather = toFather.append("1" + '\n' + Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n');
                    Socket clientSocket = new Socket(/*IP*/, 6520+no.getProcess());
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes(toFather.toString());
                    clientSocket.close();
                }
            }
            else if(e.equals("1") == true){
                int rResource = Integer.parseInt(inFromClient.readLine());
                int rPid = Integer.parseInt(inFromClient.readLine());
                no.receiveSon(rResource, rPid);
                no.receiveResp();
                if(checkQuant() == true){
                    StringBuilder toFather = new StringBuilder();
                    toFather = toFather.append("1" + '\n' + Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n');
                    Socket clientSocket = new Socket(/*IP*/, 6520+no.getProcess());
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes(toFather.toString());
                    clientSocket.close();
                }
                no = null;
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
                    flag = false;
                    StringBuilder sendMessage = new StringBuilder();
                    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                    String resourceId = inFromUser.readLine();
                    for(Message msg : messages){
                        if(msg.getResource() == Integer.parseInt(resourceId) && msg.getProcess() == pid){
                            flag = true;
                            break;
                        }
                    }
                    if(flag == false && using[Integer.parseInt(resourceId) - 1] != 1){
                        clock++;
                        Message newMsg = new Message(clock*10 + pid, Integer.parseInt(resourceId), pid);
                        sendMessage = sendMessage.append("0" + '\n' + Integer.toString(clock) + '\n' + Integer.toString(pid) + '\n' + resourceId + '\n');
                        if (messages.peekFirst() == null){ //Se a fila de msgs estiver vazia;
                            messages.addFirst(newMsg);
                        }else{
                            boolean flagQueue = false;
                            for(Message msgaux : messages){
                                if (newMsg.getClock() < msgaux.getClock()){
                                    messages.add(messages.indexOf(msgaux), newMsg); //Adiciona ordenadamente na fila
                                    flagQueue = true;
                                    break;
                                }
                            }
                            if(flagQueue == false)
                                messages.addLast(newMsg);
                        }
                        System.out.printf("Pedindo o uso do recurso %s\n\n", resourceId);
                        for(i = 0; i < 3; i++){
                            Socket clientSocket = new Socket(/*IP*/, 6520+i);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(sendMessage.toString());
                            clientSocket.close();
                        }
                    }else{
                        System.out.printf("\nO recurso %d ja esta sendo usado por este processo, ou o processo ja esta na fila para utiliza-lo\n\n", Integer.parseInt(resourceId));
                    }
                }
            }
            catch(IOException a) {
                a.printStackTrace();
            }
        }
    };

     public static synchronized boolean checkQuant(){
        if(no.getQuant() == adj.lenght()){
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
