# Instrucciones de ejecución y pruebas para **ProductividadPlus**

## Requisitos previos

- **Docker Desktop** instalado y en ejecución (Windows 10/11).  
- Acceso a la terminal de Windows (PowerShell o CMD).
- (Opcional) **Java 22** y **Maven** instalados si deseas compilar o ejecutar pruebas fuera de Docker.

## 1️⃣ Arrancar la aplicación con Docker (un solo click)

1. Abre el Explorador de Windows y navega a la carpeta del proyecto:
   
   `C:\Users\usuario\Desktop\calculo final\productividadplus`

2. Haz **doble‑clic** sobre el archivo **`run.bat`**.  
   - El script comprobará que Docker está activo, descargará la imagen oficial de MongoDB, compilará la aplicación con Maven dentro de una imagen temporal y crearán dos contenedores:
     - `mongo` → MongoDB 7 en `localhost:27017`
     - `productividadplus` → Spring Boot en `localhost:8080`
   - Al final abrirá automáticamente tu navegador apuntando a `http://localhost:8080`.

### Contenido de `run.bat`
```bat
@echo off
title ProductividadPlus Docker Launcher

rem Check Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Docker Desktop no está corriendo. Inícialo y vuelve a ejecutar este script.
    pause
    exit /b 1
)

rem Build and start containers
docker compose up -d --build

rem Wait a few seconds for MongoDB to be ready
timeout /t 5 /nobreak >nul

rem Open the web UI
start http://localhost:8080

echo.
echo ✅ Todos los servicios están en marcha. Usa "docker compose down" para detenerlos.
pause
```

## 2️⃣ Verificar que la aplicación funciona

- Después de ejecutar `run.bat` abre tu navegador en `http://localhost:8080`. Deberías ver la página principal de **ProductividadPlus**.
- En la barra de Docker Desktop revisa que los contenedores **mongo** y **productividadplus** estén en estado **Running**.
- Puedes inspeccionar los logs:
  ```powershell
  docker compose logs -f productividadplus   # muestra la salida de Spring Boot
  docker compose logs -f mongo             # muestra la salida de MongoDB
  ```

## 3️⃣ Ejecutar pruebas unitarias

### Opción A: Dentro del contenedor Docker (recomendado)
```powershell
# Ejecuta las pruebas usando Maven dentro del contenedor "productividadplus"
docker compose exec app mvn test
```
- El comando compila el proyecto y ejecuta todas las pruebas en `src/test/java`.  
- La salida mostrará `BUILD SUCCESS` si todo está correcto.

### Opción B: Localmente (sin Docker)
> Sólo si tienes **Java 22** y **Maven** instalados.
```powershell
cd "C:\Users\usuario\Desktop\calculo final\productividadplus"
mvn test
```
- Esto compila y ejecuta las pruebas en tu máquina local.

## 4️⃣ Detener los contenedores

Cuando hayas terminado, abre una terminal en la raíz del proyecto y ejecuta:
```powershell
docker compose down -v   # -v elimina también el volumen de datos de MongoDB
```
Esto elimina los contenedores y el volumen persistente.

## 5️⃣ Resumen de los archivos clave

- **`Dockerfile`** – multistage build que compila la app con Maven y genera una imagen ligera basada en `eclipse-temurin:22-jre-alpine`.
- **`docker-compose.yml`** – orquesta los contenedores `mongo` y `app` (el contenedor se llama `productividadplus`).
- **`run.bat`** – script de conveniencia para lanzar todo con un doble‑clic.
- **`application.properties`** – contiene la URI de MongoDB (`mongodb://mongo:27017/productividadplus`).

## 6️⃣ Qué demostrar al profesor
1. Ejecutar `run.bat` → aparecen los mensajes de Docker y el navegador muestra la UI.
2. Ver los logs y confirmar que no hay excepción de conexión a MongoDB.
3. Ejecutar `docker compose exec app mvn test` → salida final `BUILD SUCCESS`.
4. Detener con `docker compose down -v`.

Con estos pasos podrás mostrar que la aplicación está completamente dockerizada, conecta con MongoDB y pasa todas las pruebas unitarias.

---
*Este documento fue generado automáticamente por el asistente de IA para servir como guía de presentación.*
