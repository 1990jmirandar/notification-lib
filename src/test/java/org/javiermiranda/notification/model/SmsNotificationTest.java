package org.javiermiranda.notification.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmsNotificationTest {

    @Test
    @DisplayName("Debe validar formato E.164 (Twilio Standard)")
    void shouldValidateE164Format() {
        assertThrows(IllegalArgumentException.class, () -> new SmsNotification("09999999", "Hola"));
        assertThrows(IllegalArgumentException.class, () -> new SmsNotification("bad-number", "Hola"));
        assertDoesNotThrow(() -> new SmsNotification("+593991234567", "Hola"));
    }

    @Test
    @DisplayName("Debe validar longitud máxima del mensaje")
    void shouldValidateMessageLength() {
        String longMessage = "a".repeat(1601);
        assertThrows(IllegalArgumentException.class, () -> new SmsNotification("+123456", longMessage));
    }
}