Prefixos:
PX -> Indica rede de pixeis
STR -> Indica rede de traços

Sufixos:
C-> Indica convolução
N-> Indica normalização negativa (-1 vira 1 e 1 vira -1)

PS: PX-0 é um teste extra que fiz, em que é utilizado 0 ao invés de -1 nas normalizações

########################## Conteúdo das pastas ############################

config.json -> Arquivo de configuração da rede neural
log.txt -> Log de operações do treinamento da rede neural
pixels.mlp -> Arquivo de pesos da rede neural

Patterns -> Padrões utilizados para treinar a rede neural
Normalization -> Normalizações dos padrões utilizados para treinar a rede neural

Evaluation -> Avaliação da rede
Evaluation\Samples -> Amostras utilizadas na avaliação
Evaluation\Results -> Logs com os resultados detalhados da avaliação
Evaluation\Normalization -> Normalizações das amostras utilizadas na avaliação

########################### Conteúdo dos logs #############################

Output[x] -> Saída [-1,1] da rede neural para a classe x
Rating: Conversão de Output[x] para a faixa [0,1]
Highest Output -> Maior Output entre todas as classes
Highest Final Rating -> Maior Rating entre todas as classes
Expected Class Output -> Output da classe considerada correta (Atualmente só suportado pela rede de traços)
Expected Class Rating -> Rating da classe considerada correta (Atualmente só suportado pela rede de traços)

PS: Para saber qual classe corresponde a um certo caractere, utilizar os
nomes dos arquivos dos padrões de treinamento situados na pasta Patterns\Default. Tomar
cuidado pois os arquivos dos padrões estão começando do 1, e as classes começam do 0.