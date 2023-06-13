package com.tencent.devops.common.service.utils;

import com.tencent.devops.common.service.aop.AbstractI18NResponseAspect;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Slf4j
public class I18NUtils {

    private static final Locale ZH_CN = new Locale("zh", "CN");
    private static final Locale EN_US = new Locale("en", "US");
    private static final String I18N_ERROR_MESSAGE = "[I18N_ERR]";

    /**
     * 获取国际化信息
     *
     * @param resourceCode
     * @return
     */
    public static String getMessage(String resourceCode) {
        return getMessage(resourceCode, null);
    }

    /**
     * 获取国际化信息
     *
     * @param resourceCode
     * @param locale
     * @return
     */
    public static String getMessage(String resourceCode, Locale locale) {
        try {
            if (locale == null) {
                locale = AbstractI18NResponseAspect.getLocale();
            }

            ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/message", locale);

            return resourceBundle.getString(resourceCode);
        } catch (Throwable t) {
            log.error("i18n get message error, resource code: {}, locale: {}", resourceCode, locale, t);

            return I18N_ERROR_MESSAGE;
        }
    }

    /**
     * 获取国际化信息
     *
     * @param resourceCode
     * @param params
     * @return
     */
    public static String getMessageWithParams(String resourceCode, String[] params) {
        if (params == null) {
            params = new String[0];
        }

        String message = getMessage(resourceCode);
        if (ObjectUtils.isEmpty(message) || message.equals(I18N_ERROR_MESSAGE)) {
            return I18N_ERROR_MESSAGE;
        }

        try {
            return MessageFormat.format(message, params);
        } catch (Throwable t) {
            log.error("i18n format message error, code: {}, params: {}", resourceCode, params, t);

            return I18N_ERROR_MESSAGE;
        }
    }

    /**
     * 获取国际化信息
     *
     * @param resourceCode
     * @param params
     * @param defaultMessage
     * @return
     */
    public static String getMessageWithParams(String resourceCode, String[] params, String defaultMessage) {
        String message = getMessageWithParams(resourceCode, params);

        if (I18N_ERROR_MESSAGE.equals(message)) {
            return defaultMessage;
        }

        return message;
    }

    public static ArrayList<String> getAllLocaleMessage(String resourceCode) {
        return new ArrayList<String>() {{
            add(getMessage(resourceCode, ZH_CN));
            add(getMessage(resourceCode, EN_US));
        }};
    }
}
