package com.sen.test.dictionary.parse;

/**
 * Created by Sen on 2015/6/17.
 */
public interface ICharsParaseObtainer<I, K, T, V> {

    public void setListener(V v);
    public void getParaseResult(I i, K k, T t);
    public boolean isStop();

}
