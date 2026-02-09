# 🔔 Notification Library (Java 21)

Una librería Java robusta, agnóstica a frameworks y diseñada con **Arquitectura Hexagonal (Ports & Adapters)** para unificar el envío de notificaciones a través de múltiples canales (Email, SMS, Push) de manera transparente y resiliente.

## 🚀 Características Principales

* **Arquitectura Hexagonal:** Desacoplamiento total entre el dominio (Core) y los proveedores externos (Infraestructura).
* **Multi-Proveedor & Failover:** Estrategia de recuperación automática. Si el proveedor principal (ej. SendGrid) falla, la librería intenta automáticamente con el secundario (ej. Mailgun).
* **Resiliencia:** Sistema de reintentos configurable con *exponential backoff*.
* **Java 21 Moderno:** Uso de `Records` para inmutabilidad, `Sealed Interfaces` y `Virtual Threads` para alta concurrencia.
* **Fail-Fast:** Validaciones estrictas de dominio (Regex, E.164) en el momento de la instanciación.

---

## 🧠 Patrones de Diseño Aplicados

Este proyecto implementa múltiples patrones de diseño para garantizar mantenibilidad y escalabilidad:

1.  **Hexagonal Architecture (Ports & Adapters):**
    * Separa la lógica de negocio (`model`, `channel`) de las implementaciones externas (`provider`). Permite cambiar de Twilio a otro servicio sin tocar el núcleo.
2.  **Strategy Pattern:**
    * Los `Channels` (Email, SMS) no saben cómo enviar el mensaje, delegan esa responsabilidad a una lista de estrategias (`NotificationProvider`). Esto permite intercambiar algoritmos (proveedores) en tiempo de ejecución.
3.  **Facade Pattern:**
    * La clase `NotificationManager` actúa como una fachada que oculta la complejidad del sistema (canales, reintentos, hilos). El cliente solo interactúa con esta clase.
4.  **Builder Pattern:**
    * Utilizado en `NotificationManager` y los Records de notificaciones (`EmailNotification`) para construir objetos complejos paso a paso, garantizando que siempre estén en un estado válido.
5.  **Observer Pattern (Pub/Sub):**
    * Implementado a través de `NotificationListener`. Permite que sistemas externos se suscriban a eventos (éxito, fallo, reintento) sin acoplarse a la lógica de envío.
6.  **Failover / Chain of Responsibility (Simplificado):**
    * Dentro de cada canal, si un proveedor falla, la responsabilidad pasa automáticamente al siguiente proveedor en la lista de prioridad.

---

## 📂 Estructura del Proyecto

El proyecto está organizado como un **Monorepo** para demostrar la separación clara entre la librería (proveedor) y la aplicación demo (consumidor):

```text
/
├── src/                        # 📦 CÓDIGO FUENTE DE LA LIBRERÍA (CORE)
│   ├── main/java/org/javiermiranda/notification/
│   │   ├── NotificationManager.java  <-- Fachada Principal
│   │   ├── channel/                  <-- Lógica de Estrategia (Failover)
│   │   ├── provider/                 <-- Adaptadores (SendGrid, Twilio, etc.)
│   │   ├── spi/                      <-- Puertos (Interfaces)
│   │   └── event/                    <-- Listeners (Observer)
│
├── example/                    # 📱 EJEMPLO DE USO
│   └── demo-app/               # Aplicación cliente simulada (Consumer)
│       ├── src/main/java/.../App.java
│       └── pom.xml             # Depende de 'notification-lib'
│
├── Dockerfile                  # 🐳 Construcción Multi-Stage (Lib + Demo)
└── pom.xml                     # POM Padre/Librería
```
---

## 🛠️ Instalación Manual (Maven)

Si deseas usar la librería en un proyecto local sin Docker:

1. **Instalar en repositorio local:**
   Desde la raíz del proyecto, ejecuta:
```bash
./mvnw clean install
```


2. **Agregar dependencia en tu `pom.xml`:**
```xml
<dependency>
    <groupId>org.javiermiranda</groupId>
    <artifactId>notification-lib</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



---

## 📚 Tutorial de Uso (Paso a Paso)

El siguiente código muestra cómo integrar la librería, basado en la aplicación de demostración ubicada en `examples/demo-app`.

### 1. Configuración de Proveedores (Strategies)

Instanciamos los adaptadores específicos con sus credenciales.

```java
// Ejemplo para Email (SendGrid) y SMS (Twilio)
var sendGrid = new SendGridProvider("SG.API_KEY_SECRET");
var twilio = new TwilioProvider("AC_ACCOUNT_SID", "AUTH_TOKEN", "+1555000");
```

### 2. Creación de Canales (Channels)

Agrupamos los proveedores. El orden define la prioridad (Failover).

```java
// El canal de Email intentará usar SendGrid primero.
var emailChannel = new EmailChannel(List.of(sendGrid));

// El canal de SMS usará Twilio.
var smsChannel = new SmsChannel(List.of(twilio));
```

### 3. Construcción del Manager (Facade & Builder)

Configuramos el orquestador global.

```java
NotificationManager manager = NotificationManager.builder()
    .registerSender(emailChannel)       // Registramos capacidad de Email
    .registerSender(smsChannel)         // Registramos capacidad de SMS
    .withRetries(2)                     // Configuración global de reintentos
    .build();
```

### 4. Creación de la Notificación (Domain)

Creamos objetos inmutables y validados.

```java
var email = EmailNotification.builder()
    .recipient("usuario@empresa.com")
    .subject("Bienvenido")
    .content("Gracias por registrarte.")
    .build();
```

### 5. Envío Asíncrono

El envío utiliza **Virtual Threads** para no bloquear el hilo principal.

```java
// "Fire and Forget" con callback
manager.sendAsync(email)
    .thenRun(() -> System.out.println("✅ Notificación enviada con éxito."));
```

---

## 🐳 Ejecución con Docker (Entorno Aislado)

Este proyecto incluye un `Dockerfile` avanzado (**Multi-Stage Build**) que permite compilar la librería, compilar la app demo y ejecutarla en un entorno limpio.

**1. Construir la Imagen:**

```bash
docker build -t notification-demo .
```

**2. Ejecutar el Contenedor:**

```bash
docker run --rm --name notification-app-demo notification-demo
```

**Resultado esperado en consola:**

```text
>>> INICIANDO SISTEMA DE NOTIFICACIONES (DEMO ASÍNCRONA) <<<
[Main] Disparando notificaciones en paralelo...
[SendGrid] Conectando con API Key...
[Twilio] Preparando request POST...
✅ [Callback] El Email terminó de enviarse.
✅ [Callback] El SMS terminó de enviarse.
>>> TODOS LOS PROCESOS TERMINARON <<<
```

---

## 🧪 Testing

El proyecto cuenta con una suite de pruebas exhaustiva utilizando **JUnit 5** y **Mockito**:

* **Unit Tests:** Validación de modelos y reglas de negocio.
* **Strategy Tests:** Verificación de algoritmos de prioridad y failover.
* **Integration Mocks:** Simulación de flujo completo sin llamadas externas.

Para ejecutar los tests:

```bash
./mvnw test
```

---

**Autor:** Javier Miranda
