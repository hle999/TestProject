package com.sen.test.dictionary.file;

import com.sen.test.dictionary.info.CharsInfo;
import com.sen.test.dictionary.parse.CharsParase;
import com.sen.test.dictionary.parse.ICharsParaseObtainer;
import com.sen.test.dictionary.widget.TextScrollView;

import java.util.List;

/**
 * Created by Sgc on 2015/6/17.
 */
public class ParaseWorker extends Thread implements ICharsParaseObtainer<TextScrollView.TextHandler, String, List<CharsInfo>, Float, Float> {

    private TextScrollView.TextHandler textHandler;
    private CharsParase charsParase;

    public ParaseWorker(CharsParase charsParase) {
        this.charsParase = charsParase;
    }

    @Override
    public void setListener(TextScrollView.TextHandler textHandler) {
        this.textHandler = textHandler;
    }

    @Override
    public void getParaseResult(String tag, List<CharsInfo> charsInfos, Float height, Float width) {
        if (textHandler != null) {
            textHandler.sendMessages(TextScrollView.TextHandler.PARARSE_CHARS,
                    tag, charsInfos, (int)height.floatValue(), (int)width.floatValue(), 0);
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
