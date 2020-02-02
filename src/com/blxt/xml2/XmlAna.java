package com.blxt.xml2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class XmlAna {

	protected HashMap<Integer, Integer> indexs = new HashMap<>();
	List<IndexType> indexTypes = new ArrayList<>();

	
	public void init(String content) {
		content = content.replaceAll("<br />", "\r\n");
		indexs.clear();
		// 寻找标签位置
		indexs = findIndex(content);
		// 预处理坐标
		indexTypes = getIndexTypes(indexs);

		Element root = null;
		// 栈处理
		Stack<Element> m_stack_Element = new Stack<Element>();
	
		int indexStar = 0;
		int indexFind = -1;
		int rankCount = 0;
	
		int indexContent = 0;
		for(int i = 0; i < indexTypes.size(); i++) {
			Integer key = indexTypes.get(i).index;
			Integer type = indexTypes.get(i).type;
			if(type == 1) { // 新的标签开始
				rankCount++;
				indexStar = key;
				// 创建一个节点
				Element  element = new Element(rankCount);
				if(root == null) { // 第一个节点为根节点
					root = element;
				}
				int _tmpIdex = content.indexOf(">", indexStar);
				String strData =  content.substring(indexStar, _tmpIdex);
				element.setDataStr(strData);
				indexContent = _tmpIdex;
				System.out.println("开始:" + element.lable);
				if(!m_stack_Element.isEmpty()) {
					m_stack_Element.lastElement().subElement.add(element);
				}
				// 压入栈
				m_stack_Element.add(element);
				
			}
			else{ // 结束一个标签
				rankCount--;
				indexFind = (Integer)key;
				int indexS = indexContent + 1;
		
				if(indexFind > indexS) {
					String _text = content.substring(indexS, indexFind);
					m_stack_Element.lastElement().setText(_text);
					indexContent += _text.length();
				}
				System.out.println("结束:" + m_stack_Element.lastElement().lable);
				
				indexContent += m_stack_Element.lastElement().lable.length() + 4;

				m_stack_Element.lastElement().init();
				// 移出栈
				m_stack_Element.pop();
			}
	
		}
		if(rankCount != 0) {
			System.out.println("解析完成,但文档结构有异常" + rankCount);
		}
		//System.out.println(root.toString());
	}
	
	
	/**
	 * 预处理坐标位置,排序
	 * @param indexs
	 * @return
	 */
	private List<IndexType> getIndexTypes(HashMap<Integer, Integer> indexs) {
		List<IndexType> indexTypes = new ArrayList<>();
		Object[] arr = indexs.keySet().toArray();
		// 排序
		Arrays.sort(arr);
		// 最后利用HashMap.get(key)得到键对应的值即可
		for (Object key : arr) {
			Integer value = indexs.get(key);
			indexTypes.add(new IndexType((Integer)key, value));
		}
		return indexTypes;
	}
	

	/**
	 * 寻找标签位置
	 * @param content
	 * @return
	 */
	private HashMap<Integer, Integer> findIndex(String content) {
		HashMap<Integer, Integer> indexs = new HashMap<>();
		int indexStar = -1;
		int indexFind = -1;
		// 寻找标签起始位置
		while ((indexFind = content.indexOf("<", indexStar)) >= 0) {
			char c = content.substring(indexFind + 1, indexFind + 2).toCharArray()[0];
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				indexs.put(indexFind, 1);
			}
			indexStar = indexFind + 1;
		}

		
		// 寻找标签结束位置
		indexStar = -1;
		indexFind = -1;
		while ((indexFind = content.indexOf("/>", indexStar)) >= 0) {
			indexs.put(indexFind, -1);
			indexStar = indexFind + 1;
		}
		indexStar = -1;
		indexFind = -1;
		while ((indexFind = content.indexOf("</", indexStar)) >= 0) {
			indexs.put(indexFind, -2);
			indexStar = indexFind + 1;
		}
		
		return indexs;
	}
	
	/**
	 * 寻找下一个起始位置
	 * @param indexTypes
	 * @param index
	 * @return
	 */
	private IndexType getLastStart(List<IndexType> indexTypes, int index) {
		for(int i = index ; i < indexTypes.size(); i++) {
			if(indexTypes.get(i).type > 0) {
				return indexTypes.get(i);
			}
		}
		return null;
	}
	
	class IndexType{
		int index;
		int type;
		
		public IndexType(int index, int type) {
			super();
			this.index = index;
			this.type = type;
		}
		
	}

}
