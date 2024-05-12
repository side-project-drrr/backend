package com.drrr.web.discord;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.web.discord.model.EmbedObject;
import com.drrr.web.discord.model.Field;
import com.drrr.web.discord.model.JsonObject;
import com.drrr.web.discord.util.ApiCallUtil;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebHook {

    private final String urlString;
    private final List<EmbedObject> embeds = new ArrayList<>();
    private String username;
    private String avatarUrl;
    private boolean tts;

    public DiscordWebHook(final String urlString, final String username, final String avatarUrl, final boolean tts) {
        this.urlString = urlString;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.tts = tts;
    }

    public void addEmbed(final EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws IOException {
        if (this.embeds.isEmpty()) {
            throw DomainExceptionCode.DISCORD_ALARM_TRANSFER_FAILED.newInstance();
        }

        ApiCallUtil.callDiscordAppenderPostAPI(
                this.urlString,
                createDiscordEmbedObject(this.embeds, initializerDiscordSendForJsonObject(new JsonObject())
                ));
    }

    private JsonObject initializerDiscordSendForJsonObject(final JsonObject json) {
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);
        return json;
    }

    private JsonObject createDiscordEmbedObject(final List<EmbedObject> embeds, final JsonObject json) {
        if (embeds.isEmpty()) {
            throw DomainExceptionCode.DISCORD_ALARM_TRANSFER_FAILED.newInstance();
        }

        List<JsonObject> embedObjects = new ArrayList<>();

        for (EmbedObject embed : embeds) {
            JsonObject jsonEmbed = new JsonObject();

            jsonEmbed.put("title", embed.getTitle());

            jsonEmbed.put("description", StringEscapeUtils.escapeJson(embed.getDescription()));
            jsonEmbed.put("url", embed.getUrl());

            processDiscordEmbedColor(embed, jsonEmbed);
            processDiscordEmbedMessageFields(embed.getFields(), jsonEmbed);

            embedObjects.add(jsonEmbed);
        }
        json.put("embeds", embedObjects.toArray());

        return json;
    }

    private void processDiscordEmbedColor(final EmbedObject embed, JsonObject jsonEmbed) {
        if (embed.getColor() != null) {
            Color color = embed.getColor();
            int rgb = color.getRed();
            rgb = (rgb << 8) + color.getGreen();
            rgb = (rgb << 8) + color.getBlue();

            jsonEmbed.put("color", rgb);
        }
    }

    private void processDiscordEmbedMessageFields(final List<Field> fields, JsonObject jsonEmbed) {
        List<JsonObject> jsonFields = new ArrayList<>();

        for (Field field : fields) {
            JsonObject jsonField = new JsonObject();

            jsonField.put("name", field.name());
            jsonField.put("value", field.value());
            jsonField.put("inline", field.inline());

            jsonFields.add(jsonField);
        }

        jsonEmbed.put("fields", jsonFields.toArray());
    }
}

