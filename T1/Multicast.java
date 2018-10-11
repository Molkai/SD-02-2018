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
    public static LinkedList<Message> acks = new LinkedList<Message>();

  public Multicast (Socket connectionSocket, int c){
    this.csocket = connectionSocket;
    clock = c;
    pid = 0;
  }

  public static void main(String argv[]) throws Exception {

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
      if(ack.equals("1") == true){
        int clock_pid = Integer.parseInt(inFromClient.readLine());
        //Coisas da fila de msg
                boolean achouMsg = false;

                for(Message msg : messages){
                    if (msg.getClock() == clock_pid){ //Procura pela mensagem sendo agradecida na fila de mensagens
                        msg.receivedAck();
                        System.out.printf("Ack para clock global: \"%d\" e texto: \"%s\" recebido!\n Acks faltando: %d\n", msg.getClock(), msg.getText(), 3 - msg.getQuantAck());

                        if (messages.peekFirst() != null && messages.peekFirst().getQuantAck() == 3){ //Checa se a primeira msg da fila recebeu todos os acks
                            Message entrega = messages.remove(); // Se sim, remove da fila e entrega para a aplicação
                            System.out.printf("Mensagem removida da fila e entregue: %s\n", entrega.getText());
                            System.out.printf("Clock: %d\n", clock);
                        }

                        achouMsg = true;
                        break;
                    }
                }

                if (achouMsg == false){ //Ack chegou antes da mensagem
                    boolean achouAck = false;
                    for(Message msgAck : acks){
                        if (msgAck.getClock() == clock_pid){ // Pro caso de chegar mais de um ack antecipadamente, assim só incrementa quant ao invés de adicionar mais uma msg na lista
                            msgAck.receivedAck();
                            achouAck = true;
                            break;
                        }
                    }

                    if (achouAck == false){ // Se não achou, simplesmente adiciona o ack na fila de acks
                        Message newAck = new Message(clock_pid);
                        acks.add(newAck);
                    }

                    System.out.printf("Ack para clock global: \"%d\" recebido! \n Entretanto, a mensagem correspondente não foi encontrada na fila \n Aguardando mensagem para agradecer...", clock_pid);
                }
                //fim das coisas da fila de msg
        int messageClock = Integer.parseInt(inFromClient.readLine());
        if(messageClock > clock)
          clock = messageClock + 1;
        else
          clock++;
      }
      else if(ack.equals("0") == true){
        int messageClock = Integer.parseInt(inFromClient.readLine());
        int messagePid = Integer.parseInt(inFromClient.readLine());
        String text = inFromClient.readLine();
        System.out.printf("%s\n", text);
        //Coisas das fila de msg
                Message newMsg = new Message(messageClock*10 + messagePid, text);
                for(Message msgAck : acks){
                    if(msgAck.getClock() == newMsg.getClock()){ //Se encontrar um ack que chegou antecipadamente
                        System.out.printf("Ack antecipado para clock global: \"%d\" e texto: \"%s\" foi encontrado!\n", newMsg.getClock(), newMsg.getText());
                        for(int j = 0; j < msgAck.getQuantAck(); j++){ // Agradece pelo número de acks recebidos para aquele clock global
                            newMsg.receivedAck();
                            System.out.printf("Ack recebido!\n");
                        }

                        acks.remove(acks.indexOf(msgAck));
                        break;
                    }
                }

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

                System.out.printf("Mensagem com clock global \"%d\" e texto: \"%s\" foi adicionada na fila!\n", newMsg.getClock(), newMsg.getText());
                //fim das coisas da fila de msg
        if(messageClock > clock)
          clock = messageClock + 1;
        else
          clock++;
        int clock_pid;
        clock_pid = messageClock*10+messagePid; // Aqui não deveria ser messageClock*10+messagePid?
        StringBuilder ackMessage = new StringBuilder();
        ackMessage = ackMessage.append("1" + '\n' + Integer.toString(clock_pid) + '\n' + Integer.toString(clock) + '\n'
        + Integer.toString(pid) + '\n');
        for(i = 0; i < 3; i++){
          Socket clientSocket = new Socket(/*"IP"*/, 6520+i);
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
            String message = inFromUser.readLine();
            clock++;
            sendMessage = sendMessage.append("0" + '\n' + Integer.toString(clock) + '\n' + Integer.toString(pid) + '\n' + message + '\n');
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

}
