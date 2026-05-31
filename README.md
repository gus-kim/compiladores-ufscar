# Projeto de Compiladores - UFSCar (Linguagem LA)

Este repositório contém o desenvolvimento completo do compilador para a **Linguagem Algorítmica (LA)**, realizado durante a disciplina de Construção de Compiladores na UFSCar, sob orientação do Prof. Dr. Daniel Lucrédio.

O projeto é construído em Java, utilizando **ANTLR4** para a geração de analisadores e **Maven** para automação de build e gerenciamento de dependências.

## 📂 Estrutura do Projeto

O repositório está estruturado em módulos independentes que representam as etapas do compilador:

* **[/t1](./t1)**: **Analisador Léxico** - Tokenização e tratamento de erros de símbolos, cadeias e comentários. (Concluído ✅)
* **[/t2](./t2)**: **Analisador Sintático** - Verificação estrutural do código-fonte e validação das regras da Gramática Livre de Contexto (GLC). (Concluído ✅)
* **[/t3](./t3)**: **Analisador Semântico (Parte 1)** - Validação de tipos, checagem de escopo com tabela de símbolos e identificação de atribuições incompatíveis básicas. (Concluído ✅)
* **[/t4](./t4)**: **Analisador Semântico (Parte 2)** - Validação avançada suportando funções, procedimentos, ponteiros, registros (structs) e compatibilidade estrita de argumentos. (Concluído ✅)
* **[/t5](./t5)**: **Gerador de Código (Tradução)** - Orquestrador final que recebe a AST sem erros e utiliza o padrão *Visitor* para emitir código C válido e compilável com o GCC. (Concluído ✅)

## 🛠️ Tecnologias e Ferramentas

* **Linguagem:** Java 11+
* **Gerador de Analisadores:** ANTLR 4.11.1
* **Compilador Alvo:** GCC (GNU Compiler Collection)
* **Gerenciador de Dependências:** Apache Maven
* **Ambiente de Desenvolvimento:** GitHub Codespaces

## 👥 Autores

Como a composição do grupo pode variar ao longo das fases do compilador, a autoria detalhada está documentada individualmente em cada pasta. 

*Por favor, verifique a seção de autores no arquivo `README.md` dentro da pasta de cada trabalho (t1, t2, etc.) para consultar os nomes e RAs correspondentes.*