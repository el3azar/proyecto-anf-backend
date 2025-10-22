# 🚀 Sistema de Análisis Financiero - Backend

Este repositorio contiene el código fuente del backend para el proyecto de la cátedra de **Ingeniería de Sistemas Informáticos**. El sistema está diseñado para realizar análisis financieros de empresas, incluyendo cálculo de ratios, análisis horizontal/vertical y proyecciones de ventas.

Este backend expone una API REST que será consumida por una aplicación frontend desarrollada en React.

---

## 📋 Tabla de Contenidos

1.  [Tecnologías Utilizadas](#-tecnologías-utilizadas)
2.  [Prerrequisitos](#-prerrequisitos)
3.  [Configuración Inicial](#-configuración-inicial)
4.  [Cómo Ejecutar la Aplicación](#-cómo-ejecutar-la-aplicación)
5.  [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🛠️ Tecnologías Utilizadas

*   **Java 17**
*   **Spring Boot 3.5.6**
*   **Spring Data JPA** (Hibernate)
*   **Spring Security** (con JWT para autenticación)
*   **Maven** (Gestor de dependencias)
*   **MySQL** (Base de datos en producción)
*   **H2 Database** (Base de datos en memoria para desarrollo)
*   **Lombok**
*   **MapStruct**
*   **Apache POI** (Para manejo de archivos Excel)

---

## ✅ Prerrequisitos

Antes de empezar, asegúrate de tener instalado el siguiente software en tu máquina:

*   **JDK 17** (Java Development Kit). Puedes usar [OpenJDK](https://jdk.java.net/17/) o [Amazon Corretto](https://aws.amazon.com/es/corretto/).
*   **Apache Maven 3.8+**.
*   **Git**.
*   Un **IDE** de tu preferencia (IntelliJ IDEA, Eclipse, VS Code con el pack de Java).
*   Un cliente de base de datos como DBeaver o MySQL Workbench (será útil más adelante).

---

## 🔧 Configuración Inicial

Sigue estos pasos para configurar el proyecto en tu entorno local:

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/el3azar/proyecto-anf-backend.git
    cd proyecto-anf-backend
    ```

2.  **Verificar la configuración de la base de datos:**
    El proyecto está configurado por defecto para usar **H2**, una base de datos en memoria que no requiere instalación. El archivo de configuración se encuentra en `src/main/resources/application.properties` y ya está listo para funcionar.

3.  **Construir el proyecto con Maven:**
    Este comando descargará todas las dependencias necesarias y compilará el código fuente.
    ```bash
    mvn clean install
    ```
    Si estás en un IDE, este proceso usualmente se hace de forma automática al importar el proyecto como un proyecto de Maven.

---

## ධ Cómo Ejecutar la Aplicación

Puedes ejecutar la aplicación de dos maneras:

1.  **Desde tu IDE:**
    *   Busca la clase principal `AnalisisFinancieroBackendApplication.java`.
    *   Haz clic derecho sobre ella y selecciona `Run 'AnalisisFinancieroBackendApplication'`.

2.  **Desde la terminal (usando Maven):**
    ```bash
    mvn spring-boot:run
    ```

Una vez iniciada, la aplicación estará disponible en `http://localhost:8080`.

---

## 🗃️ Configuración de la Base de Datos

Para facilitar el desarrollo, la aplicación arranca con **H2 Database**.

*   **Consola Web de H2:** Puedes visualizar las tablas y los datos accediendo a la siguiente URL en tu navegador:
    `http://localhost:8080/h2-console`

*   **Credenciales de Conexión:**
    *   **Driver Class:** `org.h2.Driver`
    *   **JDBC URL:** `jdbc:h2:mem:analisis_financiero_db`
    *   **User Name:** `sa`
    *   **Password:** `password`

---


## 📂 Estructura del Proyecto

La estructura de carpetas está organizada por módulos de negocio para facilitar la ubicación del código:

```
└── com.anf.proyecto.backend
    ├── config/             // Configuración global (CORS)
    ├── exception/          // Manejo de excepciones
    ├── security/           // Configuración de Spring Security y JWT
    └── modules/            // Módulos principales de negocio
        ├── usuario/
        ├── empresa/
        ├── catalogo/
        ├── analisis/
        └── proyeccion/
```