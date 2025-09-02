# Servidor Web + Mini-IoC por reflexión — Taller de Arquitecturas de Servidores de Aplicaciones, Meta protocolos de objetos, Patrón IoC, Reflexión

**Alumno:** Alexandra Moreno Latorre  
**Asignatura:** Arquitectura Empresarial  
**Docente:** Luis Daniel Benavides Navarro


**Paquete:** `co.edu.escuelaing`  
**Objetivo:** construir un servidor web tipo Apache en Java que:
- Sirva **páginas HTML** e **imágenes PNG**.
- Provea un **mini-framework IoC** (por reflexión) para derivar aplicaciones web desde **POJOs**.
- Incluya una **aplicación de ejemplo**.
- Atienda **múltiples solicitudes no concurrentes** (secuencial).

---

## 📑 Tabla de contenidos
1. [Descripción general](#-descripción-general)
2. [Características](#-características)
3. [Requisitos](#-requisitos)
4. [Compilar y ejecutar](#-compilar-y-ejecutar)
5. [Imágenes con resultados](#imágenes-con-resultados)
6. [Estructura del proyecto](#-estructura-del-proyecto)

---

## 📌 Descripción general
Proyecto con un **servidor HTTP** muy sencillo (basado en `ServerSocket`) que procesa **GET** de forma **secuencial** (no concurrente). Además, expone un **mini-framework IoC** por reflexión:

- **Estáticos:** sirve archivos desde `src/main/resources/public` (HTML, PNG, etc.).
- **Dinámicos:** clases anotadas con `@RestController` y métodos `@GetMapping` se publican como endpoints.
- **Parámetros:** `@RequestParam` mapea `?key=value` (tipo `String`, con `defaultValue`).

Ejemplos incluidos:
- `HelloController` con `/` y `/hello`.
- `GreetingController` con `/greeting?name=...` (HTML grande “Hola, (nombre)”).

---

## ⚙️ Características
- **Servidor Web tipo Apache (minimal):**
    - `Content-Type` correcto (incluye `image/png`).
    - Respuestas de error: `404`, `405`, `500`.
    - **No concurrente:** atiende 1 conexión a la vez.

- **Mini-framework IoC por reflexión:**
    - Descubre `@RestController` y publica `@GetMapping("...")`.
    - `@RequestParam(value="name", defaultValue="...")` para query params.
    - **Primera versión:** puedes **pasar el POJO por CLI**.
    - **Versión final:** **escaneo del classpath** para cargar controladores sin CLI.

---

## 🖥️ Requisitos
- **Java 17** (o superior)
- **Maven 3.6+**
- IDE recomendado: **NetBeans** (también funciona en IntelliJ/VS Code)

---

## 👟 Compilar y ejecutar

### Opción A — Escaneo automático (versión final)
Carga todos los `@RestController` del classpath.
```bash
mvn -q -DskipTests package
java -cp target/classes co.edu.escuelaing.httpserver.Httpserver 8080
```

## Imágenes con resultados 
![img.png](img.png)

![img_1.png](img_1.png)

![img_2.png](img_2.png)


## 🔎 Probar en el navegador

- http://localhost:8080/
- http://localhost:8080/hello
- http://localhost:8080/greeting?name=Juan
- http://localhost:8080/img/logo.png *(si existe el PNG en `public/img/`)*

---

## 🛠️ NetBeans (sin terminal)

1. Abrir `pom.xml` como proyecto.  
2. **Project → Properties → Run**  
   - **Main Class:** `co.edu.escuelaing.Httpserver`  
   - **Arguments:** `8080`  
3. **Run (F6)** y probar en `http://localhost:8080/`.

---

## 📂 Estructura del proyecto

.
├── pom.xml
└── src/
└── main/
├── java/
│ └── co/edu/escuelaing/httpserver/
│ ├── Httpserver.java # Servidor + IoC + escaneo simple
│ ├── RestController.java # @RestController
│ ├── GetMapping.java # @GetMapping(path)
│ ├── RequestParam.java # @RequestParam(name, defaultValue)
│ ├── HelloController.java # Ejemplo: "/", "/hello"
│ └── GreetingController.java # Ejemplo: "/greeting?name=..."
└── resources/
└── public/
├── index.html
└── img/
└── logo.png # (opcional) para probar PNG

## 📧 Contacto

Alexandra Moreno Latorre

Email: alexandra.moreno-l@mail.escuelaing.edu.co

Universidad Escuela Colombiana de Ingeniería Julio Garavito

