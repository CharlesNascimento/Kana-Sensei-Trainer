package org.kansus.ocr.mlp;

/**
 * Representa um neurônio da camada de entrada da Multilayer Perceptron.
 */
public class NeuronioEntrada extends Neuronio {

	@Override
	public void fazerSinapse() {
		// nos neurônios de entrada, a saída é a entrada recebida.
		this.setValorObtido(this.getTerminalEntrada(0));
	}

	@Override
	public void calcularErro() {
		// neurônios da camada de entrada não possuem erro.
	}
}