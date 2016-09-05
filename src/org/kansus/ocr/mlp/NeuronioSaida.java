package org.kansus.ocr.mlp;

public class NeuronioSaida extends Neuronio {

	@Override
	public void calcularErro() {
		double erro;
		// ei = (di - xi) * F(yi)
		erro = (this.getValorDesejado() - this.getValorObtido()) * (1 - (Math.pow(this.getValorObtido(), 2)));
		/*
		 * System.out.println("CAMADA: " + this.getCamada().getNome() +
		 * " VALOR DESEJADO: " + this.getValorDesejado() + " VALOR OBTIDO: " +
		 * this.getValorObtido() + " NEURÔNIO: " + this.getNumero() +
		 * " ERRO CALCULADO: " + erro);
		 */
		this.setErro(erro);
	}
}