package org.kansus.ocr.mlp;



/**
 * Representação das entradas de um padrão.
 */
public class Representacao {

	// como a entrada da rede será uma imagem 24x24 pixels, a matriz de
	// representação deverá ter 24 linhas e colunas
	private final int LINHAS = 24;
	private final int COLUNAS = 24;

	private int[][] matriz = new int[LINHAS][COLUNAS];
	static int i = 0;

	/**
	 * @param valoresIniciais
	 */
	public Representacao(int[][] matriz) {
		this.matriz = matriz;
		/*File file = new File("D:\\dados" + i++ + ".txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write(toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * @return conversão da matriz de representação em um vetor unidimensional.
	 */
	public int[] getValoresEmSerie() {
		int[] valoresEmSerie = new int[LINHAS * COLUNAS];
		int k = 0;
		for (int i = 0; i < LINHAS; i++)
			for (int j = 0; j < COLUNAS; j++) {
				valoresEmSerie[k++] = this.matriz[i][j];
			}
		return valoresEmSerie;
	}

	@Override
	public String toString() {
		String representacaoTextual = "";
		for (int i = 0; i < LINHAS; i++) {
			for (int j = 0; j < COLUNAS; j++) {
				if (this.matriz[i][j] == -1)
					representacaoTextual += "0 ";
				else
					representacaoTextual += "1 ";
			}
			representacaoTextual += "\n";
		}
		return representacaoTextual;
	}
}