package org.javiermiranda.notification.model;
import lombok.Builder;
import lombok.NonNull;
import java.util.Map;

@Builder
public record PushNotification(
        @NonNull String deviceToken,
        @NonNull String title,
        @NonNull String body,
        Map<String, String> data
) implements Notification {

    public PushNotification {
        if (deviceToken.isEmpty()){
            throw new IllegalArgumentException("El token no puede ir vacío");
        }
    }

    @Override
    public String getRecipient() {
        return deviceToken;
    }
}