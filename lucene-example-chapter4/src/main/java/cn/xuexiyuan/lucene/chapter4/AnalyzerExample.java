package cn.xuexiyuan.lucene.chapter4;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 分词案例
 * 
 * Lucene 中处理中文的常用方法有三种。以“咬死猎人的狗”这句话的输出结果为例。
 * 单词方式：[咬] [死] [猎] [人] [的] [狗]
 * 二元覆盖的方式：[咬死] [死猎] [猎人] [人的] [的狗]
 * 分词的方式：[咬] [死] [猎人] [的] [狗] 
 * 
 * Lucene 中StandardTokenizer采用了单词方式分词， CJKAnalyzer采用了二元覆盖的方式分词， SmartChineseAnalyzer采用了分词的方式分词。
 * 
 * Lucene中负责语言处理的部分在org.apache.lucene.analysis包。其中TokenStream类用来进行基本的分词工作，
 * Analyzer类是TokenStream的外围包装类，负责整个解析工作。有人把文本解析比喻成人体的消化过程，吃入食物，消化分解
 * 出有用的氨基酸和葡萄糖等。Analyzer类接受的是整段文本，解析出有意义的词语。
 * 
 * @author 龙
 * @time 2017年3月30日 下午1:28:16
 */
public class AnalyzerExample {
	
	private static final String cnText = "咬死猎人的狗";
	private static final String enText = "Welcome to Apache Lucene";
	
	private static void analyzerPrint(Analyzer analyzer,String fieldName,String text){
		try {
			TokenStream ts = analyzer.tokenStream(fieldName, text);
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				System.out.print("["+ch.toString()+"] ");
			}
			System.out.println();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("=============== 使用 StandardAnalyzer 单词方式分词 ==================");
		Analyzer analyzer = new StandardAnalyzer();
		System.out.println(String.format("对  “%s” 分词结果如下：", cnText));
		analyzerPrint(analyzer,"myField",cnText);
		System.out.println(String.format("对  “%s” 分词结果如下：", enText));
		analyzerPrint(analyzer,"myField",enText);
		System.out.println("==============================================================\n");
		
		
		System.out.println("=============== 使用CJKAnalyzer 二元覆盖的方式分词 ==================");
		analyzer = new CJKAnalyzer();
		System.out.println(String.format("对  “%s” 分词结果如下：", cnText));
		analyzerPrint(analyzer,"myField",cnText);
		System.out.println(String.format("对  “%s” 分词结果如下：", enText));
		analyzerPrint(analyzer,"myField",enText);
		System.out.println("==============================================================\n");
		
		
		System.out.println("=============== 使用SmartChineseAnalyzer 方式分词 ==================");
		analyzer = new SmartChineseAnalyzer();
		System.out.println(String.format("对  “%s” 分词结果如下：", cnText));
		analyzerPrint(analyzer,"myField",cnText);
		System.out.println(String.format("对  “%s” 分词结果如下：", enText));
		analyzerPrint(analyzer,"myField",enText);
		System.out.println("==============================================================\n");
	}
}
