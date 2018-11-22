/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

public class Node{
    private int pai;
    private int quantResp;
    private int melhorId;
    private int melhorRecurso;

	public Node(int p, int r, int id){
        pai = p;
        if(p == -1)
            quantResp = 0;
        else
            quantResp = 1;
        melhorRecurso = r;
        melhorId = id;
	}

    public void receiveResp(){
        quantResp++;
    }

    public void resetQuant(){
        quantResp = 0;
    }

    public void receiveSon(int r, int id){
        if(melhorRecurso < r){
            melhorId = id;
            melhorRecurso = r;
        }
    }

    public int getQuant(){
        return quantResp;
    }

	public int getResource(){
		return melhorRecurso;
	}

    public int getProcess(){
        return pai;
    }

    public int getId(){
        return melhorId;
    }
}
