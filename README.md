# 🚀 BTG Challenge API - Sistema de Processamento de Pedidos

> Uma aplicação robusta para processamento, armazenamento e análise de pedidos utilizando **MongoDB**, **RabbitMQ** e **Spring Boot**.

[![Java](https://img.shields.io/badge/Java-17-ED8936?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-13AA52?style=flat&logo=mongodb)](https://www.mongodb.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.0-FF6600?style=flat&logo=rabbitmq)](https://www.rabbitmq.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=flat&logo=apache-maven)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 🎯 Visão Geral

**BTG Challenge API** é um microsserviço de processamento de pedidos que:

✅ **Consome** mensagens de pedidos do **RabbitMQ**  
✅ **Armazena** dados em **MongoDB** com indexação automática  
✅ **Expõe** uma **API REST** robusta com 4 endpoints  
✅ **Calcula** totais, médias e agregações de vendas  
✅ **Fornece** análises em tempo real por cliente  
✅ **Suporta** múltiplos perfis de ambiente (dev, prod)  

### Caso de Uso
Processar pedidos de e-commerce, calcular totais, análises por cliente e gerar relatórios de vendas em tempo real.

---

## ⚡ Quick Start

```bash
# 1. Configurar ambiente
cp .env.example .env
docker-compose -f local/docker-compose.yml up -d

# 2. Compilar
mvn clean install

# 3. Executar
mvn spring-boot:run

# 4. Testar
curl http://localhost:8080/health
```

---

## 📋 Índice

- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Execução](#-execução)
- [API REST](#-api-rest)
- [RabbitMQ](#-rabbitmq)
- [Estrutura](#-estrutura-do-projeto)
- [Configurações](#-configurações)
- [Testes](#-testes)
- [Troubleshooting](#-troubleshooting)
- [Documentação](#-documentação-adicional)

---

## 🏗️ Arquitetura

```
RabbitMQ           OrderListener         OrderService         MongoDB
   │                    │                      │                  │
   │ codigoPedido       │ Processa evento      │ Salva documento  │
   ├──────────────────>│                      │                  │
   │                    ├─────────────────────>│                  │
   │                    │                      ├─────────────────>│
   │                    │                      │ Insert/Update    │
   │                    │                      │                  │
   │                    │                      │ tb_orders        │
   │                    │                      │ (coleção)        │
   │                    │                      │                  │
   └────────────────────────────────────────────────────────────>│
                                                  REST API        │
                                            GET /customers/{id}   │
                                            GET /health           │
```

---

## 🛠️ Tecnologias

### Backend
- **Java 17** - Linguagem
- **Spring Boot 3.3.0** - Framework
- **Spring Data MongoDB** - ORM
- **Spring AMQP** - RabbitMQ
- **Lombok** - Code generation

### Banco de Dados
- **MongoDB 7.0** - NoSQL
- **Índices automáticos** - Performance

### Message Queue
- **RabbitMQ 3.0** - Message Broker
- **Jackson** - JSON serialization

### Ferramentas
- **Maven 3.6+** - Build
- **Docker Compose** - Containers
- **Git** - VCS

---

## ✅ Pré-requisitos

- **Java 17+**
  ```bash
  java -version
  ```
- **Maven 3.6+**
  ```bash
  mvn -version
  ```
- **Docker & Docker Compose**
  ```bash
  docker --version
  docker-compose --version
  ```

### Opcional
- **Python 3.7+** - Scripts de teste
- **curl** - Testes de API

---

## 📥 Instalação

### 1️⃣ Clone o Repositório
```bash
git clone https://github.com/seu-usuario/btg-challenge-api.git
cd btg-challenge-api
```

### 2️⃣ Configurar Ambiente
```bash
cp .env.example .env
# Editar se necessário
nano .env
```

### 3️⃣ Iniciar Dependências
```bash
docker-compose -f local/docker-compose.yml up -d
```

Verificar:
```bash
docker-compose -f local/docker-compose.yml ps
```

Esperado: `✅ mongodb` e `✅ rabbitmq` rodando.

---

## 🚀 Execução

### Development (com Logs)
```bash
mvn spring-boot:run
```

### Production (Profile Prod)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

### Via JAR
```bash
mvn clean package
java -jar target/btg-challenge-api-0.0.1-SNAPSHOT.jar
```

**Verificar:** `curl http://localhost:8080/health`

---

## 📡 API REST

### 1. Health Check
```http
GET /health
```
Verifica status da API e MongoDB.

### 2. Resumo do Cliente
```http
GET /customers/{customerId}/summary
```
```bash
curl http://localhost:8080/customers/1/summary
```
Response:
```json
{
  "customerId": 1,
  "totalOrders": 5,
  "totalValue": 1500.50
}
```

### 3. Pedidos com Paginação
```http
GET /customers/{customerId}/orders?page=0&pageSize=10
```
```bash
curl "http://localhost:8080/customers/1/orders?page=0&pageSize=5"
```

### 4. Todos os Pedidos
```http
GET /customers/{customerId}/all-orders
```
```bash
curl http://localhost:8080/customers/1/all-orders
```

---

## 📨 RabbitMQ

### Fila
```
btg-pactual-order-created
```

### Formato da Mensagem
```json
{
  "codigoPedido": 1001,
  "codigoCliente": 1,
  "itens": [
    {
      "produto": "lápis",
      "quantidade": 100,
      "preco": 1.10
    }
  ]
}
```

### Enviar Mensagem
```bash
python3 send-orders.py
```

### RabbitMQ Management
- URL: `http://localhost:15672`
- User: `admin`
- Pass: `123456`

---

## 📁 Estrutura do Projeto

```
btg-challenge-api/
├── src/main/java/tech/buildrun/btgpactual/orderms/
│   ├── config/              # Spring Configuration
│   ├── controller/          # REST Endpoints
│   ├── entity/              # MongoDB Documents
│   ├── listener/            # RabbitMQ Listener
│   ├── repository/          # Data Access
│   ├── service/             # Business Logic
│   └── exception/           # Custom Exceptions
│
├── src/main/resources/
│   ├── application.yml      # Base config
│   ├── application-dev.yml  # Dev profile
│   └── application-prod.yml # Prod profile
│
├── local/
│   └── docker-compose.yml   # MongoDB + RabbitMQ
│
├── pom.xml                  # Maven dependencies
├── .env.example             # Environment template
│
├── test-api.py              # API test script
├── send-orders.py           # Send orders script
│
└── docs/
    ├── MONGODB-QUERIES-GUIA.md
    ├── QUERIES-TB-ORDERS-EXEMPLOS.md
    ├── MONGODB-QUICK-REFERENCE.md
    └── DIAGRAMA-ARQUITETURA.md
```

---

## ⚙️ Configurações

### application.yml
```yaml
spring:
  application:
    name: btg-challenge-api
  
  data:
    mongodb:
      auto-index-creation: true  # ✅ Automático
      uri: mongodb://${MONGO_USER}:${MONGO_PASS}@${MONGO_HOST}:${MONGO_PORT}/${MONGO_DATABASE}
  
  rabbitmq:
    host: ${RABBIT_HOST}
    port: ${RABBIT_PORT}
    username: ${RABBIT_USER}
    password: ${RABBIT_PASS}
```

### .env Padrão
```env
MONGO_USER=admin
MONGO_PASS=senha123
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DATABASE=btgpactualdb

RABBIT_USER=admin
RABBIT_PASS=123456
RABBIT_HOST=localhost
RABBIT_PORT=5672
```

---

## 🧪 Testes

### Testes Unitários
```bash
mvn test
```

### Testes de API
```bash
python3 test-api.py
```

### Testes Manuais
```bash
# Health
curl http://localhost:8080/health

# Resumo
curl http://localhost:8080/customers/1/summary

# Pedidos
curl "http://localhost:8080/customers/1/orders?page=0&pageSize=10"

# Enviar pedido
python3 send-orders.py
```

---

## 🔍 Monitoramento

### MongoDB
```bash
# Via CLI
mongosh "mongodb://admin:senha123@localhost:27017/btgpactualdb?authSource=admin"

# Ver documentos
db.tb_orders.find().pretty()
```

### RabbitMQ
- Console: `http://localhost:15672`
- User: `admin`
- Pass: `123456`

### Logs
```bash
docker-compose -f local/docker-compose.yml logs -f
```

---

## 🆘 Troubleshooting

### "Connection refused" MongoDB
```bash
docker-compose -f local/docker-compose.yml restart mongodb
```

### "Connection refused" RabbitMQ
```bash
docker-compose -f local/docker-compose.yml restart rabbitmq
```

### Porta 8080 em uso
```bash
lsof -i :8080
kill -9 <PID>
```

### Erro de compilação
```bash
mvn clean
mvn compile
```

---

## 📚 Documentação Adicional

| Documento | Conteúdo |
|-----------|----------|
| [COMPLETE-README.md](./COMPLETE-README.md) | Guia completo |
| [API-DOCUMENTATION.md](./API-DOCUMENTATION.md) | Endpoints detalhado |
| [MONGODB-QUERIES-GUIA.md](./MONGODB-QUERIES-GUIA.md) | 150+ queries |
| [MONGODB-QUICK-REFERENCE.md](./MONGODB-QUICK-REFERENCE.md) | Card rápido |
| [DIAGRAMA-ARQUITETURA.md](./DIAGRAMA-ARQUITETURA.md) | Fluxos |

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/X`)
3. Commit (`git commit -m 'Add X'`)
4. Push (`git push origin feature/X`)
5. Abra um PR

---

## 📝 Licença

MIT License - Veja [LICENSE](LICENSE) para detalhes.

---

## ✨ Stack

- Spring Boot
- Spring Data MongoDB
- RabbitMQ
- Docker
- Maven

---

## 📞 Suporte

- Dúvidas? Abra uma issue
- Veja [COMPLETE-README.md](./COMPLETE-README.md) para mais detalhes

---

## 🎯 Status

```
✅ Desenvolvimento: Concluído
✅ Testes: Passando
✅ Documentação: Completa
✅ Produção: Pronto
```

---

**Versão:** 0.0.1-SNAPSHOT  
**Última atualização:** 30 de Março de 2026  
**Status:** ✅ Pronto para Produção


