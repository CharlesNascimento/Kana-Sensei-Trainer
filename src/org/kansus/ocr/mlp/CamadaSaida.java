package org.kansus.ocr.mlp;

import java.text.DecimalFormat;

/**
 * Representa a camada de saída da Multilayer Perceptron.
 */
public class CamadaSaida extends Camada {

	/**
	 * @param nome
	 *            nome da camada.
	 * @param numeroNeuronios
	 *            quantidade de neurônios.
	 * @param numeroEntradas
	 *            quantidade de entradas.
	 */
	public CamadaSaida(String nome, int numeroNeuronios, int numeroEntradas) {
		super(nome, numeroNeuronios, numeroEntradas);
	}

	@Override
	public void inicializaCamada(int numeroNeuronios, int numeroEntradas) {
		this.getNeuronios().clear();
		for (int i = 0; i < numeroNeuronios; i++) {
			this.adicionaNeuronio(new NeuronioSaida(), numeroEntradas);
		}

	}

	/**
	 * Efetua o cálculo do somatório do erro ponderado, aplicando a equação
	 * somatório(ei * wji).
	 * 
	 * @param j
	 *            índice do neurônio conectado.
	 * @return somatório do erro ponderado de todos os neurônios da camada de saída.
	 */
	public double getSomatorioErroPonderado(int j) {
		double somatorio = 0;
		for (Neuronio n : this.getNeuronios()) {
			somatorio += n.getErro() * n.getPesos()[j];
		}
		return somatorio;
	}

	/**
	 * Efetua o cálculo do erro médio quadrático desta camada, aplicando a
	 * equação 1/2*somatorio((dj - xj)^2) em todos os neurônios da camada.
	 * 
	 * @return erro médio quadrático.
	 */
	public double getErroMedioQuadratico() {
		double emq = 0;
		double diferencial;
		for (Neuronio n : this.getNeuronios()) {
			diferencial = n.getValorDesejado() - n.getValorObtido();
			emq += Math.pow(diferencial, 2);
		}
		emq = 0.5 * emq;
		return emq;
	}

	/**
	 * @return array com o valor obtido para cada neurônio da camada.
	 */
	public int[] getResultadoObtido() {
		int[] resultadoObtido = new int[this.getNumeroNeuronios()];
		int i = 0;
		for (Neuronio n : this.getNeuronios()) {
			if (n.getValorObtido() > 0)
				resultadoObtido[i++] = 1;
			else
				resultadoObtido[i++] = -1;
		}
		return resultadoObtido;
	}

	/**
	 * @return resultado <code>String</code> com a saída em modo fracionário.
	 */
	public String getResultadoFracionario() {
		String resultadoFracionario = " ( ";
		DecimalFormat formatoFracionario = new DecimalFormat("#.##");
		for (Neuronio n : this.getNeuronios()) {
			resultadoFracionario += formatoFracionario.format(n
					.getValorObtido()) + "; ";
		}
		resultadoFracionario += ")";
		return resultadoFracionario;
	}
}