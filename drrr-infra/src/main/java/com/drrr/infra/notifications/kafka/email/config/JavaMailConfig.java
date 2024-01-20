package com.drrr.infra.notifications.kafka.email.config;

import java.util.Objects;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @PropertySource는 기본적으로 .properties를 지원하는데 추가적인 custom 파일을 만들어서 사용하면 .yml도 가능하나 redundant 하다고 느껴서 .properties로 설정
 * application.yml에서 따로 JavaMailConfiguration 설정이 가능하나 classpath에서 username과 password를 읽지 못하는 문제로 인해서 따로 Config 빈을 만듬
 */
@PropertySource(value = {"classpath:security-storage-infra/email/google-smtp.properties"})
@RequiredArgsConstructor
@Configuration
public class JavaMailConfig {

    private final Environment env;

    @Bean
    public JavaMailSender getJavaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("mail.host"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(env.getProperty("mail.port"))));
        mailSender.setUsername(env.getProperty("mail.user.email"));
        mailSender.setPassword(env.getProperty("mail.user.password"));

        final Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", env.getProperty("mail.transport.protocol"));
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.timeout", env.getProperty("mail.smtp.timeout"));

        return mailSender;
    }
}
