# Guía de Deploy en el Servidor (VPS)

> Ejecutá cada bloque de comandos **en orden**, dentro del servidor via SSH o Webmin.
> Todos los comandos son para **Ubuntu/Debian**.
> IP del servidor actual: `135.125.199.172`

---

## ⚠️ ADVERTENCIAS IMPORTANTES (leer antes de empezar)

### No te bloquees del servidor

Las causas más comunes de quedar bloqueado de un VPS son:

1. **Activar UFW (firewall) sin permitir SSH primero** — Si ejecutás `sudo ufw enable` sin hacer `sudo ufw allow OpenSSH` ANTES, perdés acceso al puerto 22 y no podés volver a entrar por SSH.

2. **Deshabilitar contraseña SSH sin tener clave pública configurada** — Si ponés `PasswordAuthentication no` en `sshd_config` sin haber copiado tu clave pública SSH, te quedás afuera.

3. **Reiniciar `sshd` con una configuración rota** — Siempre usá `sudo sshd -t` para testear la config antes de reiniciar.

### Si ya te bloqueaste

- **Webmin** (si tu proveedor lo instaló): Entrá a `https://TU_IP:10000` desde el navegador. La terminal de Webmin funciona como SSH.
- **Consola VNC/web**: La mayoría de los proveedores (Hetzner, DigitalOcean, Contabo, Vultr, OVH) tienen una consola web en su panel de administración (NO Webmin, sino el panel donde compraste el VPS). Entrás directamente sin SSH.
- **Modo Rescue**: Hetzner, OVH y otros tienen un modo rescue que te deja arrancar con un sistema temporal para arreglar la config.
- **Reinstalar**: Si nada funciona, reinstalá el SO desde el panel del proveedor (perdés todo lo que no hayas respaldado).

### Orden correcto de seguridad

```
1. Permitir SSH en UFW  →  2. Permitir 80, 443, 10000  →  3. Recién ahí activar UFW
```

**NUNCA** actives UFW sin asegurarte de que SSH está permitido.

---

## 0. Conectarte al servidor

### Opción A: Por SSH (desde PowerShell o terminal)

```bash
ssh ubuntu@135.125.199.172
# o si tu usuario es root:
ssh root@135.125.199.172
```

### Opción B: Por Webmin (si tu proveedor lo instaló)

1. Abrí en el navegador: `https://135.125.199.172:10000`
2. Logueate con las credenciales que te dio el proveedor
3. Andá a **Tools → Terminal** (o "Command Shell")
4. Desde ahí podés ejecutar todos los comandos de esta guía

> ⚠️ Si Webmin no carga, es porque el firewall bloqueó el puerto 10000.
> Necesitás entrar por la **consola del panel del proveedor** (paso 0C).

### Opción C: Consola de emergencia del proveedor

Si ni SSH ni Webmin funcionan, entrá al panel web **del proveedor donde compraste el VPS**
(no Webmin) y buscá "Console", "VNC", "Terminal" o "Shell". Desde ahí ejecutás:

```bash
# Desbloquear SSH y Webmin
sudo ufw allow OpenSSH
sudo ufw allow 10000/tcp
sudo ufw reload
```

---

## 1. Actualizar el sistema e instalar herramientas

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git nano htop
```

---

## 2. Instalar Docker

```bash
# Instalar dependencias
sudo apt install -y ca-certificates curl gnupg

# Agregar la clave GPG oficial de Docker
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Agregar el repositorio oficial de Docker
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker Engine y Docker Compose
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Agregar tu usuario al grupo docker para no tener que usar sudo
sudo usermod -aG docker $USER
newgrp docker
```

> **Si estás en la terminal de Webmin**: `newgrp docker` puede no funcionar bien
> (la terminal se queda esperando). En ese caso, **cerrá la pestaña del terminal
> y abrí una nueva** en Webmin para que tome efecto el grupo docker.

Verificá que quedó bien instalado:
```bash
docker --version
docker compose version
```

---

## 3. Configurar el Firewall (UFW)

> ⚠️ **CRÍTICO**: Permitir SSH **ANTES** de activar el firewall.
> Si activás UFW sin permitir SSH, perdés acceso al servidor.

### 3.1 Instalar UFW y desactivar firewalls conflictivos

Los VPS con Virtualmin/Webmin suelen traer `firewalld` o `iptables` gestionado
por Webmin en lugar de UFW. Hay que instalar UFW y desactivar lo que interfiera:

```bash
# Instalar UFW (si no está instalado)
sudo apt install -y ufw

# Desactivar firewalld si existe (Virtualmin lo instala a veces)
sudo systemctl stop firewalld 2>/dev/null
sudo systemctl disable firewalld 2>/dev/null
sudo systemctl mask firewalld 2>/dev/null

# Si Webmin tiene su módulo de firewall activo, desactivar el servicio webmin-firewall
sudo systemctl stop webmin-firewalld 2>/dev/null
sudo systemctl disable webmin-firewalld 2>/dev/null

# Limpiar reglas viejas de iptables que puedan interferir
sudo iptables -F 2>/dev/null
sudo iptables -X 2>/dev/null
sudo iptables -P INPUT ACCEPT 2>/dev/null
sudo iptables -P FORWARD ACCEPT 2>/dev/null
sudo iptables -P OUTPUT ACCEPT 2>/dev/null
```

> Los comandos con `2>/dev/null` no dan error si el servicio no existe.
> Es seguro ejecutarlos todos aunque tu VPS no tenga firewalld instalado.

### 3.2 Configurar UFW

```bash
# 1. Permitir SSH (SIEMPRE PRIMERO!)
sudo ufw allow OpenSSH

# 2. Permitir HTTP
sudo ufw allow 80/tcp

# 3. Permitir HTTPS
sudo ufw allow 443/tcp

# 4. Permitir Webmin
sudo ufw allow 10000/tcp

# 5. Ahora sí, activar el firewall
sudo ufw enable
# → Te pregunta "proceed with operation (y|n)?" → escribí: y

# 6. Verificar que TODO está permitido
sudo ufw status
```

**Resultado esperado** (todos estos deben aparecer):
```
Status: active

To                         Action      From
--                         ------      ----
OpenSSH                    ALLOW       Anywhere
80/tcp                     ALLOW       Anywhere
443/tcp                    ALLOW       Anywhere
10000/tcp                  ALLOW       Anywhere
OpenSSH (v6)               ALLOW       Anywhere (v6)
80/tcp (v6)                ALLOW       Anywhere (v6)
443/tcp (v6)               ALLOW       Anywhere (v6)
10000/tcp (v6)             ALLOW       Anywhere (v6)
```

> **NO expongas los puertos 3306 (MySQL) ni 8091 (backend)**.
> Solo son accesibles internamente entre los contenedores Docker.

> ⚠️ **Docker y UFW**: Docker manipula `iptables` directamente, lo que significa
> que si un `docker-compose.yml` mapea un puerto (ej. `ports: "3306:3306"`),
> ese puerto queda expuesto a internet **aunque UFW no lo permita**.
> Por eso el `docker-compose.prod.yml` **no expone** los puertos de MySQL ni del backend.
> **Nunca uses el docker-compose de desarrollo en el servidor** — siempre usá
> el de producción como indica el paso 5.

---

## 4. Liberar puertos y parar servicios de Virtualmin que interfieren

> Virtualmin/Webmin instala Apache, MySQL/MariaDB y otros servicios.
> La app usa Docker para todo, así que hay que parar los del host.

```bash
# ── Parar Apache (ocupa el puerto 80) ─────────────────────────
sudo systemctl stop apache2 2>/dev/null
sudo systemctl disable apache2 2>/dev/null

# ── Parar nginx del host (si existe, no confundir con el de Docker) ──
sudo systemctl stop nginx 2>/dev/null
sudo systemctl disable nginx 2>/dev/null

# ── Parar MySQL/MariaDB del host (la app usa MySQL en Docker) ──
sudo systemctl stop mysql 2>/dev/null
sudo systemctl disable mysql 2>/dev/null
sudo systemctl stop mariadb 2>/dev/null
sudo systemctl disable mariadb 2>/dev/null

# ── Confirmar que el puerto 80 quedó libre (no debe mostrar nada) ──
sudo ss -tlnp | grep :80

# ── Confirmar que el puerto 3306 del host quedó libre ──────────
sudo ss -tlnp | grep :3306
```

> Los `2>/dev/null` hacen que no dé error si el servicio no existe.
> Esto NO afecta a Webmin — Webmin sigue funcionando en el puerto 10000.

---

## 5. Clonar los repositorios

```bash
# Crear la carpeta del proyecto
mkdir -p ~/ProyectoHotel
cd ~/ProyectoHotel

# Clonar el backend (rama Proyecto)
git clone -b Proyecto https://github.com/JJMcpark/Proyecto-Hotel.git BackendHotel

# Clonar el frontend (rama Proyecto)
git clone -b Proyecto https://github.com/thadd0/hotel-frontend.git FrontendHotel

# Copiar el docker-compose de producción a la raíz
cp BackendHotel/docker-compose.prod.yml docker-compose.yml
```

La estructura debe quedar así:
```
~/ProyectoHotel/
├── docker-compose.yml            ← copiado de docker-compose.prod.yml
├── .env                          ← lo vas a crear en el paso siguiente
├── BackendHotel/                 ← repo del backend
│   ├── .env.production.example   ← plantilla del .env
│   └── ...
└── FrontendHotel/                ← repo del frontend
    ├── .env.production           ← VITE_API_URL vacío (correcto, NO tocar)
    └── ...
```

### Verificar que .env.production del frontend está correcto

```bash
cat FrontendHotel/.env.production
```

Debe decir `VITE_API_URL=` (vacío, SIN ninguna URL).

Si tiene alguna URL (como `https://web.hotel.com`), arreglalo:
```bash
echo 'VITE_API_URL=' > FrontendHotel/.env.production
```

> ⚠️ **IMPORTANTE**: Si `VITE_API_URL` tiene una URL que no es tu servidor,
> el frontend va a intentar enviar las peticiones de login a esa URL
> y vas a ver "Error interno del servidor". Debe estar **vacío** para que
> el proxy de nginx funcione correctamente.

---

## 6. Crear y configurar el archivo .env

### 6.1 Copiar la plantilla de producción

```bash
cd ~/ProyectoHotel
cp BackendHotel/.env.production.example .env
```

### 6.2 Generar las claves necesarias

Antes de editar el `.env`, generá los valores:

```bash
# Generar contraseña para MySQL (copiar el resultado)
openssl rand -base64 24
# Ejemplo: K8vX2mP4nL9qR3wT6yB1cD5e

# Generar clave JWT (copiar el resultado)
openssl rand -base64 32
# Ejemplo: a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6=
```

### 6.3 Editar el .env

```bash
nano .env
```

Rellenar así (reemplazá los valores de ejemplo con los que generaste):

```env
# ── MySQL ──────────────────────────────────────────────────────
MYSQL_ROOT_PASSWORD=PEGAR_CONTRASEÑA_GENERADA_AQUI

# ── Credenciales del backend (MISMA contraseña que MySQL) ──────
DB_USERNAME=root
DB_PASSWORD=PEGAR_MISMA_CONTRASEÑA_DE_MYSQL_AQUI
DB_FLYWAY_USERNAME=root
DB_FLYWAY_PASSWORD=PEGAR_MISMA_CONTRASEÑA_DE_MYSQL_AQUI

# ── JWT ────────────────────────────────────────────────────────
JWT_SECRET_KEY=PEGAR_CLAVE_JWT_GENERADA_AQUI

# ── CORS (URL pública del frontend) ───────────────────────────
# SOLO por IP (sin dominio):
CORS_ALLOWED_ORIGIN=http://135.125.199.172

# ── Cookies ────────────────────────────────────────────────────
# false = HTTP (sin SSL). Cambiar a true SOLO después del paso 9.
COOKIE_SECURE=false
```

> ⚠️ **Las 3 contraseñas** (MYSQL_ROOT_PASSWORD, DB_PASSWORD, DB_FLYWAY_PASSWORD) **deben ser iguales**.

> ⚠️ **CORS_ALLOWED_ORIGIN** debe ser exactamente la URL con la que accedés desde el navegador.
> - Solo IP: `http://135.125.199.172` (sin barra al final, sin puerto)
> - Con dominio y HTTPS: `https://hospedajearroyo.com`
> - Si no coincide, el backend rechaza las peticiones y el login falla.

> ⚠️ **COOKIE_SECURE=false** cuando usás HTTP.
> Si ponés `true` sin tener HTTPS, el navegador descarta las cookies y el login no funciona.

### 6.4 Guardar y proteger

```
Ctrl + O  → Enter  (guardar en nano)
Ctrl + X           (cerrar nano)
```

```bash
# Proteger el archivo (solo vos podés leerlo)
chmod 600 .env

# Verificar que se grabó bien
cat .env
```

---

## 7. Levantar la aplicación

```bash
cd ~/ProyectoHotel
docker compose up --build -d
```

> El **primer build tarda entre 5 y 10 minutos** — Docker tiene que descargar
> las imágenes base, compilar el proyecto Java con Maven, y compilar el
> frontend con Node. Los builds siguientes son más rápidos por el caché.

Seguir los logs en tiempo real:
```bash
docker compose logs -f
# Ctrl+C para salir sin parar nada
```

---

## 8. Verificar que todo funciona

```bash
# Los 3 contenedores deben aparecer como "running"
docker compose ps

# Resultado esperado:
# NAME              STATUS
# hotel_mysql       Up (healthy)
# hotel_backend     Up
# hotel_frontend    Up
```

Probar que el backend responde (a través del proxy nginx del frontend):
```bash
# Si el backend está vivo, nginx puede alcanzarlo.
# Esta petición devuelve 401 (no autenticado) — eso es CORRECTO,
# significa que el backend está corriendo y respondiendo.
curl -s -o /dev/null -w "%{http_code}" http://localhost/api/admin/usuarios
# Debe responder: 401
```

> ⚠️ **NO uses** `curl http://localhost:8091/...` — el puerto 8091 del backend
> **no está expuesto** al host en producción (por seguridad). Solo es accesible
> entre contenedores Docker a través de la red interna.

Probar el frontend:
```bash
curl -I http://localhost
# Debe responder: HTTP/1.1 200 OK
```

Abrí en el navegador: `http://135.125.199.172` → debe cargar el login.

### Credenciales por defecto

La migración V2 crea estos usuarios iniciales:

| Rol | Documento (login) | Contraseña |
|---|---|---|
| Administrador | `00000000` | *(la que definieron en el equipo)* |
| Recepcionista | `11111111` | *(la que definieron en el equipo)* |

> Las contraseñas están almacenadas como hash bcrypt en `V2__add_data.sql`.
> **Cambialas desde la app** después del primer login en producción.

Probá loguearte. Si funciona, la app está lista por IP.

### Si el login falla ("Error interno del servidor")

Revisá estas 3 cosas en orden:

```bash
cd ~/ProyectoHotel

# 1. ¿El backend está corriendo?
docker compose ps
# hotel_backend debe decir "Up"
# Si no está, ver: docker compose logs backend

# 2. ¿VITE_API_URL del frontend está vacío?
cat FrontendHotel/.env.production
# Debe decir VITE_API_URL=   (vacío, sin URL)
# Si tiene algo, arreglalo y reconstruí:
#   echo 'VITE_API_URL=' > FrontendHotel/.env.production
#   docker compose up --build -d

# 3. ¿El .env tiene los valores correctos?
cat .env
# Verificar:
#   - CORS_ALLOWED_ORIGIN=http://135.125.199.172  (exacto, sin / al final)
#   - COOKIE_SECURE=false
#   - Las 3 contraseñas son iguales
#
# Si cambiaste algo:
#   docker compose restart backend
```

---

## 9. Conectar un dominio con HTTPS (SSL)

> Si solo vas a usar la IP, salteá este paso.
> **Hacé esto DESPUÉS de que el login funcione por IP** (paso 8).

### 9.1 Apuntar el dominio a tu servidor (DNS)

Entrá al panel donde compraste el dominio (Namecheap, GoDaddy, Cloudflare, etc.)
y creá un **registro DNS tipo A**:

| Campo | Valor |
|---|---|
| **Tipo** | A |
| **Nombre** | `@` (dominio raíz) |
| **Valor/IP** | `135.125.199.172` |
| **TTL** | 300 |

Esperá 5-30 minutos a que propague. Verificá desde el servidor:

```bash
ping hospedajearroyo.com
# Debe resolver a 135.125.199.172
```

### 9.2 Cambiar el puerto del frontend en docker-compose.yml

Nginx del host necesita el puerto 80 para HTTPS/redirección.
El contenedor frontend pasa al puerto 3000.

```bash
cd ~/ProyectoHotel
nano docker-compose.yml
```

Buscar esta línea dentro de `frontend:`:
```yaml
    ports:
      - "80:80"
```

Cambiar a:
```yaml
    ports:
      - "3000:80"
```

Guardar (Ctrl+O, Enter, Ctrl+X) y reiniciar:
```bash
docker compose up -d
```

Verificar que ahora responde en 3000:
```bash
curl -I http://localhost:3000
# Debe responder: HTTP/1.1 200 OK
```

### 9.3 Instalar nginx + Certbot en el host

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
```

### 9.4 Crear la configuración de nginx

```bash
sudo nano /etc/nginx/sites-available/hotel
```

Pegar este contenido:

```nginx
server {
    listen 80;
    server_name hospedajearroyo.com;

    # Certbot necesita esto para verificar el dominio
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirigir todo HTTP → HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

server {
    listen 443 ssl;
    server_name hospedajearroyo.com;

    # Certbot agrega los certificados automáticamente acá abajo

    # Todo el tráfico va al contenedor frontend
    location / {
        proxy_pass         http://127.0.0.1:3000;
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
}
```

Guardar (Ctrl+O, Enter, Ctrl+X).

### 9.5 Activar el sitio y obtener certificado SSL

```bash
# Activar la configuración
sudo ln -s /etc/nginx/sites-available/hotel /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Verificar que la config está bien escrita
sudo nginx -t
# Debe decir: syntax is ok / test is successful

# Recargar nginx
sudo systemctl reload nginx

# Obtener certificado SSL gratuito (Let's Encrypt)
sudo certbot --nginx -d hospedajearroyo.com
# → Te pide email: ponelo
# → Aceptar términos: Y
# → Compartir email con EFF: N (o Y, como quieras)
# → Certbot modifica la config de nginx automáticamente con los certificados
```

Verificar que la renovación automática funciona:
```bash
sudo certbot renew --dry-run
```

### 9.6 Actualizar el .env para HTTPS

```bash
cd ~/ProyectoHotel
nano .env
```

Cambiar estas dos líneas:
```env
CORS_ALLOWED_ORIGIN=https://hospedajearroyo.com
COOKIE_SECURE=true
```

Guardar (Ctrl+O, Enter, Ctrl+X) y reiniciar el backend:
```bash
docker compose restart backend
```

### 9.7 Verificar

Abrí en el navegador: `https://hospedajearroyo.com`

- Debe cargar el login con el candado verde (HTTPS)
- `http://hospedajearroyo.com` debe redirigir automáticamente a `https://`
- El login debe funcionar

### Arquitectura final con dominio

```
Usuario → https://hospedajearroyo.com
              │
              ▼
    ┌──────────────────────┐
    │   Nginx (en el host) │  ← Puerto 443 (HTTPS/SSL)
    │   + Certbot SSL      │  ← Puerto 80 (redirige a 443)
    └──────────┬───────────┘
               │ proxy_pass → 127.0.0.1:3000
               ▼
    ┌──────────────────────┐
    │   hotel_frontend     │  ← Nginx + React (puerto 3000)
    │   (contenedor)       │
    └──────────┬───────────┘
               │ /api/* y /auth/* → proxy_pass → backend:8091
               ▼
    ┌──────────────────────┐
    │   hotel_backend      │  ← Spring Boot (solo red Docker)
    │   (contenedor)       │
    └──────────┬───────────┘
               │ jdbc:mysql://mysql:3306
               ▼
    ┌──────────────────────┐
    │   hotel_mysql        │  ← MySQL 8 (solo red Docker)
    │   (contenedor)       │
    └──────────────────────┘
               │
          mysql_data (volumen persistente)
```

---

## 10. Comandos de mantenimiento (para el día a día)

```bash
cd ~/ProyectoHotel

# Ver el estado de los contenedores
docker compose ps

# Ver logs de un servicio específico
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# Parar todo (los datos de MySQL se mantienen)
docker compose down

# Volver a levantar (sin reconstruir, más rápido)
docker compose up -d

# Actualizar el código después de un git push
git -C BackendHotel pull origin Proyecto
git -C FrontendHotel pull origin Proyecto
docker compose up --build -d

# Ver uso de CPU y RAM en tiempo real
docker stats
```

---

## 11. Backup de la base de datos

```bash
# Crear un backup (lee la contraseña del .env automáticamente)
DB_PASS=$(grep "^DB_PASSWORD=" ~/ProyectoHotel/.env | cut -d= -f2)
docker exec hotel_mysql mysqldump -u root -p"$DB_PASS" hoteldatabase > ~/backup_hotel_$(date +%Y%m%d).sql

# Ver los backups creados
ls -lh ~/backup_hotel_*.sql

# Restaurar un backup (si necesitás)
# docker exec -i hotel_mysql mysql -u root -p"$DB_PASS" hoteldatabase < ~/backup_hotel_FECHA.sql
```

---

## Solución de problemas

### Me bloqueé del servidor (no puedo entrar por SSH ni Webmin)

1. Entrá por la **consola web del proveedor** (panel donde compraste el VPS, NO Webmin).
   Buscar "Console", "VNC", "Terminal" o "Shell".
2. Una vez adentro:

```bash
# Desbloquear todo
sudo ufw allow OpenSSH
sudo ufw allow 10000/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw reload

# Si también cambiaste sshd_config:
sudo nano /etc/ssh/sshd_config
# Asegurar que estas líneas digan:
#   PermitRootLogin yes
#   PasswordAuthentication yes
sudo sshd -t          # verificar que no hay errores
sudo systemctl restart sshd
```

3. Probá conectarte por SSH o Webmin de nuevo.

### El login carga pero falla ("Error interno del servidor")

Esto pasa cuando el frontend no puede comunicarse con el backend. Causas:

**1. `VITE_API_URL` tiene una URL incorrecta:**
```bash
cat ~/ProyectoHotel/FrontendHotel/.env.production
# DEBE decir: VITE_API_URL=   (vacío)
# Si tiene otra cosa:
echo 'VITE_API_URL=' > ~/ProyectoHotel/FrontendHotel/.env.production
cd ~/ProyectoHotel && docker compose up --build -d
```

**2. `CORS_ALLOWED_ORIGIN` no coincide con la URL del navegador:**
```bash
grep CORS ~/ProyectoHotel/.env
# Debe ser EXACTAMENTE la URL que usás:
#   Por IP:      http://135.125.199.172  (sin /, sin puerto)
#   Con dominio: https://hospedajearroyo.com
# Si no coincide, corregir y reiniciar:
nano ~/ProyectoHotel/.env
docker compose restart backend
```

**3. `COOKIE_SECURE=true` pero estás usando HTTP (sin HTTPS):**
```bash
grep COOKIE_SECURE ~/ProyectoHotel/.env
# Si accedés por http:// → debe ser false
# Si accedés por https:// → debe ser true
# Corregir y reiniciar:
nano ~/ProyectoHotel/.env
docker compose restart backend
```

**4. El backend no está corriendo:**
```bash
docker compose ps
# Si hotel_backend no dice "Up":
docker compose logs --tail 50 backend
```

### El puerto 80 ya está en uso
```bash
sudo ss -tlnp | grep :80
# Si aparece apache2:
sudo systemctl stop apache2 && sudo systemctl disable apache2
# Si aparece nginx del host y no debería:
sudo systemctl stop nginx
# Reiniciar frontend:
docker compose restart frontend
```

### Error "Access denied" del backend
```bash
# Verificar que las 3 contraseñas del .env son iguales
grep PASSWORD ~/ProyectoHotel/.env
# MYSQL_ROOT_PASSWORD, DB_PASSWORD y DB_FLYWAY_PASSWORD → las 3 iguales

# Si las cambiaste DESPUÉS del primer arranque, hay que borrar el volumen de MySQL:
cd ~/ProyectoHotel
docker compose down -v    # ⚠️ BORRA TODOS LOS DATOS DE MYSQL
docker compose up --build -d
```

### El dominio no carga

```bash
# 1. ¿El DNS apunta a tu IP?
ping tudominio.com
# Debe resolver a 135.125.199.172. Si no, revisá el registro A en tu proveedor de dominio.

# 2. ¿Nginx del host está corriendo?
sudo systemctl status nginx
# Si no está activo:
sudo systemctl start nginx

# 3. ¿La config de nginx es correcta?
sudo nginx -t
# Si hay errores, revisá:
sudo nano /etc/nginx/sites-available/hotel

# 4. ¿El contenedor frontend responde en el puerto 3000?
curl -I http://localhost:3000
# Si no responde, el frontend no está corriendo:
cd ~/ProyectoHotel && docker compose ps
```

### Webmin no carga (después de activar UFW)
```bash
sudo ufw allow 10000/tcp
sudo ufw reload
# Probar: https://135.125.199.172:10000
```

### Limpiar espacio en disco
```bash
docker system prune -a
# ⚠️ Esto borra imágenes no usadas, tendrás que reconstruir con --build
```

### Error de Flyway: "Migration checksum mismatch"

Esto pasa cuando un archivo de migración (ej. `V5__seed_data.sql`) fue modificado
**después** de que Flyway ya lo ejecutó en la base de datos.
Flyway guarda el checksum de cada migración; si el archivo cambió, el checksum no coincide y Flyway se niega a arrancar.

**Solución A — Borrar todo y empezar de cero** (recomendado si no hay datos importantes):
```bash
cd ~/ProyectoHotel
docker compose down -v    # ⚠️ BORRA TODOS LOS DATOS DE MYSQL
docker compose up --build -d
# Flyway ejecuta todas las migraciones desde V1 con los archivos actuales
```

**Solución B — Reparar el checksum sin perder datos**:
```bash
# Entrar al contenedor MySQL
DB_PASS=$(grep "^DB_PASSWORD=" ~/ProyectoHotel/.env | cut -d= -f2)
docker exec -it hotel_mysql mysql -u root -p"$DB_PASS" hoteldatabase

# Dentro de MySQL, actualizar el checksum de la migración afectada:
# (reemplazar V5 por la versión que falla)
DELETE FROM flyway_schema_history WHERE version = '5';
EXIT;

# Reiniciar el backend para que Flyway vuelva a ejecutar esa migración
cd ~/ProyectoHotel
docker compose restart backend
```

> ⚠️ La solución B solo funciona si los cambios en la migración no entran en conflicto
> con los datos existentes (ej. si solo se quitaron INSERTs de datos demo).

---

## Resumen: archivos de configuración

| Archivo | Ubicación en servidor | Para qué |
|---|---|---|
| `docker-compose.yml` | `~/ProyectoHotel/` | Orquesta los 3 contenedores (MySQL, backend, frontend) |
| `.env` | `~/ProyectoHotel/` | Variables secretas (contraseñas, JWT, CORS) — **NO subir a Git** |
| `.env.production.example` | En el repo backend | Plantilla para crear el `.env` en el servidor |
| `.env.production` | `FrontendHotel/` | Config de Vite — `VITE_API_URL` vacío = usa proxy nginx |
| `nginx.conf` | `FrontendHotel/` | Proxy nginx dentro del contenedor frontend (/api → backend) |
| `/etc/nginx/sites-available/hotel` | En el host (no Docker) | Nginx del host: SSL + proxy al contenedor frontend |
| `Dockerfile` | `BackendHotel/` | Compila Spring Boot con Maven |
| `Dockerfile` | `FrontendHotel/` | Compila React con Vite y sirve con nginx |

## Resumen: qué valor poner según el escenario

| Variable | Solo IP (HTTP) | Con dominio (HTTPS) |
|---|---|---|
| `VITE_API_URL` | *(vacío)* | *(vacío)* |
| `CORS_ALLOWED_ORIGIN` | `http://135.125.199.172` | `https://tudominio.com` |
| `COOKIE_SECURE` | `false` | `true` |
| Puerto frontend | `80:80` | `3000:80` |
