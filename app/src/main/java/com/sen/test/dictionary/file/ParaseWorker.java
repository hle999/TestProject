package com.sen.test.dictionary.file;

import com.sen.test.dictionary.info.CharsInfo;
import com.sen.test.dictionary.parse.CharsParase;
import com.sen.test.dictionary.parse.ICharsParaseObtainer;
import com.sen.test.dictionary.widget.TextScrollView;

import java.util.List;

/**
 * Created by Sgc on 2015/6/17.
 */
public class ParaseWorker extends Thread implements ICharsParaseObtainer<Object> {

    private TextScrollView.TextHandler textHandler;
    private CharsParase charsParase;

    public ParaseWorker(CharsParase charsParase) {
        this.charsParase = charsParase;
    }

    public CharsParase getParase() {
        return charsParase;
    }

    @Override
    public void setListener(Object obj) {
        if (obj instanceof TextScrollView.TextHandler) {
            this.textHandler = (TextScrollView.TextHandler)obj;
        }
    }

    @Override
    public void getParaseResult(Object... objects) {
        if (textHandler != null && objects != null) {
            if (objects.length == 4 && objects[0] instanceof String
                    && objects[2] instanceof Float && objects[3] instanceof Float) {
                textHandler.sendMessages(TextScrollView.TextHandler.PARARSE_CHARS,
                        (String)objects[0], objects[1], ((Float)objects[2]).intValue(), ((Float)objects[3]).intValue(), 0);
            }
        }
    }

    @Override
    public boolean isStop() {
        return false;
    }

    @Override
    public void run() {
        if (charsParase != null) {
            charsParase.setCharsParaseObtainer(this);
            charsParase.start();
            charsParase.setCharsParaseObtainer(null);
        }
        textHandler = null;
    }
}
