# Candlestick Pattern

Candlestick Pattern é um projeto desenvolvido pelos alunos da Fatec Carapicuíba 5º ADS Manhã 2016:
- Felipe Cavalcante Constantino
- Luciano Aragão Chiarelli.
Na disciplina de Sistemas Distribuidos.
O sistema processa os dados de forma distribuída entre máquinas (Cliente-Mestre-Escravo), ou até mesmo em IPC. 

## Introdução
O projeto foi baseado em um artigo da [Journal of Banking & Finance](artigo_finances.pdf) que aborda formas de como obter lucro na bolsa de valores, escolhendo melhor qual empresa comprar ações, e quando vender. Para isso, o artigo demonstra fórmulas e procedimentos que se devem realizar para alcançar tal objetivo.

Primeiramente, é apresentado ao leitor a ideia de Candlestick: uma representação gráfica de um ativo a ser analisado. No caso do artigo, ele define cada Candlestick como sendo um ativo de um dia de 24h, contendo seus preços de abertura, fechamentok, máximo e mínimo; Exemplo:

![Exemplo de estrutura de um Candlestick](https://upload.wikimedia.org/wikipedia/commons/5/5e/Candlesaltaebaixa.JPG)

Tendo os Candlesticks em mãos, e dado um certo conjunto sequencial de Candlesticks, encontraremos alguns padrões, no qual chamaremos de Candlestick Patterns (No artigo, são abordados somente oito principais e mais importantes). Achados todos os Candlestick Patterns, é necessário saber qual a tendência pela qual o mercado seguirá (Trends) e, baseado nessa tendência, será definido uma estratégia de arrendamento (Holding Strategy) para saber em que momento se deve vender ou não as ações.

Com isso, foi desenvolvido um programa em _Java_ na tentativa de utilizar tais procedimentos em arquivos de histórico de ações.
Somente foi abordado uma Trend e uma Holding Strategy básica: Analisar se o preço de fechamento (Close) do último dia de um Candlestick Pattern, e então verificar se a venda de cada terço desta ação nos próximos três dias retornará lucro.
Será aceito qualquer Candlestick Pattern, desde que que atendam uma grámatica (definido abaixo), representados por um conjunto de expressões.

## Utilização
Testa condições/expressões dadas pelo Cliente em arquivos financeiros .csv (comma-separated values), verificando se a condição inserida é verdadeira, e, se for, verifica se a Holding Strategy adotada atende ao requisito e retorna um lucro viável para cada padrão analisado.

## Organização
O proejto se divide em 3 componentes:
- **Cliente:** Representado pela classe _Client_. Conecta-se com o Mestre, e logo que a conexão for bem sucedida, manda conjuntos de condições/expressões para a máquina Mestre.
- **Mestre:** Representado pela classe _Master_. Primeiramente, identifica os escravos na rede, e quando encontrado, envia todos os arquivos .csv, e após o envio, os coloca numa fila de escravos disponíveis (ociosos). Em seguida recebe um conjunto de expressões do cliente e as manda para um escravo disponível (Esse é um ciclo interminável até que o Cliente pare de enviar expressões).
- **Escravo:** Representado pela classe _Slave_. Conecta-se com o mestre, e então fica ocioso até que um conjunto de expressões tenha chegado até ele. Assim que receber, ele começa o trabalho de percorrer o(s) arquivo(s) .csv a fim de testar o conjunto de expressões e retornar um resultado.
 
O resultado completo (Representado pela classe _GainStatistics_) é retornado ao Mestre, que é retornado de volta ao cliente e então printado na tela do mesmo.

## Como utilizar
### Obtendo os arquivos de finanças
No mestre, é preciso ter os arquivos em formato .csv com o padrão de colunas pré definido: Date, Open, High, Low, Close, mas não necessariamente nesta ordem.

Estes devem estar contidos em um diretório com o nome de _finance_ no mesmo diretório do executável. Esse tipo de arquivo pode ser facilmente obtido por meio do [Yahoo! Finance](http://finance.yahoo.com/).

1. Ao entrar no site, pesquise por uma empresa a ser analisada.
2. Acesse a seção de _Historical Prices_ no menu lateral esquerdo.
3. Vá até o final da página e clique em _Download Spreadsheet_
4. Ao finalizar o download, renomeio para um nome que preferir
5. Mova-o para a pasta _finance_, como falado anteriormente.

Pronto. Você pode baixar vários arquivos .csv de qualquer empresa.

### Iniciando o Sistema
Atendidos tais requisitos, siga o passo a passo:

1. Inicie o Mestre, ele começará a disparar pacotes de requisição na rede afim de encontrar Escravos. É necessário iniciar o Mestre antes do Cliente
2. Inicie o(s) Escravo(s), e então ele começará a receber os arquivos .csv do Mestre
3. Inicie o Cliente, e forneça o IP do Mestre
4. A partir do Cliente, forneça um conjunto de expressões que serão testadas
5. Para ir para o próximo conjunto de expressões, simplemente digite PROX
6. Quando terminar, digite SAIR. E então o Cliente somente aguardará os resultados para serem printados na tela
7. Após o término dos resultados, o sistema terminará

OBS: O Escravo não precisa necessariamente ser iniciado antes do Cliente, a única restrição é que o Mestre deve ser iniciado antes do Cliente.

### Exemplo de entrada
Quando o cliente for iniciado com sucesso, e já conectado com o Mestre, será solicitado que entre com um conjunto de expressões.
Siga o exemplo abaixo, que simula uma entrada de dados feita por parte do Cliente:
```
exp: P1_CLOSE > P2_CLOSE
exp: P3_OPEN + P3_CLOSE <= P2_CLOSE
exp: PROX
exp: P3_OPEN = P2_OPEN
exp: P4_CLOSE - P1_OPEN >= P1_HIGH * 3
exp: SAIR
```
No exemplo acima, temos dois conjuntos de expressões, o primeiro antes do PROX e o segundo depois de PROX. O comando PROX define um limitador entre conjuntos de expressões. E o comando SAIR, um encerrador.

Cada variável da expressão contém duas informações: A posição do dia a ser comparado, e o valor relativo àquele dia.
A posição do dia pode variar de P1 até P5. E o valor: OPEN, HIGH, LOW ou CLOSE (Preços de Abertura, Alta, Baixa e Fechamento, respectivamente). Formando uma variável só: P[1-5]_[OPEN/HIGH/LOW/CLOSE].

Vale também lembrar que o motor em que as expressões são testadas é na verdade em _JavaScript_. Ou seja, serão também aceitos métodos/constantes nativas de _JavaScript_ dentro da expressão. Como por exemplo: podemos utilizar a classe _Math_ para pegar o valor de PI, ou um método de potenciação, e os colocarmos dentro das expressões:
> exp: Math.PI * P3_OPEN < Math.pow(P1_OPEN, 2)
