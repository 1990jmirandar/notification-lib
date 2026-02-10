# 🧠 AI-Augmented Engineering Journey

Este documento registra la evolución técnica del proyecto **Notification Library**. Documenta las decisiones arquitectónicas clave, los patrones de diseño seleccionados y cómo la colaboración Humano-IA transformó un MVP inicial en una librería robusta, resiliente y moderna.

**Metodología:**
* **Rol Humano (Javier):** Arquitecto de Software (Definición de restricciones, selección de patrones, revisión de código y estrategia de pruebas).
* **Rol IA (Gemini):** Pair Programmer (Generación de boilerplate, propuestas de implementación en Java 21, configuración de infraestructura).

---

## 1. De MVP a Arquitectura Hexagonal

### 🚧 El Desafío
Inicialmente, el proyecto requería enviar notificaciones. La solución ingenua hubiera sido acoplar el código directamente a librerías de terceros (SDKs de Twilio/SendGrid) dentro de la lógica de negocio.

### 💡 La Decisión Arquitectónica
Se decidió implementar **Arquitectura Hexagonal (Ports & Adapters)** para desacoplar el núcleo de la infraestructura.

* **Prompt Context:** *"Necesito que el núcleo de la librería no sepa qué es SendGrid o Twilio. Quiero definir interfaces claras."*
* **Evolución:**
    1.  **MVP:** Clases simples mezcladas.
    2.  **Refactor:** Creación del paquete `spi` (Service Provider Interface).
    3.  **Resultado:** El `NotificationManager` (Core) solo habla con `NotificationProvider` (Puerto), permitiendo cambiar implementaciones (Adaptadores) sin recompilar el núcleo.

---

## 2. Resiliencia y Patrones de Diseño

### 🚧 El Desafío
Un sistema de notificaciones no puede fallar silenciosamente. Si el proveedor principal cae, el mensaje debe llegar por otra vía.

### 💡 La Solución: Strategy & Chain of Responsibility
Implementamos un mecanismo de **Failover** transparente.

* **Prompt Context:** *"¿Cómo podemos diseñar un sistema que intente enviar por un canal prioritario y, si falla, cambie automáticamente al siguiente?"*
* **Patrones Aplicados:**
    * **Strategy Pattern:** Encapsulamos cada proveedor (`SendGridProvider`, `MailgunProvider`) como una estrategia intercambiable.
    * **Failover Logic:** Implementamos un bucle en `Channel.java` que actúa como una cadena de responsabilidad simplificada: `try (Provider A) -> catch -> try (Provider B)`.

---

## 3. Modernización: Java 21 & Virtual Threads

### 🚧 El Desafío
Las operaciones de notificación son intensivas en I/O (esperar respuesta de red). Usar hilos tradicionales de Java (Platform Threads) limita la escalabilidad a unos pocos miles de envíos concurrentes antes de agotar la memoria.

### 💡 La Decisión: Project Loom
Aprovechamos las características de vanguardia de Java 21.

* **Prompt Context:** *"Quiero aprovechar Java 21. ¿Cómo podemos manejar miles de envíos sin bloquear el hilo principal?"*
* **Implementación:**
    * Uso de `Executors.newVirtualThreadPerTaskExecutor()`.
    * Cambio de `Clases` a `Records` (`EmailNotification`) para garantizar inmutabilidad y reducir boilerplate.
    * Uso de `CompletableFuture` para manejo asíncrono no bloqueante.

---

## 4. Observabilidad: Patrón Observer

### 🚧 El Desafío
En la fase de demo, notamos que la lógica de impresión en consola (`System.out.println`) estaba acoplada al flujo de la aplicación (`App.java`). Esto dificultaba la integración de sistemas de logs reales o bases de datos.

### 💡 La Solución: Desacoplamiento de Eventos
* **Prompt Context:** *"El main está sucio con logs. Necesito una forma de que la librería 'avise' cuando algo pasa, sin decidir qué hacer con esa información."*
* **Evolución:**
    1.  Creación de la interfaz `NotificationListener`.
    2.  Implementación del **Observer Pattern** en `NotificationManager`.
    3.  **Resultado:** La demo implementa `AuditLogger`, demostrando que se pueden conectar múltiples sistemas de monitoreo sin tocar el código de envío.

---

## 5. Entrega y Testing: Docker Multi-Stage

### 🚧 El Desafío
Entregar una librería Java suele ser complejo para quien la recibe (instalar JDK, Maven, configurar variables). Necesitábamos un entregable "Ejecutable" y agnóstico al entorno.

### 💡 La Solución: Contenedorización Inteligente
* **Prompt Context:** *"Quiero entregar esto listo para correr. No quiero que el usuario tenga que instalar Maven."*
* **Evolución:**
    * Diseño de un `Dockerfile` con **Multi-Stage Build**.
    * **Etapa 1:** Compilación de la librería.
    * **Etapa 2:** Compilación de la Demo App usando la librería compilada.
    * **Etapa 3:** Runtime ligero (Alpine Linux) con solo el JRE.
* **Impacto:** Reducción del tamaño de la imagen de ~800MB a ~150MB y eliminación del código fuente en producción.

---

## Conclusión

Este proyecto demuestra que la IA Generativa, bajo la dirección de un Arquitecto de Software humano, permite acelerar la implementación de patrones complejos y mejores prácticas, resultando en un software de calidad industrial, testeado y documentado.