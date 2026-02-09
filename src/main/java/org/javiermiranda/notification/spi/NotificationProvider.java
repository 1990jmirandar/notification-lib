package org.javiermiranda.notification.spi;

import org.javiermiranda.notification.model.Notification;

public interface NotificationProvider<T extends Notification> {
    void send(T notification) throws Exception;
    String getProviderName();
    default int getPriority() { return 0; }
}