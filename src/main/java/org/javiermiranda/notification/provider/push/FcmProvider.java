package org.javiermiranda.notification.provider.push;

import lombok.extern.slf4j.Slf4j;
import org.javiermiranda.notification.model.PushNotification;
import org.javiermiranda.notification.spi.NotificationProvider;

@Slf4j
public class FcmProvider implements NotificationProvider<PushNotification> {

    private final String projectId;
    private final String serviceAccountPath;

    public FcmProvider(String projectId, String serviceAccountPath) {
        this.projectId = projectId;
        this.serviceAccountPath = serviceAccountPath;
    }

    @Override
    public void send(PushNotification notification) throws Exception {
        if (projectId == null || serviceAccountPath == null) throw new IllegalStateException("Firebase Cloud Messaging credentials missing");
        log.info("[FCM] Autenticando con Service Account: {}", serviceAccountPath);
        log.info("[FCM] Enviando a proyecto: projects/{}/messages:send", projectId);
        log.info("[FCM] Payload: { token: {}, notification: { title: '{}', body: '{}' }, data: {} }",
                notification.deviceToken(), notification.title(), notification.body(), notification.data());
        log.info("[FCM] Respuesta: 200 OK. name: projects/{}/messages/0:123456789abcdef", projectId);
    }

    @Override
    public String getProviderName() { return "Firebase Cloud Messaging"; }

    @Override
    public int getPriority() {
        return 100;
    }
}