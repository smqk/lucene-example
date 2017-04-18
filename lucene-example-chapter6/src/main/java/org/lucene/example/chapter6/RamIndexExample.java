package org.lucene.example.chapter6;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 内存索引
 * 
 * @author 龙
 * @time 2017年4月9日 下午2:19:35
 */
public class RamIndexExample {
	// 当前lucene 版本
	private static final Version matchVersion = Version.LUCENE_5_2_0;
	// 分词器
	private static Analyzer analyzer = new SmartChineseAnalyzer();
	private static IndexWriterConfig conf =  new IndexWriterConfig(analyzer);
	
	// 内存路径
	private static Directory dir = new RAMDirectory();
	private static IndexWriter indexWriter = null;
	
	
	//文档域（重用field）
	private static IntField idField = new IntField("id", 0, Store.YES);
	private static StringField titleField = new StringField("title", "", Store.YES);
	private static TextField contentField = new TextField("content", "", Store.YES);
	
	
	static {
		try {
			analyzer.setVersion(matchVersion);
			conf.setRAMBufferSizeMB(50);		//设置更新文档使用的内存达到指定大小之后写入磁盘
			indexWriter = new IndexWriter(dir, conf);
			System.out.println("初始化 indexWriter 成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static Document createDocument(int id,String title,String content){
		// document 对象不允许重用
		Document doc = new Document();
		idField.setIntValue(id);
		titleField.setStringValue(title);
		contentField.setStringValue(content);
		
		doc.add(idField);
		doc.add(titleField);
		doc.add(contentField);
		return doc;
	}
	
	@Before
	public void initIndexData() throws IOException{
		int[] ids = {1,2,3};
		String[] titles = {"MongoDB 基本命令用法","centos7 快速安装 mariadb（mysql）","JavaScript Replace 全部替换字符用法"};
		String[] contents = {
				"启动mongo shell时不连接任何mongod数据库；mongo --bidb 启动mongo时指定机器名和端口mongo",
				"从最新版本的Linux系统开始，MySQL数据库默认的是 Mariadb！使用系统自带的repos安装很简单：yum install mariadb mariadb-server",
				"alert(r.replace('#',';'));"};
		
		for(int i=0;i<3;i++){
			indexWriter.addDocument(createDocument(ids[i],titles[i],contents[i]));
		}
		indexWriter.commit();
		System.out.println("添加文档到索引成功");
	}
	
	@After
	public void release() throws IOException{
		indexWriter.close();
		dir.close();
	}
	
	
	@Test
	public void searchExample() throws IOException, ParseException{
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		System.out.println("初始化 indexSearcher 成功");
		
		//QueryParser将输入的查询字符串解析成Query对象
		//没有分词的域请直接使用Query API来构建你的Query实现类，因为QueryParser会使用分词器对用户输入的文本进行分词得到N个Term,然后再根据匹配的
		QueryParser parser = new QueryParser("content",analyzer);
		Query contentQuery = parser.parse("常用数据库");
		
		ScoreDoc[] hits = indexSearcher.search(contentQuery, 5).scoreDocs;
		System.out.println(String.format("查询到  %s 条符合条件的数据,如下:", hits.length));
		for (ScoreDoc scoreDoc : hits) {
			 Document hitDoc = indexSearcher.doc(scoreDoc.doc);
			 String title = hitDoc.get("title");
			 String content = hitDoc.get("content");
			 System.out.println(String.format("title:%s ,content:%s", title,content));
		}
	}
	
	/**
	 * 最基本的词条查询
	 * 用法：查询不切分的字段
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void termQueryExample() throws IOException, ParseException{
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		System.out.println("初始化 indexSearcher 成功");
		
		//词条查询，查询不切分的字段
		Query titleQuery = new TermQuery(new Term("title", "JavaScript Replace 全部替换字符用法"));
		
		ScoreDoc[] hits = indexSearcher.search(titleQuery, 5).scoreDocs;
		System.out.println(String.format("查询到  %s 条符合条件的数据,如下:", hits.length));
		for (ScoreDoc scoreDoc : hits) {
			 Document hitDoc = indexSearcher.doc(scoreDoc.doc);
			 String title = hitDoc.get("title");
			 String content = hitDoc.get("content");
			 System.out.println(String.format("title:%s ,content:%s", title,content));
		}
	}	
	
	/**
	 * 布尔逻辑查询
	 * 用法：组合条件查询
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void booleanQueryExample() throws IOException, ParseException{
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		System.out.println("初始化 indexSearcher 成功");
		
		Query titleQuery = new TermQuery(new Term("title", "JavaScript Replace 全部替换字符用法"));
		
		QueryParser parser = new QueryParser("content",analyzer);
		Query contentQuery = parser.parse("数据库");
		
		// 使用 OR 条件合并两个查询
		BooleanQuery titleOrContentQuery = new BooleanQuery();
		titleOrContentQuery.add(titleQuery, Occur.SHOULD);
		titleOrContentQuery.add(contentQuery, Occur.SHOULD);
		
		ScoreDoc[] hits = indexSearcher.search(titleOrContentQuery, 5).scoreDocs;
		System.out.println(String.format("查询到  %s 条符合条件的数据,如下:", hits.length));
		for (ScoreDoc scoreDoc : hits) {
			 Document hitDoc = indexSearcher.doc(scoreDoc.doc);
			 String title = hitDoc.get("title");
			 String content = hitDoc.get("content");
			 System.out.println(String.format("title:%s ,content:%s", title,content));
		}
	}


}
