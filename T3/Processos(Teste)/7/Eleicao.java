/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

import java.io.*;
import java.net.*;
import java.util.*;

class Eleicao implements Runnable {
    public Socket csocket;
    private static String ip;
    private static int resource = 18;
    private static int pid = 7;
    private static int adj[] = {4, 5, 9};
    private static Node no = new Node();

    public Eleicao (Socket connectionSocket){
        this.csocket = connectionSocket;
    }

    public static void main(String argv[]) throws Exception {

        new Thread(receive).start();

    }

    private static Runnable receive = new Runnable() {
        public void run() {
            try{
                ServerSocket welcomeSocket = new ServerSocket(6527);

                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();

                    Eleicao s = new Eleicao(connectionSocket);
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
            if (e.equals("5") == true){
                ip = inFromClient.readLine();
            }
            else if (e.equals("4") == true){
                StringBuilder sendMessage = new StringBuilder();
                if(checkPai(-1, pid) == false){
                    sendMessage = sendMessage.append("0" + '\n' + Integer.toString(pid) + '\n' + Integer.toString(pid) + '\n');
                    System.out.printf("Iniciando Eleição\n\n");
                    for(i = 0; i < adj.length; i++){
                        Socket clientSocket = new Socket(ip, 6520+adj[i]);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(sendMessage.toString());
                        clientSocket.close();
                    }
                }else{
                    System.out.printf("Esse nó já esta participando de uma eleição\n\n");
                }
            }
            else if (e.equals("3") == true){
                int maiorResource = Integer.parseInt(inFromClient.readLine());
                int maiorPid = Integer.parseInt(inFromClient.readLine());
                int rcvPid = Integer.parseInt(inFromClient.readLine());

                if (no.getEleicaoId() != -1 && rcvPid == no.getProcess()){
                    StringBuilder sendBest = new StringBuilder();
                    sendBest = sendBest.append("3" + '\n' + Integer.toString(maiorResource) + '\n' + Integer.toString(maiorPid) + '\n' + Integer.toString(pid) + '\n');

                    for(i = 0; i < adj.length; i++){
                        if(adj[i] != no.getProcess()){
                            Socket clientSocket = new Socket(ip, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(sendBest.toString());
                            clientSocket.close();
                        }
                    }

                    System.out.println("Líder elegido: " + maiorPid + "\n");
                    no.finalizaEleicao();
                }
            }
            else if(e.equals("2") == true){
                int eId = Integer.parseInt(inFromClient.readLine());
                if(receiveMsg(2, eId, -1, -1)){
                    if(receiveMsg(4, -1, -1, -1) == true){
                        if(no.getProcess() == -1){
                            System.out.println("Líder elegido: " + no.getId() + "\n");
                            StringBuilder sendMessage = new StringBuilder();
                            sendMessage = sendMessage.append("3" + '\n' + Integer.toString(no.getResource()) + '\n');
                            sendMessage = sendMessage.append(Integer.toString(no.getId()) + '\n' + Integer.toString(pid) + '\n');
                            for(i = 0; i < adj.length; i++){
                                Socket clientSocket = new Socket(ip, 6520+adj[i]);
                                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                outToServer.writeBytes(sendMessage.toString());
                                clientSocket.close();
                            }
                            sendAnswer();
                            receiveMsg(3, -1, -1, -1);
                        } else {
                            sendFather();
                        }
                    }
                }
            }
            else if(e.equals("1") == true){
                int rResource = Integer.parseInt(inFromClient.readLine());
                int rPid = Integer.parseInt(inFromClient.readLine());
                int eId = Integer.parseInt(inFromClient.readLine());
                if(receiveMsg(1, eId, rPid, rResource)){
                    if(receiveMsg(4, -1, -1, -1) == true){
                        if(no.getProcess() == -1){
                            System.out.println("Líder elegido: " + no.getId() + "\n");
                            StringBuilder sendMessage = new StringBuilder();
                            sendMessage = sendMessage.append("3" + '\n' + Integer.toString(no.getResource()) + '\n');
                            sendMessage = sendMessage.append(Integer.toString(no.getId()) + '\n' + Integer.toString(pid) + '\n');
                            for(i = 0; i < adj.length; i++){
                                Socket clientSocket = new Socket(ip, 6520+adj[i]);
                                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                outToServer.writeBytes(sendMessage.toString());
                                clientSocket.close();
                            }
                            sendAnswer();
                            receiveMsg(3, -1, -1, -1);
                        } else {
                            sendFather();
                        }
                    }
                }
            }
            else if(e.equals("0") == true){
                int rPid = Integer.parseInt(inFromClient.readLine());
                int eId = Integer.parseInt(inFromClient.readLine());
                if(checkPai(rPid, eId) == true){
                    if(no.getEleicaoId() < eId){
                        System.out.println("Eleicao com maior prioridade ja esta acontecendo" + '\n');
                        receiveMsg(3, -1, -1, -1);
                        if(!(checkPai(rPid, eId))){
                            StringBuilder toNode = new StringBuilder();
                            toNode = toNode.append("0" + '\n' + Integer.toString(pid) + '\n' + Integer.toString(eId) + '\n');
                            for(i = 0; i < adj.length; i++){
                                if(adj[i] != no.getProcess()){
                                    Socket clientSocket = new Socket(ip, 6520+adj[i]);
                                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                    outToServer.writeBytes(toNode.toString());
                                    clientSocket.close();
                                }
                            }
                            if(receiveMsg(4, -1, -1, -1) == true && no.getProcess() != -1){
                                sendFather();
                            }
                        }
                    } else if(eId == no.getEleicaoId()){
                        StringBuilder toNode = new StringBuilder();
                        toNode = toNode.append("2" + '\n' + Integer.toString(no.getEleicaoId()));
                        Socket clientSocket = new Socket(ip, 6520+rPid);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(toNode.toString());
                        clientSocket.close();
                    }
                } else {
                    StringBuilder toNode = new StringBuilder();
                    toNode = toNode.append("0" + '\n' + Integer.toString(pid) + '\n' + Integer.toString(eId) + '\n');
                    for(i = 0; i < adj.length; i++){
                        if(adj[i] != no.getProcess()){
                            Socket clientSocket = new Socket(ip, 6520+adj[i]);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes(toNode.toString());
                            clientSocket.close();
                        }
                    }
                    if(receiveMsg(4, -1, -1, -1) == true && no.getProcess() != -1){
                        sendFather();
                    }
                }
            }
        }
        catch(IOException a) {
                a.printStackTrace();
        }
    }

    public static synchronized boolean checkPai(int pai, int eId){
        if(no.getEleicaoId() == -1){
            no.iniciaEleicao(pai, resource, pid, eId);
            return false;
        }
        return true;
    }

    public static synchronized void sendFather(){
        try{
            StringBuilder toFather = new StringBuilder();
            toFather = toFather.append("1" + '\n' + Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n' + Integer.toString(no.getEleicaoId()) + '\n');
            Socket clientSocket = new Socket(ip, 6520+no.getProcess());
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(toFather.toString());
            clientSocket.close();
        }
        catch(IOException a) {
            a.printStackTrace();
        }
    }

    public static synchronized boolean receiveMsg(int control, int eId, int rId, int rResource){
        switch(control){
            case 1:
                if(no.getEleicaoId() == eId){
                    no.receiveSon(rResource, rId);
                    no.receiveResp();
                    return true;
                }
            break;

            case 2:
                if(no.getEleicaoId() == eId){
                    no.receiveResp();
                    return true;
                }
            break;

            case 3:
                no.finalizaEleicao();
            break;

            case 4:
                if(no.getQuant() == adj.length){
                    no.resetQuant();
                    return true;
                }
            break;
        }
        return false;
    }

    public static void sendAnswer(){
        try{
            Socket clientSocket = new Socket(ip, 6530);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(Integer.toString(no.getResource()) + '\n' + Integer.toString(no.getId()) + '\n');
            clientSocket.close();
        }
        catch(IOException a){
            a.printStackTrace();
        }
    }

}
