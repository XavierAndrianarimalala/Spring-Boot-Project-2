# Documentation API - Finance Management

## Vue d'ensemble

Cette API REST permet de gérer une application de finances personnelles complète avec gestion des comptes, transactions, catégories et budgets.

**Base URL**: `http://localhost:8080/api`

**Format**: JSON

**Authentification**: JWT Bearer Token (sauf endpoints `/api/auth/*`)

## Authentification

### Register (Inscription)

Créer un nouveau compte utilisateur.

**Endpoint**: `POST /auth/register`

**Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response** (201 Created):
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

**Validations**:
- username: 3-50 caractères, requis
- email: format email valide, requis
- password: minimum 6 caractères, requis

### Login (Connexion)

Authentifier un utilisateur existant.

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Login successful",
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

**Erreurs**:
- 401: Identifiants invalides

---

## Accounts (Comptes)

Tous les endpoints nécessitent l'authentification JWT.

**Header requis**: `Authorization: Bearer {token}`

### Créer un compte

**Endpoint**: `POST /accounts`

**Request Body**:
```json
{
  "name": "Compte Courant",
  "description": "Mon compte principal",
  "type": "CHECKING",
  "balance": 1000.00,
  "currency": "EUR"
}
```

**Types de compte**:
- `CHECKING`: Compte courant
- `SAVINGS`: Compte épargne
- `CREDIT_CARD`: Carte de crédit
- `INVESTMENT`: Compte d'investissement
- `CASH`: Espèces
- `LOAN`: Prêt
- `OTHER`: Autre

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "id": 1,
    "name": "Compte Courant",
    "description": "Mon compte principal",
    "type": "CHECKING",
    "balance": 1000.00,
    "currency": "EUR",
    "active": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### Lister tous les comptes

**Endpoint**: `GET /accounts`

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Accounts retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Compte Courant",
      "type": "CHECKING",
      "balance": 1000.00,
      "currency": "EUR",
      "active": true
    }
  ]
}
```

### Obtenir un compte par ID

**Endpoint**: `GET /accounts/{id}`

### Comptes actifs uniquement

**Endpoint**: `GET /accounts/active`

### Solde total

**Endpoint**: `GET /accounts/total-balance`

**Response**:
```json
{
  "success": true,
  "message": "Total balance calculated successfully",
  "data": 5432.50
}
```

### Modifier un compte

**Endpoint**: `PUT /accounts/{id}`

### Supprimer un compte

**Endpoint**: `DELETE /accounts/{id}`

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Account deleted successfully",
  "data": null
}
```

### Activer/Désactiver un compte

**Endpoint**: `PATCH /accounts/{id}/toggle-status`

---

## Categories (Catégories)

### Créer une catégorie

**Endpoint**: `POST /categories`

**Request Body**:
```json
{
  "name": "Alimentation",
  "description": "Dépenses alimentaires",
  "type": "EXPENSE",
  "icon": "🍔",
  "color": "#FF5733",
  "parentId": null
}
```

**Types**:
- `INCOME`: Revenu
- `EXPENSE`: Dépense

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Category created successfully",
  "data": {
    "id": 1,
    "name": "Alimentation",
    "description": "Dépenses alimentaires",
    "type": "EXPENSE",
    "icon": "🍔",
    "color": "#FF5733",
    "parentId": null,
    "subCategories": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### Catégories hiérarchiques

Pour créer une sous-catégorie, spécifier `parentId`:

```json
{
  "name": "Restaurant",
  "type": "EXPENSE",
  "parentId": 1
}
```

### Lister toutes les catégories

**Endpoint**: `GET /categories`

### Catégories racines (avec sous-catégories)

**Endpoint**: `GET /categories/root`

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Alimentation",
      "type": "EXPENSE",
      "subCategories": [
        {
          "id": 2,
          "name": "Restaurant",
          "parentId": 1
        }
      ]
    }
  ]
}
```

### Catégories par type

**Endpoint**: `GET /categories/type/{type}`

Exemples:
- `GET /categories/type/INCOME`
- `GET /categories/type/EXPENSE`

---

## Transactions

### Créer une transaction

**Endpoint**: `POST /transactions`

**Request Body**:
```json
{
  "amount": 50.00,
  "type": "EXPENSE",
  "transactionDate": "2024-01-15",
  "description": "Courses alimentaires",
  "payee": "Supermarché Leclerc",
  "reference": "CB1234",
  "notes": "Courses de la semaine",
  "accountId": 1,
  "categoryId": 1,
  "reconciled": false
}
```

**Types de transaction**:
- `INCOME`: Revenu
- `EXPENSE`: Dépense
- `TRANSFER`: Transfert entre comptes

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Transaction created successfully",
  "data": {
    "id": 1,
    "amount": 50.00,
    "type": "EXPENSE",
    "transactionDate": "2024-01-15",
    "description": "Courses alimentaires",
    "payee": "Supermarché Leclerc",
    "account": {
      "id": 1,
      "name": "Compte Courant",
      "balance": 950.00
    },
    "category": {
      "id": 1,
      "name": "Alimentation"
    },
    "reconciled": false,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### Transfert entre comptes

Pour un transfert, utiliser `type: "TRANSFER"` et spécifier `transferAccountId`:

```json
{
  "amount": 200.00,
  "type": "TRANSFER",
  "transactionDate": "2024-01-15",
  "description": "Transfert vers épargne",
  "accountId": 1,
  "transferAccountId": 2,
  "categoryId": 1
}
```

### Lister les transactions (paginé)

**Endpoint**: `GET /transactions?page=0&size=20&sort=transactionDate,desc`

**Query Parameters**:
- `page`: Numéro de page (0-based)
- `size`: Taille de la page
- `sort`: Critère de tri (ex: `transactionDate,desc`)

**Response**:
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 150,
    "totalPages": 8
  }
}
```

### Transactions par compte

**Endpoint**: `GET /transactions/account/{accountId}?page=0&size=20`

### Transactions par période

**Endpoint**: `GET /transactions/date-range?startDate=2024-01-01&endDate=2024-01-31`

**Query Parameters**:
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)

### Recherche de transactions

**Endpoint**: `GET /transactions/search?keyword=supermarché&page=0&size=20`

Recherche dans les champs `description` et `payee`.

---

## Budgets

### Créer un budget

**Endpoint**: `POST /budgets`

**Request Body**:
```json
{
  "name": "Budget Alimentation Janvier",
  "amount": 400.00,
  "period": "MONTHLY",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "description": "Budget mensuel alimentation",
  "categoryId": 1,
  "alertThreshold": 80.00
}
```

**Périodes**:
- `WEEKLY`: Hebdomadaire
- `MONTHLY`: Mensuel
- `QUARTERLY`: Trimestriel
- `YEARLY`: Annuel
- `CUSTOM`: Personnalisé

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Budget created successfully",
  "data": {
    "id": 1,
    "name": "Budget Alimentation Janvier",
    "amount": 400.00,
    "spent": 125.50,
    "remaining": 274.50,
    "percentageUsed": 31.38,
    "period": "MONTHLY",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "category": {
      "id": 1,
      "name": "Alimentation"
    },
    "active": true,
    "alertThreshold": 80.00,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### Budgets actifs

**Endpoint**: `GET /budgets/active`

### Budgets en cours

**Endpoint**: `GET /budgets/current`

Retourne les budgets dont la période inclut la date actuelle.

### Suivi du budget

Le montant `spent` est calculé automatiquement en fonction des transactions de la catégorie durant la période du budget.

---

## Gestion des erreurs

### Format des erreurs

Toutes les erreurs suivent ce format:

```json
{
  "success": false,
  "message": "Resource not found",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Codes HTTP

- `200 OK`: Succès
- `201 Created`: Ressource créée
- `400 Bad Request`: Erreur de validation
- `401 Unauthorized`: Non authentifié
- `403 Forbidden`: Accès refusé
- `404 Not Found`: Ressource non trouvée
- `500 Internal Server Error`: Erreur serveur

### Erreurs de validation

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid"
  }
}
```

---

## Pagination

Les endpoints retournant des listes utilisent la pagination Spring Data.

**Paramètres**:
- `page`: Numéro de page (0-based)
- `size`: Nombre d'éléments par page
- `sort`: Critère de tri (format: `field,direction`)

**Exemple**:
```
GET /transactions?page=0&size=10&sort=transactionDate,desc&sort=amount,asc
```

---

## Filtrage et recherche

### Recherche de transactions
```
GET /transactions/search?keyword=restaurant
```

### Filtrage par type
```
GET /categories/type/EXPENSE
```

### Filtrage par période
```
GET /transactions/date-range?startDate=2024-01-01&endDate=2024-01-31
```

---

## Bonnes pratiques

1. **Toujours inclure le token JWT** dans l'en-tête Authorization
2. **Valider les dates** au format ISO (YYYY-MM-DD)
3. **Utiliser la pagination** pour les grandes listes
4. **Gérer les erreurs** côté client
5. **Stocker le token de manière sécurisée** (pas en localStorage pour production)

---

## Exemples complets avec cURL

### Workflow complet

```bash
# 1. Inscription
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@test.com","password":"test123"}' \
  | jq -r '.data.token')

# 2. Créer un compte
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Compte Courant","type":"CHECKING","balance":1000,"currency":"EUR"}'

# 3. Créer une catégorie
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alimentation","type":"EXPENSE"}'

# 4. Créer une transaction
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount":50,
    "type":"EXPENSE",
    "transactionDate":"2024-01-15",
    "description":"Courses",
    "accountId":1,
    "categoryId":1
  }'

# 5. Créer un budget
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Budget Janvier",
    "amount":400,
    "period":"MONTHLY",
    "startDate":"2024-01-01",
    "endDate":"2024-01-31",
    "categoryId":1
  }'
```

---

## Limites et considérations

- **Taille des pages**: Max 100 éléments
- **Token JWT**: Expire après 24h par défaut
- **Caractères spéciaux**: UTF-8 supporté
- **Montants**: Précision de 2 décimales
- **Dates**: Format ISO 8601

---

Pour plus d'informations, consultez la documentation Swagger interactive à l'adresse:
**http://localhost:8080/swagger-ui.html**
