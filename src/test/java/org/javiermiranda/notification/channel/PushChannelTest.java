package org.javiermiranda.notification.channel;

import org.javiermiranda.notification.model.EmailNotification;
import org.javiermiranda.notification.model.PushNotification;
import org.javiermiranda.notification.provider.push.FcmProvider;
import org.javiermiranda.notification.spi.NotificationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PushChannelTest {

    @Mock NotificationProvider<PushNotification> provider;
    @Mock FcmProvider highPriorityProvider;
    @Mock FcmProvider lowPriorityProvider;
    @Test
    @DisplayName("Debe soportar solo objetos PushNotification")
    void shouldSupportCorrectType() {
        PushChannel channel = new PushChannel(List.of(provider));
        assertTrue(channel.supports(PushNotification.class));
    }

    @Test
    @DisplayName("Debe reintentar con el siguiente proveedor si el primero falla")
    void shouldFailoverIfFirstProviderFails() throws Exception {

        when(highPriorityProvider.getPriority()).thenReturn(10);
        when(lowPriorityProvider.getPriority()).thenReturn(1);


        doThrow(new RuntimeException("API Error")).when(highPriorityProvider).send(any());

        PushChannel channel = new PushChannel(List.of(highPriorityProvider,lowPriorityProvider ));
        PushNotification notification = new PushNotification("1ee14111fd1","","",null);
        channel.send(notification);

        verify(highPriorityProvider).send(notification);
        verify(lowPriorityProvider).send(notification);
    }
}