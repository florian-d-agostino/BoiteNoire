# Note de cadrage : Choix d'architecture de stockage des logs

## PROBLEMATIQUE

Un besoin de stocker un nombre massif de logs ( connexions, appels API …)
Récupération de log ciblés

## CHOIX DE TECHNOLOGIE

### SQL :
Permet d'avoir des tables relationnelles
Recherche verticale et par relation
Structure et meilleure maintenance

### NoSQL :
Recherche horizontale
Scalabilité horizontale
Meilleure gestion de données massives
Modularité de fichier

## CONCLUSION

Le NOSQL est bien plus adapté pour le projet car : Les données sont massives et différentes, une lecture / écriture plus rapide des documents horizontale, un stockage en JSON ou BSON ( donc plus facile à traiter ) et une flexibilité d'ajout de champs et de documents.
