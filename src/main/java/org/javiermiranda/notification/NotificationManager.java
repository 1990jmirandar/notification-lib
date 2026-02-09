package org.javiermiranda.notification;
import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.exception.NotificationException;
import org.javiermiranda.notification.model.Notification;
import org.javiermiranda.notification.event.NotificationListener;
import org.javiermiranda.notification.service.NotificationSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NotificationManager {
    private final List<NotificationSender<?>> senders;
    private final ExecutorService executor;
    private final List<NotificationListener> listeners;
    private final int maxRetries;

    private NotificationManager(List<NotificationSender<?>> senders, ExecutorService executor, List<NotificationListener> listeners, int maxRetries) {
        this.senders = senders;
        this.executor = executor;
        this.listeners = listeners;
        this.maxRetries = maxRetries;
    }

    public CompletableFuture<Void> sendAsync(Notification notification) {
        return CompletableFuture.runAsync(() -> processNotificationWithRetry(notification), executor);
    }

    @SuppressWarnings("unchecked")
    private void processNotificationWithRetry(Notification notification) {
        var senderOpt = findSender(notification);

        if (senderOpt.isEmpty()) {
            notifyFailure(notification, new UnsupportedOperationException("No hay proveedor para: " + notification.getClass().getSimpleName()));
            return;
        }

        NotificationSender<Notification> sender = (NotificationSender<Notification>) senderOpt.get();
        int attempt = 0;
        Exception lastError = null;

        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    notifyRetry(notification, attempt, lastError);
                    Thread.sleep(100 * attempt);
                }
                sender.send(notification);
                notifySuccess(notification);
                return;

            } catch (Exception e) {
                lastError = e;
                attempt++;
                log.warn("Intento {} fallido para {}: {}", attempt, notification.getRecipient(), e.getMessage());
            }
        }

        notifyFailure(notification, new NotificationException("Fallaron todos los reintentos (" + maxRetries + ")", lastError));
    }

    private void notifySuccess(Notification n) {
        listeners.forEach(l -> {
            try { l.onSuccess(n); } catch (Exception e) { log.error("Error en listener", e); }
        });
    }

    private void notifyFailure(Notification n, Throwable t) {
        listeners.forEach(l -> {
            try { l.onFailure(n, t); } catch (Exception e) { log.error("Error en listener", e); }
        });
    }

    private void notifyRetry(Notification n, int attempt, Throwable t) {
        listeners.forEach(l -> {
            try { l.onRetry(n, attempt, t); } catch (Exception e) { log.error("Error en listener", e); }
        });
    }

    private Optional<NotificationSender<?>> findSender(Notification notification) {
        return senders.stream().filter(s -> s.supports(notification.getClass())).findFirst();
    }

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private final List<NotificationSender<?>> senders = new ArrayList<>();
        private final List<NotificationListener> listeners = new ArrayList<>();
        private ExecutorService executor;
        private int maxRetries = 0;

        public Builder registerSender(NotificationSender<?> sender) {
            this.senders.add(sender);
            return this;
        }

        public Builder addListener(NotificationListener listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder withRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder withExecutor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public NotificationManager build() {
            if (executor == null) executor = Executors.newVirtualThreadPerTaskExecutor();
            return new NotificationManager(senders, executor, listeners, maxRetries);
        }
    }
}
