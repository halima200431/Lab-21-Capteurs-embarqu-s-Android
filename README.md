# SensorMood - Lab 21 🚀

**SensorMood** est une application Android moderne conçue pour explorer et visualiser les données provenant des capteurs embarqués d'un smartphone. L'application utilise une architecture basée sur des **Fragments** et propose une interface élégante aux tons violet/rose avec des graphiques en temps réel.

## 📱 Fonctionnalités

L'application est divisée en plusieurs modules accessibles via un menu latéral (Navigation Drawer) :

- **Catalogue des capteurs** : Liste exhaustive de tous les capteurs présents sur l'appareil avec leurs caractéristiques techniques (vendeur, résolution, consommation, etc.).
- **Visualisation Graphique** : Graphiques en temps réel pour les capteurs scalaires :
    - Température ambiante 🌡️
    - Humidité relative 💧
    - Proximité 📏
    - Champ magnétique 🧲
- **Capteurs de mouvement** : Suivi des trois axes (X, Y, Z) et calcul de la norme :
    - Accéléromètre 🏃
    - Gravité 🌍
    - Gyroscope 🔄
- **Compteur de pas** : Suivi des pas lors de la session et depuis le dernier démarrage du téléphone.
- **Boussole Numérique** : Indication de l'azimut et des points cardinaux.
- **Reconnaissance d'activité** : Algorithme de classification simple capable de détecter si l'utilisateur est stable, s'il marche ou s'il saute.

## 🛠️ Spécificités Techniques

- **Langage** : Java
- **UI/UX** : Thème personnalisé "Glassmorphism", composants Material Design, et graphiques dessinés manuellement via l'API **Canvas**.
- **Gestion de l'énergie** : Les capteurs ne sont activés que lorsque le fragment correspondant est visible (`onResume`) et sont libérés dès que l'utilisateur quitte l'écran (`onPause`).
- **Mode Simulation** : Pour pallier les limites de l'émulateur Android (Goldfish/Ranchu), l'application détecte automatiquement l'environnement et génère des données simulées cohérentes pour permettre de tester l'interface et les graphiques sans appareil physique.

## 📂 Structure du Projet

```
com.example.lab21/
├── MainActivity.java         # Gestion du Drawer et de la navigation
├── fragments/                # Un fragment par type de capteur
│   ├── SensorsListFragment
│   ├── SensorGraphFragment
│   ├── MotionSensorFragment
│   ├── StepCounterFragment
│   ├── CompassFragment
│   └── ActivityRecognitionFragment
├── utils/                    # Utilitaires et logique métier
│   ├── SensorEnvironment.java # Détection émulateur et simulateur
│   ├── SensorFormatter.java   # Formatage des données techniques
│   └── UiKit.java             # Fabrique de composants UI réutilisables
└── views/
    └── LineChartView.java     # Vue personnalisée pour les graphiques
```

## 🚀 Installation

1. Cloner le projet ou copier les sources.
2. Ouvrir le dossier dans **Android Studio**.
3. Synchroniser le projet avec **Gradle**.
4. Lancer sur un appareil physique (recommandé pour les capteurs de mouvement) ou sur l'émulateur.

## 📸 Aperçu du Design

- **Couleurs principales** : `#7B2CBF` (Violet), `#9D4EDD` (Mauve), `#FF6EC7` (Rose).
- **Interface** : Cartes arrondies avec bordures légères, icônes teintées et typographie moderne.

## DEMO


https://github.com/user-attachments/assets/e46d5944-f4cc-4518-b106-e979f41eb213


**Développé dans le cadre du TP de développement mobile (Lab 21).**
