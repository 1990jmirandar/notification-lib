package org.javiermiranda.notification.service;

import org.javiermiranda.notification.model.Notification;

public interface NotificationSender<T extends Notification> {
    void send(T notification);
    boolean supports(Class<? extends Notification> clazz);
}