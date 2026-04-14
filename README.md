# API Cliente (exemplo)

API REST de exemplo em **Kotlin** e **Spring Boot** com CRUD de clientes. Em desenvolvimento local usa **H2** em memória; no **deploy com Docker** usa **MySQL 8**.

Este repositório atende entregas que exigem **README com instruções de deploy do ambiente** e **consumo da API**. Substitua abaixo o link pelo seu repositório no GitHub ao enviar a atividade:

**Repositório (GitHub):** `https://github.com/SEU-USUARIO/SEU-REPO`

---

## 1. Deploy do ambiente

“Deploy” aqui significa colocar API + banco rodando de forma reproduzível. A forma recomendada é **Docker Compose** (um comando sobe tudo).

### 1.1 Pré-requisitos

| Cenário | O que instalar |
|---------|----------------|
| **Deploy com Docker (recomendado)** | [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclui Docker Compose) |
| **Só na máquina, sem container** | **JDK 11** |

### 1.2 Deploy com Docker Compose (API + MySQL)

Na **raiz do projeto** (onde está o `docker-compose.yml`):

```bash
docker compose up --build
```

- Aguarde o MySQL ficar *healthy* e a API subir (o `depends_on` com `condition: service_healthy` garante a ordem).
- **URL base da API:** `http://localhost:8080`
- MySQL no **host** na porta **3307** (usuário/senha/banco estão no `docker-compose.yml` e em `application-docker.yml`).

**Encerrar os containers** (dados do MySQL persistem no volume `mysql_data`):

```bash
docker compose down
```

**Remover também o volume** (apagar dados do banco):

```bash
docker compose down -v
```

### 1.3 Verificar se o ambiente subiu

No navegador ou no terminal:

- `http://localhost:8080/clientes` — deve responder com JSON (mapa de clientes, possivelmente vazio `{}`).
- Ou Swagger: `http://localhost:8080/swagger-ui.html`

### 1.4 Alternativa: rodar sem Docker (apenas para desenvolvimento)

Usa **H2** em memória (dados somem ao desligar a aplicação).

**Windows:**

```bat
gradlew.bat clean build
gradlew.bat bootRun
```

**Linux / macOS:**

```bash
./gradlew clean build
./gradlew bootRun
```

Também é possível executar a função `main` em `com.viniciuspessoni.Aplicacao.kt` pelo IntelliJ.

---

## 2. Consumo da API

Use sempre a **URL base** do ambiente que você subiu (local: `http://localhost:8080`).

### 2.1 Visão geral dos endpoints

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/` ou `/clientes` | Lista todos os clientes |
| `GET` | `/cliente/{id}` | Busca um cliente pelo ID |
| `GET` | `/risco/{id}` | Calcula e grava o risco do cliente (**HTTP Basic obrigatório**) |
| `POST` | `/cliente` | Cadastra cliente (JSON) |
| `PUT` | `/cliente` | Atualiza cliente existente (JSON com `id`) |
| `DELETE` | `/cliente/{id}` | Remove por ID |
| `DELETE` | `/cliente/apagaTodos` | Remove todos os registros (útil em testes) |

**Autenticação (somente `/risco/**`):**

- Usuário: `aluno`
- Senha: `senha`

### 2.2 Exemplos com `curl`

Use `http://localhost:8080` como base (ou a URL do servidor onde fez o deploy). No Windows, se `curl` for alias do PowerShell, chame `curl.exe` ou use Postman (seção 2.3).

**Listar todos**

```bash
curl -s http://localhost:8080/clientes
```

**Buscar por ID** (troque `1` pelo ID retornado no cadastro)

```bash
curl -s http://localhost:8080/cliente/1
```

**Cadastrar** (`POST`, `Content-Type: application/json`) — um comando por linha:

```bash
curl -s -X POST http://localhost:8080/cliente -H "Content-Type: application/json" -d "{\"nome\":\"Maria\",\"idade\":25}"
```

**Atualizar** (`PUT` — inclua o `id` existente)

```bash
curl -s -X PUT http://localhost:8080/cliente -H "Content-Type: application/json" -d "{\"id\":1,\"nome\":\"Maria Silva\",\"idade\":26,\"risco\":0}"
```

**Risco** (Basic Auth — calcula `risco = 110 - idade * 5` e persiste)

```bash
curl -s -u aluno:senha http://localhost:8080/risco/1
```

**Remover um cliente**

```bash
curl -s -X DELETE http://localhost:8080/cliente/1
```

### 2.3 Consumo via Postman (ou Insomnia)

1. Crie uma requisição `POST` para `http://localhost:8080/cliente`.
2. Aba **Body** → **raw** → **JSON**, por exemplo: `{"nome":"Maria","idade":25}`.
3. Para `GET http://localhost:8080/risco/1`, em **Authorization** escolha **Basic Auth** e informe `aluno` / `senha`.

### 2.4 Corpos JSON de referência

**POST** `/cliente` (ID gerado automaticamente; não é necessário enviar `id`):

```json
{
  "nome": "Maria",
  "idade": 25
}
```

**PUT** `/cliente`:

```json
{
  "id": 1,
  "nome": "Maria Silva",
  "idade": 26,
  "risco": 0
}
```

---

## 3. Stack técnica (resumo)

| Item | Detalhe |
|------|---------|
| Linguagem | Kotlin 1.3.x |
| Framework | Spring Boot 2.2.x (Web, JPA, Security) |
| Banco local | H2 (memória) |
| Banco Docker | MySQL 8 |
| Documentação | Springfox — `/swagger-ui.html` |
| Build | Gradle |

---

## 4. Testes automatizados

```bash
./gradlew test
```

Windows: `gradlew.bat test`.

---

## 5. Arquivos relevantes no repositório

| Arquivo | Função |
|---------|--------|
| `docker-compose.yml` | Orquestra API + MySQL |
| `Dockerfile` | Imagem da API (build + runtime Java 11) |
| `src/main/resources/application.yml` | Perfil padrão (H2) |
| `src/main/resources/application-docker.yml` | Perfil `docker` (MySQL) |
| `src/main/kotlin/.../Aplicacao.kt` | Entrada da aplicação |
| `src/main/kotlin/.../controller/ClienteController.kt` | Endpoints REST |
| `src/main/kotlin/.../config/ConfiguracaoSegura.kt` | Basic Auth em `/risco/**` |

---

## 6. Licença / uso

Projeto de exemplo para aprendizado; adapte conforme sua necessidade.
