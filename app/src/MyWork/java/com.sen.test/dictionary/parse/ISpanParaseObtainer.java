package com.sen.test.dictionary.parse;

/**
 * Created by Sen on 2015/6/17.
 */
public interface ISpanParaseObtainer<K> {

    public void setListener(K k);
    public void getParaseResult(K... k);
    public boolean isStop();

}
