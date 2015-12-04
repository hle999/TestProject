package com.sen.test.dictionary.view;


import com.sen.test.dictionary.utils.PhoneticUtils;

/**
 * 字符过滤规则
 * @author Administrator
 *
 */
public class CharFilterRule {

    public static boolean filter(char value) {
        /**
         * 注意: 中式的a为0x058d + 0xDD00
         */
        return (uperEnglish(value) || lowerEnglish(value) || digitNumber(value) || value == PhoneticUtils.pin2[4]);
    }

    /**
     * 大写字母
     * @param value
     * @return
     */
    public static boolean uperEnglish(char value) {
        return value >= 'A' && value <= 'Z';
    }

    /**
     * 小写字母
     * @param value
     * @return
     */
    public static boolean lowerEnglish (char value) {
        return value >= 'a' && value <= 'z';
    }

    /**
     * 阿拉伯数字
     * @param value
     * @return
     */
    public static boolean digitNumber(char value) {
        return value >= '0' && value <= '9';
    }

    /**
     * 不可拆分的特殊字符
     * @param value
     * @return
     */
    public static boolean specialNotSeparatedSymbol(char value) {
        return (value == '-' || value == '\'' || value == '〔' || value == '—');
    }

    /**
     * 可拆分的特殊字符
     * @param value
     * @return
     */
    public static boolean specialSpiltableSymbol(char value) {
        return (value >= ' ' && value <= '@' || value >= '[' && value <= '`' || value >= '{' && value <= '~');
    }

    /**
     * 中文标点符号
     * @param value
     * @return
     */
    public static boolean chineseSymbol(char value) {
        return (value == '，' || value == '。' || value == '：');
    }
}
