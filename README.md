# 📚 Codexia

> 🚧 **Aviso:** Este projeto está em seus estágios iniciais de desenvolvimento (*Work in Progress*). O escopo, a arquitetura e as funcionalidades ainda estão sendo implementados e refinados.

A Codexia é uma API RESTful focada no gerenciamento, categorização e recuperação rápida de fragmentos de código (*snippets*). O projeto implementa armazenamento versionado, busca textual otimizada e integração sob demanda com Inteligência Artificial para análise e refatoração de código. 

A aplicação foi estruturada visando a aplicação de Clean Architecture e design patterns no ecossistema Java.

## 🚀 Funcionalidades Principais

### 📝 Gestão de Códigos (Snippets)
* Cadastro de fragmentos de código com suporte a metadados: Título, Descrição e Linguagem.
* **Versionamento de Registros:** Edições não sobrescrevem o código original. O sistema armazena o histórico de alterações para garantir a integridade e rastreabilidade dos dados.
* **Exclusão Lógica (Soft Delete):** A exclusão de registros apenas inativa o dado no banco, sem apagá-lo fisicamente.

### 🗂️ Organização e Categorização
* **Coleções:** Agrupamento lógico de códigos por contexto (ex: projetos ou domínios de estudo).
* **Tags:** Relacionamento muitos-para-muitos (N:M) que permite a criação de filtros combinados e precisos (Coleção + Tags).

### 🔍 Busca Textual (Full-Text Search)
* Motor de busca estruturado diretamente no banco de dados relacional.
* Utiliza a funcionalidade `TSVector` do PostgreSQL para realizar varreduras otimizadas em títulos, descrições e no conteúdo do código.

### 🤖 Assistente de IA Sob Demanda
Integração com API externa de IA (OpenAI/Gemini) para interagir com o código armazenado. Funcionalidades acionadas exclusivamente por requisição do usuário:
* **Explicação:** Análise e detalhamento estrutural de um trecho específico de código.
* **Refatoração:** Sugestões práticas de *Clean Code* e otimização.
* **Tradução:** Conversão de sintaxe e lógica entre diferentes linguagens de programação.

## 🛠️ Stack Tecnológica

* **Linguagem:** Java
* **Framework:** Spring Boot
* **Banco de Dados:** PostgreSQL
* **Integração:** APIs de Inteligência Artificial (REST)
* **Arquitetura:** Clean Architecture
