package org.javiermiranda.notification.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationTest {

    @ParameterizedTest
    @ValueSource(strings = {"bad-email", "user@", "@domain.com", "user@domain"})
    @DisplayName("Debe lanzar excepción si el email tiene formato inválido")
    void shouldRejectInvalidEmails(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () ->
                new EmailNotification(invalidEmail, "Subject", "Body", null, null, null)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si falta contenido Y template")
    void shouldRejectEmptyContentAndTemplate() {
        assertThrows(IllegalArgumentException.class, () ->
                new EmailNotification("valid@test.com", null, null, null, null, null)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si tiene contenido Y template (Ambigüedad)")
    void shouldRejectBothContentAndTemplate() {
        assertThrows(IllegalArgumentException.class, () ->
                new EmailNotification("valid@test.com", "Subject", "Body", "tpl-123", null, null)
        );
    }

    @Test
    @DisplayName("Debe crear notificación válida correctamente")
    void shouldCreateValidNotification() {
        assertDoesNotThrow(() ->
                new EmailNotification("valid@test.com", "Subject", "Body", null, null, null)
        );
    }
}