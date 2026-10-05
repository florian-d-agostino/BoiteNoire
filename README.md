# Boîte Noire - Pigeon Analytics Service

(c'est bien)

Service d'ingestion et d'analyse d'événements pour la plateforme de messagerie **Pigeon**. Construit avec **Spring Boot** et **MongoDB**, exploitant les pipelines d'agrégation natifs de MongoDB.

---

## 📋 Prérequis

- **Java 21**
- **Maven** (ou wrapper `./mvnw` inclus)
- **MongoDB 6+** (accessible localement sur `mongodb://localhost:27017/boitenoire` ou configuré via `application.properties`)

---

## 🚀 Démarrage rapide

### 1. Cloner le projet et se placer dans le sous-dossier

```bash
cd BoiteNoire
```

### 2. Démarrer MongoDB

Si vous utilisez Docker :
```bash
docker run -d --name pigeon-mongo -p 27017:27017 mongo:latest
```

### 3. Exécuter le générateur de logs (100 000 événements)

Le générateur est exécutable en une seule commande via Maven :
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--generate"
```
*(Sur Windows PowerShell : `.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--generate"`)*

### 4. Lancer le service Spring Boot

```bash
./mvnw spring-boot:run
```
*(Sur Windows PowerShell : `.\mvnw.cmd spring-boot:run`)*

L'application démarre par défaut sur le port `8080`.

---

## 📖 Documentation API (Swagger / OpenAPI)

Une fois l'application démarrée, l'interface Swagger UI interactive est accessible à l'adresse :
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Endpoints disponibles (`/api/analytics`) :

| Méthode | Route | Description | Paramètres |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/analytics/top-users` | Top 10 des utilisateurs les plus actifs | `startDate`, `endDate` (ISO-8601) |
| `GET` | `/api/analytics/errors/distribution` | Répartition des erreurs par type et par jour | `startDate`, `endDate` (ISO-8601) |
| `GET` | `/api/analytics/endpoints/response-times` | Temps de réponse moyen et percentile 95 par endpoint | - |
| `GET` | `/api/analytics/funnel` | Entonnoir de conversion séquentiel (LOGIN → REQUEST → PAYMENT) | - |

---

## 📂 Structure du projet

```text
LaBoiteNoire/
├── BoiteNoire/
│   ├── src/
│   │   ├── main/java/laplateforme/pigeon/boitenoire/
│   │   │   ├── controllers/      # AnalyticsController (endpoints REST + Swagger)
│   │   │   ├── dto/              # Objets de transfert de données d'analyse
│   │   │   ├── generator/        # Générateur de 100k événements
│   │   │   ├── models/           # Modèle documentaire LogEvent et énumérations
│   │   │   ├── repositories/     # LogRepository (MongoRepository)
│   │   │   └── services/         # AnalyticsService (Pipelines d'agrégation MongoDB)
│   │   └── resources/
│   │       └── application.properties
│   └── docs/
│       ├── database_choice.md    # ADR : Justification relationnel vs documentaire
│       ├── architectur.md        # Schéma documentaire & choix embedding/referencing
│       └── measurements.md       # Mesures de performance explain() avant/après index
└── README.md
```

---

## 📑 Livrables et documentation

- **Note de décision (ADR)** : [`BoiteNoire/docs/database_choice.md`](BoiteNoire/docs/database_choice.md)
- **Modèle documentaire** : [`BoiteNoire/docs/architectur.md`](BoiteNoire/docs/architectur.md)
- **Dossier de mesures & optimisation d'index** : [`BoiteNoire/docs/measurements.md`](BoiteNoire/docs/measurements.md)