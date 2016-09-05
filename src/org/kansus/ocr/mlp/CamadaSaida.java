package org.kansus.ocr.mlp;

import java.text.DecimalFormat;

/**
 * Representa a camada de sa�da da Multilayer Perceptron.
 */
public class CamadaSaida extends Camada {

	/**
	 * @param nome
	 *            nome da camada.
	 * @param numeroNeuronios
	 *            quantidade de neur�nios.
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
	 * Efetua o c�lculo do somat�rio do erro ponderado, aplicando a equa��o
	 * somat�rio(ei * wji).
	 * 
	 * @param j
	 *            �ndice do neur�nio conectado.
	 * @return somat�rio do erro ponderado de todos os neur�nios da camada de sa�da.
	 */
	public double getSomatorioErroPonderado(int j) {
		double somatorio = 0;
		for (Neuronio n : this.getNeuronios()) {
			somatorio += n.getErro() * n.getPesos()[j];
		}
		return somatorio;
	}

	/**
	 * Efetua o c�lculo do erro m�dio quadr�tico desta camada, aplicando a
	 * equa��o 1/2*somatorio((dj - xj)^2) em todos os neur�nios da camada.
	 * 
	 * @return erro m�dio quadr�tico.
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
	 * @return array com o valor obtido para cada neur�nio da camada.
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
	 * @return resultado <code>String</code> com a sa�da em modo fracion�rio.
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