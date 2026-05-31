# T5: Gerador de Código C (Linguagem LA)

Este módulo contém a implementação da quinta e última fase do compilador para a **Linguagem Algorítmica (LA)**. O objetivo deste trabalho é atuar como o orquestrador final: caso o código passe limpo pelas análises Léxica, Sintática e Semântica, o Gerador entra em ação para traduzir a Árvore Sintática Abstrata (AST) em **código C executável**.

---

## 🔍 Funcionalidades Implementadas

O Gerador de Código (implementado via padrão *Visitor*) suporta a tradução completa de:

* **Estruturas Base:** Inclusão de bibliotecas (`stdio.h`, `stdlib.h`, `string.h`) e estruturação do `main`.
* **Declarações e Tipos:** Tradução de inteiros (`int`), reais (`float`), lógicos (`int` 0 ou 1) e literais (`char[80]`), incluindo matrizes de caracteres.
* **Comandos de I/O:** Mapeamento inteligente de `leia()` para `scanf`/`gets` e `escreva()` para `printf`, dependendo do tipo da variável na Tabela de Símbolos.
* **Controlo de Fluxo:** Mapeamento de `se/senao` (`if/else`), `enquanto` (`while`), `para` (`for`), `faca/ate` (`do/while`) e `caso` (`switch/case` utilizando a extensão de intervalos `...` do GCC).
* **Estruturas Avançadas:** Suporte a ponteiros (`*` e `&`), registos aninhados (`typedef struct`) e chamadas de sub-rotinas (procedimentos e funções).
* **Operadores Lógicos e Relacionais:** Tradução contextual de operadores LA (`=`, `<>`, `e`, `ou`, `nao`) para operadores C (`==`, `!=`, `&&`, `||`, `!`), com deteção automática de uso de `strcmp` para strings literais.

---

## ⚙️ Pré-requisitos e Dependências

Para compilar, executar e testar o projeto, é necessário ter instalado:

* **Java Development Kit (JDK):** Versão 11 ou superior.
* **Apache Maven:** Para a automação da compilação e gestão de dependências.
* **ANTLR4:** (Versão 4.11.1) Transferido automaticamente pelo Maven (`pom.xml`).
* **GCC (GNU Compiler Collection):** Necessário para compilar o código `.c` de saída gerado pelo compilador.

---

## 🚀 Como Compilar e Executar

### 1. Compilar o Analisador (Gerar o .jar)
No terminal, navegue até a raiz do módulo `t5` e execute o comando abaixo para gerar o executável com as dependências embutidas:

```bash
mvn clean package
```

Isto irá gerar o ficheiro `t5-1.0-SNAPSHOT-jar-with-dependencies.jar` na pasta `target/`.

### 2. Executar o Compilador (Traduzir LA para C)
O compilador exige obrigatoriamente a passagem de dois argumentos (ficheiro de entrada e ficheiro de saída).

```bash
java -jar target/t5-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_entrada.alg> <caminho_saida.c>
```

Se o código `.alg` contiver erros, o compilador imprimirá as falhas e abortará. Se estiver correto, o ficheiro `.c` será gerado com sucesso.

### 3. Compilar e Executar o Código C Gerado
Para compilar o código de saída resultante usando o GCC e executá-lo:

```bash
gcc -o programa_saida <caminho_saida.c>
./programa_saida
```

---

## 🧪 Validação (Corretor Automático)

O compilador obteve aprovação total (**20/20**) no conjunto de testes de geração de código (CT5).

> **Nota:** O executável do corretor (`.jar`) e a pasta de `casos-de-teste` residem na raiz do repositório (nível acima desta pasta) e não são submetidos ao versionamento por questões de direitos autorais do docente.

Para correr a validação a partir da **raiz do projeto**, utilize a ferramenta oficial:

```bash
java -jar compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar t5/target/t5-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc temp casos-de-teste "820763" t5
```

---

## 👥 Autores

* **Nome:** Gustavo Kim Alcantara
  * **RA:** 820763
  * **Instituição:** UFSCar (Turma de quarta)
* **Nome:** Gustavo Borguetti Daré
  * **RA:** 818723
  * **Instituição:** UFSCar (Turma de segunda)