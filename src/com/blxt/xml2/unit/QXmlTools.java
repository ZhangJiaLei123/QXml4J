package com.blxt.xml2.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.blxt.xml2.QElement;


/**
 * 快速xml解析工具,不支持但标签,如<br />。标签必须成对存在，否则会解析出错
 * @author MI
 *
 */
public class QXmlTools{
	public static QElement init(String content) {
		// 预处理源码,排除不兼容的
		content = content.replaceAll("<br />", "\r\n");
		// 寻找标签位置
		HashMap<Integer, Integer> indexs = findIndex(content);
		// 预处理坐标
		List<IndexType> indexTypes = getIndexTypes(indexs);

		// 栈处理
		Stack<QElement> m_stack_Element = new Stack<QElement>();
		// 默认根节点
		QElement root = new QElement(0, "root");
		m_stack_Element.add(root);
		
		int rankCount = 0; // 节点处理计数
	
		int indexContent = 0;
		for(int i = 0; i < indexTypes.size(); i++) {
			Integer key = indexTypes.get(i).index;
			Integer type = indexTypes.get(i).type;
			if(type > 0) { // 新的标签开始
				int indexStar = key;
				if(type == 1) {// 创建一个节点
					rankCount++;
					QElement  element = new QElement(rankCount);
					if(root == null) { // 第一个节点为根节点
						root = element;
					}
					// 获取标签内数据 如   <meta charset="utf-8"> 123456</meta>
					int _tmpIdex = content.indexOf(">", indexStar);
					String strData =  content.substring(indexStar, _tmpIdex);
					element.setDataStr(strData);
					indexContent = _tmpIdex;
					//System.out.println("开始:" + element.lable);
					if(!m_stack_Element.isEmpty()) {
						m_stack_Element.lastElement().addSubElement(element);
					}
					// 压入栈
					m_stack_Element.add(element);
				}
				else { // 注释
					int _tmpIdex = content.indexOf(">", indexStar);
					String note =  content.substring(indexStar, _tmpIdex + 1);
					m_stack_Element.lastElement().setNote(note);
				}
				
			}
			else{ // 结束一个标签
				rankCount--;
				int indexFind = (Integer)key;
				int indexS = indexContent + 1;
		
				if(indexFind > indexS) {
					String _text = content.substring(indexS, indexFind);
					m_stack_Element.lastElement().setText(_text);
					indexContent += _text.length();
				}
				//System.out.println("结束:" + m_stack_Element.lastElement().lable);
				indexContent += m_stack_Element.lastElement().getLable().length() + 4;
				// 移出栈
				m_stack_Element.pop();
			}
	
		}
		if(rankCount != 0) {
			System.err.println("解析完成,但文档结构有异常" + rankCount);
		}
		System.out.println(root.getSubElement().get(0).toString());
		return root;
	}
	

	/**
	 * 预处理坐标位置,排序
	 * @param indexs
	 * @return
	 */
	private static List<IndexType> getIndexTypes(HashMap<Integer, Integer> indexs) {
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
	private static HashMap<Integer, Integer> findIndex(String content) {
		HashMap<Integer, Integer> indexs = new HashMap<>();
		int indexStar = -1;
		int indexFind = -1;
		// 寻找标签起始位置
		while ((indexFind = content.indexOf("<", indexStar)) >= 0) {
			char c = content.substring(indexFind + 1, indexFind + 2).toCharArray()[0];
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) { // 正常标签
				indexs.put(indexFind, 1);
			}
			else { // 注释<!-- 注释 --> <?-- 注释 -->
				indexs.put(indexFind, 2);
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



}
