package org.javiermiranda.notification.provider.sms;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.spi.NotificationProvider;

@Slf4j
public class TwilioProvider implements NotificationProvider<SmsNotification> {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber; // Twilio requiere un número origen comprado

    public TwilioProvider(String accountSid, String authToken, String fromNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    @Override
    public void send(SmsNotification notification) throws Exception {
        if (accountSid == null || authToken == null) throw new IllegalStateException("Twilio credentials missing");

        log.info("[Twilio] Preparando request POST https://api.twilio.com/2010-04-01/Accounts/{}/Messages.json", accountSid);
        log.info("[Twilio] Auth: Basic ****");
        log.info("[Twilio] From: {}", fromNumber);
        log.info("[Twilio] To: {}", notification.phoneNumber());
        log.info("[Twilio] Body: {}", notification.message());
        Thread.sleep(100);

        log.info("[Twilio] Respuesta: 201 CREATED. SID: SMxxxxxxxxxxxxxxxx");
    }

    @Override
    public String getProviderName() { return "Twilio"; }

    @Override
    public int getPriority() { return 10; }
}