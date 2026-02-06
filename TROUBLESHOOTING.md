# 🔧 Guia de Troubleshooting - Sistema de Assinaturas

## 🚨 Problema: "Connection to localhost:5432 refused"

### Causa
O PostgreSQL não está rodando ou não está acessível na porta 5432.

### Solução

#### Opção 1: Rodar apenas os serviços de infraestrutura no Docker
```bash
# 1. Certifique-se que o Docker Desktop está rodando
open -a Docker

# 2. Aguarde alguns segundos e inicie os serviços
cd /Users/arthur/Documents/projects/subscriptionapplication
docker-compose up -d postgres redis rabbitmq

# 3. Verifique se estão rodando
docker-compose ps

# 4. Rode a aplicação localmente
./mvnw spring-boot:run
```

#### Opção 2: Rodar TUDO no Docker (recomendado)
```bash
# 1. Certifique-se que o Docker Desktop está rodando
open -a Docker

# 2. Suba todos os serviços (incluindo a aplicação)
cd /Users/arthur/Documents/projects/subscriptionapplication
docker-compose up -d

# 3. Verifique os logs
docker-compose logs -f app

# 4. Acesse a aplicação
curl http://localhost:8080/api/actuator/health
```

---

## 🔍 Verificando o Status dos Serviços

### Verificar containers rodando
```bash
docker-compose ps
```

### Verificar logs de um serviço específico
```bash
docker logs subscriptions-api        # Aplicação
docker logs subscriptions-db         # PostgreSQL
docker logs subscriptions-cache      # Redis
docker logs subscriptions-mq         # RabbitMQ
```

### Verificar health da aplicação
```bash
curl http://localhost:8080/api/actuator/health | jq .
```

---

## ⚠️ Problema: "Found 0 JPA repository interfaces"

### Causa
O Spring Data está em modo estrito porque detectou múltiplos módulos (JPA e Redis).

### Solução (opcional)
Se você não está usando Redis como repositório de dados (apenas como cache), pode adicionar esta configuração:

**Em `application.yml`:**
```yaml
spring:
  data:
    redis:
      repositories:
        enabled: false  # Desabilita Redis repositories
```

Isso força o Spring a usar apenas JPA repositories.

---

## 🔄 Reiniciando os Serviços

### Reiniciar apenas a aplicação
```bash
docker restart subscriptions-api
```

### Reiniciar todos os serviços
```bash
docker-compose restart
```

### Parar tudo e limpar
```bash
docker-compose down
docker-compose up -d
```

---

## 🔗 URLs Úteis

- **Aplicação**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/actuator/health
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **PostgreSQL**: localhost:5432 (user/password)
- **Redis**: localhost:6379

---

## 📊 Portas Utilizadas

| Serviço    | Porta(s)      | Container             |
|------------|---------------|-----------------------|
| PostgreSQL | 5432          | subscriptions-db      |
| Redis      | 6379          | subscriptions-cache   |
| RabbitMQ   | 5672, 15672   | subscriptions-mq      |
| API        | 8080          | subscriptions-api     |

---

## 🐛 Debug Rápido

### Aplicação não inicia?
1. Verifique se o Docker está rodando: `docker ps`
2. Verifique se o PostgreSQL está healthy: `docker-compose ps`
3. Verifique os logs: `docker-compose logs -f app`

### Erro de conexão com banco?
```bash
# Teste a conexão diretamente
docker exec -it subscriptions-db psql -U user -d subscriptions_db
```

### Redis não conecta?
```bash
# Teste o Redis
docker exec -it subscriptions-cache redis-cli ping
# Deve retornar: PONG
```

### RabbitMQ não conecta?
```bash
# Acesse o management UI
open http://localhost:15672
# Usuário: guest / Senha: guest
```
