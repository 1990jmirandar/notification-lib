package org.javiermiranda.notification.event;

import org.javiermiranda.notification.model.Notification;

public interface NotificationListener {
    void onSuccess(Notification notification);
    void onFailure(Notification notification, Throwable error);
    void onRetry(Notification notification, int attempt, Throwable error);
}