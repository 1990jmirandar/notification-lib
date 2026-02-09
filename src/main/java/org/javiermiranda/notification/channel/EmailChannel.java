package org.javiermiranda.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.model.Notification;
import org.javiermiranda.notification.spi.NotificationProvider;
import org.javiermiranda.notification.service.NotificationSender;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class EmailChannel implements NotificationSender<EmailNotification> {

    private final List<NotificationProvider<EmailNotification>> providers;

    public EmailChannel(List<NotificationProvider<EmailNotification>> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt((NotificationProvider p) -> p.getPriority()).reversed())
                .toList();
    }

    @Override
    public void send(EmailNotification notification) {
        Throwable lastError = null;
        for (var provider : providers) {
            try {
                log.debug("Intentando enviar email vía {}...", provider.getProviderName());
                provider.send(notification);
                return;
            } catch (Exception e) {
                log.warn("Fallo con proveedor {}: {}. Intentando siguiente...", provider.getProviderName(), e.getMessage());
                lastError = e;
            }
        }
        throw new RuntimeException("Todos los proveedores de Email fallaron.", lastError);
    }

    @Override
    public boolean supports(Class<? extends Notification> clazz) {
        return EmailNotification.class.isAssignableFrom(clazz);
    }
}