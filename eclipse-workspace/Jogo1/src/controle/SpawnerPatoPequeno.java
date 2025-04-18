package controle;

import java.util.LinkedList;
import java.util.TimerTask;

import fase.Fase;
import inimigos.Inimigo;
import inimigos.PatoPequeno;
import janela.Janela;
//*******************************************************************
//Recebemos nossa lista de inimigos, e criamos um novo inimigo
//passando para sua construtora, sua coordenada, x e y, e sua
//pontua��o
//*******************************************************************
public class SpawnerPatoPequeno extends TimerTask {
	@Override
	public void run() {
		LinkedList<Inimigo>lista=Fase.getListaInimigos();
		PatoPequeno p = new PatoPequeno(Janela.getLarguraJanela()+100,(int)(Math.random()*((Janela.getAlturaJanela()-200)+1)),30*Fase.getConstPositiva());
		p.load();
		lista.add(p);
	}
}