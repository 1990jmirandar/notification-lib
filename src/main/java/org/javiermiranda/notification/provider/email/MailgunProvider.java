package org.javiermiranda.notification.provider.email;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.spi.NotificationProvider;

@Slf4j
public class MailgunProvider implements NotificationProvider<EmailNotification> {

    private final String domain;
    private final String apiKey;

    public MailgunProvider(String domain, String apiKey) {
        this.domain = domain;
        this.apiKey = apiKey;
    }

    @Override
    public void send(EmailNotification notification) {
        log.info("[Mailgun] Conectando a dominio {}...", domain);
        log.info("[Mailgun] Fallback enviando a: {}", notification.recipient());
        log.info("[Mailgun] Éxito.");
    }

    @Override
    public String getProviderName() { return "Mailgun"; }

    @Override
    public int getPriority() {
        return 1;
    }
}