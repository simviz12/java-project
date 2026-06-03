# Guía de Ejecución: ProductividadPlus en IntelliJ IDEA

Para ejecutar este proyecto en tu entorno local usando **IntelliJ IDEA**, debes seguir estos pasos. Recuerda que ahora el sistema utiliza **MongoDB** en lugar de MySQL.

## 1. Prerrequisito Obligatorio: Instalar e Iniciar MongoDB

Antes de arrancar la aplicación en IntelliJ, la base de datos debe estar funcionando en tu equipo.

1. Descarga e instala **[MongoDB Community Server](https://www.mongodb.com/try/download/community)** para Windows.
2. Durante la instalación, asegúrate de marcar la opción para instalar **MongoDB Compass** (te servirá para ver tus datos gráficamente) y de instalar MongoDB como un "Servicio" (*Run service as Network Service user*).
3. Una vez instalado, el servicio de MongoDB se iniciará automáticamente en el puerto por defecto: `27017`.
4. *(Opcional)* Abre MongoDB Compass y conéctate a la URI `mongodb://localhost:27017` para confirmar que está funcionando.

---

## 2. Abrir el Proyecto en IntelliJ IDEA

1. Abre **IntelliJ IDEA**.
2. Selecciona **Open** (Abrir) en la pantalla de bienvenida.
3. Navega hasta la carpeta del proyecto en tu escritorio: `C:\Users\usuario\Desktop\calculo final\productividadplus` y haz clic en **OK**.
4. IntelliJ detectará automáticamente que es un proyecto **Maven** porque existe el archivo `pom.xml`.
5. Espera unos segundos a que IntelliJ indexe los archivos y descargue las dependencias de Maven (verás una barra de progreso en la esquina inferior derecha).

---

## 3. Configurar el SDK de Java (Si es necesario)

Si IntelliJ no tiene configurada la versión de Java correcta:
1. Ve a **File** > **Project Structure** (Estructura del Proyecto).
2. En la sección **Project**, asegúrate de que el **SDK** seleccionado sea **Java 17** (o Java 22, la versión que tienes instalada).
3. Haz clic en **Apply** y **OK**.

---

## 4. Ejecutar la Aplicación

1. En el panel de la izquierda (Project tool window), navega a:
   `src` > `main` > `java` > `com` > `productividadplus`
2. Busca la clase principal llamada **`ProductividadPlusApplication`** (tiene un ícono verde de "Play").
3. Haz **clic derecho** sobre el archivo `ProductividadPlusApplication` y selecciona **Run 'ProductividadPlusApplication'** (o haz clic en el botón verde de "Play" al lado del nombre de la clase dentro del archivo).
4. Verás que se abre la consola en la parte inferior. Si MongoDB está encendido, el sistema se inicializará y mostrará el mensaje de arranque exitoso.

### Inicio Exitoso
Cuando veas en la consola algo similar a: `Started ProductividadPlusApplication in 3.45 seconds`
Significa que todo está listo.

---

## 5. Acceder a la Aplicación Web

1. Abre tu navegador web favorito (Chrome, Edge, Firefox).
2. Ingresa a la siguiente dirección:
   **http://localhost:8080/**
3. ¡Listo! Verás la pantalla de inicio de sesión. Puedes ingresar con los usuarios de prueba que se generaron automáticamente (ej. `admin@empresa.com` / `Admin123`).
