# MadaSearch (MadagascarBrowser) 🇲🇬

**Navigateur intelligent nouvelle génération conçu pour la productivité, la confidentialité et l'économie de bande passante.**

MadaSearch est un navigateur Windows natif développé en **C# / .NET 8** et **WPF**, propulsé par le runtime **Microsoft Edge WebView2**. Il intègre un moteur d'économie de données (DataSaver), un bloqueur de publicités et traqueurs, la gestion multi-espaces de travail, des profils privés isolés, la persistance locale SQLite et une architecture d'assistance intelligente locale.

---

## 🌟 Fonctionnalités Clés

1. **Navigation Moderne & WebView2** : Affichage haute fidélité des standards web HTML5/CSS3/JavaScript avec isolation des processus.
2. **DataSaver (Économie de données)** : Détection et blocage des contenus lourds (médias non requis, images selon option, requêtes superflues), avec calcul dynamique des octets économisés. Idéal pour les forfaits mobiles et connexions lentes.
3. **Bloqueur de Publicités & Traqueurs** : Filtrage strict basé sur des règles de domaines et liste blanche paramétrable.
4. **Espaces de Travail Intelligents** : Organisation de vos sessions par contexte (Personnel, Travail, Études, Recherche, Projets).
5. **Gestionnaire d'Onglets Robuste** : Création, duplication, réorganisation, regroupement par domaine et fermeture sécurisée.
6. **Navigation Privée** : Isolation complète du profil dans un dossier temporaire dédié, sans historique SQLite ni persistance de session.
7. **Favoris & Historique SQLite** : Base de données locale chiffrée/protégée avec migrations versionnées, requêtes paramétrées et transactions sûres.
8. **Assistant Intelligent Local** : Architecture découplée (`IAssistantService`) avec service local NoOp/Hors-ligne respectueux de la vie privée.

---

## 🏗️ Architecture du Projet

```text
MadaSearch/
├── MadaSearch.sln
├── src/
│   ├── MadaSearch.Core/        # Modèles, Interfaces, Utilitaires d'URL et Recherche
│   ├── MadaSearch.Data/        # SQLite, Migrations versionnées, Repositories
│   ├── MadaSearch.Browser/     # Gestionnaire WebView2, DataSaver, Bloqueur, Sessions
│   ├── MadaSearch.Assistant/   # Abstraction IA, Service Local sans Cloud, Mode NoOp
│   └── MadaSearch.Shell/       # Application WPF, MVVM ViewModels, Thèmes M3 Deep Blue
├── tests/
│   ├── MadaSearch.Core.Tests/
│   ├── MadaSearch.Data.Tests/
│   └── MadaSearch.Browser.Tests/
├── .github/workflows/ci.yml
└── README.md
```

---

## 🚀 Compilation & Exécution sous Windows

### Prérequis
- Windows 10/11 x64
- [.NET 8.0 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Microsoft Edge WebView2 Runtime](https://developer.microsoft.com/en-us/microsoft-edge/webview2/) (inclus par défaut sur Windows 11)

### Commandes en ligne de commande (PowerShell / CMD)

```powershell
# 1. Cloner et se placer dans le dossier MadaSearch
cd MadaSearch

# 2. Restaurer les packages NuGet
dotnet restore MadaSearch.sln

# 3. Compiler en mode Release
dotnet build MadaSearch.sln --configuration Release

# 4. Lancer la suite complète de tests unitaires et d'intégration
dotnet test tests/MadaSearch.Core.Tests/MadaSearch.Core.Tests.csproj
dotnet test tests/MadaSearch.Data.Tests/MadaSearch.Data.Tests.csproj
dotnet test tests/MadaSearch.Browser.Tests/MadaSearch.Browser.Tests.csproj

# 5. Démarrer MadaSearch
dotnet run --project src/MadaSearch.Shell/MadaSearch.Shell.csproj
```

---

## 📊 Matrice d'état des fonctionnalités

| Fonctionnalité | État | Couverture Tests |
| :--- | :---: | :---: |
| Architecture Solution .NET 8 / WPF | ✅ Terminé | CI GitHub Actions |
| Gestion des Onglets & Navigation | ✅ Terminé | `TabManagerTests` |
| Normalisation URLs & Moteurs de recherche | ✅ Terminé | `UrlValidatorTests`, `SearchEngineHelperTests` |
| Moteur DataSaver (Octets économisés) | ✅ Terminé | `DataSaverTests` |
| Bloqueur de Publicités & Traqueurs | ✅ Terminé | `ContentBlockerTests` |
| Base SQLite & Migrations versionnées | ✅ Terminé | `SqliteRepositoryTests` |
| Favoris & Historique paramétrés | ✅ Terminé | `SqliteRepositoryTests` |
| Espaces de travail & Sessions | ✅ Terminé | `SessionRepository` |
| Isolation Mode Privé | ✅ Terminé | `SessionRepository` |
| Assistant Local Sans Cloud | ✅ Terminé | `AssistantImplementations` |
| Thème Deep Blue & Electric Blue | ✅ Terminé | Intégré dans Shell WPF |

---
*Conçu avec fierté pour Madagascar et l'international.*
