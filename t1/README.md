# T1: Analisador Léxico (Linguagem LA)

Este módulo contém a implementação da primeira fase do compilador para a **Linguagem Algorítmica (LA)**. O foco deste trabalho é a tokenização do código-fonte e o tratamento rigoroso de erros léxicos, conforme a especificação da disciplina de Construção de Compiladores.

---

## 🔍 Funcionalidades Implementadas

O analisador identifica e classifica as seguintes categorias de tokens:

* **Palavras-Reservadas:** Reconhecimento de 38 termos fixos (ex: `algoritmo`, `vetor`, `procedimento`, `de`).
* **Operadores e Símbolos:** Aritméticos, relacionais, lógicos e delimitadores de pontuação.
* **Tokens Dinâmicos:** Identificadores, números inteiros, números reais e cadeias de caracteres (strings).
* **Tratamento de Erros Léxicos:**
    * Símbolos não identificados (caracteres fora do alfabeto da linguagem).
    * Cadeias de caracteres (strings) não fechadas.
    * Comentários de bloco (`{ }`) não fechados, com suporte a múltiplas linhas.

---

## 🚀 Como Compilar e Executar

O projeto utiliza o **Maven** para gerenciar o ciclo de vida do ANTLR e as dependências do Java.

### 1. Compilar (Gerar o Executável)

Certifique-se de estar dentro da pasta `t1` no terminal e execute:

```bash
mvn clean package
```

Isso gerará o arquivo `t1-1.0-SNAPSHOT-jar-with-dependencies.jar` dentro da pasta `target/`.

### 2. Executar Manualmente

Caso deseje testar um arquivo de código LA específico:

```bash
java -jar target/t1-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_entrada> <caminho_saida>
```

---

## 🧪 Validação (Corretor Automático)

O projeto foi validado utilizando o script de correção oficial da disciplina. 

> **Nota:** O executável do corretor (`.jar`) e a pasta de `casos-de-teste` são fornecidos pelo docente e **não** estão incluídos neste repositório para manter o ambiente limpo e livre de dependências externas no controle de versão.

Para rodar a bateria de 37 testes (assumindo que o corretor e os testes estejam no diretório pai, fora da pasta `t1`):

```bash
java -jar compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar t1/target/t1-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc temp casos-de-teste "820763" t1
```

---

## 🛠️ Detalhes Técnicos

* **Ferramenta:** ANTLR 4.11.1
* **LALexer.g4:** Gramática léxica com definições de tokens e tratamento de erros via regras de fallback.
* **Principal.java:** Classe responsável por orquestrar a leitura do `CharStream`, iterar sobre os tokens gerados e realizar a escrita formatada conforme exigido pelo gabarito.

---

## 👤 Autor

* **Nome:** Gustavo Kim Alcantara
* **RA:** 820763
* **Instituição:** UFSCar (Turma de quarta)