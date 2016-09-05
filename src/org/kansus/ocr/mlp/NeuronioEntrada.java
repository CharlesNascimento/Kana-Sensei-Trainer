package org.kansus.ocr.mlp;

/**
 * Representa um neur�nio da camada de entrada da Multilayer Perceptron.
 */
public class NeuronioEntrada extends Neuronio {

	@Override
	public void fazerSinapse() {
		// nos neur�nios de entrada, a sa�da � a entrada recebida.
		this.setValorObtido(this.getTerminalEntrada(0));
	}

	@Override
	public void calcularErro() {
		// neur�nios da camada de entrada n�o possuem erro.
	}
}