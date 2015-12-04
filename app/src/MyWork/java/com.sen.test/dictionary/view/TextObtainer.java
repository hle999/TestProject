package com.sen.test.dictionary.view;

public interface TextObtainer<T> {

    public static final int ANALY_TEXT = 0x01;
    public static final int ANALY_NEW_LINE = 0x02;  //换行
    public static final int ANALY_FINISH = 0x03;    //完成

    public void getCharArray(int state, char[] charArray, int color);
    public void setHandler(T t);

}
