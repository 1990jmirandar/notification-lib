package com.demo.app;

import org.javiermiranda.notification.event.NotificationListener;
import org.javiermiranda.notification.model.Notification;

public class AuditLogger implements NotificationListener {

    @Override
    public void onSuccess(Notification notification) {
        System.out.println("📝 [AUDIT] ÉXITO: Notificación enviada a " + notification.getRecipient());
    }

    @Override
    public void onFailure(Notification notification, Throwable error) {
        System.err.println("🚨 [AUDIT] FALLO CRÍTICO: No se pudo enviar a " + notification.getRecipient() +
                ". Error: " + error.getMessage());
    }

    @Override
    public void onRetry(Notification notification, int attempt, Throwable error) {
        System.out.println("⚠️ [AUDIT] REINTENTO #" + attempt +
                ": Falló envío a " + notification.getRecipient() +
                " (" + error.getMessage() + ")");
    }
}