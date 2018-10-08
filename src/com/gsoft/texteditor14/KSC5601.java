package com.gsoft.texteditor14;

import com.gsoft.common.Util.ArrayListChar;

public class KSC5601 {
	String srcText;
	String dstText;
	
	public KSC5601(String srcText) {
		this.srcText = srcText;
		int i;
		char c;
		char oldC=0;
		ArrayListChar list = new ArrayListChar(1000);
		list.resizeInc = 500;
		list.setText(srcText);
		for (i=0; i<list.count; i++) {
			c = list.getItem(i);
			if ('가'<=c && c<='힣') {
				String message = "new Mapping('";
				list.insert(i, message);
				i += message.length();
			}
			if ('가'<=oldC && oldC<='힣') {
				String message = "',";
				list.insert(i, message);
				i += message.length();
			}
			if (oldC=='0' && c=='x') {
				String message = "),";
				list.insert(i+5, message);
				i += 5 + message.length();
			}
			oldC = c;
		}
		
		this.dstText = new String(list.getItems());
	}

}
