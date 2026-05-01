# T3: Analisador Semântico (Linguagem LA)

Este módulo contém a implementação da terceira fase do compilador para a **Linguagem Algorítmica (LA)**. O foco deste trabalho é a análise semântica do código-fonte, garantindo a coerência de tipos, regras de escopo e declarações de variáveis, reportando erros lógicos sem interromper o processo de compilação.

---

## 🔍 Funcionalidades Implementadas

O analisador semântico percorre a Árvore Sintática Abstrata (AST) gerada pelo ANTLR4 utilizando o padrão *Visitor* e uma Tabela de Símbolos, focando em detectar os seguintes cenários:

* **Identificador já declarado:** Verifica se variáveis, constantes, funções ou tipos já foram declarados anteriormente no mesmo escopo.
* **Tipo não declarado:** Valida a existência dos tipos atribuídos às variáveis.
* **Identificador não declarado:** Impede o uso de variáveis, funções ou procedimentos não previamente definidos.
* **Atribuição incompatível:** Analisa os tipos de dados em atribuições e expressões matemáticas/lógicas, garantindo as coerções válidas e reportando operações inválidas.

---

## 🚀 Como Compilar e Executar

O projeto utiliza o **Maven** para gerenciar a compilação cruzada da gramática ANTLR4 e do código Java.

### 1. Compilar (Gerar o Executável)
No terminal, certifique-se de estar dentro da pasta `t3` e execute:

```bash
mvn clean package
```

Isso gerará o executável `t3-1.0-SNAPSHOT-jar-with-dependencies.jar` na pasta `target/`.

### 2. Executar Manualmente
O analisador deve ser executado em linha de comando passando obrigatoriamente dois argumentos (entrada e saída). Para testar a compilação de um arquivo específico sem o corretor automático:

```bash
java -jar target/t3-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_entrada> <caminho_saida>
```

---

## 🧪 Validação (Corretor Automático)

O analisador foi validado utilizando o script de correção oficial, atingindo aprovação total (9/9) na bateria de testes de erros semânticos.

> **Nota:** O executável do corretor (`.jar`) e a pasta de `casos-de-teste` residem na raiz do repositório (nível acima desta pasta) e são ferramentas externas fornecidas pelo docente, não submetidas ao versionamento por questões de direitos autorais.

Para rodar os testes do T3 a partir da **raiz do projeto**, utilize:

```bash
java -jar compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar t3/target/t3-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc temp casos-de-teste "820763" t3
```

---

## 🛠️ Detalhes Técnicos

* **Ferramenta:** ANTLR 4.11.1
* **LALexer.g4 / LAParser.g4:** Gramáticas léxica e sintática (herdadas da fase anterior).
* **TabelaDeSimbolos / Escopos:** Estruturas de dados implementadas para gerenciar o escopo de variáveis e seus respectivos tipos.
* **LASemantico (Visitor):** Classe principal que percorre a árvore sintática aplicando as regras semânticas.
* **Principal.java:** Orquestrador do fluxo de compilação, responsável por ler o código e gravar os erros encontrados no arquivo de saída.

---

## 👥 Autores

* **Nome:** Gustavo Kim Alcantara
  * **RA:** 820763
  * **Instituição:** UFSCar (Turma de quarta)
* **Nome:** Gustavo Borguetti Daré
  * **RA:** 818723
  * **Instituição:** UFSCar (Turma de segunda)