# WatchList — Claude Instructions

## Project
Android app (Kotlin + Jetpack Compose) for tracking movies/TV series to watch, with streaming service availability lookup via TMDB.

## Build & Run
- Open in Android Studio
- Add TMDB Read Access Token to `local.properties`: `TMDB_READ_ACCESS_TOKEN=eyJ...`
  - Use the **API Read Access Token** (long JWT starting with `eyJ`), NOT the short v3 API Key
  - Get it at https://www.themoviedb.org/settings/api
- Sync Gradle → Run

## Architecture
MVVM + Clean Architecture, single-module Android app.

```
app/src/main/kotlin/com/watchlist/
├── data/
│   ├── db/          # Room entities, DAOs, AppDatabase
│   ├── model/       # Domain models
│   ├── preferences/ # RegionDataStore, ApiKeyProvider
│   ├── remote/      # TmdbApi, TmdbAuthInterceptor, DTOs
│   └── repository/  # WatchlistRepository, StreamingRepository, SearchRepository
├── di/              # Hilt modules (DatabaseModule, NetworkModule, DataStoreModule)
├── ui/
│   ├── component/   # Reusable Composables
│   ├── navigation/  # Screen sealed class, AppNavHost
│   ├── screen/      # watchlist/, search/, services/, settings/
│   └── theme/       # Material3 theme
└── WatchListApp.kt  # @HiltAndroidApp, syncs API key from DataStore to ApiKeyProvider
```

## Key Versions
- Kotlin 2.1.0 / KSP 2.1.0-1.0.29 / AGP 8.7.3 / Gradle 8.11.1
- Hilt 2.53.1 (must be ≥2.53 for Kotlin 2.1 metadata support)
- Compose BOM 2025.01.01 / Room 2.6.1

## Key Conventions
- All API auth uses Bearer token (JWT); managed via `TmdbAuthInterceptor` + `ApiKeyProvider`
- Only `flatrate` (subscription) providers are shown — no rent/buy
- `results[countryCode]?.flatrate` — watch providers keyed by ISO country code
- 150ms delay between watch-provider requests in `StreamingRepository.refreshAll()`
- Search debounce: 400ms in `SearchViewModel`
- Movie titles come from TMDB `title` field; TV from `name` field — both nullable, filtered in `SearchRepository`
- `buildConfig = true` required in `app/build.gradle.kts` for `BuildConfig.TMDB_READ_ACCESS_TOKEN`
- PRAGMA `foreign_keys=ON` enabled via `RoomDatabase.Callback` in `DatabaseModule`

## Do Not
- Do not add rent/buy streaming options — subscription only
- Do not store the API key in source code — it goes in `local.properties` or app Settings
- Do not use the TMDB v3 API Key (short string) — only the Read Access Token (JWT) works with Bearer auth
