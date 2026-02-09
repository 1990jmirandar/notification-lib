package org.javiermiranda.notification.provider;

import org.javiermiranda.notification.provider.email.SendGridProvider;
import org.javiermiranda.notification.provider.email.MailgunProvider;
import org.javiermiranda.notification.provider.sms.TwilioProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderTest {

    @Test
    void sendGridShouldHaveCorrectMetadata() {
        var provider = new SendGridProvider("key");
        assertEquals("SendGrid", provider.getProviderName());
        assertEquals(10, provider.getPriority());
    }

    @Test
    void mailgunShouldHaveLowerPriority() {
        var provider = new MailgunProvider("domain", "key");
        assertEquals("Mailgun", provider.getProviderName());
        assertEquals(1, provider.getPriority()); // Backup
    }

    @Test
    void twilioShouldValidateCredentials() {
        var provider = new TwilioProvider(null, null, null);
        assertThrows(IllegalStateException.class, () -> provider.send(null));
    }
}