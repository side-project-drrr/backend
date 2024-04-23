package com.drrr.property;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;


// prefix
@ConfigurationProperties("extract")
@ConfigurationPropertiesBinding
public record ExtractCategoryProperty(
        String entryPoint
) {


    public String createUri(String path) {
        return entryPoint + path;
    }

}
