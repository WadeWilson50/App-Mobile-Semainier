# Mon Semainier Gourmand (Android)

Application Android native (Kotlin & Jetpack Compose, Material 3) de planification de menus hebdomadaires familiaux et de liste de courses intelligente, optimisée pour **Cookeo**, **Thermomix TM6**, **Airfryer** et **Poêle & Casserole**.

## Fonctionnalités principales

- **Planning hebdomadaire intelligent (Lundi – Dimanche)** :
  - 3 personnes par défaut en semaine, 4 personnes le week-end, ajustable par créneau (1 à 12 convives).
  - Créneaux libres par défaut (Lundi, Mardi et Jeudi midi) activables ou désactivables à tout moment.
  - Génération automatique respectant le moment de la journée (`Midi` / `Soir`), la variété des appareils, les recettes favorites et la limite d'un plat de poisson par semaine.
  - Déplacement et échange rapide de repas entre créneaux, ou régénération individuelle.
- **Livre de 24 recettes & ajout personnalisé** :
  - Catalogue complet préchargé (6 recettes par appareil) avec temps, réglages de cuisson, quantités par personne et gestion des favoris.
  - Recherche par nom ou ingrédient, filtres par appareil, style (*Traditionnel*, *Convivial*, *Express ≤ 15 min*) et favoris.
  - Création et suppression de recettes personnalisées persistées localement dans Room.
- **Liste de courses agrégée par rayons** :
  - Calcul automatique des quantités selon les repas actifs et le nombre de convives.
  - Regroupement par 5 rayons (*Fruits & Légumes*, *Boucherie & Poissonnerie*, *Crèmerie & Œufs*, *Boulangerie*, *Épicerie*), ajout d'articles libres, coche/décoche, copie et partage natif Android.
- **Transfert multi-appareils** :
  - Encodage et décodage compatibles avec le format `MENU-...` (partage système Android, presse-papiers) et sauvegarde/restauration `.json`.
