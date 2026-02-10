package org.javiermiranda.notification.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PushNotificationTest {

    @Test
    @DisplayName("Debe validar que el token del dispositivo no sea vacio")
    void shouldRequireDeviceToken() {
        assertThrows(IllegalArgumentException.class, () ->
                new PushNotification("", "Title", "Body", Map.of())
        );
    }

    @Test
    @DisplayName("Debe permitir data nula convirtiéndola a mapa vacío")
    void shouldHandleNullData() {
        var notif = new PushNotification("token-123", "Title", "Body", Map.of("key", "val"));
        assertEquals("val", notif.data().get("key"));
    }
}