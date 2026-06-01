# Fatec ADS

Sistema web academico desenvolvido com Spring Boot para gerenciamento de usuarios, cursos, professores, alunos e disciplinas. O projeto usa paginas Thymeleaf no servidor, autenticacao com Spring Security e persistencia em banco PostgreSQL.

## Visao geral

O Fatec ADS centraliza operacoes basicas de cadastro e consulta em um ambiente administrativo simples. A interface segue uma proposta de design limpa e objetiva, com inspiracao em principios de Material Design, Human Interface, design responsivo, acessibilidade e experiencia centrada no usuario.

## Funcionalidades

- Login e logout de usuarios.
- Cadastro publico de usuarios.
- Recuperacao de senha por token enviado por e-mail.
- Listagem, criacao, edicao e exclusao de cursos.
- Listagem, criacao, edicao e exclusao de professores.
- Listagem, criacao, edicao e exclusao de alunos.
- Listagem, criacao, edicao e exclusao de disciplinas.
- Associacao de alunos a cursos.
- Associacao de disciplinas a cursos e professores.

## Tecnologias

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Lombok
- Maven Wrapper

## Requisitos

Antes de executar o projeto, instale ou configure:

- JDK 17 ou superior.
- PostgreSQL em execucao.
- Banco de dados chamado `fatecads_db`.
- Usuario do banco com acesso ao PostgreSQL.

As configuracoes padrao estao em `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fatecads_db
spring.datasource.username=postgres
spring.datasource.password=123456
server.port=8080
```

Se necessario, altere usuario, senha, porta ou nome do banco conforme o ambiente local.

## Configuracao do banco

Crie o banco no PostgreSQL:

```sql
CREATE DATABASE fatecads_db;
```

O projeto esta configurado com:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Com isso, as tabelas sao criadas ou atualizadas automaticamente pelo Hibernate ao iniciar a aplicacao.

## Configuracao de e-mail

A recuperacao de senha usa SMTP do Gmail. No arquivo `application.properties`, configure um e-mail valido e uma senha de aplicativo:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=seu_email@gmail.com
```

Para Gmail, use uma senha de aplicativo gerada na conta Google. Nao utilize a senha principal da conta.

## Como executar

Na raiz do projeto, execute:

```bash
./mvnw spring-boot:run
```

No Windows, tambem e possivel usar:

```bash
mvnw.cmd spring-boot:run
```

Depois acesse:

```text
http://localhost:8080/fatecads
```

## Testes

Para executar os testes automatizados:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

## Principais rotas

| Rota | Descricao |
| --- | --- |
| `/fatecads` | Pagina inicial publica |
| `/login` | Tela de login |
| `/home` | Pagina inicial apos autenticacao |
| `/users/create` | Cadastro de usuario |
| `/users/list` | Lista de usuarios |
| `/forgot-password` | Solicitacao de recuperacao de senha |
| `/reset-password` | Redefinicao de senha por token |
| `/course/list` | Lista de cursos |
| `/course/new` | Cadastro de curso |
| `/professor/list` | Lista de professores |
| `/professor/new` | Cadastro de professor |
| `/student/list` | Lista de alunos |
| `/student/new` | Cadastro de aluno |
| `/subject/list` | Lista de disciplinas |
| `/subject/new` | Cadastro de disciplina |

## Estrutura do projeto

```text
src/
  main/
    java/br/com/fatecads/fatecads/
      config/       Configuracoes de seguranca e autenticacao
      controller/   Controladores MVC
      entity/       Entidades JPA
      repository/   Interfaces de acesso ao banco
      service/      Regras de negocio
    resources/
      static/css/   Estilos da aplicacao
      templates/    Paginas Thymeleaf
      application.properties
  test/
    java/           Testes automatizados
```

## Modelo de dados

Entidades principais:

- `User`: usuario do sistema, credenciais, perfil e token de recuperacao de senha.
- `Course`: curso, periodo e carga horaria.
- `Professor`: professor, telefone, formacao e RM.
- `Student`: aluno, dados pessoais, matricula e curso vinculado.
- `Subject`: disciplina, codigo, carga horaria, curso e professor vinculados.

## Seguranca

A aplicacao usa Spring Security com:

- Senhas criptografadas com BCrypt.
- Login customizado em `/login`.
- Redirecionamento para `/home` apos login.
- Logout com retorno para `/login?logout`.
- Acesso publico as rotas de login, cadastro de usuario, recuperacao de senha, pagina inicial e arquivos estaticos.
- Demais rotas protegidas por autenticacao.

## Observacoes de design

O projeto pode evoluir mantendo alguns principios ja validados para este tipo de sistema:

- Layout minimalista e responsivo.
- Navegacao clara para rotinas administrativas.
- Componentes consistentes, seguindo uma ideia de design system.
- Acessibilidade em formularios, botoes, contraste e mensagens de erro.
- Experiencia objetiva para uso academico e administrativo.

## Autor

Projeto academico desenvolvido no contexto da Fatec ADS.
