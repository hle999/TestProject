package com.sen.test.dictionary.search;


import com.sen.test.dictionary.io.DictFile;
import com.sen.test.dictionary.io.DictIOFile;
import com.sen.test.dictionary.io.DongManParam;
import com.sen.test.dictionary.utils.BiHuaYinBiao;
import com.sen.test.dictionary.utils.PhoneticUtils;

/**
 * Created by Administrator on 14-7-25.
 */
public class DictWordSearch {

    /* 字典类型 */
    public static final int		DICT_TYPE_ERROR		 = -1;	// 未知类型
    public static final int		DICT_TYPE_ENG		 = 0;	// 英语类型字典
    public static final int		DICT_TYPE_CHI		 = 1;	// 中文字典类型

    private DictFile mDictFile = null;

    private DictIOFile mDictIOFile = null;
    private int mAllKeyCount = 0;

    private int currentDictID=-1;

    public DictWordSearch() {
    }

//    public boolean open(int mDictId) {
//        mDictFile = new DictFile();
//        if(  mDictFile.dictFileOpen(DictFile.DICT_LOCAL_NAME[mDictId], "r") == false ) {
//            return false;
//        }
//        if( mDictFile.dictFileInitial(mDictId) == false ) {
//            return false;
//        }
//        mAllKeyCount = mDictFile.dictFileGetKeycount();
//        return true;
//    }

//    public void close() {
//        if (mDictFile != null) {
//            mDictFile.dictFileClosed();
//            mDictFile = null;
//        }
//    }

//    public DictFile getDictFile() {
//        return mDictFile;
//    }

    public boolean open(int mDictId) {
        mDictIOFile = new DictIOFile();
        if (mDictIOFile.dictFileOpen(mDictId, "r")) {
            mAllKeyCount = mDictIOFile.getAllKeycount();
        }
        return true;
    }

    public void close() {
        if (mDictIOFile != null) {
            mDictIOFile.dictFileClosed();
            mDictIOFile = null;
        }
    }

    /**
     * 保存了每本英语词典的一级缓存，当程序退出时必须清空
     */
    public static void clearDictCache() {
        DictIOFile.clearDictCache();
    }

    public DictIOFile getDictIOFile() {
        return mDictIOFile;
    }

    public int getAllKeyCount() {
        return mAllKeyCount;
    }

    public int getDictId() {
        return currentDictID;
    }

//    public byte[] getExplainByte(int wordIndex) {
//        byte[] explainData = null;
//        if (mDictFile != null) {
//            if(mDictFile.dictFileGetDictId() != DictFile.ID_DMDICT) {
//                explainData = mDictFile.loadCurExplain(wordIndex);
//            }
//        }
//        return explainData;
//    }

    /**
     * 获取生词或单词的解释内容数据
     * @param wordIndex
     * @return
     */
    public byte[] getExplainByte(int wordIndex) {
        byte[] explainData = null;
        if (mDictIOFile != null) {
            if(mDictIOFile.getDictID() != DictIOFile.ID_DMDICT) {
                explainData = mDictIOFile.loadCurExplain(wordIndex);
            }
        }
        return explainData;
    }

//    public int getExplainByteStart(byte[] explainData) {
//        int startIndex = 0;
//        if((mDictFile.dictFileGetEncryptSwitch() & 0xF) != 0) {    // 解释有加密
//            startIndex = (byte2int(explainData[1]) << 0) + (byte2int(explainData[2]) << 8);
//        }
//        return startIndex;
//    }

    /**
     * @param explainData
     * @return 数据的开始位置
     */
    public int getExplainByteStart(byte[] explainData) {
        int startIndex = 0;
        if((mDictIOFile.getEncryptSwitch() & 0xF) != 0) {    // 解释有加密
            startIndex = (byte2int(explainData[1]) << 0) + (byte2int(explainData[2]) << 8);
        }
        return startIndex;
    }

//    public int getExplainByteLength(byte[] explainData) {
//        int length = 0;
//        if (explainData != null) {
//            length = explainData.length;
//        }
//        if((mDictFile.dictFileGetEncryptSwitch() & 0xF) != 0) {    // 解释有加密
//            length = (byte2int(explainData[3]) << 0) + (byte2int(explainData[4]) << 8);
//        }
//        return length;
//    }

    /**
     * @param explainData
     * @return 数据的结束位置
     */
    public int getExplainByteLength(byte[] explainData) {
        int length = 0;
        if (explainData != null) {
            length = explainData.length;
        }
        if((mDictIOFile.getEncryptSwitch() & 0xF) != 0) {    // 解释有加密
            length = (byte2int(explainData[3]) << 0) + (byte2int(explainData[4]) << 8);
        }
        return length;
    }

//    public DongManParam getDongManParam(int wordIndex) {
//        DongManParam dongManParam = null;
//        if (mDictFile.dictFileGetDictId() == DictFile.ID_DMDICT) {
//            dongManParam = mDictFile.dictFileGetDMDictData(wordIndex);
//        }
//        return dongManParam;
//    }

    /**
     * 动漫数据
     * @param wordIndex
     * @return
     */
    public DongManParam getDongManParam(int wordIndex) {
        DongManParam dongManParam = null;
        if (mDictIOFile != null && mDictIOFile.getDictID() == DictIOFile.ID_DMDICT) {
            dongManParam = mDictIOFile.getDMDictData(wordIndex);
        }
        return dongManParam;
    }

//    public int getKeyIndex(String key) {
//        int addrNumber = 0;
//        if (key != null && key.length() != 0) {
//            /**
//             * 注意:将中式a转换为英式，再查找
//             */
//            key = PhoneticUtils.returnOldCharseqence(key);
//            addrNumber = mDictFile.dictFileGetDictDataWithCMP(key);
//            if (addrNumber == -1) {
//                addrNumber = 0;
//            }
//        }
//        return addrNumber&0x00ffffff; // 高位用于表示是否精确匹配的单词
//    }

    /**
     * 生词或单词的位置
     * @param key 该字符串必须完全匹配字典里的单词
     * @return
     */
    public int getKeyIndex(String key) {
        int index = 0;
        if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndex(key);
                if (index == -1) {
                    index = 0;
                }
            }
        }
        return index; // 高位用于表示是否精确匹配的单词
    }

//    public int getSearchKeyIndex(String key) {
//        int addrNumber = 0;
//        if (key != null && key.length() != 0) {
//            /**
//             * 注意:将中式a转换为英式，再查找
//             */
//            key = PhoneticUtils.returnOldCharseqence(key);
//            addrNumber = mDictFile.dictFileGetDictDataWithCMP(key);
//            if (addrNumber == -1) {
//                return addrNumber;
//            }
//        }
//        return addrNumber&0x00ffffff; // 高位用于表示是否精确匹配的单词
//    }

    /**
     * 搜索匹配字符的最佳单词的位置
     * @param key
     * @return
     */
    public int getSearchKeyIndex(String key) {
        int index = 0;
        if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndex(key);
                if (index == -1) {
                    return index;
                }
            }
        }
        return index; // 高位用于表示是否精确匹配的单词
    }

//    public String getKeyWord(int index) {
//        if (mDictFile != null) {
//            return mDictFile.dictFileGetDictKeyByAddressNumber(index);
//        }
//        return null;
//    }

    /**
     * 获取生词或单词
     * @param index
     * @return
     */
    public String getKeyWord(int index) {
        if (mDictIOFile != null) {
            return mDictIOFile.getKeyStringByIndex(index);
        }
        return null;
    }

    /**
     * 获取笔画字典中的生词拼音
     * @param content
     * @param start
     * @param length
     * @return 格式为:ā
     */
    public String getBihuaPhonetic(byte[] content, int start, int length) {
//        return BiHuaYinBiao.getYinBiao(content, start, length);
        return DictWordSearch.formatBihuaPinYin(BiHuaYinBiao.getYinBiao(content, start, length));
    }

    /**
     * 获取笔画字典中的生词拼音
     * @param content
     * @param start
     * @param length
     * @return 格式为:a1
     */
    public String getBihuaPhoneticWithNum(byte[] content, int start, int length) {
        return DictWordSearch.formatBihuaPinYin(BiHuaYinBiao.getYinBiao(content, start, length));
    }

    private int byte2int(byte b) {
        return (b & 0xFF);
    }

    /**
     * 判断字符串是否为全是空格
     * @param str
     * @return
     */
    public static boolean isBlankString(String str) {
        if (str.length() > 0) {
            for (int i=0;i<str.length();i++) {
                if (str.charAt(i) != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断字符串的类型(中文或英文)
     * @param s
     * @return
     */
    public static int getDictType(String s) {
        int value;

        if(s == null) {
            return DICT_TYPE_ERROR;
        }
        for(int i = 0;i < s.length();i++) {
            value = s.charAt(i);
            /**
             * 注意: 中式的a为0x058d + 0xDD00
             */
            if(value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z' || value == 0x058d + 0xDD00) {
                return DICT_TYPE_ENG;
            }else if(value >= 0x80) {
                return DICT_TYPE_CHI;
            }
        }

        return DICT_TYPE_ERROR;
    }

    /**
     * 过滤拼音中的特殊编码
     * @param str
     * @return
     */
    public static String formatBihuaPinYin(String str) {
        String nStr = "";
        int len = str.length();
        boolean hasYinBiao = false;
        int yinbiaoLevel = 0;
        for(int i=0;i < len; i++){
            char ch = str.charAt(i);
            if (ch == 0x0069) {//i编码
                ch = 'i';
            } else if (ch == 0x0075) {//u编码
                ch = 'u';
            } else if (ch == 0x00fc) {//ü编码
                ch = 'v';
            } else if (ch == 0x006f) {//o编码
                ch = 'o';
            }else if (ch == 0x0251) {//a编码
                ch = 'a';
            } else if (ch == 0x0062) {//b编码
                ch = 'b';
            } else if (!hasYinBiao) {
                for (int k=0;k<BiHuaYinBiao.YinBiaoNum.length;k++) {
                    if (ch == BiHuaYinBiao.YinBiaoNum[k]) {
                        int yIndex = k+1;
                        int intNum = yIndex/BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int deciNum = yIndex%BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int h = k/BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        h++;
                        if (!(intNum >0 && deciNum == 0)) {//判断是否整数
                            yinbiaoLevel = k%BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                            yinbiaoLevel++;
//                            ch = BiHuaYinBiao.YinBiaoNum[h*BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];

                            hasYinBiao = true;
                        }
                        ch = BiHuaYinBiao.YinBiaoNum[h*BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];
                        break;
                    }
                }
            }
            nStr += ch;
        }
        nStr += yinbiaoLevel;
        return nStr;
    }

    /**
     * 判断是否英文小写字母或阿拉伯数字
     *
     * @param c
     * @return
     */
    private static boolean isEnglishOrNumber(char c) {
        if (c >= 'a' && 'z' >= c) {
            return true;
        }else if (Character.isDigit(c)) {
            return true;
        }
        return false;
    }
}
