package org.kansus.ocr.mlp;

/**
 * Representa um padrão da rede neural.
 */
public class Padrao {

	private String elemento;
	private Representacao representacao;
	private int[] saida;

	/**
	 * @param elemento nome do elemento deste padrão.
	 * @param representacao {@link Representacao} dos dados do padrão.
	 * @param saida saída do padrão.
	 */
	public Padrao(String elemento, Representacao representacao, int[] saida) {
		this.elemento = elemento;
		this.representacao = representacao;
		this.saida = saida;
	}

	/**
	 * @return nome do elemento deste padrão.
	 */
	public String getElemento() {
		return elemento + "\n" + representacao.toString() + "\n";
	}

	/**
	 * @return {@link Representacao} dos dados do padrão.
	 */
	public Representacao getRepresentacao() {
		return representacao;
	}

	/**
	 * @return saída do padrão.
	 */
	public int[] getSaida() {
		return saida;
	}
}