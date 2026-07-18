<img width="1171" height="335" alt="Image" src="https://github.com/user-attachments/assets/521eed34-1e4e-44c7-ab6b-764670021d3b" />

# Object Storage API

Proyecto base para aprender a interactuar con almacenamiento de objetos (Cloudflare R2 y AWS S3) desde una API REST construida con **Java 17** y **Spring Boot**, utilizando el **AWS SDK v2**.

Al ser R2 compatible con la API de S3, este mismo código funciona indistintamente contra un bucket de Cloudflare R2, un bucket real de AWS S3, o incluso MinIO en local, cambiando únicamente configuración (sin tocar el código de negocio).

## ✨ Características

- Subida de archivos (`multipart/form-data`) con validación de tamaño y contenido
- Descarga de archivos con `Content-Type` correcto (previsualización de imágenes, PDFs, etc.)
- Listado de archivos del bucket
- Eliminación de archivos (con verificación de existencia)
- Manejo centralizado de excepciones (`@RestControllerAdvice`)
- Configuración por variables de entorno (`.env`)
- Compatible con Cloudflare R2, AWS S3 y MinIO usando el mismo cliente

## 🛠️ Stack técnico

- Java 17
- Spring Boot
- AWS SDK for Java v2 (`software.amazon.awssdk:s3`)
- Maven
- Docker (opcional, para pruebas locales con MinIO)

## 📋 Prerrequisitos

- JDK 17+
- Una cuenta de Cloudflare con un bucket R2 creado, un bucket de AWS S3, o Docker para levantar MinIO local
- Un token de acceso (Access Key / Secret Key) con permisos de lectura/escritura sobre el bucket

## ⚙️ Configuración

Copia el archivo `.env.example` a `.env` en la raíz del proyecto (mismo nivel que `pom.xml`) y completa tus credenciales:

```env
STORAGE_ACCESS_KEY_ID=
STORAGE_SECRET_KEY=
STORAGE_ENDPOINT= # Para Cloudflare R2, usa el endpoint que te da Cloudflare. Para AWS S3, deja en blanco.
STORAGE_BUCKET_NAME=
STORAGE_REGION=auto # Para AWS S3, usa la región real (ej. us-east-1). Para R2/MinIO, deja "auto".
```

### Usar con AWS S3 en vez de R2

Deja `STORAGE_ENDPOINT` en blanco y define `STORAGE_REGION` con la región real donde creaste el bucket. El SDK detecta automáticamente el endpoint correcto de AWS cuando no se define un `endpointOverride`.

### Usar con MinIO (pruebas locales)

No necesitas cuenta en Cloudflare ni en AWS para probar el proyecto. Con Docker puedes levantar un servidor S3-compatible en tu propia máquina:

```bash
docker compose up -d
```
Esto levanta MinIO en `http://localhost:9000` (API) y `http://localhost:9001` (consola web). Entra a la consola con `minioadmin` / `minioadmin`, crea un bucket, y configura tu `.env` así:

```env
STORAGE_ACCESS_KEY_ID=minioadmin
STORAGE_SECRET_KEY=minioadmin
STORAGE_ENDPOINT=http://localhost:9000
STORAGE_BUCKET_NAME=nombre-del-bucket-creado
STORAGE_REGION=auto
```

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

## ⚠️ Manejo de errores

La API centraliza el manejo de excepciones en `GlobalExceptionHandler`, devolviendo respuestas HTTP claras en vez de errores genéricos:

| Situación                                  | Código HTTP |
|---------------------------------------------|-------------|
| Archivo no encontrado (`download`/`delete`) | 404         |
| Archivo vacío o excede el tamaño permitido  | 400 / 413   |
| Error interno inesperado                    | 500         |

El tamaño máximo de archivo se controla tanto a nivel de Spring (`application.properties`) como con una validación explícita en el servicio.

## 📌 Notas

- R2 no soporta todas las funcionalidades avanzadas de S3 (versionado, Object Lock, replicación entre regiones), pero las operaciones CRUD básicas son 100% compatibles.
- El `Content-Type` se guarda al momento de subir el archivo y se reutiliza en la descarga para permitir previsualización en el navegador o en Postman.
- El cliente S3 usa `forcePathStyle(true)`, necesario para MinIO y compatible también con R2 y AWS S3.

## 👨‍💻 Autor

[![GitHub](https://img.shields.io/badge/GitHub-LuisOrihuel08-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/LuisOrihuel08)

**FullStack Developer** · 2026
