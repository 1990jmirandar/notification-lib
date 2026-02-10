package org.javiermiranda.notification.channel;

import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.spi.NotificationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailChannelTest {

    @Mock NotificationProvider<EmailNotification> primaryProvider;
    @Mock NotificationProvider<EmailNotification> backupProvider;

    @Test
    @DisplayName("Failover: Debe usar backup si el primario falla")
    void shouldFailoverToBackup() throws Exception {
        when(primaryProvider.getPriority()).thenReturn(10);
        doThrow(new RuntimeException("API Error")).when(primaryProvider).send(any());

        when(backupProvider.getPriority()).thenReturn(1);

        EmailChannel channel = new EmailChannel(List.of(backupProvider, primaryProvider));
        EmailNotification notification = new EmailNotification("test@test.com", "Sub", "Body", null, null, null);
        channel.send(notification);
        verify(primaryProvider).send(any());
        verify(backupProvider).send(any());
    }

    @Test
    @DisplayName("Prioridad: Debe llamar primero al proveedor con mayor prioridad")
    void shouldRespectPriority() throws Exception {
        when(primaryProvider.getPriority()).thenReturn(100);
        when(backupProvider.getPriority()).thenReturn(1);

        EmailChannel channel = new EmailChannel(List.of(backupProvider, primaryProvider));
        EmailNotification notification = new EmailNotification("test@test.com", "Sub", "Body", null, null, null);

        channel.send(notification);
        InOrder inOrder = inOrder(primaryProvider, backupProvider);
        inOrder.verify(primaryProvider).send(any());
        inOrder.verify(backupProvider, never()).send(any());
    }
}