public class Message{
	private int clock_pid;
	private int quant;
	private String text_mes;

	public Message(int c, String text){
		clock_pid = c;
		quant = 0;
		text_mes = text;
	}

	public Message(){
		quant = 1;
	}

	public void receivedAck(){
		quant++;
	}

	public void setMessage(int c, String text){
		clock_pid = c;
		text_mes = text;
	}

	public int getClock(){
		return clock_pid;
	}

	public int getQuantAck(){
		return quant;
	}

	public String getText(){
		return text_mes;
	}
}