package org.javiermiranda.notification.model;

import lombok.Builder;
import lombok.NonNull;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Builder
public record EmailNotification(
        @NonNull String recipient,
        String subject,
        String content,
        String templateId,
        Map<String, Object> templateData,
        List<File> attachments
) implements Notification {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public EmailNotification {
        if (!EMAIL_PATTERN.matcher(recipient).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + recipient);
        }
        if ((content == null && templateId == null) || (content != null && templateId != null)) {
            throw new IllegalArgumentException("El correo debe contener 'content' o 'templateId', pero no ambos.");
        }
        attachments = (attachments == null) ? Collections.emptyList() : attachments;
        templateData = (templateData == null) ? Collections.emptyMap() : templateData;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }
}