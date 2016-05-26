# CandlestickPattern

CandlestickPattern é um projeto desenvolvido pelos alunos Felipe Cavalcante Constantino e Luciano Aragão Chiarelli.

O sistema processa os dados de forma distribuída (Cliente-Mestre-Escravo). 

## Uso
Testa condições dadas por arquivos financeiros csv (comma-separated values), verificando se a condição inserida é verdadeira, e, se 
for, qual atende o requisito.

## Como utilizar

Primeiramente há a necessidade de possuir arquivos em formato csv com o padrão de colunas pré definido: Date, Open, High, Low, Close, mas não necessariamente nesta ordem.
Estes devem estar contidos em um diretório nomeado como finance. Esse tipo de arquivo pode ser facilmente obtido por meio do [Yahoo! Finance](finance.yahoo.com/).

É necessário iniciar escravo e mestre antes do cliente, logo que iniciado o cliente é necessário inserir o IP do mestre, assim é estabelecida a 
conexão e o os arquivos começam a ser enviados para os escravos.

Após o envio dos arquivos, é notificado o recebimento dos mesmos nos escravos. Em sequência é possível inserir uma expressão que deseja 
testar, por exemplo: <i>P1_CLOSE > P2_OPEN</i>. Assim que concluída a inserção de todas as expressões é necessário digitar PROX e então é 
iniciado o processamento em massa.
