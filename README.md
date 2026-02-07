# Sistema de Gestão de Assinaturas

Sistema completo de gestão de assinaturas para serviços de streaming desenvolvido com Spring Boot, incluindo processamento de pagamentos, renovação automática, cache Redis, mensageria RabbitMQ e API REST completa.

## 🚀 Tecnologias Utilizadas

- **Spring Boot 4.0.2** - Framework principal
- **PostgreSQL** - Banco de dados principal
- **Redis** - Cache e sessões
- **RabbitMQ** - Mensageria assíncrona
- **Flyway** - Migrações de banco de dados
- **Lombok** - Redução de boilerplate
- **JUnit 5 + Mockito** - Testes
- **OpenAPI/Swagger** - Documentação da API
- **Docker & Docker Compose** - Containerização

## 📋 Funcionalidades

### ✅ Gestão de Usuários
- Criar, atualizar, listar e buscar usuários
- Validação de email único
- Cache automático dos dados

### ✅ Gestão de Assinaturas
- Criar assinaturas com diferentes planos (Básico, Premium, Família)
- Cancelar, suspender e reativar assinaturas
- Alterar planos
- Controle de uma assinatura ativa por usuário
- Cache inteligente por TTL

### ✅ Processamento de Pagamentos
- Simulação de gateway de pagamento
- Histórico completo de pagamentos
- Retry automático em falhas
- Relatórios por período

### ✅ Renovação Automática
- Processamento diário às 9h (agendado)
- Retry automático com backoff
- Máximo 3 tentativas antes da suspensão
- Notificações via RabbitMQ

### ✅ Cache Inteligente (Redis)
- Cache de usuários (TTL: 1h)
- Cache de assinaturas (TTL: 30min)
- Cache de assinaturas ativas (TTL: 5min)
- Cache de histórico de pagamentos (TTL: 2h)

### ✅ Mensageria (RabbitMQ)
- Filas para renovação automática
- Notificações de sucesso/falha
- Retry com delay automático
- Dead letter queues

## 🏗️ Arquitetura

```
src/
├── main/
│   ├── java/com/globo/subscriptionapplication/
│   │   ├── config/               # Configurações (Cache, RabbitMQ)
│   │   ├── controller/           # REST Controllers
│   │   ├── domain/
│   │   │   ├── enums/           # Enums (Planos, Status)
│   │   │   ├── model/           # Entidades JPA
│   │   │   └── repository/      # Repositórios Spring Data
│   │   ├── dto/                 # DTOs de Request/Response
│   │   ├── exception/           # Tratamento de exceções
│   │   ├── messaging/           # Listeners RabbitMQ
│   │   └── service/             # Regras de negócio
│   └── resources/
│       ├── db/migration/        # Scripts Flyway
│       └── application.yml      # Configurações
└── test/                        # Testes unitários
```

## 🚦 Como Executar

### Pré-requisitos
- Java 21
- Docker & Docker Compose
- Maven 3.8+

### 1. Iniciar Infraestrutura
```bash
docker-compose up -d
```
Isso iniciará:
- PostgreSQL na porta 5432
- Redis na porta 6379
- RabbitMQ na porta 5672 (Management: 15672)

### 2. Compilar e Executar
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### 3. Acessar Aplicação
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 📚 API Endpoints

### Usuários
```http
POST   /api/users                        # Criar usuário
GET    /api/users/{userId}               # Buscar usuário por ID

```

### Assinaturas
```http
POST   /api/subscriptions                      # Criar assinatura
GET    /api/subscriptions/{subscriptionId}     # Buscar assinatura por ID
GET    /api/subscriptions/user/{userId}/active # Buscar assinatura ativa do usuário
PUT    /api/subscriptions/{subscriptionId}/cancel # Cancelar assinatura
PUT    /api/subscriptions/{subscriptionId}/plan   # Atualizar plano

```


## 🧪 Exemplos de Uso

### 1. Criar um Usuário
```bash
curl -X POST http://localhost:8080/subscriptions/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com"
  }'
```

### 2. Criar uma Assinatura
```bash
curl -X POST http://localhost:8080/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid-do-usuario",
    "plan": "PREMIUM"
  }'
```

### 3. Alterar Plano
```bash
curl -X PUT http://localhost:8080/subscriptions/{id}/plan \
  -H "Content-Type: application/json" \
  -d '{
    "plan": "FAMILIA"
  }'
```

## 📊 Planos Disponíveis

| Plano    | Preço   | Descrição                    |
|----------|---------|------------------------------|
| BASICO   | R$ 19,90| Acesso básico, 1 tela       |
| PREMIUM  | R$ 29,90| HD, 2 telas                 |
| FAMILIA  | R$ 39,90| 4K, 4 telas, perfis família |

## 🔧 Configurações

### Banco de Dados
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/subscriptions_db
    username: postgres
    password: postgres
```

### Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### RabbitMQ
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 📈 Monitoramento

### Actuator Endpoints
- `/actuator/health` - Status da aplicação
- `/actuator/metrics` - Métricas
- `/actuator/cache` - Status do cache

### Logs
A aplicação utiliza logging estruturado com diferentes níveis:
- `INFO` - Operações principais
- `DEBUG` - Detalhes de cache e queries
- `WARN` - Pagamentos falhados
- `ERROR` - Erros críticos

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com cobertura
./mvnw test jacoco:report

# Ver relatório de cobertura
open target/site/jacoco/index.html
```

## 🔄 Renovação Automática

O sistema executa renovação automática das assinaturas:
- **Agendamento**: Todos os dias às 9h
- **Retry**: Máximo 3 tentativas
- **Delay**: 1 minuto entre tentativas
- **Suspensão**: Automática após 3 falhas

### Fluxo de Renovação
1. Identifica assinaturas expiradas
2. Processa pagamento
3. Se sucesso: renova por mais 1 mês
4. Se falha: agenda retry ou suspende
5. Envia notificações via RabbitMQ

## 🚀 Deploy

### Usando Docker
```bash
# Build da aplicação
./mvnw clean package

# Build da imagem Docker
docker build -t subscription-app .

# Executar com Docker Compose
docker-compose up
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'Add nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT.

## 📞 Suporte

Para suporte ou dúvidas:
- Abra uma issue no GitHub
- Consulte a documentação da API em `/swagger-ui.html`
- Verifique os logs da aplicação
