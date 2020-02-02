package com.blxt.qxml4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.blxt.quicklog4j.QLog;
import com.blxt.qxml4j.unit.QXmlTools;

public class XmlMain2 {

	public static void main(String[] args) {
		File file = new File("E:\\MyComputer\\Desktop\\books - 副本.txt");
		String mainContent = "";
	    if ((file.isFile()) && (file.exists()))
	    {
	      mainContent = readFile(file);
	    }
	    else
	    {
	      System.out.println("文件不存在");
	      return;
	    }
	    
	    QElement html = QXmlTools.init(mainContent);
	  
	  //  QElement content = html.getSubElement().get(0).findEleByClass("content");
	    QElement content = html.getSubElement().get(0);
	    QLog.i("结果" , content.toString());
	}
	
	

	 public static String readFile(File file)
	  {
	    String strRes = "";
	    try
	    {
	      FileReader reader = new FileReader(file);
	      BufferedReader br = new BufferedReader(reader);
	      
	      String str = null;
	      while ((str = br.readLine()) != null) {
	        if (str.trim().length() != 0) {
	          strRes = strRes + str + "\r\n";
	        }
	      }
	      br.close();
	      reader.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return strRes;
	  }

}
