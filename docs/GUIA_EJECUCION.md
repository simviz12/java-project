# Guía de ejecución — ProductividadPlus

Pasos para instalar, configurar y verificar que el sistema funciona en **Windows**.

---

## Requisitos previos

| Software | Versión | Verificar |
|----------|---------|-----------|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| MySQL Server | 8.x | Servicio MySQL en ejecución |

### Instalar Java 17
1. Descarga desde [Adoptium](https://adoptium.net/temurin/releases/?version=17)
2. Instala y agrega `JAVA_HOME` al PATH
3. Verifica: `java -version` debe mostrar `17`

### Instalar Maven
1. Descarga desde [maven.apache.org](https://maven.apache.org/download.cgi)
2. Descomprime y agrega `bin/` al PATH de Windows
3. Verifica: `mvn -version`

### Instalar MySQL
1. Descarga MySQL Installer para Windows
2. Instala MySQL Server 8.x
3. Anota la contraseña del usuario `root`

---

## Paso 1 — Crear la base de datos

Abre **MySQL Workbench** o línea de comandos:

```sql
CREATE DATABASE productividadplus
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

---

## Paso 2 — Configurar la aplicación

Edita el archivo:

`productividadplus/src/main/resources/application.properties`

Cambia al menos estas líneas:

```properties
spring.datasource.password=TU_PASSWORD_DE_MYSQL
```

Opcional (correos — si no configuras, la app arranca igual pero no envía emails):

```properties
spring.mail.username=tu_correo@gmail.com
spring.mail.password=tu_app_password_de_gmail
```

---

## Paso 3 — Compilar el proyecto

Abre **PowerShell** o **CMD**:

```powershell
cd "c:\Users\usuario\Desktop\calculo final\productividadplus"
mvn clean install
```

Debe terminar con **BUILD SUCCESS**. Si falla, revisa Java y Maven en el PATH.

---

## Paso 4 — Ejecutar la aplicación

```powershell
mvn spring-boot:run
```

Espera el mensaje similar a:

```
Started ProductividadPlusApplication in X seconds
```

---

## Paso 5 — Abrir en el navegador

URL: **http://localhost:8080/login**

Al **primer arranque** se cargan automáticamente usuarios, proyectos y tareas de prueba.

---

## Paso 6 — Credenciales de prueba

| Rol | Email | Contraseña | Qué probar |
|-----|-------|------------|------------|
| Administrador | admin@empresa.com | Admin123 | `/admin` — crear usuario |
| Gerente | gerente1@empresa.com | Gerente123 | `/tareas`, `/estadisticas`, PDF |
| Empleado | empleado1@empresa.com | Empleado123 | `/mis-tareas`, `/reportes/nuevo` |

---

## Paso 7 — Checklist de verificación (¿funciona todo?)

Marca cada ítem después de probarlo:

- [ ] Login como gerente → redirige a `/dashboard-gerente`
- [ ] Login como empleado → redirige a `/dashboard-empleado`
- [ ] Gerente crea tarea en `/tareas/crear`
- [ ] Empleado ve la tarea en `/mis-tareas`
- [ ] Empleado cambia estado PENDIENTE → EN_PROGRESO
- [ ] Empleado crea reporte en `/reportes/nuevo`
- [ ] Gerente ve reportes en `/reportes/globales`
- [ ] Gerente exporta PDF
- [ ] Estadísticas en `/estadisticas`
- [ ] Logout funciona
- [ ] Admin crea usuario en `/admin/usuarios/nuevo`

---

## Problemas frecuentes

### `mvn` no se reconoce
Maven no está en el PATH. Reinstala Maven o usa la ruta completa al ejecutable.

### Error de conexión a MySQL
- Verifica que MySQL esté corriendo (Servicios de Windows)
- Revisa usuario, contraseña y que exista la BD `productividadplus`
- Puerto 3306 libre

### Puerto 8080 ocupado
Agrega en `application.properties`:
```properties
server.port=8081
```

### Correos no llegan
Normal si no configuraste SMTP. La aplicación funciona; solo fallan notificaciones por email.

---

## Ejecutar tests

```powershell
mvn test
```

---

## Detener la aplicación

En la terminal donde corre Spring Boot: **Ctrl + C**

---

## Estructura del proyecto (referencia)

```
productividadplus/
├── pom.xml
├── src/main/java/com/productividadplus/   ← Código Java
├── src/main/resources/
│   ├── application.properties               ← Configuración
│   └── templates/                           ← Vistas HTML
└── docs/                                    ← Wiki, HU, diagramas
```
