public class Message{
	private int clock_pid;
	private int quant;
    private int resourceId;
    private int pId;

	public Message(int c, int r, int p){
		clock_pid = c;
		quant = 0;
        resourceId = r;
        pId = p;
	}

	public void receivedAck(){
		quant++;
	}

	public int getClock(){
		return clock_pid;
	}

	public int getQuantAck(){
		return quant;
	}

	public int getResource(){
		return resourceId;
	}

    public int getProcess(){
        return pId;
    }
}
