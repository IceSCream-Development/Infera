# Infera

Infera es una aplicación Android educativa desarrollada en Kotlin con Jetpack Compose. Su objetivo
es brindar un espacio interactivo de aprendizaje, con funcionalidades como registro de usuarios,
autenticación, chatbot, lecciones, logros y perfil personalizado.

## Características principales

- Pantalla de bienvenida (WelcomeScreen)
- Registro y login de usuarios
- Pantalla de usuario (WelcomeUserScreen)
- Menú principal con varias secciones:
    - Aprende
    - ChatBot
    - Logros
    - Lecciones
    - Perfil

## Requisitos

- Android Studio (recomendado Arctic Fox o superior)
- Kotlin
- Jetpack Compose

## Instalación

1. Clona este repositorio:
   ```
   git clone <url-de-tu-repositorio>
   ```
2. Abre el proyecto en Android Studio.
3. Sincroniza las dependencias y ejecuta la aplicación en un emulador o dispositivo físico.

## Uso

- Al abrir la app, accede a la pantalla de bienvenida.
- Puedes registrarte o iniciar sesión.
- Una vez autenticado, accede a la pantalla principal donde podrás navegar entre las distintas
  secciones: aprender, chatbot, logros, lecciones y perfil.

## Estructura del proyecto

- `MainActivity.kt`: Punto de entrada principal de la app.
- `ui/`: Contiene los componentes y pantallas de la interfaz de usuario.
- `data/`: Lógica de autenticación (AuthRepository).

## Autor

- Equipo de desarrollo: icescream

## Licencia

Este proyecto se distribuye bajo la licencia MIT (puedes cambiar esta sección si tienes otro tipo de
licencia).