package com.blxt.xml2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Element {
	public String content;
	public String dataStr;
	int rankCount = 0;
	public String lable = null;
	public String lableId = null;
	public String lableClass = null;
	private List<String> note = null;
	private Map<String, Object> datas = null;
	private String text = null;

	public List<Element> subElement = new ArrayList<Element>();

	public Element(int rankCount) {
		this.rankCount = rankCount;
		// this.indexStar = indexStar;
	};

	public Element(String content) {
		// this.content = content;
	};

	public void init() {
//		lable = getlable(dataStr);
//		datas = getDatas(dataStr);
//		getDefaultId(datas);
	}

	private String getlable(String strContent) {
		if (strContent == null) {
			return null;
		}
		strContent = strContent.trim();
		int index = strContent.indexOf(" ");
		if (strContent.startsWith("<")) {
			strContent = strContent.substring(1);
		}

		if (index < 0) {
			index = strContent.length();
			if (strContent.endsWith("/>")) {
				index -= 2;
			} else if (strContent.endsWith(">")) {
				index--;
			}
		}
		return strContent.substring(0, index).trim();
	}

	private Map<String, Object> getDatas(String content) {
		if (content == null) {
			return null;
		}
		content = content.trim();
		int index = content.indexOf(" ");
		if (index < 0) {
			return null;
		}
		int indexL = content.length() - 1;
		if (content.endsWith("/>")) {
			indexL--;
		}
		String dataStr = content.substring(index, indexL);
		Map<String, Object> datas = anakeyValue(dataStr);

		return datas;
	}

	/**
	 * 解析键值对
	 * 
	 * @param content
	 * @return
	 */
	public static Map<String, Object> anakeyValue(String content) {
		if ((content == null) || (content.length() <= 1)) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		int index = -1;
		while ((content != null) && (content.length() > 0) && ((index = content.indexOf("=")) > 0)) {
			String key = content.substring(0, index).trim();
			int indexL = content.indexOf("=", ++index);
			if (indexL < 0) {
				indexL = content.length();
			} else {
				indexL = content.lastIndexOf("\"", indexL);
				if ((indexL < 0) || (content.lastIndexOf("\"", indexL - 1) < 0)) {
					indexL = content.length();
				} else {
					indexL++;
				}
			}
			String vakue = content.substring(index, indexL).trim();

			vakue = vakue.endsWith("\"") ? vakue.substring(0, vakue.length() - 1) : vakue;
			vakue = vakue.startsWith("\"") ? vakue.substring(1, vakue.length()) : vakue;
			map.put(key, vakue);
			content = content.substring(indexL, content.length());
		}
		return map;
	}

	/**
	 * 获取默认id
	 */
	private String getDefaultId(Map<String, Object> datas) {
		if (datas != null) {
			Object oId = datas.get("id");
			if (oId != null) {
				lableId = oId.toString();
			}
			Object oClass = datas.get("class");
			if (oClass != null) {
				lableClass = oClass.toString();
			}
		}
		return null;
	}

	public String toString() {
		String str = "";
		if (this.note != null) {
			for (String s : this.note) {
				str = str + "\r\n" + s;
			}
		}
		str = str + "\r\n<" + this.lable;
		if (this.datas != null) {
			str = str + " " + dataToString();
			if ((this.text != null) || (this.subElement != null)) {
				str = str + ">";
			}
		} else {
			str += ">";
		}
		if (this.subElement != null) {
			String strSub = "";
			for (Element e : this.subElement) {
				String s = e.toString();
				strSub = strSub + s.replace("\r\n", "\r\n\t");
			}
			str += strSub;
		}
		if (this.text != null) {
			if (this.subElement != null && this.subElement.size() > 0) {
				str += "\r\n\t";
			}
			str += this.text;
		}
		if ((this.subElement == null) && (this.text == null)) {
			str = str + "/>";
		} else {
			if (this.subElement != null && this.subElement.size() > 0) {
				str += "\r\n";
			}
			str += "</" + this.lable + ">";
		}
		return str;
	}

	public String subToString() {
		String str = "";
		if (this.subElement == null) {
			return null;
		}
		for (Element e : this.subElement) {
			str = str + e.toString();
		}
		return str.trim();
	}

	public String dataToString() {
		String str = "";
		if (this.datas == null) {
			return str;
		}
		Set<Map.Entry<String, Object>> set = this.datas.entrySet();
		Iterator<Map.Entry<String, Object>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
			str = str + (String) entry.getKey() + "=\"" + entry.getValue() + "\" ";
		}
		return str.trim();
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		if(text != null) {
			text = text.trim();
		}
		this.text = text;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
		lable = getlable(dataStr);
		datas = getDatas(dataStr);
		getDefaultId(datas);
	}

}
