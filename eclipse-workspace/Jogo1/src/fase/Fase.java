package fase;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import controle.*;
import dao.*;
import inimigos.*;
import janela.Janela;
import player.Jogador;

public class Fase extends JPanel implements ActionListener, MouseListener{
	/**
	 * 
	 */
	protected Image fundo; // background da fase
	public static int faseAtual;
	protected static Jogador player1;
	protected static Jogador player2;
	protected static LinkedList<Inimigo> ListaInimigos = new LinkedList<Inimigo>();
	protected long tempoExcluiInimigo=1000;
	protected Timer timerFase;// timer
	protected java.util.Timer timerPatoDourado;
	protected java.util.Timer timerPatoPequeno;
	protected java.util.Timer timerPatoFedido;
	protected java.util.Timer timerPatoNormal;
	protected java.util.Timer timerExcluidor;
	protected java.util.Timer timerTemporizador;
	protected Graphics2D graficos;
	protected int taxaDeAtualizacao;
	protected boolean usarSave;
	protected JLabel labelU1; 
	protected JLabel labelU2; 
	protected JLabel labelTimer; 
	protected String formato;
	protected static int constanteNegativaPontuacao;
	protected static int constantePositivaPontuacao;
	protected static int tempoDecorrido;
	/*===================================================================================
	 *Construtora da Fase, que atraves dela � feita a implementa��o da fase, 
	 *recebemos O tempo de spawn de cada inimigo, o src de background da fase e o numero 
	 *da fase em quest�o, 
	 *===================================================================================*/
	public Fase(int tempoPatoDourado,
			int tempoPatoFedido,
			int tempoPatoNormal,
			int tempoPatoPequeno, 
			int tempoBallonBoy,
			String imgURL,
			int numeroFase, 
			int taxaDeAtualizacao,
			boolean usarSave,
			String formato,
			int constN,
			int constP){
		//setLayout(null);
		tempoDecorrido=65;
		setFocusable(true); // melhora desempenho;
		setDoubleBuffered(true);
		this.taxaDeAtualizacao = taxaDeAtualizacao;
		ImageIcon img = new ImageIcon(imgURL); // recebo o src da img;
		img.setImage(img.getImage().getScaledInstance(Janela.getLarguraJanela(), Janela.getAlturaJanela(), ABORT));
		this.fundo = img.getImage();
		addKeyListener(new TecladoAdapter()); // leitura das teclas
		this.faseAtual=numeroFase;
		this.constantePositivaPontuacao = constP;
		this.constanteNegativaPontuacao =constN;
		timerTemporizador = new java.util.Timer();
		timerTemporizador.scheduleAtFixedRate(new TaskTemporizador(),0,1000);
		timerFase=  new Timer(taxaDeAtualizacao, this); // 
		timerFase.start();
		this.usarSave=usarSave;
		this.formato = formato;
		if(usarSave){
			construirSaveAntigo(formato);
		}
		else {
			inicializaJogadores();
		}
		inicializaInimigos(tempoPatoDourado, tempoPatoFedido, tempoPatoNormal,tempoPatoPequeno);
		
		labelU1 = new JLabel();
		labelU2 = new JLabel();
		labelTimer= new JLabel();

		add(labelU1);
		add(labelU2);
		add(labelTimer);
		labelU1.setFont(new Font("Verdana", Font.PLAIN, 27));
		labelU2.setFont(new Font("Verdana", Font.PLAIN, 27));
		labelTimer.setFont(new Font("Verdana", Font.PLAIN, 40));
		labelU1.setForeground(Color.RED);
		labelU2.setForeground(Color.BLUE);
		
	}
//	************************************************
//	atravz deste metodo criamos a fase atual
//	com os dados do inimigo e os dados dos players
//	contidos no possivel save, .txt ou .json.
//	***********************************************
	public void construirSaveAntigo(String formato) {
		InimigoDao iDAO = new InimigoDao();
		JogadorDAO jDAO = new JogadorDAO();
		if(formato=="JSON") {
			iDAO.construtoraJSON(faseAtual,Janela.getUsername1());
			player1 = jDAO.construtoraJSON(faseAtual,Janela.getUsername1(),1);
			player1.load();
			player2 = jDAO.construtoraJSON(faseAtual,Janela.getUsername1(),2);
			player2.load();
			jDAO.excluirSaveJSON(faseAtual, Janela.getUsername1());
			iDAO.excluirSaveJSON(faseAtual,Janela.getUsername1());}
		else if(formato=="TXT") {
			iDAO.construtoraTXT(faseAtual,Janela.getUsername1());
			player1 = jDAO.construtoraTXT(faseAtual,Janela.getUsername1(), 1);
			player1.load();
			player2 = jDAO.construtoraTXT(faseAtual,Janela.getUsername1(), 2);
			player2.load();
			jDAO.excluirSaveTXT(faseAtual, Janela.getUsername1());
			iDAO.excluirSaveTXT(faseAtual,Janela.getUsername1());
		}
	}
	/*============================================================================
	 * Metodo para spawnar os jogadores, invocando sua construtora e passando
	 * qual player �, src de sua imagem, e suas coordenadas de spawn.
	 * ===========================================================================
	 */
	public void inicializaJogadores() {
		player1 = new Jogador(1, "res\\mira1.png", 100, 100);
		player1.load();
		player2 = new Jogador(2, "res\\mira2.png", 100, 100);
		player2.load();
	}
	/*============================================================================
	 * Metodo para spawnar os Inimigos, invocando sua construtora e passando
	 * qual inimigo �, src de sua imagem, e suas coordenadas de spawn.
	 * ===========================================================================
	 */
	public void inicializaInimigos(int tempoPatoDourado,int tempoPatoFedido,int tempoPatoNormal,int tempoPatoPequeno) {
		timerPatoDourado = new java.util.Timer();
		timerPatoDourado.scheduleAtFixedRate(new SpawnerPatoDourado(),30,tempoPatoDourado);
		timerPatoFedido = new java.util.Timer();
		timerPatoFedido.scheduleAtFixedRate(new SpawnerPatoFedido(),30,tempoPatoFedido);
		timerPatoNormal = new java.util.Timer();
		timerPatoNormal.scheduleAtFixedRate(new SpawnerPatoNormal(),30,tempoPatoNormal);
		timerPatoPequeno = new java.util.Timer();
		timerPatoPequeno.scheduleAtFixedRate(new SpawnerPatoPequeno(),30,tempoPatoPequeno);
		timerExcluidor = new java.util.Timer();
		timerExcluidor.scheduleAtFixedRate(new ExcluiInimigos(), 500, tempoExcluiInimigo);
		
	}
	/*==================public void pain==========================================
	 * Metodo responsavel para fazer com que as imagens sejam "printadas" graficamente
	 * na tela do usuario.
	 * ===========================================================================
	 */
	public void paint(Graphics g) { 
		this.requestFocus();
		graficos = (Graphics2D) g;
		graficos.drawImage(fundo, 0, 0, null);
		Iterator<Inimigo> it = getListaInimigos().iterator();
		while(it.hasNext()) {
			try{
				Inimigo p = it.next();
				graficos.drawImage(p.getImg(),p.getX(),p.getY(),this);}
			catch(Exception e) {
				break;
			}
				
		}
		labelU1.setText(Janela.getUsername1()+": "+player1.getPontuacao());
		labelU2.setText("                                                                          "+Janela.getUsername2()+": "+player2.getPontuacao());
		labelTimer.setText("                                   "+tempoDecorrido);
		labelU1.print(g);
		labelU2.print(g);
		labelTimer.print(g);
		
		graficos.drawImage(player1.getImg(), player1.getX(), player1.getY(), this);
		graficos.drawImage(player2.getImg(), player2.getX(), player2.getY(), this);

		g.dispose();
	}
	/*====================actionPerformed===========================
	 * Metodo responsavel para faze a atualiza��o constante da fase.
	 * =============================================================
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		player1.update();
		player2.update();
		Iterator<Inimigo> it = getListaInimigos().iterator();
		while(it.hasNext()) {
			try {
			Inimigo p = it.next();
			p.update();}
			catch(Exception p) {
				break;
			}
		}
		repaint();
	}
//	******************************************************
//	com este metodo encerramos nossa fase atual,
//	deletamos todos o inimigos assim como os player e
//	automaticamente salvamos o ranking dos players.
//	*******************************************************
	public void encerra() {
		timerFase.stop();
		insereRanking();
		player1.deletImg();
		player2.deletImg();
		
		Iterator<Inimigo> iterador= getListaInimigos().iterator();
		while(iterador.hasNext()) {
			try {
			Inimigo in = iterador.next();
			in.deletImg();
			iterador.remove();
			}
			catch(Exception in){
				break;}
			}
		timerTemporizador.cancel();
		timerPatoDourado.cancel();
		timerPatoFedido.cancel();
		timerPatoPequeno.cancel();
		timerPatoNormal.cancel();
		timerExcluidor.cancel();
		this.removeAll();
		validate();
		repaint();
	}
//	**************************************************
//	Chamada dos metodos responsavel pelo salvamento
//	dos rankings
//	**************************************************
	public void insereRanking() {
		//inserir em txt
		RankingDAO.insereRankingDAOTXT(player1.getPontuacao(),player2.getPontuacao(),faseAtual,Janela.getUsername1(),Janela.getUsername2());
		
		//inserir em json
		RankingDAO.insereRankingDAOJSON(player1.getPontuacao(),player2.getPontuacao(),faseAtual,Janela.getUsername1(),Janela.getUsername2());

	}
	/*================class TecladoAdapter=======================
	 * Esta classe lida com a leitura das teclas pressionadas
	 * pelo usuario.
	 * ==========================================================
	 */
	private class TecladoAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			player1.keyPressed(e);
			player2.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			player1.KeyRelease(e);
			player2.KeyRelease(e);
		}
	}
//	****************************************************
//	Chamamos nossos metodos de salvamento, de acordo
//	com a escolha do usuario, podendo ser salvo em
//	.txt ou em .json
//	****************************************************
	public static void salvar(String formato) {
		Iterator<Inimigo> it = getListaInimigos().iterator();
		JogadorDAO j = new JogadorDAO();	
		InimigoDao pDAO = new InimigoDao();
		int i=0;
		if (formato=="JSON") {
		while(it.hasNext()) {
			i++;
			try {
				Inimigo p = it.next();
				pDAO.inserirJSON(p,i,Janela.getUsername1(),faseAtual);
			}
			catch(Exception p){
				break;}
			}	
		j.inserirJSON(faseAtual,player1, player2, Janela.getUsername1());
		}
		else if (formato=="TXT")
		{
			while(it.hasNext()) {
				i++;
				try {
					Inimigo p = it.next();
					pDAO.inserirTXT(p,i,Janela.getUsername1(),faseAtual);
				}
				catch(Exception p){
					break;}
				}	
			j.inserirTXT(faseAtual,player1, player2, Janela.getUsername1());
			}
		}
	public static void diminuiSegundo() {
		tempoDecorrido--;
	}
	public static int getConstNegativa() {
		return constanteNegativaPontuacao;
	}
	public static int getConstPositiva() {
		return constantePositivaPontuacao;
	}
	
	public static LinkedList<Inimigo> getListaInimigos() {
		return ListaInimigos;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
	
	
