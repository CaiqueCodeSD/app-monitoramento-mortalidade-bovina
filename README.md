# 📱🐂 App de Monitoramento de Mortalidade Bovina

Aplicação Android desenvolvida para registrar e visualizar dados de mortalidade bovina, evoluindo de dados locais para consumo de API simulada.

---

## 🚀 Tecnologias utilizadas

- Kotlin
- Android SDK
- Retrofit
- Coroutines
- StateFlow
- Shimmer (Facebook)

---

## 📡 API (Mockoon)

A aplicação utiliza uma API simulada com Mockoon para testes de integração.

### 🔧 Como configurar

1. Instale o Mockoon
2. Crie uma rota `GET /registros`
3. Utilize um JSON de exemplo (presente no projeto ou gerado manualmente)

---

## 🌐 Configuração da base URL

Dependendo do ambiente:

### Emulador Android:
http://10.0.2.2:3000/
### Dispositivo físico:
http://SEU_IP_LOCAL:3000/


---

## 🧪 Cenários testados

- Sucesso com dados
- Lista vazia
- Erro de servidor (500)
- Timeout
- Falha de conexão

---

## 🎯 Funcionalidades

- Listagem de registros de mortalidade
- Loading com Shimmer
- Tratamento de erros
- Empty state (sem dados)

---

## 📌 Observação

A API utilizada é apenas para simulação e não representa um ambiente de produção.
