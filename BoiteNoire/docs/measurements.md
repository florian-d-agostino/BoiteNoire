# Mesures et optimisation de performance

## 1. Requête choisie

Pour l'optimisation, on a pris la requête du **Top 10 des utilisateurs les plus actifs sur une période** (ici sur un mois), car c'est celle qui demande le plus de ressources avec le filtre par date, le regroupement et le tri :

```javascript
db.logs.explain("executionStats").aggregate([
  { 
    $match: { 
      timestamp: { 
        $gte: ISODate("2026-03-01T00:00:00Z"), 
        $lte: ISODate("2026-03-31T23:59:59Z") 
      } 
    } 
  },
  { 
    $group: { 
      _id: "$userId", 
      totalEvents: { $sum: 1 } 
    } 
  },
  { 
    $sort: { totalEvents: -1 } 
  },
  { 
    $limit: 10 
  }
])
```

---

## 2. Mesure avant index

Sans index, Mongo fait un scan complet de la collection (`COLLSCAN`).

- **Documents examinés** : 100 000
- **Documents retournés** : 10
- **Temps d'exécution** : 148 ms

Mongo est obligé de charger et parcourir les 100 000 documents en mémoire juste pour en filtrer une partie sur un mois.

---

## 3. Création et justification de l'index

Commande exécutée :
```javascript
db.logs.createIndex({ "timestamp": 1, "userId": 1 })
```

**Justification de l'ordre des champs :**
- On a mis `timestamp` en premier pour filtrer directement la plage de dates (`Range`). Ça permet à Mongo d'éliminer tout de suite les logs hors période sans les lire sur le disque.
- On a ajouté `userId` en second pour que le regroupement (`$group`) se fasse directement à partir de l'index, sans avoir besoin d'ouvrir tous les sous-documents.

---

## 4. Mesure après index

Avec l'index `{ timestamp: 1, userId: 1 }`, Mongo passe par un `IXSCAN`.

- **Documents examinés** : 8 245
- **Documents retournés** : 10
- **Temps d'exécution** : 8 ms

---

## 5. Constat

1. Sans index, la base parcourt inutilement l'ensemble des 100 000 documents via un COLLSCAN très lourd.
2. L'index composé permet de cibler directement les ~8 200 logs de la période sans toucher au reste de la base.
3. Le temps d'exécution passe de 148 ms à 8 ms, ce qui rend la requête quasiment instantanée.