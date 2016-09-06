package com.kansus.kstrainer;

/**
 * Listener para tarefas que precisam mostrar o progresso ao usu�rio.
 * 
 * @author Charles
 */
public interface NeuralNetworkListener {

	/**
	 * Chamado quando houve um progresso na tarefa.
	 * 
	 * @param message
	 *            mensagem descrevendo o progresso.
	 * @param progress
	 *            quantidade de progresso (varia de 0 � 100).
	 */
	public void onProgressChanged(String message, int progress);

	/**
	 * Chamado quando a tarefa estiver finalizada.
	 * 
	 * @param message
	 *            mensagem a ser mostrada pro usu�rio (opcional).
	 */
	public void onProgressCompleted(String message);
}
