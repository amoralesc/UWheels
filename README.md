# UWheels

UWheels es una aplicación móvil para dispositivos Android que permite a usuarios conectar con otras usuarios para utilizar servicios de movilidad compartida. Permite a los usuarios comunicarse, crear y unirse a rutas compartidas y mucho más.

## Especifcaciones 📋

La aplicación está desarrollada para dispositivos Android con una versión SDK mínima de 24.

## Tech Stack 🛠️

* [Kotlin](https://kotlinlang.org/) - Lenguaje de desarrollo
* [Android Studio](https://developer.android.com/studio) - Herramienta y entorno de desarrollo
* [Firebase](https://firebase.google.com/) - Servicios de autenticación, base de datos, almacenamiento cloud
* [Google Maps Platform](https://mapsplatform.google.com/) - Servicios de localización

## Instalación 📦

### Prerequisitos 📚

Este proyecto está construido con tecnologías Firebase y de Google Maps Platform. Si desea compilar y corren el código fuente localmente, debe tener estos servicios enlazados con una cuenta de Google Cloud Platform. Si desea utilizar los servicios contratados por ABMODEL, debe solicitar acceso a estos contactando al equipo. Sin embargo, su solicitud solo será tenida en cuenta si también pertenece a la misma organización (Pontificia Universidad Javeriana) o afines.

1. Contar con Android Studio y el plugin de Kotlin instalado.
2. Crear o contar con una cuenta de Google Cloud Platform con facturación activada.
3. Crear un nuevo proyecto en Google Cloud.
4. Habilitar las APIs de Google Maps Platform y tomar nota de la clave API.
5. Crear un nuevo proyecto de Firebase a partir del proyecto Google Cloud del paso 4.

### Pasos de instalación 📚

1. Cree un nuevo [proyecto en Google Cloud](https://cloud.google.com/resource-manager/docs/creating-managing-projects) y habilite la facturación.

2. Habilite las APIs de Google Maps Platform y tome nota de la clave API.

3. Cree un nuevo [proyecto de Firebase enlazado al de GCP](https://firebase.google.com/firebase-and-gcp).

4. Clone el repositorio y entre al proyecto de Android Studio.

```
git clone https://github.com/IntroCompuMovil202210J/UWheels.git
```

5. Coloque su Google Maps API en el archivo local.properties bajo el nombre '''MAPS_API_KEY'''.

```
MAPS_API_KEY=[your_api_key]
```

6. Conecte su proyecto de Android Studio a su proyecto de Firebase. Se recomienda utilizar el asistente de Firebase ubicado en Tools > Firebase.

## Wiki 📖

Para más información sobre el proyecto, refiérase a la [wiki](https://github.com/IntroCompuMovil202210J/UWheels/wiki).

## Equipo ✒️

_El equipo de ABMODEL pertenece a la Pontificia Universidad Javeriana y está conformado por:_

* **María Camila Aguirre Collante** - [CamilaAguirreCollante](https://github.com/CamilaAguirreCollante)
* **Johanna Lisette Bolívar Calderón** - [Johabc](https://github.com/Johabc)
* **Fabio Alejandro Camargo Díaz** - [alejo2300](https://github.com/alejo2300)
* **Alejandro Morales Contreras** - [amoralesc](https://github.com/amoralesc)
* **Jessica Tatiana Naizaque Guevara** - [JessicaNaizaque](https://github.com/JessicaNaizaque)
* **David Santiago Suárez Barragán** - [sdsuarez](https://github.com/sdsuarez)

### Licencia 📝

Este proyecto está bajo licencia Apache 2.0. Una copia de la licencia es adjuntada. El uso de la marca, el logo y el nombre de la aplicación (UWheels) con fines comerciales está explícitamente prohibido sin previo consentimiento.

---

⌨️ con ❤️ por [ABMODEL](https://github.com/orgs/IntroCompuMovil202210J/teams/abmodel) 😊
