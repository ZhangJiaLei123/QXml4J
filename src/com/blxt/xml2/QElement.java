package com.blxt.xml2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**
 * 快速xml解析工具的实体类,自动排版,可重写编辑
 * @author MI
 *
 */
public class QElement {
	protected int rankCount = 0;  				//< 当前节点层次
	protected String lable = null;				//< 节点标签
	protected String lableId = null;        	//< 节点ID
	protected String lableClass = null;     	//< 节点class
	protected String note = null;           	//< 节点注释
	protected String text = null;           	//< 节点文本
	protected Map<String, Object> datas = null; //< 节点键值对
	protected List<QElement> subElements = new ArrayList<QElement>(); // 子节点

	public QElement(int rankCount) {
		this.rankCount = rankCount;
	};

	public QElement(int rankCount, String lable) {
		this.rankCount = rankCount;
		this.lable = lable;
	};


	private String makeLable(String strContent) {
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

	private Map<String, Object> makeDatas(String content) {
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
		Map<String, Object> datas = makeValue(dataStr);

		return datas;
	}

	/**
	 * 解析键值对
	 * 
	 * @param content
	 * @return
	 */
	private Map<String, Object> makeValue(String content) {
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
	private String makeDefaultId(Map<String, Object> datas) {
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

	public void setDataStr(String dataStr) {
		lable = makeLable(dataStr);
		datas = makeDatas(dataStr);
		makeDefaultId(datas);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getRankCount() {
		return rankCount;
	}

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	public String getLableId() {
		return lableId;
	}

	public void setLableId(String lableId) {
		this.lableId = lableId;
	}

	public String getLableClass() {
		return lableClass;
	}

	public void setLableClass(String lableClass) {
		this.lableClass = lableClass;
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}

	public List<QElement> getSubElement() {
		return subElements;
	}

	public void addSubElement(QElement subElement) {
		if(this.subElements == null) {
			this.subElements = new ArrayList<QElement>();
		}
		this.subElements.add(subElement);
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
	
	public String toString() {
		String str = "";
		if (this.note != null) {
			str += "\r\n" + note;
		}
		str = str + "\r\n<" + this.lable;
		if (this.datas != null) {
			str = str + " " + dataToString();
			if ((this.text != null) || (this.subElements != null)) {
				str = str + ">";
			}
		} else {
			str += ">";
		}
		if (this.subElements != null) {
			String strSub = "";
			for (QElement e : this.subElements) {
				String s = e.toString();
				strSub = strSub + s.replace("\r\n", "\r\n\t");
			}
			str += strSub;
		}
		if (this.text != null) {
			if (this.subElements != null && this.subElements.size() > 0) {
				str += "\r\n\t";
			}
			str += this.text;
		}
		if ((this.subElements == null) && (this.text == null)) {
			str = str + "/>";
		} else {
			if (this.subElements != null && this.subElements.size() > 0) {
				str += "\r\n";
			}
			str += "</" + this.lable + ">";
		}
		return str;
	}

	private String dataToString() {
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


}
