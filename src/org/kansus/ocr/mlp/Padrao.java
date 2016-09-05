package org.kansus.ocr.mlp;

/**
 * Representa um padr�o da rede neural.
 */
public class Padrao {

	private String elemento;
	private Representacao representacao;
	private int[] saida;

	/**
	 * @param elemento nome do elemento deste padr�o.
	 * @param representacao {@link Representacao} dos dados do padr�o.
	 * @param saida sa�da do padr�o.
	 */
	public Padrao(String elemento, Representacao representacao, int[] saida) {
		this.elemento = elemento;
		this.representacao = representacao;
		this.saida = saida;
	}

	/**
	 * @return nome do elemento deste padr�o.
	 */
	public String getElemento() {
		return elemento + "\n" + representacao.toString() + "\n";
	}

	/**
	 * @return {@link Representacao} dos dados do padr�o.
	 */
	public Representacao getRepresentacao() {
		return representacao;
	}

	/**
	 * @return sa�da do padr�o.
	 */
	public int[] getSaida() {
		return saida;
	}
}