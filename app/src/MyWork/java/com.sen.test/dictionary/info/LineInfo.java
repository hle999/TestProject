package com.sen.test.dictionary.info;

import java.util.ArrayList;
import java.util.List;

public class LineInfo {

    /**
     * 行纵坐标
     */
	public int y = 0;
    /**
     * 行高
     */
	public int height = 0;
    /**
     * 行左边距
     */
    public int paddingLeft=0;
    /**
     * 当前行里的字体大小
     */
	public float textSize = 0;
    /**
     * 当前行里包含的不用颜色的内容
     */
	public List<LineTextInfo> lineTextInfos= null;
    /**
     * 当前行选中字的开始位置
     */
	public int selectStart = -1;
    /**
     * 一共选了多少个字
     */
	public int selectOffset = 0;
    /**
     * 是否整行被选中
     */
    public boolean selectTotalLine = false;
	
	public void addLineTextInfo(LineTextInfo lineTextInfo) {
		if (lineTextInfos == null) {
			lineTextInfos = new ArrayList<LineTextInfo>();
		}
		lineTextInfos.add(lineTextInfo);
	}
	
	public int getLineTextArrayLength() {
		if (lineTextInfos != null) {
			int length=0;
			for (LineTextInfo lineTextInfo:lineTextInfos) {
				length += lineTextInfo.startOffset;
			}
			return length;
		}
		return 0;
	}
	
	public void clearSelect() {
		selectStart = -1;
		selectOffset = 0;
        selectTotalLine = false;
	}
	
	public void clearArray() {
		if (lineTextInfos != null) {
			for (LineTextInfo lineTextInfo:lineTextInfos) {
				lineTextInfo.charArray = null;
			}
			lineTextInfos.clear();
			lineTextInfos = null;
		}
	}
	
}
