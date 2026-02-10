package org.javiermiranda.notification.model;

import lombok.Builder;
import lombok.NonNull;

import java.util.regex.Pattern;

@Builder
public record SmsNotification(
        @NonNull String phoneNumber,
        @NonNull String message
) implements Notification {

    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    public SmsNotification {
        if (!E164_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("El número de telefono debe de estar dentro del formato E.164 (e.j., +5939999999): " + phoneNumber);
        }

        if (message.isEmpty()) {
            throw new IllegalArgumentException("SMS debe de contener un mensaje");
        }
        if (message.length() > 1600) {
            throw new IllegalArgumentException("SMS demasiado largo, límite 1600 caracteres");
        }
    }

    @Override
    public String getRecipient() {
        return phoneNumber;
    }
}