package org.javiermiranda.notification.provider.email;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.spi.NotificationProvider;

@Slf4j
public class SendGridProvider implements NotificationProvider<EmailNotification> {

    private final String apiKey;

    public SendGridProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void send(EmailNotification notification) {
        log.info("[SendGrid] Conectando con API Key: {}...", apiKey.substring(0, 3) + "***");
        log.info("[SendGrid] Enviando a: {}", notification.recipient());
        log.info("[SendGrid] Éxito.");
    }

    @Override
    public String getProviderName() { return "SendGrid"; }

    @Override
    public int getPriority() {
        return 10;
    }
}