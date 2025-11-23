# Sistema de Gestión de Cotizaciones GDI

Este proyecto es una aplicación de escritorio desarrollada en Java con Swing para la gestión integral de cotizaciones, clientes y productos/servicios. Utiliza PostgreSQL como motor de base de datos y está orientado a empresas que requieren controlar y automatizar el proceso de cotización de servicios y productos.

---

## **Requisitos para la Ejecución**

Antes de ejecutar el sistema, asegúrate de cumplir con los siguientes requisitos:

- **PostgreSQL** versión **mínima 15** (recomendado 15.4 o superior, probado hasta 17.6).
- **Java** versión **21** (JDK 21).
- **Dependencias externas** incluidas en la carpeta `lib/`.

### **Configuración del Puerto de PostgreSQL**

- Por defecto, el sistema está configurado para conectarse al puerto **5432** de PostgreSQL.
- **Si tu PostgreSQL está en otro puerto** (por ejemplo, **5433**), debes cambiar el puerto en el archivo de conexión:
  1. Abre el archivo `src/dataBase/DatabaseConnection.java`.
  2. Busca la línea donde aparece el puerto (ejemplo: `5432`).
  3. Si tu puerto es `5433`, reemplaza **5432** por **5433** usando la función de búsqueda y reemplazo (Ctrl+F o Buscar/Reemplazar en tu editor).
  4. Guarda los cambios.

  Ejemplo de línea a modificar:
  ```java
  private static final String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion_gdi";
  // Cambia 5432 por 5433 si tu PostgreSQL está en ese puerto
  ```

- **Puedes colocar el puerto que corresponda a tu instalación de PostgreSQL** en esa línea.

---

## **Instalación y Ejecución**

### 1. **Carga de la Base de Datos**

- Si **NO tienes la base de datos cargada**, debes ejecutar previamente los scripts SQL proporcionados para crear las tablas, funciones y procedimientos necesarios.
- Si **YA tienes la base de datos cargada**, puedes omitir este paso y continuar con la ejecución del sistema.

### 2. **Configuración de la Conexión**

- Abre el archivo `src/dataBase/DatabaseConnection.java`.
- Edita los siguientes campos para que coincidan con tu configuración de PostgreSQL:
  - `URL`: Host, puerto y nombre de la base de datos.
  - `USER`: Usuario de la base de datos.
  - `PASSWORD`: Contraseña de la base de datos.

  Ejemplo:
  ```java
  private static final String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion_gdi";
  private static final String USER = "postgres";
  private static final String PASSWORD = "tu_contraseña";
  ```

- **Si tu puerto es diferente a 5432**, cámbialo como se explicó arriba.

### 3. **Compilación y Ejecución**

1. Compila el proyecto desde tu IDE o usando el comando:
   ```
   javac -d bin src/**/*.java
   ```
2. Ejecuta la aplicación principal:
   ```
   java -cp bin App
   ```

---

## **Uso del Sistema**

Al iniciar la aplicación, se mostrará la ventana principal con acceso a los siguientes módulos:

- **Clientes:**  
  Permite registrar y buscar clientes, ver sus datos y gestionar su estado.

- **Productos/Servicios:**  
  Permite registrar productos y servicios, modificar precios y controlar stock.

- **Cotizaciones:**  
  Permite crear cotizaciones, agregar productos/servicios, aplicar descuentos, y exportar a PDF.

- **Reportes:**  
  Acceso a reportes analíticos y alertas.

---

## **Reportes y Exportación a PDF**

- El sistema incluye un **Panel de Reportes** con acceso a reportes de stock, productos más cotizados, historial de clientes, alertas de vencimiento, resumen mensual, ranking de clientes y verificación de integridad.
- Cada reporte puede ser **exportado a PDF** con un solo clic, usando la funcionalidad de la carpeta `exportador`.
- La exportación de cotizaciones individuales también está disponible desde el panel de cotizaciones y al generar una nueva cotización.

> Consulta la documentación JavaDoc en el código fuente (`src/gui/ReportesPanel.java`, `src/exportador/ExportarReportePDF.java`, `src/exportador/ExportarCotizacionPDF.java`) para más detalles sobre el uso y la estructura de los reportes y exportaciones.

---

## **Personalización**

Puedes adaptar el sistema a tus necesidades modificando los procedimientos almacenados, funciones y la lógica de negocio en los archivos Java y SQL.

---

## **Soporte y Contribuciones**

Para reportar errores, solicitar nuevas funcionalidades o contribuir al proyecto, utiliza el sistema de issues de GitHub o contacta al equipo de desarrollo.

---
