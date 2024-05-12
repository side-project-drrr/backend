package com.drrr.web.discord.model;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class EmbedObject {
    /**
     * <b>Discord Embed Message에 들어갈 Message 내용 List</b>
     */
    private final List<Field> fields = new ArrayList<>();

    /**
     * <b>Discord Embed Message 제목</b>
     */
    private String title;

    /**
     * <b>Discord Embed Message 상세 내용</b>
     */
    private String description;

    /**
     * <b>Discord Embed Message URL 형식</b>
     */
    private String url;

    /**
     * <b>Discord Embed Message 겉 색깔</b>
     */
    private Color color;


    public String getTitle() {
        return title;
    }

    public EmbedObject setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EmbedObject setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public EmbedObject setUrl(String url) {
        this.url = url;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public EmbedObject setColor(Color color) {
        this.color = color;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public EmbedObject addField(String name, String value, boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }
}
