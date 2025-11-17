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
