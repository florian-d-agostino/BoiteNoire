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
<<<<<<< HEAD
## JUSTIFICATION DES CHOIX
=======
## JUSTIFICATION DES CHOIX 
>>>>>>> b644eda25a254241ed25dd6e70488f007b04c9a2

Donc les champs `_id`, `eventType` et `timestamp` permettent de filtrer et de regrouper les events.

La section `other` permet d'avoir des requêtes imbriquées (embedding).

`userId` permet de faire du référencement (referencing).

Pigeon a énormément de logs, il est impossible de recopier l'ensemble des données utilisateur à chaque fois : il est donc essentiel d'avoir un ID qui référence ces informations.