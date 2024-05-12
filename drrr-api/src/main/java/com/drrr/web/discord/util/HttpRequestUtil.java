package com.drrr.web.discord.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public class HttpRequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getHeaderMap(final HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        final Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.startsWith("Sec")) { // 'Sec'으로 시작하지 않는 헤더만 추가
                headerMap.put(headerName, request.getHeader(headerName));
            }
        }

        return toJson(headerMap);
    }

    public static String getParamMap(final HttpServletRequest request) {
        final Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, String> flatParamMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length == 1) {
                flatParamMap.put(key, values[0]);
            } else {
                flatParamMap.put(key, String.join(",", values));
            }
        }

        return toJson(flatParamMap);
    }

    public static String getBody(final HttpServletRequest request) throws IOException {
        String messageBody = "";

        if (request.getInputStream().isReady()) {
            final ServletInputStream inputStream = request.getInputStream();
            messageBody = StreamUtils.copyToString(inputStream,
                    StandardCharsets.UTF_8);
        }

        return messageBody;
    }

    private static String toJson(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error converting object to JSON", e);
            return "{}";
        }
    }

}