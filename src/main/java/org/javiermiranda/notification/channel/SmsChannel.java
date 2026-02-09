package org.javiermiranda.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.Notification;
import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.service.NotificationSender;
import org.javiermiranda.notification.spi.NotificationProvider;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class SmsChannel implements NotificationSender<SmsNotification> {

    private final List<NotificationProvider<SmsNotification>> providers;

    public SmsChannel(List<NotificationProvider<SmsNotification>> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt((NotificationProvider p) -> p.getPriority()).reversed())
                .toList();
    }

    @Override
    public void send(SmsNotification notification) {
        Throwable lastError = null;
        for (var provider : providers) {
            try {
                provider.send(notification);
                return;
            } catch (Exception e) {
                log.warn("Fallo SMS con {}: {}", provider.getProviderName(), e.getMessage());
                lastError = e;
            }
        }
        throw new RuntimeException("Fallo envío SMS en todos los proveedores", lastError);
    }

    @Override
    public boolean supports(Class<? extends Notification> clazz) {
        return SmsNotification.class.isAssignableFrom(clazz);
    }
}