package org.kansus.ocr.mlp;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe base que representa uma camada da Multilayer Perceptron.
 */
public abstract class Camada {

	private String nome;
	private List<Neuronio> neuronios = new ArrayList<Neuronio>();

	/**
	 * @param nome
	 *            nome da camada.
	 * @param numeroNeuronios
	 *            quantidade de neurônios.
	 * @param numeroEntradas
	 *            quantidade de entradas.
	 */
	public Camada(String nome, int numeroNeuronios, int numeroEntradas) {
		this.nome = nome;
		this.inicializaCamada(numeroNeuronios, numeroEntradas);
	}

	/**
	 * Inicializa esta camada.
	 * 
	 * @param numeroNeuronios
	 *            quantidade de neurônios.
	 * @param numeroEntradas
	 *            quantidade de entradas.
	 */
	public abstract void inicializaCamada(int numeroNeuronios,
			int numeroEntradas);

	/**
	 * @return nome da camada.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome
	 *            nome da camada.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return quantidade de neurônios desta camada.
	 */
	public int getNumeroNeuronios() {
		return this.neuronios.size();
	}

	/**
	 * @return lista de neurônios desta camada.
	 */
	public List<Neuronio> getNeuronios() {
		return neuronios;
	}

	/**
	 * Adiciona um novo neurônio à esta camada.
	 * 
	 * @param novoNeuronio
	 *            neurônio a ser adicionado.
	 * @param numeroEntradas
	 */
	public void adicionaNeuronio(Neuronio novoNeuronio, int numeroEntradas) {
		novoNeuronio.inicializaNeuronio(numeroEntradas);
		novoNeuronio.setCamada(this);
		this.neuronios.add(novoNeuronio);
		novoNeuronio.setNumero(this.getNumeroNeuronios() - 1);
	}

	/**
	 * @return <code>String</code> com a lista de pesos dos neurônios desta
	 *         camada.
	 */
	public String getListaDePesosNeuronios() {
		String listaPesos = "";
		for (Neuronio n : this.neuronios) {
			for (int i = 0; i < n.getPesos().length; i++) {
				listaPesos += n.getPesos()[i] + ";";
			}
		}
		return listaPesos;
	}
}
