# Floripa na Praia

Floripa na Praia é uma aplicação simples desenvolvida em Spring Boot para facilitar a gestão e recomendação de praias na região de Florianópolis.

## Tecnologias Utilizadas
- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security
- Banco de Dados PostgreSQL/MySQL
- Swagger para documentação da API

## Configuração do Ambiente
### Requisitos
- JDK 17+
- Maven 3+
- Docker (opcional, para banco de dados)

### Instalação e Execução
1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-usuario/floripa-na-praia.git
   cd floripa-na-praia
   ```
2. Configure o banco de dados no `application.properties` ou `application.yml`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/floripa
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```
3. Execute a aplicação:
   ```sh
   mvn spring-boot:run
   ```
4. A API estará disponível em `http://localhost:8080`.

## Endpoints Principais
| Método | Endpoint           | Descrição                        |
|---------|-------------------|--------------------------------|
| GET     | /praias           | Lista todas as praias        |
| GET     | /praias/{id}      | Obtém detalhes de uma praia  |
| POST    | /praias           | Adiciona uma nova praia      |
| PUT     | /praias/{id}      | Atualiza informações de praia |
| DELETE  | /praias/{id}      | Remove uma praia             |

## Autenticação
A aplicação conta com autenticação baseada em JWT. Para acessar determinados recursos, é necessário obter um token de autenticação.

## Documentação da API
A documentação interativa pode ser acessada através do Swagger em:
```
http://localhost:8080/swagger-ui.html
```

## Contribuição
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b minha-feature`)
3. Commit suas alterações (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin minha-feature`)
5. Abra um Pull Request

## Licença
Este projeto está sob a licença MIT.

