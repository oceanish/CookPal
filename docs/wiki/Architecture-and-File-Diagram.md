# Архитектура и схема файлов

## Архитектура

Приложение построено по архитектуре **MVVM** (Model-View-ViewModel) с ручным внедрением зависимостей.

```
┌─────────────────────────────────────────────────────┐
│  View (Jetpack Compose)                              │
│  ┌───────────┐ ┌──────────┐ ┌──────────────────┐   │
│  │ Products  │ │ Search   │ │ Detail           │   │
│  │ Screen    │ │ Screen   │ │ Screen           │   │
│  └─────┬─────┘ └────┬─────┘ └──────┬───────────┘   │
│        │             │              │               │
│  ┌─────▼─────────────▼──────────────▼───────────┐   │
│  │          ViewModel (StateFlow)               │   │
│  │  ProductsVM  SearchVM  DetailVM  FavoritesVM │   │
│  └─────────────────────┬────────────────────────┘   │
├────────────────────────┼────────────────────────────┤
│  Model                 │                            │
│  ┌─────────────────────▼────────────────────────┐   │
│  │            RecipeRepository                  │   │
│  └────┬─────────────────────────────┬───────────┘   │
│       │                             │               │
│  ┌────▼──────────┐          ┌───────▼──────────┐   │
│  │  Room (Local) │          │ Retrofit (Remote)│   │
│  │  · ProductDao │          │ SpoonacularApi   │   │
│  │  · FavoriteDao│          │ Moshi            │   │
│  │  · ShoppingDao│          │ OkHttp           │   │
│  └───────┬───────┘          └───────┬──────────┘   │
│          │                         │               │
│  ┌───────▼─────────────────────────▼───────────┐   │
│  │              AppContainer (DI)              │   │
│  └─────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

## Структура файлов

```
CookPal/
├── build.gradle.kts                        # Корневой build файл
├── settings.gradle.kts                     # Настройки модулей
├── gradle.properties                       # Свойства Gradle
├── local.properties                        # API-ключ (не коммитится)
├── .gitignore
├── README.md
│
├── gradle/wrapper/
│   └── gradle-wrapper.properties
│
├── .github/workflows/
│   └── android_ci.yml                      # CI/CD pipeline
│
├── docs/wiki/
│   ├── Home.md
│   ├── Functional-Requirements.md
│   ├── Architecture-and-File-Diagram.md
│   ├── Database-Schema.md
│   └── Presentation.md
│
├── app/                                    # Модуль приложения
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/cookpal/
│       │   │   ├── CookPalApp.kt          # Application
│       │   │   ├── MainActivity.kt         # Точка входа + BottomNav
│       │   │   ├── di/
│       │   │   │   └── AppContainer.kt     # DI контейнер
│       │   │   ├── navigation/
│       │   │   │   ├── BottomNavItem.kt    # Элементы навигации
│       │   │   │   └── NavGraph.kt         # Граф маршрутов
│       │   │   └── ui/
│       │   │       ├── theme/
│       │   │       │   ├── Color.kt
│       │   │       │   ├── Theme.kt
│       │   │       │   └── Type.kt
│       │   │       ├── products/
│       │   │       │   ├── ProductsScreen.kt
│       │   │       │   └── ProductsViewModel.kt
│       │   │       ├── search/
│       │   │       │   ├── SearchScreen.kt
│       │   │       │   └── SearchViewModel.kt
│       │   │       ├── detail/
│       │   │       │   ├── DetailScreen.kt
│       │   │       │   └── DetailViewModel.kt
│       │   │       └── favorites/
│       │   │           ├── FavoritesAndShoppingScreen.kt
│       │   │           └── FavoritesViewModel.kt
│       │   └── res/
│       │       ├── values/
│       │       │   ├── strings.xml
│       │       │   ├── colors.xml
│       │       │   └── themes.xml
│       │       └── drawable/
│       ├── test/                           # Unit-тесты
│       └── androidTest/                    # UI-тесты
│
└── data/                                   # Модуль данных
    ├── build.gradle.kts
    └── src/main/java/com/cookpal/data/
        ├── local/
        │   ├── CookPalDatabase.kt          # Room БД
        │   ├── entity/
        │   │   ├── ProductEntity.kt
        │   │   ├── FavoriteRecipeEntity.kt
        │   │   └── ShoppingItemEntity.kt
        │   └── dao/
        │       ├── ProductDao.kt
        │       ├── FavoriteRecipeDao.kt
        │       └── ShoppingItemDao.kt
        ├── remote/
        │   ├── SpoonacularApi.kt           # Retrofit интерфейс
        │   ├── RecipeSearchResponse.kt     # DTO для поиска
        │   └── RecipeInfoResponse.kt       # DTO для информации
        └── repository/
            └── RecipeRepository.kt         # Единый репозиторий
```
