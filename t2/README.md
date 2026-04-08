# T2: Analisador Sintático (Linguagem LA)

Este módulo contém a implementação da segunda fase do compilador para a **Linguagem Algorítmica (LA)**. O foco deste trabalho é a verificação estrutural (análise sintática) do código-fonte, garantindo que a sequência de tokens gerada pelo analisador léxico obedeça à Gramática Livre de Contexto (GLC) da linguagem.

---

## 🔍 Funcionalidades Implementadas

O analisador foi expandido para incluir o `LAParser` e conta com as seguintes características:

* **Herança Léxica:** Continua identificando e reportando corretamente os erros léxicos tratados no Trabalho 1 (símbolos não identificados, cadeias e comentários não fechados).
* **Verificação Sintática:** Valida regras de gramática estrutural, incluindo:
    * Declarações (locais e globais, constantes, tipos e registros).
    * Estruturas de controle de fluxo (`se/entao/senao`, `caso/seja`, `para/ate`, `enquanto`, `faca/ate`).
    * Comandos de I/O (`leia`, `escreva`).
    * Precedência de expressões aritméticas, relacionais e lógicas.
* **Tratamento Customizado de Erros:** Utiliza um `BaseErrorListener` sobreposto para desligar o aviso padrão do ANTLR e imprimir a mensagem de erro formatada de forma legível.
* **Interrupção Segura (Fail-Fast):** Ao detectar o *primeiro* erro (léxico ou sintático), o parser grava a mensagem `Linha X: erro sintatico proximo a Y`, encerra o arquivo com `Fim da compilacao` e lança uma `ParseCancellationException` para garantir a interrupção imediata da JVM sem perda de dados no I/O.

---

## 🚀 Como Compilar e Executar

O projeto utiliza o **Maven** para gerenciar a compilação cruzada da gramática ANTLR4 e do código Java.

### 1. Compilar (Gerar o Executável)
No terminal, certifique-se de estar dentro da pasta `t2` e execute:

```bash
mvn clean package
```

Isso gerará o executável `t2-1.0-SNAPSHOT-jar-with-dependencies.jar` na pasta `target/`.

### 2. Executar Manualmente
Para testar a compilação de um arquivo específico sem o corretor automático:

```bash
java -jar target/t2-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_entrada> <caminho_saida>
```

---

## 🧪 Validação (Corretor Automático)

A precisão da gramática foi validada utilizando o script de correção oficial, atingindo aprovação total na bateria de testes.

> **Nota:** O executável do corretor (`.jar`) e a pasta de `casos-de-teste` residem na raiz do repositório (nível acima desta pasta) e são ferramentas externas fornecidas pelo docente, não submetidas ao versionamento por questões de direitos autorais.

Para rodar os **62 casos de teste** do T2 a partir da **raiz do projeto**, utilize:

```bash
java -jar compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar t2/target/t2-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc temp casos-de-teste "820763" t2
```

---

## 🛠️ Detalhes Técnicos

* **Ferramenta:** ANTLR 4.11.1
* **LALexer.g4:** Gramática léxica (tokens) isolada.
* **LAParser.g4:** Gramática sintática livre de contexto.
* **Principal.java:** Orquestrador do fluxo léxico-sintático, responsável pela injeção do `ErrorListener` customizado e gerenciamento do stream de saída.

---

## 👤 Autor

* **Nome:** Gustavo Kim
* **RA:** 820763
* **Instituição:** UFSCar