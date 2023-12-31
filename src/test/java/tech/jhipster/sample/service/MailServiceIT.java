package tech.jhipster.sample.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.thymeleaf.TemplateEngine;
import tech.jhipster.sample.config.Constants;
import tech.jhipster.sample.domain.User;
import tech.jhipster.sample.util.JHipsterProperties;

/**
 * Integration tests for {@link MailService}.
 */
@MicronautTest(transactional = false)
@Property(name = "micronaut.security.enabled", value = "false")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MailServiceIT {

    private static final String[] languages = {
        "en",
        "fr",
        // jhipster-needle-i18n-language-constant - JHipster will add/remove languages in this array
    };
    private static final Pattern PATTERN_LOCALE_3 = Pattern.compile("([a-z]{2})-([a-zA-Z]{4})-([a-z]{2})");
    private static final Pattern PATTERN_LOCALE_2 = Pattern.compile("([a-z]{2})-([a-z]{2})");

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private TemplateEngine templateEngine;

    @Inject
    private MessageSource messageSource;

    @Spy
    private Mailer mailer;

    @Captor
    private ArgumentCaptor<Email> messageCaptor;

    private MailService mailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mailer.sendMail(any(Email.class))).thenReturn(new CompletableFuture<>());
        mailService = new MailService(jHipsterProperties, mailer, messageSource, templateEngine);
    }

    @Test
    void testSendEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false);
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo("john.doe@example.com");
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getPlainText()).isInstanceOf(String.class);
        assertThat(message.getPlainText()).hasToString("testContent");
    }

    @Test
    void testSendHtmlEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", true);
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo("john.doe@example.com");
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getHTMLText()).isInstanceOf(String.class);
        assertThat(message.getHTMLText()).hasToString("testContent");
    }

    @Test
    void testSendEmailFromTemplate() throws Exception {
        User user = new User();
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        user.setLangKey("en");
        mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title");
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("test title");
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo("john.doe@example.com");
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getHTMLText()).isEqualToNormalizingNewlines("<html>test title, http://127.0.0.1:8080, john</html>\n");
    }

    @Test
    void testSendActivationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        mailService.sendActivationEmail(user);
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo(user.getEmail());
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getHTMLText()).isNotEmpty();
    }

    @Test
    void testCreationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        mailService.sendCreationEmail(user);
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo(user.getEmail());
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getHTMLText()).isNotEmpty();
    }

    @Test
    void testSendPasswordResetMail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        mailService.sendCreationEmail(user);
        verify(mailer).sendMail(messageCaptor.capture());
        Email message = messageCaptor.getValue();
        assertThat(message.getRecipients().get(0).getAddress()).isEqualTo(user.getEmail());
        assertThat(message.getFromRecipient().getAddress()).isEqualTo(jHipsterProperties.getMail().getFrom());
        assertThat(message.getHTMLText()).isNotEmpty();
    }

    @Test
    public void testSendLocalizedEmailForAllSupportedLanguages() throws Exception {
        User user = new User();
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        for (String langKey : languages) {
            user.setLangKey(langKey);
            mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title");
            verify(mailer, atLeastOnce()).sendMail(messageCaptor.capture());
            Email message = messageCaptor.getValue();

            String propertyFilePath = "i18n/messages_" + getJavaLocale(langKey) + ".properties";
            URL resource = this.getClass().getClassLoader().getResource(propertyFilePath);
            File file = new File(new URI(resource.getFile()).getPath());
            Properties properties = new Properties();
            properties.load(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

            String emailTitle = (String) properties.get("email.test.title");
            assertThat(message.getSubject()).isEqualTo(emailTitle);
            assertThat(message.getHTMLText())
                .isEqualToNormalizingNewlines("<html>" + emailTitle + ", http://127.0.0.1:8080, john</html>\n");
        }
    }

    /**
     * Convert a lang key to the Java locale.
     */
    private String getJavaLocale(String langKey) {
        String javaLangKey = langKey;
        Matcher matcher2 = PATTERN_LOCALE_2.matcher(langKey);
        if (matcher2.matches()) {
            javaLangKey = matcher2.group(1) + "_" + matcher2.group(2).toUpperCase();
        }
        Matcher matcher3 = PATTERN_LOCALE_3.matcher(langKey);
        if (matcher3.matches()) {
            javaLangKey = matcher3.group(1) + "_" + matcher3.group(2) + "_" + matcher3.group(3).toUpperCase();
        }
        return javaLangKey;
    }
}
