package com.sen.test.dictionary.file;

import com.sen.test.dictionary.parse.SpanParase;
import com.sen.test.dictionary.parse.ISpanParaseObtainer;
import com.sen.test.dictionary.widget.TextScrollView;

/**
 * Created by Sgc on 2015/6/17.
 */
public class ParaseWorker extends Thread implements ISpanParaseObtainer<Object> {

    private TextScrollView.TextHandler textHandler;
    private SpanParase spanParase;

    public ParaseWorker(SpanParase spanParase) {
        this.spanParase = spanParase;
    }

    public SpanParase getParase() {
        return spanParase;
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
        if (spanParase != null) {
            spanParase.setCharsParaseObtainer(this);
            spanParase.start();
            spanParase.setCharsParaseObtainer(null);
        }
        textHandler = null;
    }
}
