# 🚀 Manual de Despliegue en Producción (VPS)

## Índice

1. [Requisitos del servidor](#1-requisitos-del-servidor)
2. [Elegir un VPS](#2-elegir-un-vps)
3. [Configuración inicial del servidor](#3-configuración-inicial-del-servidor)
4. [Instalar Docker en el servidor](#4-instalar-docker-en-el-servidor)
5. [Subir el proyecto al servidor](#5-subir-el-proyecto-al-servidor)
6. [Configurar variables de entorno](#6-configurar-variables-de-entorno)
7. [Levantar la aplicación](#7-levantar-la-aplicación)
8. [Configurar dominio y HTTPS (SSL)](#8-configurar-dominio-y-https-ssl)
9. [Firewall y seguridad](#9-firewall-y-seguridad)
10. [Mantenimiento y actualizaciones](#10-mantenimiento-y-actualizaciones)
11. [Backups de la base de datos](#11-backups-de-la-base-de-datos)
12. [Monitoreo](#12-monitoreo)
13. [Checklist final de producción](#13-checklist-final-de-producción)

---

## 1. Requisitos del servidor

### Mínimos (para este proyecto)

| Recurso | Mínimo | Recomendado |
|---|---|---|
| **RAM** | 2 GB | 4 GB |
| **CPU** | 1 vCPU | 2 vCPU |
| **Disco** | 20 GB SSD | 40 GB SSD |
| **SO** | Ubuntu 22.04 LTS | Ubuntu 24.04 LTS |

**¿Por qué estos valores?**
- MySQL consume ~500MB de RAM mínimo
- Spring Boot con Java 21 consume ~300-500MB
- Docker + nginx son livianos (~100MB)
- El disco necesita espacio para las imágenes de Docker (~2-3GB)

---

## 2. Elegir un VPS

Opciones económicas ordenadas por precio:

| Proveedor | Plan básico | RAM | CPU | Disco | Precio aprox. |
|---|---|---|---|---|---|
| **Hetzner** | CX22 | 4 GB | 2 vCPU | 40 GB | ~€4/mes |
| **DigitalOcean** | Basic | 2 GB | 1 vCPU | 50 GB | ~$12/mes |
| **Contabo** | VPS S | 8 GB | 4 vCPU | 200 GB | ~€6/mes |
| **Vultr** | Cloud | 2 GB | 1 vCPU | 50 GB | ~$12/mes |
| **AWS EC2** | t3.small | 2 GB | 2 vCPU | 20 GB | ~$18/mes |
| **Oracle Cloud** | Free tier | 1 GB | 1 vCPU | 50 GB | GRATIS |

**Recomendación**: Hetzner o Contabo si estás en Latinoamérica/Europa. DigitalOcean si querés
algo más sencillo con buena documentación.

---

## 3. Configuración inicial del servidor

Después de crear tu VPS, te van a dar una IP y acceso root por SSH.

### 3.1 Conectarse por SSH

```bash
# Desde tu PC (PowerShell, Git Bash, o terminal de Linux/Mac)
ssh root@TU_IP_DEL_SERVIDOR
# Ejemplo: ssh root@203.0.113.50
```

Si usás Windows y no tenés SSH, podés usar **PuTTY** o **Windows Terminal**.

### 3.2 Actualizar el sistema

```bash
# Actualizar todo
apt update && apt upgrade -y

# Instalar utilidades básicas
apt install -y curl wget git nano htop ufw
```

### 3.3 Crear un usuario dedicado (NO usar root para todo)

```bash
# Crear usuario
adduser hotel
# → Te pide contraseña y datos. Poné una contraseña segura.

# Darle permisos de sudo
usermod -aG sudo hotel

# Cambiar al nuevo usuario
su - hotel
```

### 3.4 Configurar SSH con clave pública (recomendado)

Desde **tu PC** (no el servidor):

```bash
# Generar par de claves (si no tenés una)
ssh-keygen -t ed25519 -C "hotel-server"
# → Enter para todo (guarda en ~/.ssh/id_ed25519)

# Copiar la clave pública al servidor
ssh-copy-id hotel@TU_IP_DEL_SERVIDOR
# O manualmente:
# cat ~/.ssh/id_ed25519.pub | ssh hotel@TU_IP_DEL_SERVIDOR "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

Ahora podés conectarte sin contraseña:

```bash
ssh hotel@TU_IP_DEL_SERVIDOR
```

### 3.5 Deshabilitar acceso root por SSH (seguridad)

```bash
sudo nano /etc/ssh/sshd_config
```

Buscá y cambiá estas líneas:

```
PermitRootLogin no
PasswordAuthentication no
```

```bash
sudo systemctl restart sshd
```

⚠️ **IMPORTANTE**: Verificá que podés entrar con tu clave ANTES de deshabilitar contraseñas.

---

## 4. Instalar Docker en el servidor

Conectate al servidor como el usuario `hotel` y ejecutá:

```bash
# 1. Instalar dependencias
sudo apt install -y ca-certificates curl gnupg lsb-release

# 2. Agregar clave GPG de Docker
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 3. Agregar repositorio
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 4. Instalar Docker Engine + Compose
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 5. Agregar tu usuario al grupo docker
sudo usermod -aG docker $USER

# 6. Aplicar el grupo (o cerrar sesión y volver a entrar)
newgrp docker

# 7. Verificar
docker --version
docker compose version
```

---

## 5. Subir el proyecto al servidor

### Opción A: Desde GitHub (recomendado)

```bash
# En el servidor, como usuario hotel
cd ~
git clone https://github.com/JJMcpark/Proyecto-Hotel.git
cd Proyecto-Hotel
```

Si el repo es privado:

```bash
# Usar token personal de GitHub
git clone https://TU_TOKEN@github.com/JJMcpark/Proyecto-Hotel.git
```

### Opción B: Subir archivos con SCP (sin Git)

Desde **tu PC**:

```powershell
# Subir todo el proyecto
scp -r "C:\Users\esteb\OneDrive\Escritorio\ProyectoHotel" hotel@TU_IP:/home/hotel/

# O lo esencial (sin node_modules ni target)
scp -r BackendHotel/src BackendHotel/pom.xml BackendHotel/Dockerfile hotel@TU_IP:/home/hotel/ProyectoHotel/BackendHotel/
scp -r FrontendHotel/src FrontendHotel/package.json FrontendHotel/package-lock.json FrontendHotel/Dockerfile FrontendHotel/nginx.conf FrontendHotel/vite.config.js FrontendHotel/index.html hotel@TU_IP:/home/hotel/ProyectoHotel/FrontendHotel/
scp docker-compose.yml .env.example hotel@TU_IP:/home/hotel/ProyectoHotel/
```

---

## 6. Configurar variables de entorno

En el servidor:

```bash
cd ~/Proyecto-Hotel    # o ~/ProyectoHotel según cómo lo subiste

# Copiar plantilla
cp .env.example .env

# Editar con nano
nano .env
```

### Valores para producción

```env
# ═══════════════════════════════════════════════════════════
# Contraseña de MySQL — USAR UNA FUERTE
# ═══════════════════════════════════════════════════════════
MYSQL_ROOT_PASSWORD=X8k#mP2$vL9nQ4wR!jT6yB

# ═══════════════════════════════════════════════════════════
# Credenciales de la app
# ═══════════════════════════════════════════════════════════
DB_USERNAME=root
DB_PASSWORD=X8k#mP2$vL9nQ4wR!jT6yB
DB_FLYWAY_USERNAME=root
DB_FLYWAY_PASSWORD=X8k#mP2$vL9nQ4wR!jT6yB

# ═══════════════════════════════════════════════════════════
# JWT — Generá tu propia clave:
#   openssl rand -base64 32
# ═══════════════════════════════════════════════════════════
JWT_SECRET_KEY=TU_CLAVE_BASE64_DE_32_BYTES_AQUI

# ═══════════════════════════════════════════════════════════
# CORS — URL pública de tu frontend
# ═══════════════════════════════════════════════════════════
# Si usás dominio:
CORS_ALLOWED_ORIGIN=https://hotel.tudominio.com
# Si NO usás dominio y accedés por IP:
# CORS_ALLOWED_ORIGIN=http://TU_IP_DEL_SERVIDOR

# ═══════════════════════════════════════════════════════════
# Cookies — SIEMPRE true con HTTPS
# ═══════════════════════════════════════════════════════════
COOKIE_SECURE=true
# Si todavía NO tenés HTTPS configurado, dejalo en false temporalmente
```

### Generar la clave JWT

```bash
# En el servidor:
openssl rand -base64 32
# Output ejemplo: a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V=

# Copiá ese valor en JWT_SECRET_KEY dentro del .env
```

### Proteger el archivo .env

```bash
# Solo el usuario hotel puede leerlo
chmod 600 .env
```

---

## 7. Levantar la aplicación

```bash
cd ~/Proyecto-Hotel

# Construir y levantar todo
docker compose up --build -d

# El primer build tarda 3-10 minutos (descarga de imágenes + compilación)
# Los siguientes builds son más rápidos gracias al caché
```

### Verificar

```bash
# Ver que los 3 contenedores estén corriendo
docker compose ps

# Resultado esperado:
# NAME              STATUS
# hotel_mysql       Up (healthy)
# hotel_backend     Up
# hotel_frontend    Up

# Ver logs
docker compose logs -f

# Probar el backend
curl http://localhost:8091/actuator/health
# → {"status":"UP"}

# Probar el frontend
curl -I http://localhost
# → HTTP/1.1 200 OK
```

Abrí en el navegador: `http://TU_IP_DEL_SERVIDOR`

---

## 8. Configurar dominio y HTTPS (SSL)

### 8.1 Apuntar tu dominio al servidor

En el panel de tu proveedor de dominios (Namecheap, GoDaddy, Cloudflare, etc.):

```
Tipo: A
Nombre: hotel       (o @ para el dominio raíz)
Valor: TU_IP_DEL_SERVIDOR
TTL:   300          (5 minutos)
```

Esperá 5-30 minutos a que propague. Verificá con:

```bash
ping hotel.tudominio.com
# Debería resolverse a tu IP
```

### 8.2 Instalar Certbot (certificado SSL gratuito con Let's Encrypt)

Vamos a usar un contenedor nginx externo con Certbot para manejar HTTPS.
Primero, modifica el `docker-compose.yml` para que el frontend no exponga el puerto 80
directamente, sino que un nginx externo maneje HTTPS y proxee a los contenedores.

**Opción más simple**: Instalar nginx + certbot directamente en el servidor (fuera de Docker).

```bash
# 1. Instalar nginx y certbot
sudo apt install -y nginx certbot python3-certbot-nginx

# 2. Crear configuración de nginx
sudo nano /etc/nginx/sites-available/hotel
```

Contenido de `/etc/nginx/sites-available/hotel`:

```nginx
server {
    listen 80;
    server_name hotel.tudominio.com;

    # Certbot necesita esto para verificar el dominio
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirigir todo HTTP → HTTPS (Certbot lo agrega automáticamente)
    location / {
        return 301 https://$server_name$request_uri;
    }
}

server {
    listen 443 ssl;
    server_name hotel.tudominio.com;

    # Certbot va a agregar estas líneas automáticamente:
    # ssl_certificate /etc/letsencrypt/live/hotel.tudominio.com/fullchain.pem;
    # ssl_certificate_key /etc/letsencrypt/live/hotel.tudominio.com/privkey.pem;

    # Todo el tráfico va al frontend (que internamente proxea al backend)
    location / {
        proxy_pass         http://127.0.0.1:80;       # ← puerto del contenedor frontend
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_set_header   Upgrade           $http_upgrade;
        proxy_set_header   Connection        "upgrade";
    }
}
```

```bash
# 3. Activar el sitio
sudo ln -s /etc/nginx/sites-available/hotel /etc/nginx/sites-enabled/
sudo rm /etc/nginx/sites-enabled/default   # quitar config default

# 4. Verificar la configuración
sudo nginx -t

# 5. Recargar nginx
sudo systemctl reload nginx

# 6. Obtener certificado SSL (Certbot modifica la config automáticamente)
sudo certbot --nginx -d hotel.tudominio.com
# → Te pide email, aceptar términos, y si querés redirigir HTTP a HTTPS (sí)
```

**IMPORTANTE**: Cuando uses este nginx externo, debés cambiar el puerto del frontend en `docker-compose.yml`
para evitar conflicto con el nginx del host:

```yaml
  frontend:
    ports:
      - "3000:80"      # ← Cambiar de "80:80" a "3000:80"
```

Y actualizar el proxy del nginx del host:

```nginx
    proxy_pass http://127.0.0.1:3000;   # ← Cambiar 80 por 3000
```

### 8.3 Renovación automática del certificado

Certbot instala un timer automático. Verificá:

```bash
sudo systemctl status certbot.timer
# Debería estar activo

# Test de renovación
sudo certbot renew --dry-run
```

### 8.4 Actualizar el .env para HTTPS

```bash
nano .env
```

```env
CORS_ALLOWED_ORIGIN=https://hotel.tudominio.com
COOKIE_SECURE=true
```

```bash
# Reiniciar el backend para que tome los cambios
docker compose restart backend
```

---

## 9. Firewall y seguridad

### 9.1 Configurar UFW (firewall)

```bash
# Permitir SSH (IMPORTANTE: hacelo ANTES de activar el firewall)
sudo ufw allow OpenSSH

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# NO exponer MySQL ni el backend directamente
# (se acceden solo internamente via Docker o nginx)

# Si necesitás acceder a MySQL desde tu PC para debugging:
# sudo ufw allow from TU_IP_DE_CASA to any port 3306

# Activar el firewall
sudo ufw enable

# Ver estado
sudo ufw status
```

**Resultado esperado:**

```
Status: active

To                         Action      From
--                         ------      ----
OpenSSH                    ALLOW       Anywhere
80/tcp                     ALLOW       Anywhere
443/tcp                    ALLOW       Anywhere
```

### 9.2 Ocultar puertos innecesarios en docker-compose.yml

En producción, NO necesitás exponer MySQL ni el backend directamente.
Editá el `docker-compose.yml`:

```yaml
  mysql:
    # ports:
    #   - "3306:3306"       ← COMENTAR esto en producción
    # MySQL es accesible solo desde la red interna de Docker

  backend:
    # ports:
    #   - "8091:8091"       ← COMENTAR esto en producción
    # El backend es accesible solo desde el contenedor nginx
```

⚠️ Solo dejá expuesto el frontend (que es el nginx que proxea todo).

### 9.3 Fail2Ban (protección contra fuerza bruta SSH)

```bash
sudo apt install -y fail2ban
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

---

## 10. Mantenimiento y actualizaciones

### 10.1 Actualizar el código (deploy)

Cuando hagas cambios en el código y los pushees a GitHub:

```bash
# En el servidor
cd ~/Proyecto-Hotel

# Bajar los cambios
git pull origin master

# Reconstruir y reiniciar
docker compose up --build -d

# Verificar que todo está bien
docker compose ps
docker compose logs -f --tail 50
```

### 10.2 Script de deploy automático

Creá un script para simplificar:

```bash
nano ~/deploy.sh
```

```bash
#!/bin/bash
set -e

echo "═══════════════════════════════════════"
echo "  Desplegando Proyecto Hotel..."
echo "═══════════════════════════════════════"

cd ~/Proyecto-Hotel

echo "📥 Bajando cambios de GitHub..."
git pull origin master

echo "🔨 Construyendo imágenes..."
docker compose build

echo "🚀 Reiniciando servicios..."
docker compose up -d

echo "🧹 Limpiando imágenes viejas..."
docker image prune -f

echo "✅ Estado actual:"
docker compose ps

echo ""
echo "═══════════════════════════════════════"
echo "  Deploy completado!"
echo "═══════════════════════════════════════"
```

```bash
chmod +x ~/deploy.sh

# Ahora para cada deploy solo ejecutás:
~/deploy.sh
```

### 10.3 Actualizar Docker y el sistema

```bash
# Actualizar paquetes del sistema (hacelo periódicamente)
sudo apt update && sudo apt upgrade -y

# Actualizar Docker
sudo apt install --only-upgrade docker-ce docker-ce-cli
```

---

## 11. Backups de la base de datos

### 11.1 Backup manual

```bash
# Crear backup
docker exec hotel_mysql mysqldump -u root -p"$(grep DB_PASSWORD ~/Proyecto-Hotel/.env | cut -d= -f2)" hoteldatabase > ~/backup_hotel_$(date +%Y%m%d_%H%M%S).sql

# Verificar que se creó
ls -la ~/backup_hotel_*.sql
```

### 11.2 Restaurar un backup

```bash
# Restaurar
docker exec -i hotel_mysql mysql -u root -p"TU_PASSWORD" hoteldatabase < ~/backup_hotel_20260327_120000.sql
```

### 11.3 Backup automático con cron

```bash
# Crear el script de backup
nano ~/backup_mysql.sh
```

```bash
#!/bin/bash

BACKUP_DIR=~/backups
MAX_BACKUPS=30   # mantener últimos 30 días
DATE=$(date +%Y%m%d_%H%M%S)
ENV_FILE=~/Proyecto-Hotel/.env

# Leer la contraseña del .env
DB_PASS=$(grep "^DB_PASSWORD=" "$ENV_FILE" | cut -d= -f2)

# Crear directorio si no existe
mkdir -p "$BACKUP_DIR"

# Hacer el backup
docker exec hotel_mysql mysqldump -u root -p"$DB_PASS" hoteldatabase | gzip > "$BACKUP_DIR/hotel_$DATE.sql.gz"

# Borrar backups viejos (más de MAX_BACKUPS días)
find "$BACKUP_DIR" -name "hotel_*.sql.gz" -mtime +"$MAX_BACKUPS" -delete

echo "[$(date)] Backup completado: hotel_$DATE.sql.gz"
```

```bash
chmod +x ~/backup_mysql.sh

# Agregar al cron (todos los días a las 3:00 AM)
crontab -e
# Agregar esta línea:
0 3 * * * /home/hotel/backup_mysql.sh >> /home/hotel/backups/backup.log 2>&1
```

---

## 12. Monitoreo

### 12.1 Ver recursos de los contenedores

```bash
# Uso de CPU/RAM en tiempo real
docker stats

# Resultado:
# CONTAINER ID   NAME              CPU %   MEM USAGE / LIMIT     NET I/O
# xxxx           hotel_mysql       0.5%    450MiB / 3.84GiB      ...
# xxxx           hotel_backend     1.2%    350MiB / 3.84GiB      ...
# xxxx           hotel_frontend    0.0%    5MiB / 3.84GiB        ...
```

### 12.2 Health check del backend

```bash
# Verificar que el backend responde
curl -s http://localhost:8091/actuator/health | python3 -m json.tool
# → { "status": "UP" }
```

### 12.3 Script de monitoreo simple

```bash
nano ~/check_hotel.sh
```

```bash
#!/bin/bash
# Verificar que todos los contenedores estén corriendo

CONTAINERS=("hotel_mysql" "hotel_backend" "hotel_frontend")

for container in "${CONTAINERS[@]}"; do
    STATUS=$(docker inspect -f '{{.State.Status}}' "$container" 2>/dev/null)
    if [ "$STATUS" != "running" ]; then
        echo "⚠️  $container NO está corriendo (status: $STATUS)"
        echo "Reiniciando..."
        cd ~/Proyecto-Hotel && docker compose up -d
        exit 1
    fi
done

echo "✅ Todos los contenedores están corriendo"
```

```bash
chmod +x ~/check_hotel.sh

# Ejecutar cada 5 minutos con cron
crontab -e
# Agregar:
*/5 * * * * /home/hotel/check_hotel.sh >> /home/hotel/monitor.log 2>&1
```

---

## 13. Checklist final de producción

Antes de considerar tu servidor "listo", verificá cada punto:

### Seguridad

- [ ] Acceso SSH con clave pública (no contraseña)
- [ ] Root login deshabilitado en SSH
- [ ] Firewall (UFW) activado — solo puertos 22, 80, 443
- [ ] MySQL NO expuesto al exterior (puerto 3306 cerrado)
- [ ] Backend NO expuesto al exterior (puerto 8091 cerrado)
- [ ] `.env` con permisos 600 (solo lectura para el owner)
- [ ] Contraseñas fuertes en `.env`
- [ ] `COOKIE_SECURE=true`
- [ ] JWT_SECRET_KEY generada con `openssl rand -base64 32`
- [ ] Fail2Ban instalado

### HTTPS

- [ ] Dominio apuntando al servidor (registro A)
- [ ] Certificado SSL instalado con Certbot
- [ ] Redirección HTTP → HTTPS funcionando
- [ ] Renovación automática del certificado activa

### Aplicación

- [ ] Los 3 contenedores corriendo (`docker compose ps`)
- [ ] Backend health check pasa (`/actuator/health`)
- [ ] Frontend carga en el navegador
- [ ] Login funciona
- [ ] Las APIs responden correctamente

### Mantenimiento

- [ ] Backup automático de MySQL configurado (cron)
- [ ] Script de deploy (`~/deploy.sh`) listo
- [ ] Monitoreo básico configurado
- [ ] Sistema operativo actualizado

---

## Resumen de la arquitectura en producción

```
                    Internet
                       │
                       ▼
              ┌──────────────────┐
              │   Nginx (host)   │  ← Puerto 443 (HTTPS)
              │   + Certbot SSL  │  ← Puerto 80  (redirige a 443)
              └────────┬─────────┘
                       │ proxy_pass :3000
                       ▼
              ┌──────────────────┐
              │  hotel_frontend  │  ← Nginx + React (puerto 3000)
              │    (contenedor)  │
              └────────┬─────────┘
                       │ proxy_pass :8091
                       ▼
              ┌──────────────────┐
              │  hotel_backend   │  ← Spring Boot (puerto 8091)
              │    (contenedor)  │
              └────────┬─────────┘
                       │ jdbc:mysql://mysql:3306
                       ▼
              ┌──────────────────┐
              │   hotel_mysql    │  ← MySQL 8 (puerto 3306 interno)
              │    (contenedor)  │
              └──────────────────┘
                       │
                  mysql_data (volumen persistente)
```

Flujo de una petición:
1. El usuario abre `https://hotel.tudominio.com`
2. **Nginx del host** recibe la petición HTTPS, termina el SSL
3. Proxea a **hotel_frontend** (contenedor nginx)
4. Si es un archivo estático (JS, CSS, HTML) → lo sirve directamente
5. Si es `/api/*` o `/auth/*` → proxea a **hotel_backend**
6. El backend consulta **hotel_mysql** y devuelve la respuesta
7. La respuesta viaja de vuelta: MySQL → Backend → Frontend nginx → Host nginx → Usuario
