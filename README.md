# Simple VSRG Android

Un juego VSRG simple de 4 teclas para Android.

## Características
- 4 teclas (lanes)
- Nivel de prueba de ~30 segundos
- Sistema de puntuación básico
- Detección de timing (Perfect, Good, Miss)

## Cómo abrir en Android Studio

1. Clona este repositorio:
```bash
git clone https://github.com/MiguelV467/simple-vsrg-android.git
```

2. Abre Android Studio

3. Selecciona "Open an Existing Project"

4. Navega a la carpeta del proyecto clonado y selecciónala

5. Espera a que Gradle sincronice las dependencias

6. Conecta tu dispositivo Android o inicia un emulador

7. Presiona el botón "Run" (▶️) o usa Shift + F10

## Requisitos
- Android Studio Arctic Fox o superior
- Android SDK 24 o superior
- Dispositivo Android o emulador

## Cómo jugar
- El juego inicia automáticamente
- Las notas caen desde arriba
- Toca las teclas en la parte inferior cuando las notas lleguen a la línea de timing
- Obtén puntos por tocar las notas con buen timing

## Estructura del proyecto
- `MainActivity.java` - Activity principal
- `GameView.java` - Vista del juego con lógica de renderizado y input
- `Note.java` - Clase para las notas del juego

## Próximas mejoras
- [ ] Cargar mapas personalizados
- [ ] Más niveles
- [ ] Efectos de sonido
- [ ] Sistema de combo
- [ ] Menú principal
