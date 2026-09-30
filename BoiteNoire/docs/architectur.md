# STRUCTURE

## EVENTS x 5
* Login (Identifiants)
* Request (Requête API)
* Notification (Notification)
* Payment (Paiement d'abonnement)
* Error (Erreur)

## MODEL

* `_id`
* `userId` (id)
* `eventType` (Login / API_request / Notification / Subscription_Payment / Error)
* `timestamp` (date et heure)
* `other` :
  * `endpoint` (URL)
  * `method` (GET / POST / PUT / DELETE)
  * `statusCode` (200 / 400...)
  * `timeResponse` (en ms)
  * `client_ip` (ip)


---
## JUSTIFICATION DES CHOIX 

Donc les champs `_id`, `eventType` et `timestamp` permettent de filtrer et de regrouper les events.

La section `other` permet d'avoir des requêtes imbriquées (embedding).

`userId` permet de faire du référencement (referencing).

Pigeon a énormément de logs, il est impossible de recopier l'ensemble des données utilisateur à chaque fois : il est donc essentiel d'avoir un ID qui référence ces informations.