package com.sen.test.dictionary.parse;

/**
 * Created by Sen on 2015/6/17.
 */
public interface ICharsParaseObtainer<I, K, T, V, W> {

    public void setListener(I i);
    public void getParaseResult(K k, T t, V v, W w);
    public boolean isStop();

}
