# PhotoGallery - con busqueda y favoritos
App Android nativa en Kotlin que muestra las fotos mas recientes de Unsplash, permite buscar por categorias, guardar favoritos y administrar el contenido cacheado para navegar sin conexion parcial.

🎥 **Video Demo del proyecto**

[![Ver Video demo en Google Drive](https://img.shields.io/badge/▶️%20Ver%20demo%20en%20Drive-blue?style=for-the-badge)](https://drive.google.com/drive/folders/1o9NpR97X2GL4d0OWrUxk8jY047hl3QJd?usp=sharing)

## De que trata la app
Photo actua como un catalogo minimalista inspirado en el cliente oficial de Unsplash:
- Feed inicial con las fotos mas recientes y soporte para pull-to-refresh.
- Barra de busqueda con chips para categorias rapidas (naturaleza, ciudades, animales, tecnologia).
- Detalle enriquecido (autor, likes, resolucion) con carga de imagenes via Coil.
- Marcado/desmarcado de favoritos desde el feed o el detalle, con lista dedicada.
- Pantalla de ajustes para ver el tamano del cache, fecha de ultima sincronizacion y vaciar datos locales.
- Fallback automatico al cache local cuando la red falla, evitando dejar la UI vacia.

## Stack tecnico y arquitectura
- **Lenguaje:** Kotlin + Coroutines (Dispatchers.IO, viewModelScope).
- **UI / Jetpack:** ViewModel, LiveData, Navigation Component, ViewBinding, Material Components.
- **Networking:** Retrofit + Moshi + OkHttp Logging.
- **Persistencia:** Room (PhotoDao, PhotoEntity, FavoritePhotoEntity) y `CacheTracker` (SharedPreferences) para saber cuando se refresco.
- **Imagenes:** Coil 2 para cargar las urls en `ImageView`.
- **Arquitectura:** MVVM con `PhotoRepository` como capa de acceso a datos y `ServiceLocator` para instanciar Room + Retrofit sin frameworks de DI.
- **Estados:** `UiState` (Loading/Success/Empty/Error) y `Event` para manejar Snackbars de una sola vez.

```
app/src/main/java/com/example/photo
|- core/          -> CacheTracker, UiState, ServiceLocator, Event
|- data/
|  |- local/      -> Room DB, DAO y entidades para feed/favoritos
|  |- remote/     -> UnsplashApi + DTOs Moshi
|  |- repository/ -> PhotoRepository + DefaultPhotoRepository + mapeadores
|- domain/model/  -> Photo (parcelable) y CacheStatus
|- ui/
   |- home/       -> Feed + busqueda + chips de categorias
   |- detail/     -> Vista de detalle con favorito
   |- favorites/  -> Lista persistida de favoritos
   |- settings/   -> Estado del cache y acciones de limpieza
```

## API utilizada
Se consume la [Unsplash API](https://unsplash.com/developers) a traves de `UnsplashApi`, agregando encabezados `Accept-Version` y `Authorization: Client-ID <ACCESS_KEY>`.  
Actualmente la clave vive como `buildConfigField("String", "UNSPLASH_ACCESS_KEY", "...")` en `app/build.gradle.kts`; te recomendamos reemplazarla con tu propia clave y, si vas a publicar, moverla a `local.properties` o variables de entorno.

## Requisitos
- Android Studio Ladybug (o superior) con Gradle 8+ y JDK 11.
- Dispositivo o emulador con Android 7.0 (API 24) o superior.
- Clave de desarrollador de Unsplash.

## Como ejecutar el proyecto
1. **Clona** el repositorio y abrelo en Android Studio.
2. **Configura la clave** Unsplash editando `app/build.gradle.kts` o agregando una clave privada y sincroniza Gradle.
3. **Sincroniza** dependencias (`Gradle Sync`) y deja que se genere Room.
4. **Ejecuta** `Run > Run 'app'` o `./gradlew installDebug` para instalar en un dispositivo.
5. Opcional: corre pruebas unitarias con `./gradlew test` y, si configuras instrumentadas, `./gradlew connectedAndroidTest`.

## Buenas practicas incluidas
- Manejo de estados vacios/errores con mensajes contextuales en el feed.
- Reutilizacion de favoritos en busquedas (el DAO marca los ids actuales para que la UI muestre el corazon correcto).
- Estrategia de cache defensiva: si la red falla durante un refresh, se devuelve el ultimo cache valido.
- Documentacion adicional en `DOCUMENTACION_APP.txt` con flujos completos de la arquitectura.

## Proximos pasos sugeridos
1. Anadir pruebas de integracion para `DefaultPhotoRepository`.
2. Mover la clave de Unsplash a secretos locales o al nuevo Android Gradle Secrets Gradle Plugin.
3. Implementar widgets o shortcuts para abrir la seccion de favoritos directamente.

Lista para compartir en GitHub o en tu portafolio. Asegurate de actualizar el enlace del video demo cuando tengas una version mas reciente.
