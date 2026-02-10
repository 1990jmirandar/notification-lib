package org.javiermiranda.notification.channel;

import org.javiermiranda.notification.model.SmsNotification;
import org.javiermiranda.notification.spi.NotificationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SmsChannelTest {

    @Mock NotificationProvider<SmsNotification> provider;

    @Test
    @DisplayName("Debe soportar solo objetos SmsNotification")
    void shouldSupportCorrectType() {
        SmsChannel channel = new SmsChannel(List.of(provider));
        assertTrue(channel.supports(SmsNotification.class));
    }

}