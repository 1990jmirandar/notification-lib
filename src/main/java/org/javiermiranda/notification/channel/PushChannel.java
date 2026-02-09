package org.javiermiranda.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.Notification;
import org.javiermiranda.notification.model.PushNotification;
import org.javiermiranda.notification.service.NotificationSender;
import org.javiermiranda.notification.spi.NotificationProvider;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class PushChannel implements NotificationSender<PushNotification> {
    private final List<NotificationProvider<PushNotification>> providers;

    public PushChannel(List<NotificationProvider<PushNotification>> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt((NotificationProvider p) -> p.getPriority()).reversed())
                .toList();
    }

    @Override
    public void send(PushNotification notification) {
        Throwable lastError = null;
        for (var provider : providers) {
            try {
                provider.send(notification);
                return;
            } catch (Exception e) {
                log.warn("Fallo Push con {}: {}", provider.getProviderName(), e.getMessage());
                lastError = e;
            }
        }
        throw new RuntimeException("Fallo Push en todos los proveedores", lastError);
    }

    @Override
    public boolean supports(Class<? extends Notification> clazz) {
        return PushNotification.class.isAssignableFrom(clazz);
    }
}