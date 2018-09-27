import java.io.*;
import java.net.*;

class Multicast implements Runnable {
	public Socket csocket;
	public static int clock;
	public static int pid;

	public Multicast (Socket connectionSocket){
		this.csocket = connectionSocket;
		clock = 0;
		pid = 0;
	}

	public static void main(String argv[]) throws Exception {

		new Thread(receive).start();
		new Thread(send).start();

	}

	private static Runnable receive = new Runnable() {
        public void run() {
        	try{
	        	ServerSocket welcomeSocket = new ServerSocket(/*PORT*/);

				while(true) {
					Socket connectionSocket = welcomeSocket.accept();

					Multicast s = new Multicast(connectionSocket);
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
				int messageClock = Integer.parseInt(inFromClient.readLine());
				if(messageClock > clock)
					clock = messageClock + 1;
				else
					clock++;
				System.out.printf("%d\n", clock);
			}
			else if(ack.equals("0") == true){
				int messageClock = Integer.parseInt(inFromClient.readLine());
				int messagePid = Integer.parseInt(inFromClient.readLine());
				String text = inFromClient.readLine();
				System.out.printf("%s\n", text);
				//Coisas das fila de msg
				if(messageClock > clock)
					clock = messageClock + 1;
				else
					clock++;
				int clock_pid;
				clock_pid = messageClock*10+pid;
				StringBuilder ackMessage = new StringBuilder();
				ackMessage = ackMessage.append("1" + '\n' + Integer.toString(clock_pid) + '\n' + Integer.toString(clock) + '\n' 
				+ Integer.toString(pid) + '\n');
				for(i = 0; i < 3; i++){
					Socket clientSocket = new Socket(/*IP*/, 6520+i);
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
			    	for(i = 0; i < 1; i++){
							Socket clientSocket = new Socket(/*IP*/, 6520+i);
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