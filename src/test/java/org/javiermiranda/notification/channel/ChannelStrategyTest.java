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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelStrategyTest {

    @Mock NotificationProvider<EmailNotification> highPriorityProvider;
    @Mock NotificationProvider<EmailNotification> lowPriorityProvider;

    @Test
    @DisplayName("Debe ordenar proveedores por prioridad (Mayor a Menor)")
    void shouldSortProvidersByPriority() throws Exception {
        when(highPriorityProvider.getPriority()).thenReturn(100);
        when(lowPriorityProvider.getPriority()).thenReturn(1);

        EmailChannel channel = new EmailChannel(List.of(lowPriorityProvider, highPriorityProvider));

        EmailNotification notification = new EmailNotification("test@test.com", "Sub", "Body", null, null, null);

        channel.send(notification);

        InOrder inOrder = inOrder(highPriorityProvider, lowPriorityProvider);

        inOrder.verify(highPriorityProvider).send(notification);
        inOrder.verify(lowPriorityProvider, never()).send(any());
    }

    @Test
    @DisplayName("Debe reintentar con el siguiente proveedor si el primero falla")
    void shouldFailoverIfFirstProviderFails() throws Exception {

        when(highPriorityProvider.getPriority()).thenReturn(10);
        when(lowPriorityProvider.getPriority()).thenReturn(1);


        doThrow(new RuntimeException("API Error")).when(highPriorityProvider).send(any());

        EmailChannel channel = new EmailChannel(List.of(highPriorityProvider, lowPriorityProvider));
        EmailNotification notification = new EmailNotification("test@test.com", "Sub", "Body", null, null, null);

        channel.send(notification);

        verify(highPriorityProvider).send(notification);
        verify(lowPriorityProvider).send(notification);
    }
}