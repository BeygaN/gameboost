# GamePulse – Android Studio (Java)

Aplicación educativa para consultar información de juegos Android y registrar pruebas personales de rendimiento.

## Funciones
- Catálogo de ejemplo con cuatro juegos.
- Búsqueda por nombre y filtros por categoría.
- Ficha de cada juego con descripción, exigencia estimada y recomendaciones.
- Registro manual de FPS promedio y temperatura.
- Cálculo orientativo de una puntuación de rendimiento de 0 a 100.
- Historial local de las últimas 10 pruebas usando `SharedPreferences`.

## Abrir en Android Studio
1. Descomprime `GamePulse.zip`.
2. Abre Android Studio y selecciona **Open**.
3. Selecciona la carpeta `GamePulse` (la que contiene `settings.gradle`).
4. Espera a que Gradle sincronice el proyecto.
5. Ejecuta la app en un emulador o dispositivo Android.

## Requisitos del proyecto
- Lenguaje: Java.
- Interfaz: XML + vistas Android nativas creadas desde Java.
- `minSdk`: 23 (Android 6.0).
- `compileSdk`: 35.
- Android Gradle Plugin: 8.7.3.

## Personalizar el catálogo
En `app/src/main/java/com/example/gamepulse/MainActivity.java`, busca el método `loadGames()` y agrega o modifica los objetos `Game`.

## Sobre los datos de rendimiento
La app no puede leer de forma universal los FPS ni la temperatura interna de otros juegos mientras se ejecutan. Por eso, el usuario introduce los valores observados y GamePulse calcula una puntuación orientativa. Los perfiles y FPS del catálogo son referencias ilustrativas, no mediciones ni requisitos oficiales.
