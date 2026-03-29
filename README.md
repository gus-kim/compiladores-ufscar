# Analisador Léxico - Linguagem Algorítmica (LA)
**Disciplina:** Construção de Compiladores (Prof. Daniel Lucrédio)
**Membros do Grupo:** Gustavo Kim Alcantara - 820763

## Requisitos
- **Java Development Kit (JDK):** Versão 11 ou superior
- **Apache Maven:** Versão 3.6 ou superior

## Como Compilar o Projeto
Abra o terminal na pasta raiz do projeto (onde está o `pom.xml`) e execute o comando:
`mvn clean package`

Isso gerará o analisador léxico dentro da pasta `target/`, em um arquivo nomeado `t1-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Como Executar
Use o comando abaixo no terminal, passando o arquivo de entrada e o arquivo de saída desejado:
`java -jar target/t1-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho_arquivo_entrada> <caminho_arquivo_saida>`