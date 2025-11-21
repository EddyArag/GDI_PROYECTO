# Sistema de Gestión de Cotizaciones GDI

Este proyecto es una aplicación de escritorio desarrollada en Java con Swing para la gestión integral de cotizaciones, clientes y productos/servicios. Utiliza PostgreSQL como motor de base de datos y está orientado a empresas que requieren controlar y automatizar el proceso de cotización de servicios y productos.

## Características principales

- **Gestión de Cotizaciones:**  
  - Crear, modificar, eliminar y reactivar cotizaciones.
  - Visualizar detalles, totales, descuentos e impuestos.
  - Buscar cotizaciones por número, ordenar por total, y exportar a PDF.
  - Usar cotizaciones como plantilla para nuevas operaciones.

- **Gestión de Clientes:**  
  - Registrar, modificar, eliminar y reactivar clientes.
  - Búsqueda avanzada por nombre o RUC.
  - Visualización de datos completos y observaciones.

- **Gestión de Productos/Servicios:**  
  - Registrar, modificar, eliminar y reactivar productos y servicios.
  - Búsqueda y orden por descripción y precio.
  - Control de stock y precios unitarios.

- **Reportes y Consultas Analíticas:**  
  - Reporte de stock disponible y productos más cotizados.
  - Historial de cotizaciones por cliente.
  - Alertas de vencimiento de ofertas.
  - Resúmenes mensuales y ranking de clientes por gasto.

- **Configuración y Seguridad:**  
  - Conexión segura a PostgreSQL.
  - Eliminación lógica para preservar la integridad histórica.

## Estructura del Proyecto

- `src/`: Código fuente Java organizado por módulos (`gui`, `dataBase`, etc.).
- `lib/`: Dependencias externas.
- `bin/`: Archivos compilados.
- `README.md`: Documentación y guía de uso.

## Requisitos

- Java 8 o superior.
- PostgreSQL 12 o superior.
- Ejecución previa de los scripts SQL para crear tablas, funciones y procedimientos.
- Configuración correcta de la conexión en `src/dataBase/DatabaseConnection.java`.

## Instalación y Ejecución

1. Clona el repositorio.
2. Configura la conexión a la base de datos en `src/dataBase/DatabaseConnection.java`.
3. Compila el proyecto desde tu IDE o usando el comando:
   ```
   javac -d bin src/**/*.java
   ```
4. Ejecuta la aplicación principal:
   ```
   java -cp bin App
   ```

## Uso del Sistema

Al iniciar la aplicación, se mostrará la ventana principal con acceso a los siguientes módulos:

- **Clientes:**  
  Permite registrar y buscar clientes, ver sus datos y gestionar su estado.

- **Productos/Servicios:**  
  Permite registrar productos y servicios, modificar precios y controlar stock.

- **Cotizaciones:**  
  Permite crear cotizaciones, agregar productos/servicios, aplicar descuentos, y exportar a PDF.

- **Reportes:**  
  Acceso a reportes analíticos y alertas.

## Personalización

Puedes adaptar el sistema a tus necesidades modificando los procedimientos almacenados, funciones y la lógica de negocio en los archivos Java y SQL.

## Soporte y Contribuciones

Para reportar errores, solicitar nuevas funcionalidades o contribuir al proyecto, utiliza el sistema de issues de GitHub o contacta al equipo de desarrollo.

---

## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Database Configuration

Before running the application, you must configure the database connection:

1. Open `src/dataBase/DatabaseConnection.java`.
2. Edit the following fields to match your PostgreSQL setup:
   - `URL`: Host, port, and database name.
   - `USER`: Database username.
   - `PASSWORD`: Database password.

Example:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion_gdi";
private static final String USER = "postgres";
private static final String PASSWORD = "your_password";
```

**Important:**  
You must have a PostgreSQL database created with the name specified in `URL`, and all required tables, functions, and procedures must be loaded.  
Make sure you have previously executed all the provided SQL scripts to set up the schema and initial data.

---

## Configuración de la Base de Datos

Antes de ejecutar la aplicación, debes configurar la conexión a la base de datos:

1. Abre `src/dataBase/DatabaseConnection.java`.
2. Edita los siguientes campos para que coincidan con tu configuración de PostgreSQL:
   - `URL`: Host, puerto y nombre de la base de datos.
   - `USER`: Usuario de la base de datos.
   - `PASSWORD`: Contraseña de la base de datos.

Ejemplo:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion_gdi";
private static final String USER = "postgres";
private static final String PASSWORD = "tu_contraseña";
```

**Importante:**  
Debes tener creada una base de datos PostgreSQL con el nombre especificado en `URL`, y todas las tablas, funciones y procedimientos necesarios deben estar cargados.  
Asegúrate de haber ejecutado previamente todos los scripts SQL proporcionados para configurar el esquema y los datos iniciales.
