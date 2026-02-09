package org.javiermiranda.notification;

import org.javiermiranda.notification.channel.SmsChannel;
import org.javiermiranda.notification.model.SmsNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationManagerRetryTest {

    @Mock SmsChannel smsChannel;

    @Test
    @DisplayName("Manager debe reintentar 2 veces si el canal falla continuamente")
    void shouldRetryOnChannelFailure() {
        when(smsChannel.supports(SmsNotification.class)).thenReturn(true);
        doThrow(new RuntimeException("Channel Error")).when(smsChannel).send(any());
        NotificationManager manager = NotificationManager.builder()
                .registerSender(smsChannel)
                .withRetries(2)
                .build();

        SmsNotification notification = new SmsNotification("+593999999", "Test");

        CompletableFuture<Void> future = manager.sendAsync(notification);
        try { future.join(); } catch (Exception ignored) {}
        verify(smsChannel, times(3)).send(notification);
    }
}