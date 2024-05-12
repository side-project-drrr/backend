package com.drrr.web.discord;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.web.discord.constant.DiscordConstants;
import com.drrr.web.discord.model.EmbedObject;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class DiscordAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String username;
    private String avatarUrl;

    private static Color getLevelColor(final ILoggingEvent eventObject) {
        final String level = eventObject.getLevel().levelStr;
        if (level.equals("WARN")) {
            return Color.yellow;
        } else if (level.equals("ERROR")) {
            return Color.red;
        }

        return Color.blue;
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        final Map<String, String> mdcPropertyMap = eventObject.getMDCPropertyMap();
        final DiscordWebHook discordWebhook = new DiscordWebHook(mdcPropertyMap.get(DiscordConstants.WEBHOOK_URL),
                username, avatarUrl, false);
        final Color messageColor = getLevelColor(eventObject);

        final String level = eventObject.getLevel().levelStr;
        String exceptionBrief = "";
        String exceptionDetail = "";
        final IThrowableProxy throwable = eventObject.getThrowableProxy();
        log.info("{}", eventObject.getMessage());

        if (throwable != null) {
            exceptionBrief = throwable.getClassName() + ": " + throwable.getMessage();
        }

        if (exceptionBrief.equals("")) {
            exceptionBrief = DiscordConstants.EXCEPTION_NOT_FOUND;
        }
        System.out.println("$$$$$$$$$$$$$$$$$ DIS CORD $$$$$$$$$$$$$$");
        System.out.println("mdcPropertyMap REQUEST URI" + mdcPropertyMap.get(DiscordConstants.REQUEST_URI));
        System.out.println("mdcPropertyMap IP ADDRESS" + mdcPropertyMap.get(DiscordConstants.IP_ADDRESS));
        System.out.println("mdcPropertyMap HEADERS" + mdcPropertyMap.get(DiscordConstants.HEADERS));
        System.out.println("mdcPropertyMap PARAMS" + mdcPropertyMap.get(DiscordConstants.PARAMS));
        System.out.println("mdcPropertyMap BODY" + mdcPropertyMap.get(DiscordConstants.BODY));
        System.out.println("$$$$$$$$$$$$$$$$$ DIS CORD $$$$$$$$$$$$$$");

        discordWebhook.addEmbed(new EmbedObject()
                .setTitle("[" + level + " - " + DiscordConstants.BRIEF_EXCEPTION_MDC + "]")
                .setColor(messageColor)
                .setDescription(exceptionBrief)
                .addField("[" + DiscordConstants.EXCEPTION_LEVEL_MDC + "]",
                        StringEscapeUtils.escapeJson(level),
                        true)
                .addField("[" + DiscordConstants.ERROR_OCCUR_TIME_MDC + "]",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        false)
                .addField(
                        "[" + DiscordConstants.REQUEST_URI_MDC + "]",
                        StringEscapeUtils.escapeJson(mdcPropertyMap.get(DiscordConstants.REQUEST_URI)),
                        false)
                .addField(
                        "[" + DiscordConstants.USER_IP_MDC + "]",
                        StringEscapeUtils.escapeJson(mdcPropertyMap.get(DiscordConstants.IP_ADDRESS)),
                        false)
                .addField(
                        "[" + DiscordConstants.HEADER_MAP_MDC + "]",
                        StringEscapeUtils.escapeJson(
                                mdcPropertyMap.get(DiscordConstants.HEADERS).replaceAll("[\\{\\{\\}]", "")),
                        true)
                .addField(
                        "[" + DiscordConstants.PARAMETER_MAP_MDC + "]",
                        StringEscapeUtils.escapeJson(
                                mdcPropertyMap.get(DiscordConstants.PARAMS).replaceAll("[\\{\\{\\}]", "")),
                        false)
                .addField("[" + DiscordConstants.BODY_MDC + "]",
                        StringEscapeUtils.escapeJson(mdcPropertyMap.get(DiscordConstants.BODY)),
                        false)
        );

        if (throwable != null) {
            exceptionDetail = ThrowableProxyUtil.asString(throwable);

            final String exception = exceptionDetail.substring(0, 1000);
            discordWebhook.addEmbed(
                    new EmbedObject()
                            .setTitle("[" + DiscordConstants.EXCEPTION_DETAIL_MDC + "]")
                            .setColor(messageColor)
                            .setDescription(exception)
            );
        }

        try {
            discordWebhook.execute();
        } catch (IOException ioException) {
            throw DomainExceptionCode.DISCORD_ALARM_TRANSFER_FAILED.newInstance();
        }
    }


}
