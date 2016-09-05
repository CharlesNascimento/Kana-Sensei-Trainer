package org.kansus.ocr.mlp;

import java.util.Random;

/**
 * Classe base que representa um neurônio da rede neural.
 */
public abstract class Neuronio {

	private Camada camada;
	private int numero;

	private double[] terminaisEntrada;
	private double[] pesos;

	private double valorDesejado;
	private double valorObtido;
	private double erro;
	
	private static Random gerador = new Random();

	/**
	 * Inicializa o neurônio.
	 * 
	 * @param numeroEntradas
	 *            quantidade de entradas do neurônio.
	 */
	public void inicializaNeuronio(int numeroEntradas) {
		this.terminaisEntrada = new double[numeroEntradas];
		this.pesos = new double[numeroEntradas];
	}

	/**
	 * @return número identificador deste neurônio.
	 */
	public final int getNumero() {
		return numero;
	}

	/**
	 * @param erro
	 *            erro deste neurônio.
	 */
	public final void setErro(double erro) {
		this.erro = erro;
	}

	/**
	 * @param numero
	 *            número para identificar este neurôrio.
	 */
	public final void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * @return erro deste neurônio.
	 */
	public double getErro() {
		return this.erro;
	}

	/**
	 * @return {@link Camada} à qual este neurônio pertence.
	 */
	public Camada getCamada() {
		return camada;
	}

	/**
	 * @param camada
	 *            {@link Camada} à qual este neurônio pertence.
	 */
	public void setCamada(Camada camada) {
		this.camada = camada;
	}

	/**
	 * @return valor desejado como saída.
	 */
	public double getValorDesejado() {
		return valorDesejado;
	}

	/**
	 * @param valorDesejado
	 *            valor desejado como saída.
	 */
	public void setValorDesejado(double valorDesejado) {
		this.valorDesejado = valorDesejado;
	}

	/**
	 * @return valor obtido como saída.
	 */
	public double getValorObtido() {
		return this.valorObtido;
	}

	protected void setValorObtido(double valorObtido) {
		this.valorObtido = valorObtido;
	}

	/**
	 * @param valoresPesos
	 *            pesos das conexões deste neurônio.
	 */
	public void setPesos(double[] valoresPesos) {
		this.pesos = valoresPesos;
	}

	/**
	 * @return pesos das conexões deste neurônio.
	 */
	public double[] getPesos() {
		return pesos;
	}

	/**
	 * Adiciona um terminal de entrada para este neurônio.
	 * 
	 * @param indice
	 * @param terminalEntrada
	 */
	public void setTerminalEntrada(int indice, double terminalEntrada) {
		this.terminaisEntrada[indice] = terminalEntrada;
	}

	/**
	 * @param indice
	 *            indice do terminal de entrada no vetor.
	 * @return terminal de entrada
	 */
	public double getTerminalEntrada(int indice) {
		return this.terminaisEntrada[indice];
	}

	/**
	 * Efetua uma sinapse, calculando a saída desse neurônio. A saída é
	 * calculada aplicando a função de ativação sobre o somatório do produto
	 * entre terminais de entrada e seus respectivos pesos.
	 */
	public void fazerSinapse() {
		double somatorio = 0;
		for (int i = 0; i < this.terminaisEntrada.length; i++) {
			somatorio += (this.terminaisEntrada[i] * this.pesos[i]);
		}
		//System.out.println("SOMATORIO: " + somatorio);
		this.setValorObtido(this.funcaoAtivacao(somatorio / (this.camada.getNumeroNeuronios())));
		//System.out.println("VALOR OBTIDO: " + this.getValorObtido());
	}

	/**
	 * Função de ativação para calcular o valor obtido na sinapse. A função
	 * utilizada nesse caso é: ( 1 - e**-2x) f(x) = ( 1 + e**-2x).
	 * 
	 * @param x
	 *            somatório do produto entre terminais de entrada e seus
	 *            respectivos pesos.
	 * @return saída do neurônio.
	 */
	private double funcaoAtivacao(double x) {
		return ((1 - Math.exp(-2 * x)) / (1 + Math.exp(-2 * x)));
	}

	/**
	 * Gera pesos aleatórios para as conexões deste neurônio.
	 */
	public void gerarPesosIniciais() {
		for (int j = 0; j < this.pesos.length; j++) {
			this.pesos[j] = gerador.nextDouble();
		}
	}

	/**
	 * Ajusta os pesos deste neurônio, aplicando a equação wij += n*ej*xi para
	 * todos os terminais de entrada.
	 * 
	 * @param taxaDeAprendizado
	 *            Taxa de aprendizado(n) da rede.
	 */
	public void ajustarPesos(double taxaDeAprendizado) {
		int i;
		double[] novosPesos = new double[this.terminaisEntrada.length];

		for (i = 0; i < this.terminaisEntrada.length; i++) {
			novosPesos[i] = this.getPesos()[i]
					+ (taxaDeAprendizado * this.getErro() * this.terminaisEntrada[i]);
			/*System.out.println ("Erro: " + this.getErro() + "; Terminal de Entrada: " + this.terminaisEntrada[j]);
			System.out.println ("Peso #" + j + " ===> valor anterior: " 
					+ this.pesos[j] + "; valor atual: " + novosPesos[j] + ".");*/
		}

		this.setPesos(novosPesos);
	}

	/**
	 * Calcula o erro deste neurônio.
	 */
	public abstract void calcularErro();
}