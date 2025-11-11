# Finance Management Application - Backend API

Application complète de gestion financière personnelle avec Spring Boot 3.2, Java 21, PostgreSQL et JWT.

## Technologies

- **Java**: 21
- **Spring Boot**: 3.2.5
- **Spring Security**: JWT Authentication
- **Spring Data JPA**: Accès aux données
- **PostgreSQL**: 16
- **Maven**: Build tool
- **OpenAPI/Swagger**: Documentation API
- **Docker**: Containerisation

## Fonctionnalités

### Authentification
- Inscription et connexion utilisateur
- Authentification JWT
- Gestion des rôles (USER, ADMIN)

### Gestion des comptes
- Création de comptes bancaires multiples
- Types de comptes (courant, épargne, crédit, investissement, etc.)
- Suivi des soldes en temps réel
- Activation/désactivation des comptes

### Gestion des catégories
- Catégories de revenus et dépenses
- Hiérarchie de catégories (parent/enfant)
- Personnalisation (icônes, couleurs)

### Gestion des transactions
- Enregistrement des revenus et dépenses
- Transferts entre comptes
- Recherche et filtrage avancés
- Pagination des résultats
- Réconciliation des transactions

### Gestion des budgets
- Création de budgets par catégorie
- Périodes personnalisables (hebdomadaire, mensuel, trimestriel, annuel)
- Suivi du montant dépensé vs budget
- Alertes de dépassement de seuil
- Calcul automatique des pourcentages

## Structure du projet

```
finance-app/
├── src/
│   └── main/
│       ├── java/com/finance/
│       │   ├── config/              # Configuration Spring
│       │   ├── controller/          # Controllers REST
│       │   ├── dto/                 # DTOs (Java Records)
│       │   ├── entity/              # Entities JPA
│       │   ├── exception/           # Gestion des exceptions
│       │   ├── mapper/              # Mappers Entity <-> DTO
│       │   ├── repository/          # Repositories JPA
│       │   ├── security/            # Configuration JWT
│       │   └── service/             # Services métier
│       └── resources/
│           ├── application.yml
│           └── application-prod.yml
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Installation et démarrage

### Prérequis
- Java 21
- Maven 3.8+
- Docker et Docker Compose

### Étape 1: Cloner le projet

```bash
git clone <repository-url>
cd finance-app
```

### Étape 2: Configurer les variables d'environnement

```bash
cp .env.example .env
# Éditer .env avec vos configurations
```

### Étape 3: Démarrer PostgreSQL avec Docker

```bash
docker-compose up -d
```

Cela démarre :
- PostgreSQL sur le port 5432
- pgAdmin sur le port 5050 (http://localhost:5050)
  - Email: admin@finance.com
  - Password: admin

### Étape 4: Compiler et lancer l'application

```bash
# Compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application démarre sur http://localhost:8080

## Documentation API

### Swagger UI
Une fois l'application lancée, accédez à la documentation interactive :
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Endpoints principaux

#### Authentification
```
POST   /api/auth/register        - Inscription
POST   /api/auth/login           - Connexion
```

#### Comptes
```
GET    /api/accounts             - Liste des comptes
GET    /api/accounts/{id}        - Détails d'un compte
POST   /api/accounts             - Créer un compte
PUT    /api/accounts/{id}        - Modifier un compte
DELETE /api/accounts/{id}        - Supprimer un compte
GET    /api/accounts/active      - Comptes actifs
GET    /api/accounts/total-balance - Solde total
PATCH  /api/accounts/{id}/toggle-status - Activer/désactiver
```

#### Catégories
```
GET    /api/categories           - Liste des catégories
GET    /api/categories/{id}      - Détails d'une catégorie
POST   /api/categories           - Créer une catégorie
PUT    /api/categories/{id}      - Modifier une catégorie
DELETE /api/categories/{id}      - Supprimer une catégorie
GET    /api/categories/root      - Catégories racines
GET    /api/categories/type/{type} - Par type (INCOME/EXPENSE)
```

#### Transactions
```
GET    /api/transactions         - Liste paginée
GET    /api/transactions/{id}    - Détails d'une transaction
POST   /api/transactions         - Créer une transaction
PUT    /api/transactions/{id}    - Modifier une transaction
DELETE /api/transactions/{id}    - Supprimer une transaction
GET    /api/transactions/account/{id} - Par compte
GET    /api/transactions/date-range - Par période
GET    /api/transactions/search  - Recherche par mot-clé
```

#### Budgets
```
GET    /api/budgets              - Liste des budgets
GET    /api/budgets/{id}         - Détails d'un budget
POST   /api/budgets              - Créer un budget
PUT    /api/budgets/{id}         - Modifier un budget
DELETE /api/budgets/{id}         - Supprimer un budget
GET    /api/budgets/active       - Budgets actifs
GET    /api/budgets/current      - Budgets en cours
PATCH  /api/budgets/{id}/toggle-status - Activer/désactiver
```

## Exemples de requêtes

### 1. Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Réponse:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 2. Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'
```

### 3. Créer un compte (avec JWT)

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Compte Courant",
    "description": "Mon compte principal",
    "type": "CHECKING",
    "balance": 1000.00,
    "currency": "EUR"
  }'
```

### 4. Créer une transaction

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 50.00,
    "type": "EXPENSE",
    "transactionDate": "2024-01-15",
    "description": "Courses alimentaires",
    "payee": "Supermarché",
    "accountId": 1,
    "categoryId": 1
  }'
```

### 5. Créer un budget

```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Budget Alimentation Janvier",
    "amount": 400.00,
    "period": "MONTHLY",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "categoryId": 1,
    "alertThreshold": 80.00
  }'
```

## Modèle de données

### User
- id, username, email, password
- firstName, lastName, role
- enabled, createdAt, updatedAt

### Account
- id, name, description, type
- balance, currency, active
- userId, createdAt, updatedAt

### Category
- id, name, description, type
- icon, color, parentId
- userId, createdAt, updatedAt

### Transaction
- id, amount, type, transactionDate
- description, payee, reference, notes
- accountId, categoryId, userId
- transferAccountId, reconciled
- createdAt, updatedAt

### Budget
- id, name, amount, spent
- period, startDate, endDate
- description, categoryId, userId
- active, alertThreshold
- createdAt, updatedAt

## Sécurité

- Authentification JWT
- Mots de passe hashés avec BCrypt
- CORS configuré
- Validation des DTOs
- Contrôle d'accès par utilisateur

## Configuration

### application.yml
Toutes les configurations sont externalisables via variables d'environnement :
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `SERVER_PORT`
- `JWT_SECRET`, `JWT_EXPIRATION`

### Profils Spring
- **dev** (par défaut): Logs détaillés, DDL auto
- **prod**: Logs minimaux, DDL validate

## Tests

```bash
# Exécuter les tests
mvn test

# Avec couverture
mvn clean test jacoco:report
```

## Build pour production

```bash
# Créer le JAR
mvn clean package -DskipTests

# Lancer le JAR
java -jar target/finance-app-1.0.0.jar --spring.profiles.active=prod
```

## Docker (Application complète)

Créer un Dockerfile pour l'application :

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/finance-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Puis ajouter au docker-compose.yml :

```yaml
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=postgres
      - DB_PORT=5432
    depends_on:
      - postgres
```

## Support et contribution

Pour toute question ou contribution :
1. Ouvrir une issue
2. Créer une pull request
3. Contacter l'équipe de développement

## Licence

Apache License 2.0
