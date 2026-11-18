# Registro de Prompts y Decisiones IA — CampusGPS

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| "Laboratorio 13 — CampusGPS desde Empty Activity (API 24)" | Estructuró el plan de 10 commits en `main` y 3 en `mejora-ia`. | Se reconstruyó la base MVVM en el Paso 2 dado que se partía de Empty Activity. | Para garantizar arquitectura MVVM limpia sin dependencias faltantes. |
| "Implementa filtro por categoría para los lugares del campus" | Sugirió agregar `FilterChip` de Material 3 y filtrar en el ViewModel. | Se combinó `_lugares` y `_categoriaFiltro` usando `combine` en `MapaViewModel`. | Evita duplicar lógica de filtrado en Composables y mantiene la reactividad de Compose. |
| "Agrega radio de cercanía de 100m, 200m y 500m con un círculo en el mapa" | Propuso dibujar un `Circle` de Maps Compose centrado en la posición GPS. | Se guardó el estado del radio en `MapaViewModel` en lugar del Composable. | Mantiene el principio de responsabilidad única de MVVM. |
