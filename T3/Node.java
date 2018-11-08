/*
Nomes: Vinícius de Souza Carvalho RA: 726592
       Vitor Mesquita Fogaça          726597
*/

public class Node{
    private int pai;
    private int quantResp;
    private int melhorId;
    private int maiorRecurso;

	public Node(int p, int r, int id){
        pai = p;
        quantResp = 0;
        maiorRecurso = r;
        melhorId = id;
	}

    public void receiveResp(){
        quantResp++;
    }

    public void resetQuant(){
        quantResp = 0;
    }

    public int receiveSon(int r, int id){
        if(maiorRecurso < r){
            melhorId = id;
            maiorRecurso = r;
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

    public int[] getId(){
        return melhorId;
    }
}
