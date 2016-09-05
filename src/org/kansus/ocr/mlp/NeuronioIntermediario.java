package org.kansus.ocr.mlp;


/**
 * Representa um neurônio da(s) camada(s) intermediária(s) da Multilayer
 * Perceptron.
 */
public class NeuronioIntermediario extends Neuronio {

	@Override
	public void calcularErro() {
		double erro;
		CamadaIntermediaria camadaIntermediaria = (CamadaIntermediaria) this.getCamada();
		CamadaSaida camadaSaida = (CamadaSaida) camadaIntermediaria.getCamadaSaida();
		erro = camadaSaida.getSomatorioErroPonderado(this.getNumero()) * (1 - (Math.pow(this.getValorObtido(), 2)));
		this.setErro(erro);
		/*System.out.print ("Neurônio #" + this.getNumero() + " da camada intermediária ===> ");
		System.out.print ("VO: " + this.getValorObtido() + "; ");
		System.out.println ("E: " + this.getErro());*/
	}
}