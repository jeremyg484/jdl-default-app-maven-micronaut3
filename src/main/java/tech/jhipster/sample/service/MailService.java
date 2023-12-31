package tech.jhipster.sample.service;

import io.micronaut.context.MessageSource;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tech.jhipster.sample.domain.User;
import tech.jhipster.sample.util.JHipsterProperties;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Singleton
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final Mailer mailer;

    private final MessageSource messageSource;

    private final TemplateEngine templateEngine;

    public MailService(JHipsterProperties jHipsterProperties, Mailer mailer, MessageSource messageSource, TemplateEngine templateEngine) {
        this.jHipsterProperties = jHipsterProperties;
        this.mailer = mailer;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        log.debug("Send email[html '{}'] to '{}' with subject '{}' and content={}", isHtml, to, subject, content);

        EmailPopulatingBuilder emailPopulatingBuilder = EmailBuilder
            .startingBlank()
            .to(to)
            .from(jHipsterProperties.getMail().getFrom())
            .withSubject(subject);

        if (isHtml) {
            emailPopulatingBuilder.withHTMLText(content);
        } else {
            emailPopulatingBuilder.withPlainText(content);
        }

        try {
            mailer.sendMail(emailPopulatingBuilder.buildEmail());
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, MessageSource.MessageContext.of(locale)).orElse(null);
        sendEmail(user.getEmail(), subject, content, true);
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }
}
