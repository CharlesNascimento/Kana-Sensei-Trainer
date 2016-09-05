package org.kansus.ocr.mlp;

/**
 * Representa a camada de entrada da Multilayer Perceptron.
 */
public class CamadaEntrada extends Camada {

	/**
	 * @param nome
	 *            nome da camada.
	 * @param numeroNeuronios
	 *            quantidade de neurônios.
	 * @param numeroEntradas
	 *            quantidade de entradas.
	 */
	public CamadaEntrada(String nome, int numeroNeuronios, int numeroEntradas) {
		super(nome, numeroNeuronios, numeroEntradas);
	}

	@Override
	public void inicializaCamada(int numeroNeuronios, int numeroEntradas) {
		for (int i = 0; i < numeroNeuronios; i++) {
			this.adicionaNeuronio(new NeuronioEntrada(), numeroEntradas);
		}
	}
}