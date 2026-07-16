<img width="1171" height="335" alt="Image" src="https://github.com/user-attachments/assets/521eed34-1e4e-44c7-ab6b-764670021d3b" />
# Cloudflare R2 Test API

Proyecto base para aprender a interactuar con almacenamiento de objetos (Cloudflare R2 y AWS S3) desde una API REST construida con **Java 17** y **Spring Boot**, utilizando el **AWS SDK v2**.

Al ser R2 compatible con la API de S3, este mismo código funciona indistintamente contra un bucket de Cloudflare R2 o un bucket real de AWS S3, cambiando únicamente configuración (sin tocar el código de negocio).

## ✨ Características

- Subida de archivos (`multipart/form-data`)
- Descarga de archivos con `Content-Type` correcto (previsualización de imágenes, PDFs, etc.)
- Listado de archivos del bucket
- Eliminación de archivos
- Configuración por variables de entorno (`.env`)
- Compatible con Cloudflare R2 y AWS S3 usando el mismo cliente

## 🛠️ Stack técnico

- Java 17
- Spring Boot
- AWS SDK for Java v2 (`software.amazon.awssdk:s3`)
- Maven

## 📋 Prerrequisitos

- JDK 17+
- Una cuenta de Cloudflare con un bucket R2 creado, o un bucket de AWS S3
- Un token de acceso (Access Key / Secret Key) con permisos de lectura/escritura sobre el bucket

## ⚙️ Configuración

Crea un archivo `.env` en la raíz del proyecto (mismo nivel que `pom.xml`):

```env
STORAGE_ACCESS_KEY_ID=tu_access_key
STORAGE_SECRET_KEY=tu_secret_key
STORAGE_ENDPOINT=https://<ACCOUNT_ID>.r2.cloudflarestorage.com
STORAGE_BUCKET_NAME=nombre-de-tu-bucket
STORAGE_REGION=nombre-de-la-region
```

### Usar con AWS S3 en vez de R2

Para apuntar a un bucket real de S3, deja `STORAGE_ENDPOINT` y  `STORAGE_REGION` vacíos. El SDK detecta automáticamente el endpoint correcto de AWS cuando no se define un `endpointOverride`.

## ▶️ Ejecución

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## 📡 Endpoints

| Método | Endpoint                     | Descripción                          |
|--------|-------------------------------|---------------------------------------|
| POST   | `/storage/upload`          | Sube un archivo (`file` como form-data) |
| GET    | `/storage/download/{key}`  | Descarga/previsualiza un archivo      |
| GET    | `/storage/list`            | Lista los archivos del bucket         |
| DELETE | `/storage/{key}`           | Elimina un archivo por su key         |

## 📌 Notas

- R2 no soporta todas las funcionalidades avanzadas de S3 (versionado, Object Lock, replicación entre regiones), pero las operaciones CRUD básicas son 100% compatibles.
- El `Content-Type` se guarda al momento de subir el archivo y se reutiliza en la descarga para permitir previsualización en el navegador o en Postman.

## 📄 Licencia

Proyecto de práctica con fines de aprendizaje.

## 👨‍💻 Autor

[![GitHub](https://img.shields.io/badge/GitHub-LuisOrihuel08-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/LuisOrihuel08)

**FullStack Developer** · 2026
