package org.kansus.ocr.mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kansus.ocr.NeuralNetworkListener;

/**
 * Representa uma rede neural Multilayer Perceptron.
 */
public class MLP {

	private double taxaDeAprendizado = 0.5;
	private double erroMinimo = 0.01;
	private int numeroMaximoCiclos = 500000000;

	private ArrayList<Padrao> conjuntoTreinamento = new ArrayList<Padrao>();

	private Camada camadaEntrada;
	private Camada camadaIntermediaria;
	private Camada camadaSaida;

	private Map<String, Camada> camadas = new HashMap<String, Camada>();

	// progresso
	private NeuralNetworkListener progressListener;
	private int progressoAteAqui;
	private float relevancia = 0.8f;

	public MLP() {
		this.camadaEntrada = new CamadaEntrada("C0", 576, 1);
		this.camadaIntermediaria = new CamadaIntermediaria("C1", 8, this.camadaEntrada.getNumeroNeuronios());
		this.camadaSaida = new CamadaSaida("C2", 1, this.camadaIntermediaria.getNumeroNeuronios());

		this.camadas.put(this.camadaEntrada.getNome(), this.camadaEntrada);
		this.camadas.put(this.camadaIntermediaria.getNome(), this.camadaIntermediaria);
		this.camadas.put(this.camadaSaida.getNome(), this.camadaSaida);

		((CamadaIntermediaria) this.camadaIntermediaria).setCamadaSaida(camadaSaida);
	}

	/**
	 * Efetua o treinamento da rede MLP atrav�s do algoritmo backpropagation.
	 * 
	 * @param progressListener
	 *            listener de progresso.
	 * @param progresso
	 *            progresso que j� foi efetuado at� aqui.
	 */
	public void treinar(NeuralNetworkListener progressListener, int progresso) {
		this.progressListener = progressListener;
		this.progressoAteAqui = progresso;
		this.backpropagation();
	}

	/**
	 * Algoritmo backpropagation para treinamento da rede. Funcionamento: 1.
	 * Inicializar pesos e par�metros; 2. Repita at� o erro ser m�nimo ou a
	 * realiza��o de um dado n�mero de ciclos: 2.1. Para cada padr�o de
	 * treinamento X: 2.1.1. Definir sa�da da rede atrav�s de fase forward;
	 * 2.1.2. Comparar sa�das produzidas com as sa�das desejadas; 2.1.3.
	 * Atualizar pesos dos nodos atrav�s da fase backward.
	 */
	private void backpropagation() {
		int numeroCiclos = 1;
		this.inicializarPesos();
		double somaErroMedioQuadratico;
		double mediaErroMedioQuadratico;
		DecimalFormat formatoPercentagem = new DecimalFormat("###0.00%");
		int progressoTreinamento = 0;
		progressListener.onProgressChanged("Treinando rede...", this.progressoAteAqui);

		long inicio = System.currentTimeMillis();

		do {
			somaErroMedioQuadratico = 0;
			for (Padrao p : this.conjuntoTreinamento) {
				this.configuraValoresDesejados(p);
				this.forward(p.getRepresentacao());
				this.calcularErros();
				this.backward();
				somaErroMedioQuadratico += this.getErroMedioQuadratico();
			}
			mediaErroMedioQuadratico = somaErroMedioQuadratico / this.conjuntoTreinamento.size();
			numeroCiclos++;
			if ((numeroCiclos % 10) == 0) {
				progressoTreinamento = (int) (((float) numeroCiclos / (float) numeroMaximoCiclos) * 100f * relevancia);
				this.progressListener.onProgressChanged("Erro: " + mediaErroMedioQuadratico + " / " + "�poca: " + numeroCiclos,
						progressoTreinamento + progressoAteAqui);
			}
			System.out.println("Erro: " + mediaErroMedioQuadratico + " | " + "�poca atual: " + numeroCiclos);
		} while ((this.erroMinimo < mediaErroMedioQuadratico) && (numeroCiclos < this.numeroMaximoCiclos));

		long tempoPercorrido = System.currentTimeMillis() - inicio;
		progressListener.onProgressCompleted("Treinamento efetuado com sucesso.\nCiclos: " + numeroCiclos + "\nM�dia EMQ: "
				+ formatoPercentagem.format(mediaErroMedioQuadratico) + "\nDura��o: " + (tempoPercorrido / 1000F)
				+ " segundos.");
	}

	/**
	 * Configura os valores desejados nos neur�nios da camada de sa�da.
	 * 
	 * @param p
	 *            padr�o
	 */
	private void configuraValoresDesejados(Padrao p) {
		Camada c = this.camadaSaida;
		int i = 0;
		for (Neuronio n : c.getNeuronios()) {
			n.setValorDesejado(p.getSaida()[i]);
			i++;
		}
	}

	/**
	 * Executa a fase foward da rede MLP. Funcionamento: 1. A entrada �
	 * apresentada � camada de rede (camada C0); 2. Para cada camada Ci a partir
	 * da camada de entrada: 2.1. Ap�s os nodos da camada Ci (i>0) calcularem
	 * seus sinais de sa�da, estes servem como entrada para a defini��o das
	 * sa�das produzidas pelos nodos da camada C(i+1); 3. As sa�das produzidas
	 * pelos nodos da �ltima camada s�o comparadas �s sa�das desejadas.
	 * 
	 * @param representacao
	 *            {@link Representacao} a ser processada.
	 */
	private void forward(Representacao representacao) {
		int[] entradas = representacao.getValoresEmSerie();
		int i;
		Camada camadaAtual;
		Camada camadaAnterior = null;

		for (String nomeCamada : this.camadas.keySet()) {
			camadaAtual = this.camadas.get(nomeCamada);
			i = 0;
			if (camadaAtual instanceof CamadaEntrada) {
				for (Neuronio n : camadaAtual.getNeuronios()) {
					n.setTerminalEntrada(0, entradas[i++]);
					n.fazerSinapse();
				}

			} else {
				for (Neuronio nAtual : camadaAtual.getNeuronios()) {
					for (Neuronio nAnterior : camadaAnterior.getNeuronios()) {
						nAtual.setTerminalEntrada(i++, nAnterior.getValorObtido());
					}
					i = 0;
					nAtual.fazerSinapse();
				}
			}
			camadaAnterior = camadaAtual;
		}
	}

	/**
	 * Calcula os erros de todos os neur�nios da rede, come�ando da camada de
	 * sa�da.
	 */
	private void calcularErros() {
		Object[] chaves = this.camadas.keySet().toArray();
		int tamanhoChaves = chaves.length;
		Camada camadaAtual;
		for (int i = (tamanhoChaves - 1); i > 0; i--) {
			camadaAtual = this.camadas.get(chaves[i]);
			for (Neuronio n : camadaAtual.getNeuronios()) {
				n.calcularErro();
			}
		}
	}

	/**
	 * Executa a fase backward da rede MLP. Funcionamento: 1. A partir da �ltima
	 * camada, at� chegar na camada de entrada: 1.1. Os nodos da camada atual
	 * ajustam seus pesos de forma a reduzir seus erros; 1.2. O erro de um nodo
	 * das camadas intermedi�rias � calculado utilizando os erros dos nodos da
	 * camada seguinte conectadas a ele, ponderados pelos pesos das conex�es
	 * entre eles.
	 */
	private void backward() {
		Object[] chaves = this.camadas.keySet().toArray();
		int tamanhoChaves = chaves.length;

		Camada camadaAtual;
		for (int i = (tamanhoChaves - 1); i > 0; i--) {
			camadaAtual = this.camadas.get(chaves[i]);
			for (Neuronio n : camadaAtual.getNeuronios()) {
				n.ajustarPesos(this.taxaDeAprendizado);
			}
		}
	}

	/**
	 * Inicializa pesos nas camadas intermedi�rias e de sa�da da rede com
	 * valores rand�micos.
	 */
	private void inicializarPesos() {
		for (String elemento : this.camadas.keySet()) {
			Camada c = this.camadas.get(elemento);
			if (!(c instanceof CamadaEntrada)) {
				for (Neuronio n : c.getNeuronios()) {
					n.gerarPesosIniciais();
				}
			}
		}
	}

	/**
	 * Efetua o reconhecimento de uma entrada.
	 * 
	 * @param representacao
	 *            {@link Representacao} da entrada a ser reconhecida.
	 * @return <code>String</code> com o resultado do reconhecimento.
	 */
	public int reconhecer(Representacao representacao) {
		int[] resultadoObtido = null;
		this.forward(representacao);
		Camada c = this.camadaSaida;
		resultadoObtido = ((CamadaSaida) c).getResultadoObtido();
		System.out.println(((CamadaSaida) c).getResultadoFracionario());

		ArrayList<Integer> possiveisResultados = new ArrayList<Integer>();

		int resultado = 0;
		double maior = 0;

		for (int i = 0; i < resultadoObtido.length; i++) {
			if (resultadoObtido[i] == 1) {
				possiveisResultados.add(i);
			}
		}

		if (possiveisResultados.size() == 1) {
			resultado = possiveisResultados.get(0);
		} else if (possiveisResultados.size() == 0) {
			resultado = -1;
		} else {
			for (Integer i : possiveisResultados) {
				double atual = ((CamadaSaida) c).getNeuronios().get(i).getValorObtido();
				if (atual > maior) {
					resultado = i;
					maior = atual;
				}
			}
		}
		return resultado;
	}

	/**
	 * Adiciona um novo padr�o ao conjunto de treinamento.
	 * 
	 * @param elemento
	 *            nome do elemento do padr�o.
	 * @param representacao
	 *            {@link Representacao} dos dados do padr�o.
	 * @param saida
	 *            sa�da do padr�o.
	 */
	public void adicionarPadrao(String elemento, Representacao representacao, int[] saida) {
		Padrao p = new Padrao(elemento, representacao, saida);
		this.conjuntoTreinamento.add(p);
	}

	/**
	 * Remove todos os padr�es do conjunto de treinamento.
	 */
	public void removerPadroes() {
		this.conjuntoTreinamento.clear();
	}

	/**
	 * Redefine o n�mero de neur�nios na camada intermedi�ria.
	 * 
	 * @param novoNumeroNeuronios
	 *            nova quantidade de neur�nios.
	 */
	public void redefinirNeuroniosCamadaIntermediaria(int novoNumeroNeuronios) {
		this.camadaIntermediaria.inicializaCamada(novoNumeroNeuronios, this.camadaEntrada.getNumeroNeuronios());
		this.camadaSaida.inicializaCamada(this.getCamadaSaida().getNumeroNeuronios(), novoNumeroNeuronios);
	}

	/**
	 * Salva todos os pesos da rede neural em arquivo.
	 * 
	 * @param nomeArquivo
	 *            nome do arquivo.
	 */
	public void salvarPesosArquivo(String nomeArquivo) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(nomeArquivo)));

			for (String elemento : this.camadas.keySet()) {
				Camada c = this.camadas.get(elemento);
				if (!(c instanceof CamadaEntrada)) {
					for (Neuronio n : c.getNeuronios()) {
						for (int i = 0; i < n.getPesos().length; i++) {
							bw.write(String.valueOf(n.getPesos()[i]));
							if (i != n.getPesos().length - 1)
								bw.write(";");
						}
						bw.newLine();
					}
				}
			}
			bw.close();
		} catch (Exception e) {
			System.err.println("Erro escrevendo para arquivo.");
		}
	}

	/**
	 * Carrega os pesos da rede neural a partir de um arquivo.
	 * 
	 * @param nomeArquivo
	 *            nome do arquivo.
	 */
	public void carregarPesosArquivo(String nomeArquivo) {
		String[] pesos;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(nomeArquivo)));
			for (String elemento : this.camadas.keySet()) {
				Camada c = this.camadas.get(elemento);
				if (!(c instanceof CamadaEntrada)) {
					for (Neuronio n : c.getNeuronios()) {
						double novosPesos[] = new double[n.getPesos().length];
						pesos = br.readLine().split(";");
						for (int i = 0; i < n.getPesos().length; i++) {
							novosPesos[i] = Double.parseDouble(pesos[i]);
						}
						n.setPesos(novosPesos);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.err.println("Erro lendo do arquivo.");
		}
	}

	/**
	 * @return erro m�dio quadr�tico.
	 */
	private double getErroMedioQuadratico() {
		return ((CamadaSaida) this.camadaSaida).getErroMedioQuadratico();
	}

	/**
	 * @return taxa de aprendizado da rede neural.
	 */
	public final double getTaxaDeAprendizado() {
		return taxaDeAprendizado;
	}

	/**
	 * @param taxaDeAprendizado
	 *            taxa de aprendizado da rede neural.
	 */
	public final void setTaxaDeAprendizado(double taxaDeAprendizado) {
		this.taxaDeAprendizado = taxaDeAprendizado;
	}

	/**
	 * @return erro m�nimo da rede neural.
	 */
	public final double getErroMinimo() {
		return erroMinimo;
	}

	/**
	 * @param erroMinimo
	 *            erro m�nimo da rede neural.
	 */
	public final void setErroMinimo(double erroMinimo) {
		this.erroMinimo = erroMinimo;
	}

	/**
	 * @return n�mero m�ximo de ciclos no treinamento da rede neural.
	 */
	public final int getNumeroMaximoCiclos() {
		return numeroMaximoCiclos;
	}

	/**
	 * @param numeroMaximoCiclos
	 *            n�mero m�ximo de ciclos no treinamento da rede neural.
	 */
	public final void setNumeroMaximoCiclos(int numeroMaximoCiclos) {
		this.numeroMaximoCiclos = numeroMaximoCiclos;
	}

	/**
	 * @return {@link Camada} de entrada da rede neural.
	 */
	public final Camada getCamadaEntrada() {
		return camadaEntrada;
	}

	/**
	 * @return {@link Camada} intermedi�ria da rede neural.
	 */
	public final Camada getCamadaIntermediaria() {
		return camadaIntermediaria;
	}

	/**
	 * @return {@link Camada} de sa�da da rede neural.
	 */
	public final Camada getCamadaSaida() {
		return camadaSaida;
	}

	/**
	 * @return <code>String</code> com todos os padr�es do conjunto de
	 *         treinamento da rede neural.
	 */
	public String getPadroes() {
		String padroes = "";
		for (Padrao p : this.conjuntoTreinamento) {
			padroes += p.getElemento() + '\n';
		}
		return padroes;
	}
}