# 🐳 Tutorial de Docker — Proyecto Hotel

## Índice

1. [¿Qué es Docker y por qué lo usamos?](#1-qué-es-docker-y-por-qué-lo-usamos)
2. [Instalación de Docker](#2-instalación-de-docker)
3. [Conceptos clave](#3-conceptos-clave)
4. [Estructura de archivos Docker del proyecto](#4-estructura-de-archivos-docker-del-proyecto)
5. [Explicación de cada archivo](#5-explicación-de-cada-archivo)
6. [Variables de entorno (.env)](#6-variables-de-entorno-env)
7. [Levantar el proyecto en local](#7-levantar-el-proyecto-en-local)
8. [Comandos Docker útiles](#8-comandos-docker-útiles)
9. [Troubleshooting — Problemas comunes](#9-troubleshooting--problemas-comunes)

---

## 1. ¿Qué es Docker y por qué lo usamos?

Docker es una herramienta que empaqueta tu aplicación junto con TODO lo que necesita para
funcionar (Java, Node, MySQL, etc.) dentro de **contenedores** aislados. Esto significa que:

- **Funciona en cualquier máquina** igual: tu PC, la de tu compañero, un servidor en la nube.
- **No necesitas instalar Java, Node, MySQL** en tu máquina o servidor — Docker lo trae todo.
- **Despliegue reproducible**: `docker-compose up` y todo arranca, siempre de la misma forma.

### Analogía simple

Imaginate que tu app es una receta de cocina. Sin Docker, cada vez que alguien la quiere cocinar
necesita conseguir los ingredientes (Java 21, Node 22, MySQL 8) e instalar todos. Con Docker,
le mandás un **tupper con el plato ya hecho**. Solo necesita un microondas (Docker) para calentarlo.

---

## 2. Instalación de Docker

### Windows

1. Descarga **Docker Desktop** desde: https://www.docker.com/products/docker-desktop/
2. Ejecuta el instalador. Decile que **sí** a todo (usa WSL2 — Windows Subsystem for Linux).
3. Reinicia la PC cuando te lo pida.
4. Abre Docker Desktop y verificá que el ícono de la ballena esté verde en la barra de tareas.

Verificar instalación:
```powershell
docker --version
# Docker version 27.x.x, build ...

docker compose version
# Docker Compose version v2.x.x
```

### Linux (Ubuntu/Debian)

```bash
# 1. Actualizar paquetes
sudo apt update && sudo apt upgrade -y

# 2. Instalar dependencias
sudo apt install -y ca-certificates curl gnupg lsb-release

# 3. Agregar la clave GPG oficial de Docker
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 4. Agregar el repositorio de Docker
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. Instalar Docker Engine + Compose
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 6. Agregar tu usuario al grupo docker (para no usar sudo cada vez)
sudo usermod -aG docker $USER

# 7. Cerrar sesión y volver a loguearte, o ejecutar:
newgrp docker

# 8. Verificar
docker --version
docker compose version
```

### macOS

1. Descarga Docker Desktop desde https://www.docker.com/products/docker-desktop/
2. Arrastra Docker a Aplicaciones.
3. Abrilo y seguí las instrucciones.

---

## 3. Conceptos clave

| Concepto | Explicación |
|---|---|
| **Imagen** | Una "foto" de tu aplicación empaquetada. Se construye una vez y se puede usar muchas veces. |
| **Contenedor** | Una instancia corriendo de una imagen. Como si la "foto" cobrara vida. |
| **Dockerfile** | Receta para construir una imagen. Dice qué instalar y qué copiar. |
| **docker-compose.yml** | Archivo que **orquesta** múltiples contenedores (MySQL + Backend + Frontend). |
| **Volumen** | Almacenamiento persistente. Sin esto, la DB se borra cada vez que apagás el contenedor. |
| **Network** | Red virtual interna. Los contenedores se pueden hablar entre sí por nombre (ej: `mysql`). |
| **Multi-stage build** | Técnica para reducir el tamaño de la imagen: compilar en una etapa, ejecutar en otra. |
| **.env** | Archivo de variables de entorno. Contraseñas, claves, configuraciones. |

---

## 4. Estructura de archivos Docker del proyecto

```
ProyectoHotel/
├── docker-compose.yml          ← 🎯 Archivo principal — orquesta todo
├── .env                        ← 🔒 Variables de entorno (NO se sube a git)
├── .env.example                ← 📋 Plantilla del .env (SÍ se sube a git)
│
├── BackendHotel/
│   ├── Dockerfile              ← 🏗️ Cómo construir la imagen del backend
│   ├── .dockerignore           ← 🚫 Qué NO copiar al construir
│   ├── pom.xml
│   └── src/
│
└── FrontendHotel/
    ├── Dockerfile              ← 🏗️ Cómo construir la imagen del frontend
    ├── .dockerignore           ← 🚫 Qué NO copiar al construir
    ├── nginx.conf              ← 🔀 Config de nginx (proxy + SPA)
    ├── package.json
    └── src/
```

---

## 5. Explicación de cada archivo

### 5.1 `BackendHotel/Dockerfile`

```dockerfile
# ── Etapa 1: compilar ────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
#    ↑ Usa una imagen que ya tiene Maven + Java 21 instalados
#      "alpine" = versión liviana de Linux
#      "AS build" = le ponemos nombre a esta etapa para referirla después

WORKDIR /app
# ↑ Crea y se mueve al directorio /app dentro del contenedor

# Descarga dependencias primero (capa cacheada si pom.xml no cambia)
COPY pom.xml .
RUN mvn dependency:go-offline -q
# ↑ TRUCO IMPORTANTE: Si solo cambias código Java (no el pom.xml),
#   Docker reutiliza esta capa cacheada y no descarga las dependencias de nuevo.
#   Esto ahorra MINUTOS en cada build.

COPY src ./src
RUN mvn package -DskipTests -q
# ↑ Compila el proyecto. -DskipTests porque los tests se corren aparte.
#   Genera target/hotel-0.0.1-SNAPSHOT.jar

# ── Etapa 2: imagen final ligera ──────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
#    ↑ Solo necesitamos el JRE (Java Runtime) — NO el JDK completo.
#      La imagen final pesa ~200MB en vez de ~700MB

WORKDIR /app

COPY --from=build /app/target/hotel-0.0.1-SNAPSHOT.jar app.jar
#    ↑ Copia el .jar de la etapa "build" a esta imagen limpia
#      La etapa de build (con Maven, código fuente, etc.) se DESCARTA

EXPOSE 8091
# ↑ Documenta que el contenedor escucha en el puerto 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
# ↑ Comando que se ejecuta cuando arranca el contenedor
```

**¿Por qué multi-stage?** Porque sin esto, la imagen final tendría Maven, el código fuente,
las dependencias de build, etc. Con multi-stage, la imagen final solo tiene Java + el .jar.

### 5.2 `FrontendHotel/Dockerfile`

```dockerfile
# ── Etapa 1: build ───────────────────────────────────────────────
FROM node:22-alpine AS build
#    ↑ Imagen con Node.js 22 (LTS) instalado

WORKDIR /app

COPY package*.json ./
RUN npm ci
# ↑ "npm ci" es más estricto que "npm install":
#   - Usa EXACTAMENTE las versiones del package-lock.json
#   - Es más rápido y reproducible
#   Mismo truco de caché: si package.json no cambia, no reinstala.

COPY . .

# VITE_API_URL vacío → URLs relativas → nginx hace el proxy al backend
RUN npm run build
# ↑ Genera la carpeta /app/dist con el HTML/JS/CSS estático

# ── Etapa 2: servir con nginx ─────────────────────────────────────
FROM nginx:alpine
#    ↑ Servidor web ultra-liviano (~40MB)

COPY --from=build /app/dist  /usr/share/nginx/html
#    ↑ Copia los archivos estáticos compilados

COPY nginx.conf               /etc/nginx/conf.d/default.conf
#    ↑ Nuestra configuración personalizada de nginx

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
# ↑ Arranca nginx en primer plano (necesario para que Docker lo maneje)
```

### 5.3 `FrontendHotel/nginx.conf`

```nginx
server {
    listen 80;
    # ↑ Escucha en el puerto 80 (HTTP)

    server_name localhost;

    root /usr/share/nginx/html;
    # ↑ Donde están los archivos compilados del frontend

    index index.html;

    # ── Proxy: llamadas a /api → backend ─────────────────────────
    location /api/ {
        proxy_pass         http://backend:8091/api/;
        #                   ↑ "backend" es el nombre del servicio en docker-compose
        #                   Docker lo resuelve automáticamente a la IP del contenedor
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        # ↑ Estos headers le dicen al backend quién es el cliente real
    }

    # ── Proxy: llamadas a /auth → backend ────────────────────────
    location /auth/ {
        proxy_pass         http://backend:8091/auth/;
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
    # ↑ ¿Por qué proxy? Porque así el navegador NO hace llamadas cross-origin.
    #   Todo va al mismo dominio (puerto 80) y nginx reenvía al backend.
    #   Esto evita problemas de CORS y las cookies HttpOnly funcionan perfecto.

    # ── SPA fallback (React Router) ───────────────────────────────
    location / {
        try_files $uri $uri/ /index.html;
        # ↑ Si pide /habitaciones y no existe ese archivo, sirve index.html
        #   Necesario para que React Router maneje las rutas en el frontend
    }
}
```

### 5.4 `docker-compose.yml` (raíz del proyecto)

```yaml
services:

  # ── Base de datos ─────────────────────────────────────────────
  mysql:
    image: mysql:8
    #     ↑ Descarga la imagen oficial de MySQL 8 de Docker Hub
    container_name: hotel_mysql
    #               ↑ Nombre fijo del contenedor (para identificarlo fácil)
    restart: unless-stopped
    #        ↑ Se reinicia automáticamente si falla (excepto si vos lo parás)
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      #                    ↑ Toma el valor del archivo .env
      MYSQL_DATABASE: hoteldatabase
      #               ↑ Crea esta base de datos automáticamente al iniciar
    volumes:
      - mysql_data:/var/lib/mysql
      # ↑ Volumen persistente: los datos de MySQL sobreviven a reinicios
    ports:
      - "3306:3306"
      # ↑ Expone MySQL en el puerto 3306 del host
      #   Formato: "puerto_de_tu_pc:puerto_dentro_del_contenedor"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
      # ↑ Cada 10 segundos verifica que MySQL esté vivo
      #   El backend NO arranca hasta que este check pase
    networks:
      - hotel_net

  # ── Backend (Spring Boot) ─────────────────────────────────────
  backend:
    build:
      context: ./BackendHotel
      #        ↑ Directorio donde está el Dockerfile del backend
      dockerfile: Dockerfile
    container_name: hotel_backend
    restart: unless-stopped
    ports:
      - "8091:8091"
    environment:
      DB_HOST: mysql
      #        ↑ Nombre del servicio mysql en este mismo compose
      #        Docker lo traduce a la IP interna del contenedor de MySQL
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      DB_FLYWAY_USERNAME: ${DB_FLYWAY_USERNAME}
      DB_FLYWAY_PASSWORD: ${DB_FLYWAY_PASSWORD}
      JWT_SECRET_KEY: ${JWT_SECRET_KEY}
      CORS_ALLOWED_ORIGIN: ${CORS_ALLOWED_ORIGIN}
      COOKIE_SECURE: ${COOKIE_SECURE:-false}
      #              ↑ ":-false" = si no está definida, usa "false" por defecto
    depends_on:
      mysql:
        condition: service_healthy
        # ↑ Espera a que el healthcheck de MySQL pase antes de arrancar
    networks:
      - hotel_net

  # ── Frontend (React + nginx) ──────────────────────────────────
  frontend:
    build:
      context: ./FrontendHotel
      dockerfile: Dockerfile
    container_name: hotel_frontend
    restart: unless-stopped
    ports:
      - "80:80"
      # ↑ El frontend queda en http://localhost (puerto 80)
    depends_on:
      - backend
    networks:
      - hotel_net
      # ↑ Todos en la misma red para que se puedan comunicar por nombre

volumes:
  mysql_data:
  # ↑ Define el volumen persistente para MySQL

networks:
  hotel_net:
    driver: bridge
  # ↑ Red interna virtual — los contenedores se ven entre sí
```

---

## 6. Variables de entorno (.env)

### 6.1 ¿Qué es el archivo `.env`?

Es un archivo de texto plano que contiene variables sensibles (contraseñas, claves secretas).
Docker Compose lo lee automáticamente si está en el mismo directorio que `docker-compose.yml`.

**REGLA DE ORO: NUNCA subas el `.env` a Git.** Solo sube el `.env.example` como plantilla.

### 6.2 Cómo crear tu `.env`

```powershell
# Desde la raíz del proyecto (donde está docker-compose.yml)
cd "C:\Users\esteb\OneDrive\Escritorio\ProyectoHotel"

# Copiar la plantilla
copy .env.example .env

# Ahora editá .env con tu editor favorito
```

### 6.3 Explicación de cada variable

```env
# ════════════════════════════════════════════════════════════════
# MYSQL_ROOT_PASSWORD
# ════════════════════════════════════════════════════════════════
# Contraseña del usuario "root" de MySQL dentro del contenedor.
# Docker la usa para CREAR el servidor MySQL por primera vez.
# IMPORTANTE: una vez creada la DB (primer docker-compose up),
# cambiar esta variable NO cambia la contraseña existente.
# Para cambiarla hay que borrar el volumen: docker volume rm proyectohotel_mysql_data
#
# Ejemplo:
MYSQL_ROOT_PASSWORD=Hotel2026Seguro!

# ════════════════════════════════════════════════════════════════
# DB_USERNAME / DB_PASSWORD
# ════════════════════════════════════════════════════════════════
# Credenciales que Spring Boot usa para conectarse a MySQL.
# Como usamos root, estas deben coincidir con MYSQL_ROOT_PASSWORD.
#
# Si querés un usuario separado (más seguro en producción),
# tendrías que crear el usuario en MySQL manualmente o con un script init.
#
# Ejemplo con root:
DB_USERNAME=root
DB_PASSWORD=Hotel2026Seguro!

# ════════════════════════════════════════════════════════════════
# DB_FLYWAY_USERNAME / DB_FLYWAY_PASSWORD
# ════════════════════════════════════════════════════════════════
# Credenciales que Flyway (el sistema de migraciones) usa.
# Flyway es el que ejecuta los archivos V1__init_schema.sql, V2__add_data.sql, etc.
# Generalmente se usa el mismo usuario root.
#
DB_FLYWAY_USERNAME=root
DB_FLYWAY_PASSWORD=Hotel2026Seguro!

# ════════════════════════════════════════════════════════════════
# JWT_SECRET_KEY
# ════════════════════════════════════════════════════════════════
# Clave secreta para firmar los tokens JWT (autenticación).
# DEBE ser una clave Base64 de al menos 256 bits (32 bytes).
#
# ¿Cómo generar una?
#
#   En Linux/macOS/Git Bash:
#     openssl rand -base64 32
#
#   En PowerShell:
#     [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])
#
#   O usá cualquier generador online de Base64 (32 bytes).
#
# Ejemplo (NO uses este, generá el tuyo):
JWT_SECRET_KEY=dGVzdF9zZWNyZXRfa2V5XzEyMzQ1Njc4OTBhYmNkZWY=

# ════════════════════════════════════════════════════════════════
# CORS_ALLOWED_ORIGIN
# ════════════════════════════════════════════════════════════════
# URL desde la que el frontend hace peticiones al backend.
# El backend solo acepta peticiones de este origen.
#
# Con Docker (nginx proxy), el frontend está en http://localhost (puerto 80).
# Las peticiones pasan por nginx → backend, así que técnicamente
# son same-origin. Pero se configura igual por si accedés al backend directo.
#
# En LOCAL (con Docker):
CORS_ALLOWED_ORIGIN=http://localhost
#
# En PRODUCCIÓN:
# CORS_ALLOWED_ORIGIN=https://hotel.tudominio.com

# ════════════════════════════════════════════════════════════════
# COOKIE_SECURE
# ════════════════════════════════════════════════════════════════
# Si las cookies HttpOnly (refresh token) requieren HTTPS.
#
# En LOCAL:     false  (usamos HTTP)
# En PRODUCCIÓN: true  (usamos HTTPS)
#
COOKIE_SECURE=false
```

### 6.4 Resumen rápido de valores

| Variable | Local | Producción |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | Lo que quieras | Contraseña fuerte |
| `DB_USERNAME` | `root` | `root` (o un usuario dedicado) |
| `DB_PASSWORD` | Igual que MYSQL_ROOT_PASSWORD | Igual que MYSQL_ROOT_PASSWORD |
| `DB_FLYWAY_USERNAME` | `root` | `root` |
| `DB_FLYWAY_PASSWORD` | Igual que MYSQL_ROOT_PASSWORD | Igual que MYSQL_ROOT_PASSWORD |
| `JWT_SECRET_KEY` | Base64 de 32 bytes | Base64 de 32 bytes (diferente) |
| `CORS_ALLOWED_ORIGIN` | `http://localhost` | `https://tudominio.com` |
| `COOKIE_SECURE` | `false` | `true` |

---

## 7. Levantar el proyecto en local

### 7.1 Primer arranque (paso a paso)

```powershell
# 1. Abrí una terminal y andá a la raíz del proyecto
cd "C:\Users\esteb\OneDrive\Escritorio\ProyectoHotel"

# 2. Asegurate de que Docker Desktop esté corriendo (ícono verde en la barra)

# 3. Creá el .env si no lo hiciste todavía
copy .env.example .env
# Editá .env y poné tus valores reales

# 4. Construí y levantá todo
docker compose up --build -d
#                  ↑ --build = reconstruye las imágenes
#                  ↑ -d = en segundo plano (detached)
```

### 7.2 ¿Qué pasa internamente?

Cuando ejecutás `docker compose up --build -d`, Docker hace esto en orden:

1. **Lee** `docker-compose.yml` y `.env`
2. **Construye** la imagen del backend (Maven compile → JAR → imagen JRE)
3. **Construye** la imagen del frontend (npm build → imagen nginx)
4. **Descarga** la imagen de MySQL 8 (si no la tiene)
5. **Arranca** MySQL y espera al healthcheck
6. **Arranca** el backend (se conecta a MySQL, ejecuta Flyway migraciones)
7. **Arranca** el frontend (nginx empieza a servir)

### 7.3 Verificar que todo funciona

```powershell
# Ver los 3 contenedores corriendo
docker compose ps

# Deberías ver:
# NAME              STATUS
# hotel_mysql       Up (healthy)
# hotel_backend     Up
# hotel_frontend    Up

# Ver logs en tiempo real (Ctrl+C para salir)
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql
```

Ahora abrí el navegador:
- **Frontend**: http://localhost (puerto 80)
- **Backend API directo**: http://localhost:8091
- **Swagger/OpenAPI**: http://localhost:8091/swagger-ui.html

### 7.4 Parar y volver a arrancar

```powershell
# Parar todo (los datos de MySQL se mantienen)
docker compose down

# Parar y BORRAR los datos de MySQL (cuidado)
docker compose down -v

# Arrancar sin reconstruir (más rápido si no cambiaste código)
docker compose up -d

# Arrancar reconstruyendo solo el backend
docker compose up --build -d backend

# Reconstruir solo el frontend
docker compose up --build -d frontend
```

---

## 8. Comandos Docker útiles

### Básicos

```powershell
# Ver contenedores corriendo
docker ps

# Ver TODOS los contenedores (incluso detenidos)
docker ps -a

# Ver imágenes descargadas/construidas
docker images

# Ver uso de disco de Docker
docker system df

# Limpiar todo lo que no se usa (imágenes, contenedores, redes viejas)
docker system prune -a
# ⚠️ Esto borra TODAS las imágenes no usadas — ahorra mucho espacio
```

### Inspeccionar contenedores

```powershell
# Entrar dentro de un contenedor (terminal interactiva)
docker exec -it hotel_backend bash
docker exec -it hotel_mysql bash
docker exec -it hotel_frontend sh    # nginx usa sh, no bash

# Conectarse a MySQL desde dentro del contenedor
docker exec -it hotel_mysql mysql -u root -p
# → te pide la contraseña (MYSQL_ROOT_PASSWORD)

# Ver los logs del backend
docker logs hotel_backend

# Seguir los logs en tiempo real
docker logs -f hotel_backend --tail 100
#                            ↑ muestra las últimas 100 líneas
```

### Reconstruir

```powershell
# Reconstruir todo desde cero (sin caché)
docker compose build --no-cache

# Reconstruir y arrancar
docker compose up --build -d

# Reconstruir solo un servicio
docker compose build backend
docker compose build frontend
```

---

## 9. Troubleshooting — Problemas comunes

### ❌ "Error: port is already allocated"

Significa que el puerto ya está siendo usado por otro proceso.

```powershell
# Ver qué usa el puerto 3306 (MySQL)
netstat -ano | findstr :3306

# Ver qué usa el puerto 8091 (backend)
netstat -ano | findstr :8091

# Ver qué usa el puerto 80 (frontend)
netstat -ano | findstr :80
```

**Solución**: Pará el proceso que usa ese puerto, o cambiá el puerto en docker-compose.yml.
Por ejemplo, para usar el puerto 8080 en vez de 80:
```yaml
frontend:
  ports:
    - "8080:80"   # accederías desde http://localhost:8080
```

### ❌ "backend exited with code 1" o "Communications link failure"

El backend no puede conectarse a MySQL.

1. Verificá que MySQL esté corriendo: `docker compose ps`
2. Verificá los logs de MySQL: `docker compose logs mysql`
3. Confirmá que las contraseñas en `.env` coinciden
4. **Error frecuente**: Si cambiaste MYSQL_ROOT_PASSWORD después del primer arranque,
   la contraseña vieja sigue en el volumen. Solución:
   ```powershell
   docker compose down -v   # borra el volumen de MySQL
   docker compose up --build -d  # arranca de cero
   ```

### ❌ "npm ci" falla en el build del frontend

Generalmente es porque falta el `package-lock.json`.

```powershell
cd FrontendHotel
npm install     # esto genera el package-lock.json
# Commitealo a git
```

### ❌ El frontend carga pero las llamadas a /api fallan

1. Verificá que el backend esté corriendo: `docker compose logs backend`
2. Abrí http://localhost:8091/actuator/health en el navegador — debería devolver `{"status":"UP"}`
3. Si el backend está bien pero nginx no proxea, revisá `docker compose logs frontend`

### ❌ "No space left on device"

Docker se comió tu disco.

```powershell
# Ver cuánto espacio usa Docker
docker system df

# Limpiar imágenes y contenedores viejos
docker system prune -a

# Si tenés volúmenes huérfanos
docker volume prune
```
