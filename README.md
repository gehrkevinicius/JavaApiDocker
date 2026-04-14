# API Cliente (exemplo)

API REST de exemplo para estudo de **Kotlin**, **Spring Boot** e **testes automatizados**. Expõe CRUD de clientes em memória (desenvolvimento local) ou em **MySQL** quando rodando com Docker.

## Stack

| Tecnologia | Uso |
|------------|-----|
| Kotlin 1.3.x | Linguagem |
| Spring Boot 2.2.x | Web, JPA, Security |
| H2 | Banco em memória (perfil padrão) |
| MySQL 8 | Banco com Docker (`spring.profiles.active=docker`) |
| Springfox | Documentação Swagger (UI) |
| Gradle | Build |

## Requisitos

- **JDK 11** (recomendado para alinhar com o `Dockerfile` e o Gradle do projeto)
- Opcional: **Docker** e **Docker Compose** para subir API + MySQL

## Como rodar localmente (sem Docker)

Na raiz do projeto:

**Windows (PowerShell ou CMD):**

```bat
gradlew.bat clean build
gradlew.bat bootRun
```

**Linux / macOS:**

```bash
./gradlew clean build
./gradlew bootRun
```

A API sobe em **http://localhost:8080** usando **H2** em memória (`application.yml`).

**IntelliJ:** abra o projeto, localize `com.viniciuspessoni.Aplicacao.kt` e execute a função `main`.

## Como rodar com Docker Compose

Sobe a API e o MySQL na mesma rede; a API usa o perfil `docker` (`application-docker.yml`).

```bash
docker compose up --build
```

- API: **http://localhost:8080**
- MySQL exposto no host na porta **3307** (container continua na 3306 internamente)

O healthcheck da API consulta `GET /clientes`. Para parar: `docker compose down` (o volume `mysql_data` mantém os dados entre execuções).

## Documentação Swagger

Com a aplicação no ar, abra no navegador:

**http://localhost:8080/swagger-ui.html**

## Endpoints principais

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/` ou `/clientes` | Lista todos os clientes (mapa `id` → cliente) |
| `GET` | `/cliente/{id}` | Busca cliente por ID |
| `GET` | `/risco/{id}` | Calcula e persiste o risco do cliente (**requer Basic Auth**) |
| `POST` | `/cliente` | Cadastra cliente (corpo JSON) |
| `PUT` | `/cliente` | Atualiza cliente existente |
| `DELETE` | `/cliente/{id}` | Remove por ID |
| `DELETE` | `/cliente/apagaTodos` | Remove todos (útil para testes) |

### Autenticação (endpoint `/risco/**`)

Apenas `GET /risco/**` exige **HTTP Basic**:

- **Usuário:** `aluno`
- **Senha:** `senha`

Os demais endpoints ficam públicos (CSRF desabilitado na configuração de segurança).

### Exemplos de corpo (JSON)

**POST** `/cliente` — o ID é gerado pelo banco; envie nome e idade (e opcionalmente `risco`, padrão `0`):

```json
{
  "nome": "Vinny",
  "idade": 30
}
```

**PUT** `/cliente` — inclua o `id` do registro a atualizar:

```json
{
  "id": 1,
  "nome": "Vinny",
  "idade": 31,
  "risco": 0
}
```

A regra de risco no domínio é: `risco = 110 - idade * 5` (calculada ao acessar `GET /risco/{id}`).

## Testes

```bash
./gradlew test
```

No Windows: `gradlew.bat test`.

## Estrutura útil do projeto

- `src/main/kotlin/com/viniciuspessoni/Aplicacao.kt` — ponto de entrada Spring Boot
- `src/main/kotlin/com/viniciuspessoni/controller/ClienteController.kt` — REST
- `src/main/resources/application.yml` — H2 (local)
- `src/main/resources/application-docker.yml` — MySQL (Docker)
- `docker-compose.yml` — serviços `cliente-api` e `mysql`
- `Dockerfile` — build multi-stage (JAR com Java 11)
- `Dockerfile.debug` — imagem auxiliar para depuração de build

## Licença / uso

Projeto de exemplo para aprendizado; adapte conforme sua necessidade.
