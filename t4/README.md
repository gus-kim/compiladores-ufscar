# T4: Analisador Semântico - Parte 2 (Linguagem LA)

Este módulo contém a implementação da quarta fase do compilador para a **Linguagem Algorítmica (LA)**. O foco deste trabalho é a expansão da análise semântica do código-fonte para suportar tipos avançados, estruturas de dados complexas e sub-rotinas.

Nesta fase, o compilador **não deve interromper a execução** ao encontrar o primeiro erro, reportando todas as falhas semânticas até o fim do arquivo.

---

## 🔍 Funcionalidades Implementadas

O analisador foi expandido para detectar mais 5 categorias de erros semânticos avançados:

* **Identificadores expandidos:** Verificação de declarações duplicadas e uso não declarado para ponteiros, registros, funções e procedimentos.
* **Chamadas de função/procedimento:** Validação estrita de escopo, número, ordem e tipo exato dos argumentos passados em relação aos parâmetros formais declarados.
* **Atribuições complexas:** Validação de compatibilidade em atribuições e expressões matemáticas envolvendo ponteiros (`^` e `&`) e tipos compostos (registros/structs).
* **Comando retorne:** Validação de escopo, garantindo o bloqueio do uso do comando `retorne` em locais não permitidos (ex: no programa principal ou dentro de procedimentos).

---

## 🚀 Como Compilar e Executar

O projeto utiliza o **Maven** para gerenciar a compilação do código Java e da gramática ANTLR4.

### 1. Compilar (Gerar o Executável)
No terminal, certifique-se de estar dentro da pasta `t4` e execute:

```bash
mvn clean package
```

Isso gerará o executável `t4-1.0-SNAPSHOT-jar-with-dependencies.jar` na pasta `target/`.

### 2. Executar Manualmente
O analisador deve ser executado em linha de comando passando obrigatoriamente dois argumentos. Para testar um arquivo sem o corretor automático:

```bash
java -jar target/t4-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_entrada> <caminho_saida>
```

---

## 🧪 Validação (Corretor Automático)

O analisador foi validado utilizando o script de correção oficial, atingindo aprovação total (9/9) na bateria de testes de erros semânticos (CT4).

> **Nota:** O executável do corretor (`.jar`) e a pasta de `casos-de-teste` residem na raiz do repositório (nível acima desta pasta) e não são submetidos ao versionamento por questões de direitos autorais do docente.

Para rodar os testes do T4 a partir da **raiz do projeto**, utilize:

```bash
java -jar compiladores-corretor-automatico-1.0-SNAPSHOT-jar-with-dependencies.jar "java -jar t4/target/t4-1.0-SNAPSHOT-jar-with-dependencies.jar" gcc temp casos-de-teste "820763" t4
```

---

## 👥 Autores

* **Nome:** Gustavo Kim Alcantara
  * **RA:** 820763
  * **Instituição:** UFSCar (Turma de quarta)
* **Nome:** Gustavo Borguetti Daré
  * **RA:** 818723
  * **Instituição:** UFSCar (Turma de segunda)