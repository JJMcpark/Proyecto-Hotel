# Guía de Deploy en el Servidor (VPS)

> Ejecutá cada bloque de comandos en orden, dentro del servidor via SSH.
> Todos los comandos son para Ubuntu/Debian.

---

## 0. Conectarte al servidor desde tu PC

```bash
ssh root@TU_IP_DEL_SERVIDOR
# Ejemplo: ssh root@203.0.113.50
# Si usás Windows: abrí PowerShell o Windows Terminal y ejecutá lo mismo
```

---

## 1. Actualizar el sistema

```bash
sudo apt update && sudo apt upgrade -y
```

---

## 2. Instalar Docker

```bash
# Instalar dependencias necesarias para agregar el repositorio de Docker
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

Verificá que quedó bien instalado:
```bash
docker --version
docker compose version
```

---

## 3. Liberar el puerto 80

> Virtualmin/Webmin instala Apache automáticamente, que ocupa el puerto 80.
> Si no lo liberás, el contenedor del frontend no va a poder arrancar.

```bash
# Ver si Apache está corriendo en el puerto 80
sudo ss -tlnp | grep :80

# Pararlo y deshabilitarlo permanentemente
sudo systemctl stop apache2
sudo systemctl disable apache2

# Confirmar que el puerto 80 quedó libre (no debe mostrar nada)
sudo ss -tlnp | grep :80
```

---

## 4. Clonar los repositorios

```bash
# Crear la carpeta del proyecto
mkdir -p ~/ProyectoHotel
cd ~/ProyectoHotel

# Clonar el backend (rama Proyecto)
git clone -b Proyecto https://github.com/JJMcpark/Proyecto-Hotel.git BackendHotel

# Clonar el frontend (rama Proyecto)
git clone -b Proyecto https://github.com/thadd0/hotel-frontend.git FrontendHotel

# Copiar el docker-compose y la plantilla del .env a la raíz del proyecto
cp BackendHotel/docker-compose.prod.yml docker-compose.yml
cp BackendHotel/.env.example .env
```

La estructura debe quedar así:
```
~/ProyectoHotel/
├── docker-compose.yml   ← recién copiado
├── .env                 ← recién copiado, hay que editarlo
├── BackendHotel/        ← repo del backend
└── FrontendHotel/       ← repo del frontend
```

---

## 5. Configurar el archivo .env

```bash
cd ~/ProyectoHotel
nano .env
```

El archivo abierto se ve así — editá los valores marcados con ←:

```
MYSQL_ROOT_PASSWORD=    ← Poné una contraseña fuerte. Ejemplo: Hotel2026#Seguro
DB_USERNAME=root
DB_PASSWORD=            ← La MISMA contraseña que MYSQL_ROOT_PASSWORD
DB_FLYWAY_USERNAME=root
DB_FLYWAY_PASSWORD=     ← La MISMA contraseña que MYSQL_ROOT_PASSWORD
JWT_SECRET_KEY=         ← Ver abajo cómo generarla
CORS_ALLOWED_ORIGIN=    ← Ver abajo
COOKIE_SECURE=false     ← Dejalo en false por ahora
```

### Generar el JWT_SECRET_KEY

Ejecutá este comando y copiá el resultado en el .env:
```bash
openssl rand -base64 32
```
Ejemplo de resultado: `K8vX2mP4nL9qR3wT6yB1cD5eF0gH7iJ+`

### CORS_ALLOWED_ORIGIN

- Si vas a acceder por IP: `http://TU_IP_DEL_SERVIDOR`
- Si tenés dominio: `https://tudominio.com`

### Guardar y salir de nano

```
Ctrl + O   → Enter   (guardar)
Ctrl + X             (cerrar)
```

### Proteger el archivo (que solo vos puedas leerlo)

```bash
chmod 600 .env
```

---

## 6. Levantar la aplicación

```bash
cd ~/ProyectoHotel
docker compose up --build -d
```

> El **primer build tarda entre 5 y 10 minutos** — Docker tiene que descargar
> las imágenes base, compilar todo el proyecto Java con Maven, y compilar el
> frontend con Node. Los builds siguientes son muchísimo más rápidos por el caché.

Seguir los logs en tiempo real:
```bash
docker compose logs -f
# Ctrl+C para salir sin parar nada
```

---

## 7. Verificar que todo funciona

```bash
# Los 3 contenedores deben aparecer como "running"
docker compose ps

# Resultado esperado:
# NAME              STATUS
# hotel_mysql       Up (healthy)
# hotel_backend     Up
# hotel_frontend    Up
```

Probar el backend (debe responder {"status":"UP"}):
```bash
curl http://localhost:8091/actuator/health
```

Probar el frontend (debe responder HTTP 200):
```bash
curl -I http://localhost
```

Abrí en el navegador: `http://TU_IP_DEL_SERVIDOR`

---

## 8. Comandos de mantenimiento (para el día a día)

```bash
# Ver el estado de los contenedores
docker compose ps

# Ver los logs de un servicio específico
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# Parar todo (los datos de MySQL se mantienen)
docker compose down

# Volver a levantar (sin reconstruir, más rápido)
docker compose up -d

# Actualizar el código después de un git push
cd ~/ProyectoHotel
git -C BackendHotel pull origin Proyecto
git -C FrontendHotel pull origin Proyecto
docker compose up --build -d

# Ver uso de CPU y RAM de cada contenedor en tiempo real
docker stats
```

---

## 9. Backup de la base de datos

```bash
# Crear un backup (reemplazá TU_PASSWORD)
docker exec hotel_mysql mysqldump -u root -pTU_PASSWORD hoteldatabase > ~/backup_hotel_$(date +%Y%m%d).sql

# Ver los backups creados
ls -lh ~/backup_hotel_*.sql
```

---

## Solución de problemas comunes

### El puerto 80 ya está en uso
```bash
sudo systemctl stop apache2 && sudo systemctl disable apache2
docker compose restart frontend
```

### El backend no arranca (no se conecta a MySQL)
```bash
# Verificar que MySQL pasó el healthcheck
docker compose ps
# Si mysql dice "starting" en vez de "healthy", esperá un poco más
docker compose logs mysql
```

### Me equivoqué en el .env y necesito cambiar algo
```bash
nano ~/ProyectoHotel/.env
# Editá el valor
docker compose restart backend   # si cambiaste algo del backend
# Si cambiaste MYSQL_ROOT_PASSWORD después del primer arranque,
# hay que borrar el volumen y empezar de cero:
docker compose down -v
docker compose up -d
```

### Limpiar espacio en disco (imágenes viejas de Docker)
```bash
docker system prune -a
# ⚠️ Esto borra imágenes no usadas, tendrás que reconstruir con --build
```
