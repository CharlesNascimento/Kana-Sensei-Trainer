package org.kansus.ocr.mlp;

/**
 * Representa uma camada intermediária (escondida) da Multilayer Perceptron.
 */
public class CamadaIntermediaria extends Camada {

	private Camada camadaSaida = null;

	/**
	 * @param nome
	 *            nome da camada.
	 * @param numeroNeuronios
	 *            quantidade de neurônios.
	 * @param numeroEntradas
	 *            quantidade de entradas.
	 */
	public CamadaIntermediaria(String nome, int numeroNeuronios,
			int numeroEntradas) {
		super(nome, numeroNeuronios, numeroEntradas);
	}

	@Override
	public void inicializaCamada(int numeroNeuronios, int numeroEntradas) {
		this.getNeuronios().clear();
		for (int i = 0; i < numeroNeuronios; i++) {
			this.adicionaNeuronio(new NeuronioIntermediario(), numeroEntradas);
		}
	}

	/**
	 * @param camadaSaida
	 *            camada de saída.
	 */
	public void setCamadaSaida(Camada camadaSaida) {
		this.camadaSaida = camadaSaida;
	}

	/**
	 * @return camada de saída.
	 */
	public Camada getCamadaSaida() {
		return camadaSaida;
	}
}