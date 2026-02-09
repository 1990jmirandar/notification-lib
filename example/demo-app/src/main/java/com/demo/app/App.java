package com.demo.app;

import org.javiermiranda.notification.NotificationManager;
import org.javiermiranda.notification.channel.EmailChannel;
import org.javiermiranda.notification.channel.SmsChannel;
import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.provider.email.SendGridProvider;
import org.javiermiranda.notification.provider.sms.TwilioProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SISTEMA DE NOTIFICACIONES (DEMO ASÍNCRONA) <<<");

        // 1. Configurar
        var sendGrid = new SendGridProvider("SG.FAKE_KEY");
        var twilio = new TwilioProvider("AC_FAKE", "AUTH_TOKEN", "+1555000");

        var emailChannel = new EmailChannel(List.of(sendGrid));
        var smsChannel = new SmsChannel(List.of(twilio));

        NotificationManager manager = NotificationManager.builder()
                .registerSender(emailChannel)
                .registerSender(smsChannel)
                .withRetries(2)
                .addListener(new AuditLogger())
                .build();

        // 2. Preparar Notificaciones
        EmailNotification email = EmailNotification.builder()
                .recipient("cliente@empresa.com")
                .subject("Bienvenido")
                .content("Gracias por registrarte.")
                .build();

        SmsNotification sms = SmsNotification.builder()
                .phoneNumber("+593991234567")
                .message("Tu código es 8842")
                .build();

        System.out.println("[Main] Disparando notificaciones en paralelo...");

        // 3. ENVÍO ASÍNCRONO REAL (Fire & Forget)


        CompletableFuture<Void> futureEmail = manager.sendAsync(email);

        CompletableFuture<Void> futureSms = manager.sendAsync(sms);

        System.out.println("[Main] Las tareas ya están corriendo en background. Yo sigo libre.");

        // 4. Sincronización Final (Solo para que la consola no se cierre)
        // Esperamos a que AMBOS terminen antes de matar el programa.
        CompletableFuture.allOf(futureEmail, futureSms).join();

        System.out.println(">>> TODOS LOS PROCESOS TERMINARON <<<");
    }
}