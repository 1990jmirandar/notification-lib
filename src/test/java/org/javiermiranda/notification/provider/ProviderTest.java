package org.javiermiranda.notification.provider;

import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.model.PushNotification;
import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.provider.email.SendGridProvider;
import org.javiermiranda.notification.provider.email.MailgunProvider;
import org.javiermiranda.notification.provider.push.FcmProvider;
import org.javiermiranda.notification.provider.sms.TwilioProvider;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProviderTest {
    @Test
    void sendGridShouldHaveCorrectMetadata() {
        var provider = new SendGridProvider("key");
        assertEquals("SendGrid", provider.getProviderName());
        assertEquals(10, provider.getPriority());
    }

    @Test
    void sendGridShouldSendPush() {
        var provider = new SendGridProvider("key");
        EmailNotification notification = new EmailNotification(
                "email@dominio.con",
                "Título Hola",
                "Cuerpo Mundo",
                null,
                null,
                null
        );
        assertDoesNotThrow(() -> provider.send(notification));
    }

    @Test
    void mailgunShouldHaveLowerPriority() {
        var provider = new MailgunProvider("domain", "key");
        assertEquals("Mailgun", provider.getProviderName());
        assertEquals(1, provider.getPriority()); // Backup
    }

    @Test
    void sendMailgunShouldSendPush() {
        var provider = new MailgunProvider("domain","key");
        EmailNotification notification = new EmailNotification(
                "email@dominio.con",
                "Título Hola",
                "Cuerpo Mundo",
                null,
                null,
                null
        );
        assertDoesNotThrow(() -> provider.send(notification));
    }

    @Test
    void twilioShouldValidateCredentials() {
        var provider = new TwilioProvider(null, null, null);
        assertThrows(IllegalStateException.class, () -> provider.send(null));
    }


    @Test
    void sendTwilioShouldSendPush() {
        var provider = new TwilioProvider("account","token","+59399875421");
        SmsNotification notification = new SmsNotification(
                "+59399875421",
                "Título Hola"
        );
        assertDoesNotThrow(() -> provider.send(notification));
    }

    @Test
    void sendFCMShouldHaveCorrectMetadata() {
        var provider = new FcmProvider("", "");
        assertEquals("Firebase Cloud Messaging", provider.getProviderName());
        assertEquals(100, provider.getPriority());
    }

    @Test
    void fcmValidateCredentials() {
        var provider = new FcmProvider(null, null);
        assertThrows(IllegalStateException.class, () -> provider.send(null));
    }

    @Test
    void sendFcmShouldSendPush() {
        var provider = new FcmProvider("project-123", "sa.json");
        PushNotification notification = new PushNotification(
                "device-token-123",
                "Título Hola",
                "Cuerpo Mundo",
                Map.of("click_action", "OPEN_APP")
        );
        assertDoesNotThrow(() -> provider.send(notification));
    }
}