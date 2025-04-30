# 🧩 HubSpot Integrator Backend

Integração moderna com a API do **HubSpot CRM**, construída com **Java 21 + Spring Boot 3.3**, focada em clareza, boas práticas e tecnologias atuais.

## ✅ Funcionalidades implementadas

- Autenticação OAuth 2.0 (Authorization Code Flow)
- Criação de contatos no HubSpot via API
- Recebimento de Webhooks para eventos do tipo `contact.creation`
- Validação de assinatura do Hubspot utilizando hash SHA-256
- Controle de taxa de requisição (Rate Limit) com Resilience4j
- Documentação da API com Swagger
- Testes unitários e de integração com JUnit e Mockito

---
## 🚀 Como rodar localmente

### Pré-requisitos

- Java 21
- Maven 3.9+
- Conta gratuita no HubSpot Developer
- Ferramenta de tunelamento como [Ngrok](https://ngrok.com) (para testar webhooks)

### Variáveis de ambiente

Configure as variáveis a seguir (no `.env` ou no ambiente de execução):

```properties
HUBSPOT_CLIENT_ID=<seu-client-id>
HUBSPOT_CLIENT_SECRET=<seu-client-secret>
HUBSPOT_REDIRECT_URI=http://<sua-url-ngrok>/api/oauth/callback
```

```Url de redirecionamento
> ⚠️ Atenção: a URI de redirecionamento (redirect_uri) deve estar registrada no app da HubSpot.  
> Se estiver rodando localmente com Ngrok, copie a URL gerada (ex.: `https://<ngrok-id>.ngrok-free.app/api/oauth/callback`) e registre como redirect_uri no painel da HubSpot conforme exemplo da variável acima.  
> Do contrário, o OAuth 2.0 irá falhar com erro de "redirect_uri mismatch".
```

> Essas credenciais são geradas ao criar um app no [HubSpot Developer](https://developers.hubspot.com/).

### Iniciando o projeto

Clone o projeto e rode:

```bash
./mvnw spring-boot:run
```
---

## 🔐 Autenticação OAuth 2.0

Para iniciar o fluxo de autenticação com o HubSpot, acesse:

```
GET http://localhost:8080/api/oauth/authorization
```

Você será redirecionado para o consentimento do HubSpot. Após autorizar, será redirecionado de volta ao callback que troca o `code` por um `access_token`.

---

## 📬 Webhook de criação de contato

Endpoint configurado para escutar eventos do tipo `contact.creation`:

Também foi implementada a validação da assinatura da HubSpot seguindo o padrão da versão v1, conforme documentação oficial.
A lógica compara o hash SHA-256 do clientSecret + requestBody com o valor enviado no header X-HubSpot-Signature.
Essa verificação está encapsulada na classe HubspotSignatureValidator.

```
POST /api/webhook
```
Como esse endpoint requer HTTPS, é necessário utilizar ferramentas como Ngrok ou de sua preferência:

```bash
ngrok http 8080
```

Registre a URL pública no app do HubSpot para testes.

---

## 🧪 Testes

Implementei testes com `MockMvc`, `Mockito` e `JUnit`, cobrindo:

- Criação de contatos com validação de payload e resposta
- Geração da URL de autorização do OAuth
- Casos de sucesso e falha

```bash
mvn test
```

---

## 📘 Documentação da API (Swagger)

Você pode visualizar os contratos da API em:

```
http://localhost:8080/swagger-ui/index.html
```
Para manter o código das Controllers mais limpo e organizado, foram criadas interfaces de documentação do Swagger para que então as Controllers implementem essas interfaces.
Essa abordagem promove uma separação clara entre a lógica de negócio da Controller e a documentação da API, facilitando a manutenção e a leitura do código.

## 📦 Justificativa das dependências

### 🌐 `RestClient`
Escolhi o `RestClient` do Spring 6+ em vez do `RestTemplate` por ser a abordagem mais moderna, fluente e compatível com o futuro do ecossistema Spring. Ele também permite aplicar interceptadores de forma mais simples e elegante.

### 🔁 `Resilience4j`
Utilizado para controle de taxa de requisição à API do HubSpot, respeitando os limites de `Rate Limit` impostos pela plataforma e tratando exceções como `429 Too Many Requests`.

### 📖 `Springdoc OpenAPI`
Adicionei `springdoc-openapi-starter-webmvc-ui` para documentação automática dos endpoints com Swagger, facilitando o entendimento e o uso da API.

### 🧪 `Mockito`, `spring-boot-starter-test`
Usados para simular serviços externos e garantir que as unidades funcionem corretamente em isolamento.

### ⚙️ Outras dependências
- `lombok`: redução de boilerplate (getters/setters, builder, requiredArgsConstructor, etc.)
- `spring-boot-starter-validation`: validações automáticas via `@Valid` e `jakarta.validation` (@Email, @NotEmpty, etc.)
- `webflux`: usado como base para o `RestClient`, mesmo sem programação reativa explícita utilizei-o para configurar o interceptador.

---

---
## 🧠 Melhorias futuras
- Implementação do Redis para armazenar e gerenciar access_token e refresh_token, melhorando a performance e facilitando o controle de expiração e renovação dos tokens. Isso evita a sobrecarga do fluxo de autenticação OAuth 2.0 em cada requisição.
- Criptografia do Token: Armazenamento criptografado dos tokens (no Redis ou em qualquer armazenamento) para evitar acessos não autorizados, garantindo maior segurança nas comunicações com o HubSpot.
- Logs: Implementação de logs robustos com Slf4j para rastrear falhas, erros e detalhes cruciais como tokens expirados ou falhas na validação de webhooks. Isso facilita a análise e resolução de problemas em produção.
- Integração com ferramentas de monitoramento como Datadog ou Grafana para monitorar a saúde do sistema e gerar alertas em caso de falhas críticas.
---

## ✍️ Considerações finais

Este projeto foi construído com atenção a boas práticas, clareza de código e tecnologias modernas do ecossistema Java. O uso de registros (`record`), interceptadores, controle de taxa, e tratamento de erros foi feito com o objetivo de criar uma base sólida e extensível.

> Caso deseje rodar o webhook localmente, lembre-se de expor a porta da sua máquina com ferramentas como Ngrok.

---

**Desenvolvido por:** Luan Olegario
- Contato: luanm.olegario@outlook.com
- Linkedin: https://www.linkedin.com/in/luan-olegario

> GPT foi usado como apoio na documentação e sugestões, mas todas as decisões técnicas e implementações são autorais.


## ✍️ Git

> Para todas as pessoas que desejarem contribuir com o projeto, deverão criar uma nova branch para suas alterações. Os commits diretamente na branch main não serão aceitos e todas as contribuições passarão por avaliação antes de serem incorporadas ao projeto.
