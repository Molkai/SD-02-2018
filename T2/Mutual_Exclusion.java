/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

import java.io.*;
import java.net.*;
import java.util.*;

class Multicast implements Runnable {
    public Socket csocket;
    public static int clock;
    public static int pid;
    public static LinkedList<Message> messages = new LinkedList<Message>();
    public static int[] using = new int[3];

    public Multicast (Socket connectionSocket, int c){
        this.csocket = connectionSocket;
        clock = c;
        pid = 0;
    }

    public static void main(String argv[]) throws Exception {

        for(int i = 0; i < 3; i++)
            using[i] = 0;
        new Thread(receive).start();
        new Thread(send).start();

    }

    private static Runnable receive = new Runnable() {
        public void run() {
            try{
                clock = 1;
                ServerSocket welcomeSocket = new ServerSocket(/*Porta entre 6520 e 5522 para três processos*/);

                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();

                    Multicast s = new Multicast(connectionSocket, clock);
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
        String ack = inFromClient.readLine();
        if(ack.equals("2") == true){
            String messageClock = Integer.parseInt(inFromClient.readLine());
            System.out.print("Recurso sendo usado no momento, aguardando ser liberado\n");
            if(messageClock > clock)
              clock = messageClock + 1;
            else
              clock++;
        }
        else if(ack.equals("1") == true){
            int clock_pid = Integer.parseInt(inFromClient.readLine());
            int messageClock = Integer.parseInt(inFromClient.readLine());
            if(messageClock > clock)
              clock = messageClock + 1;
            else
              clock++;

            for(Message msg : messages){
                if (msg.getClock() == clock_pid){ //Procura pela mensagem sendo agradecida na fila de mensagens
                    msg.receivedAck();
                    System.out.printf("Ack para clock global: \"%d\" recebido!\n Acks faltando: %d\n", msg.getClock(), 3 - msg.getQuantAck());

                    if (msg.getQuantAck() == 3){
                        if(msg.getResource == 1){
                            using[0] = 1;
                            resource1();
                        }
                        else if(msg.getResource == 2){
                            using[1] = 1;
                            resource2();
                        }
                        else{
                            using[2] = 1;
                            resource3();
                        }
                        Message entrega = messages.remove(messages.indexOf(msg)); // Se sim, remove da fila e entrega para a aplicação
                        for(Message msg2 : messages)
                            if(msg2.getResource == entrega.getResource){
                                StringBuilder ackMessage = new StringBuilder();
                                ackMessage = ackMessage.append("1" + '\n' + Integer.toString(msg2.getClock) + '\n' + Integer.toString(clock) + '\n');
                                Socket clientSocket = new Socket(/*"IP"*/, 6520+msg2.getProcess);
                                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                                outToServer.writeBytes(ackMessage.toString());
                                clientSocket.close();
                                msg = messages.remove(messages.indexOf(msg2));
                            }
                    }
                }
            }
            //fim das coisas da fila de msg
        }
        else if(ack.equals("0") == true){
            Message msg;
            int messageClock = Integer.parseInt(inFromClient.readLine());
            int messagePid = Integer.parseInt(inFromClient.readLine());
            int messageRid = Integer.parseInt(inFromClient.readLine());
            if(messageClock > clock)
                clock = messageClock + 1;
            else
                clock++;
            int clock_pid;
            clock_pid = messageClock*10+messagePid;
            for(msg : messages){
                if(msg.getResource() == messageRid && msg.getProcess() == pid)
                    break;
            }
            if((pid != messagePid && using[messageRid] == 1) || (pid != messagePid && msg != NULL && msg.getClock > clockpid)){
                Message newMsg = new Message(messageClock*10 + messagePid, messageRid, messagePid);
                if (messages.peekFirst() == null){ //Se a fila de msgs estiver vazia;
                    messages.add(newMsg);
                }else{
                    for(Message msg : messages){
                        if (newMsg.getClock() > msg.getClock()){
                            messages.add(messages.indexOf(msg), newMsg); //Adiciona ordenadamente na fila
                            break;
                        }
                    }
                }
                StringBuilder nackMessage = new StringBuilder();
                nackMessage = nackMessage.append("2" + '\n' +  Integer.toString(clock) + '\n');
                Socket clientSocket = new Socket(/*"IP"*/, 6520+messagePid);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(nackMessage.toString());
                clientSocket.close();
            }
            else {
                StringBuilder ackMessage = new StringBuilder();
                ackMessage = ackMessage.append("1" + '\n' + Integer.toString(clock_pid) + '\n' + Integer.toString(clock) + '\n');
                Socket clientSocket = new Socket(/*"IP"*/, 6520+messagePid);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(ackMessage.toString());
                clientSocket.close();
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

                while(true){
                    StringBuilder sendMessage = new StringBuilder();
                    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                    String resourceId = inFromUser.readLine();
                    clock++;
                    Message newMsg = new Message(clock*10 + pid, resourceId, pid);
                    sendMessage = sendMessage.append("0" + '\n' + Integer.toString(clock) + '\n' + Integer.toString(pid) + '\n' + resourceId + '\n');
                    if (messages.peekFirst() == null){ //Se a fila de msgs estiver vazia;
                        messages.add(newMsg);
                    }else{
                        for(Message msg : messages){
                            if (newMsg.getClock() > msg.getClock()){
                                messages.add(messages.indexOf(msg), newMsg); //Adiciona ordenadamente na fila
                                break;
                            }
                        }
                    }
                    System.out.print("Pedindo o uso do recurso %s\n", resourceId);
                    for(i = 0; i < 3; i++){
                        Socket clientSocket = new Socket(/*"IP"*/, 6520+i);
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

    public void resource1(){

    }

    public void resource2(){

    }

    public void resource3(){

    }


}
