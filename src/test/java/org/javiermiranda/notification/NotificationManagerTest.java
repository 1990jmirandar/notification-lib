package org.javiermiranda.notification;

import org.javiermiranda.notification.channel.SmsChannel;
import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.provider.sms.TwilioProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationManagerTest {
    @Mock
    TwilioProvider twilioMock;
    @Test
    @DisplayName("Integración: Manager debe enrutar SMS a Twilio")
    void shouldRouteSmsToTwilio() throws Exception {
        SmsChannel smsChannel = new SmsChannel(List.of(twilioMock));

        NotificationManager manager = NotificationManager.builder()
                .registerSender(smsChannel)
                .build();

        SmsNotification notification = new SmsNotification("+593999999", "Hola Mundo");

        CompletableFuture<Void> future = manager.sendAsync(notification);
        future.join();
        verify(twilioMock, times(1)).send(any(SmsNotification.class));
    }
}