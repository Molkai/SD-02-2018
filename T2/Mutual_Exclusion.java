/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

import java.io.*;
import java.net.*;
import java.util.*;

class Mutual implements Runnable {
    public Socket csocket;
    public static int clock;
    public static int pid;
    public static LinkedList<Message> messages = new LinkedList<Message>();
    public static int[] using = new int[3];
    public static int[] remover = new int[2];

    public Mutual (Socket connectionSocket, int c){
        this.csocket = connectionSocket;
        clock = c;
        pid = /*0,1 ou 2*/;
    }

    public static void main(String argv[]) throws Exception {

        for(int i = 0; i < 3; i++)
            using[i] = 0;
        clock = 1;
        new Thread(receive).start();
        new Thread(send).start();

    }

    private static Runnable receive = new Runnable() {
        public void run() {
            try{
                ServerSocket welcomeSocket = new ServerSocket(/*6520, 6521 ou 6522*/);

                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();

                    Mutual s = new Mutual(connectionSocket, clock);
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
            int messageClock = Integer.parseInt(inFromClient.readLine());
            System.out.print("Recurso sendo usado no momento, aguardando ser liberado.\n\n");
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
            Message entrega = ackIn(inFromClient, clock_pid);
            if(entrega != null){
                if(entrega.getResource() == 1){
                    using[0] = 1;
                    resource1();
                    using[0] = 0;
                }
                else if(entrega.getResource() == 2){
                    using[1] = 1;
                    resource2();
                    using[1] = 0;
                }
                else{
                    using[2] = 1;
                    resource3();
                    using[2] = 0;
                }
                 // Se sim, remove da fila e entrega para a aplicação
                i = 0;
                for(Message msg2 : messages)
                    System.out.printf("%d ", msg2.getClock());
                System.out.printf("\n\n");
                for(Message msg2 : messages)
                    if(msg2.getResource() == entrega.getResource()){
                        StringBuilder ackMessage = new StringBuilder();
                        ackMessage = ackMessage.append("1" + '\n' + Integer.toString(msg2.getClock()) + '\n' + Integer.toString(clock) + '\n');
                        Socket clientSocket = new Socket(/*IP*/, 6520+msg2.getProcess());
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(ackMessage.toString());
                        clientSocket.close();
                        remover[i] = messages.indexOf(msg2) - i;
                        i++;
                        System.out.printf("ACK para: %d clock: %d\n",msg2.getProcess(), msg2.getClock());
                    }
                System.out.printf("\n%d\n\n", i);
                for(int j = 0; j < i; j++){
                    Message msg = messages.remove(remover[j]);
                    System.out.printf("Retirada da fila clock: %d\n", msg.getClock());
                }
                System.out.printf("\n");
            }
        }
        else if(ack.equals("0") == true){
            Message msg = null;
            int messageClock = Integer.parseInt(inFromClient.readLine());
            int messagePid = Integer.parseInt(inFromClient.readLine());
            int messageRid = Integer.parseInt(inFromClient.readLine());
            if(messageClock > clock)
                clock = messageClock + 1;
            else
                clock++;
            int clock_pid;
            clock_pid = messageClock*10+messagePid;
            for(Message msgaux : messages){
                if(msgaux.getResource() == messageRid && msgaux.getProcess() == pid){
                    msg = msgaux;
                    break;
                }
            }
            if(pid != messagePid && using[messageRid-1] == 1){
                Message newMsg = new Message(messageClock*10 + messagePid, messageRid, messagePid);
                if (messages.peekFirst() == null){ //Se a fila de msgs estiver vazia;
                    messages.addFirst(newMsg);
                }else{
                    boolean flag = false;
                    for(Message msgaux : messages){
                        if (newMsg.getClock() < msgaux.getClock()){
                            messages.add(messages.indexOf(msgaux), newMsg); //Adiciona ordenadamente na fila
                            flag = true;
                            break;
                        }
                    }
                    if(flag == false)
                        messages.addLast(newMsg);
                }
                StringBuilder nackMessage = new StringBuilder();
                nackMessage = nackMessage.append("2" + '\n' +  Integer.toString(clock) + '\n');
                Socket clientSocket = new Socket(/*IP*/, 6520+messagePid);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(nackMessage.toString());
                clientSocket.close();
            }
            else if(pid != messagePid && msg != null){
                if(clock_pid > msg.getClock()){
                    Message newMsg = new Message(messageClock*10 + messagePid, messageRid, messagePid);
                    if (messages.peekFirst() == null){ //Se a fila de msgs estiver vazia;
                        messages.addFirst(newMsg);
                    }else{
                        boolean flag = false;
                        for(Message msgaux : messages){
                            if (newMsg.getClock() < msgaux.getClock()){
                                messages.add(messages.indexOf(msgaux), newMsg); //Adiciona ordenadamente na fila
                                flag = true;
                                break;
                            }
                        }
                        if(flag == false)
                            messages.addLast(newMsg);
                    }
                    StringBuilder nackMessage = new StringBuilder();
                    nackMessage = nackMessage.append("2" + '\n' +  Integer.toString(clock) + '\n');
                    Socket clientSocket = new Socket(/*IP*/, 6520+messagePid);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes(nackMessage.toString());
                    clientSocket.close();
                } else{
                    StringBuilder ackMessage = new StringBuilder();
                    ackMessage = ackMessage.append("1" + '\n' + Integer.toString(clock_pid) + '\n' + Integer.toString(clock) + '\n');
                    Socket clientSocket = new Socket(/*IP*/, 6520+messagePid);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes(ackMessage.toString());
                    clientSocket.close();
                }
            } else {
                StringBuilder ackMessage = new StringBuilder();
                ackMessage = ackMessage.append("1" + '\n' + Integer.toString(clock_pid) + '\n' + Integer.toString(clock) + '\n');
                Socket clientSocket = new Socket(/*IP*/, 6520+messagePid);
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
                    if(flag == false){
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
                    }
                }
            }
            catch(IOException a) {
                a.printStackTrace();
            }
        }
    };

    public static synchronized Message ackIn(BufferedReader inFromClient, int clock_pid){
        for(Message msg : messages)
            if (msg.getClock() == clock_pid){
                msg.receivedAck();
                System.out.printf("Ack para clock global \"%d\" recebido!\nAcks faltando: %d\n\n", msg.getClock(), 3 - msg.getQuantAck());
                if (msg.getQuantAck() == 3){
                    Message entrega = messages.remove(messages.indexOf(msg));
                    return entrega;
                }
                break;
            }
        return null;
    }

    public static void resource1(){
        try{
            System.out.print("Usando o recurso 1\n\n");
            Thread.sleep(5000);
            System.out.print("Recurso 1 liberado.\n\n");
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
    }

    public static void resource2(){
        try{
            System.out.print("Usando o recurso 2\n\n");
            Thread.sleep(7000);
            System.out.print("Recurso 2 liberado.\n\n");
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
    }

    public static void resource3(){
        try{
            System.out.print("Usando o recurso 3\n\n");
            Thread.sleep(10000);
            System.out.print("Recurso 3 liberado.\n\n");
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
    }


}
