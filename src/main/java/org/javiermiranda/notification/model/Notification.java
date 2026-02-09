package org.javiermiranda.notification.model;

public sealed interface Notification permits EmailNotification, SmsNotification, PushNotification {
    String getRecipient();
}