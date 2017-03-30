package cn.xuexiyuan.lucene.demo;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermAutomatonQuery;
import org.apache.lucene.util.AttributeImpl;

/**
 * 分词测试
 * 
 * @author 龙
 * @time 2017年2月10日 下午12:45:49
 */
public class AnalyzerTest {

	public static void main(String[] args) throws IOException {
		//Analyzer 类是TokenStream 的外围包装类，负责整个解析工作
		Analyzer analyzer = new StandardAnalyzer();
		
		//取得token流，TokenStream 类进行基本的分词工作
		TokenStream ts = analyzer.tokenStream("myField", "待切分文本Lucene study");
		
		ts.reset();
		while (ts.incrementToken()) {
			System.out.println(ts);
		}
		ts.close();
		
	}

}
