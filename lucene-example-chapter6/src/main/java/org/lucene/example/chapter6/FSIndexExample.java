package org.lucene.example.chapter6;

import java.io.IOException;
import java.nio.file.Paths;

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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * FS : File System 
 * 
 * @author 龙
 * @time 2017年4月9日 下午2:19:35
 */
public class FSIndexExample {
	// 当前lucene 版本
	private static final Version matchVersion = Version.LUCENE_5_2_0;
	
	private static final String indexPath = "F:\\SolrIndex";
	
	// 分词器
	private static Analyzer analyzer = null;
	private static IndexWriterConfig conf =  null;
	
	// 索引文件路径
	private static Directory dir = null;
	private static IndexWriter indexWriter = null;
	
	
	//文档域（重用field）
	private static IntField idField = new IntField("id", 0, Store.YES);
	private static StringField titleField = new StringField("title", "", Store.YES);
	private static TextField contentField = new TextField("content", "", Store.YES);
	
	
	static {
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			
			//分词器
			analyzer = new SmartChineseAnalyzer();
			analyzer.setVersion(matchVersion);
			
			conf = new IndexWriterConfig(analyzer);
			conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			indexWriter = new IndexWriter(dir, conf);
			System.out.println("初始化 indexWriter 成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static Document createDocument(int id,String title,String content){
		// document 对象不允许重用
		Document doc = new Document();
		// 重用field，可以节省许多GC消耗时间
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
				"alert(r.replace('&',';'));"};
		
		for(int i=0;i<3;i++){
			indexWriter.addDocument(createDocument(ids[i],titles[i],contents[i]));
		}
		indexWriter.commit();
		System.out.println("初始化索引数据成功");
	}
	
	@After
	public void release() throws IOException{
		indexWriter.close();
		dir.close();
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
		int numDocs = indexSearcher.getIndexReader().numDocs();
		System.out.println(String.format("文档总条数：%s", numDocs));
		
		//词条查询，查询不切分的字段
		Query titleQuery = new TermQuery(new Term("title", "JavaScript Replace 全部替换字符用法"));
		ScoreDoc[] hits = indexSearcher.search(titleQuery, 5).scoreDocs;
		
		System.out.println(String.format("查询到  %s 条符合条件的数据,如下:", hits.length));
		for (ScoreDoc scoreDoc : hits) {
			 Document hitDoc = indexSearcher.doc(scoreDoc.doc);
			 String id = hitDoc.get("id");
			 String title = hitDoc.get("title");
			 String content = hitDoc.get("content");
			 System.out.println(String.format("id:%s ,title:%s ,content:%s",id, title,content));
		}
	}
	
	/**
	 * 删除索引库中的索引文档
	 *  IndexReader 和 IndexWriter 都能够进行文档删除，其中的区别是：当IndexWriter 打开索引的时候，IndexReader 的删除操作会抛出 LockObtainFailedException异常
	 * @throws IOException
	 */
	@Test
	public void deleteAllDocumentExample() throws IOException{
		System.out.println(String.format("删除前文档总条数：%s", indexWriter.numDocs()));
		indexWriter.deleteAll();
		indexWriter.commit();
		System.out.println(String.format("删除后文档总条数：%s", indexWriter.numDocs()));
	}
	
	@Test
	public void deleteDocumentExample() throws IOException{
		System.out.println(String.format("删除前文档总条数：%s", indexWriter.numDocs()));
		indexWriter.deleteDocuments(new Term("title","MongoDB 基本命令用法"));
		indexWriter.commit();
		System.out.println(String.format("删除后文档总条数：%s", indexWriter.numDocs()));
	}
	
	@Test
	public void searchAndUpdateDocument() throws IOException{
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		Term term = new Term("title", "JavaScript Replace 全部替换字符用法");
		Query titleQuery = new TermQuery(term);
		ScoreDoc[] hits = indexSearcher.search(titleQuery, 5).scoreDocs;
		if(hits.length == 0){
			throw new IllegalArgumentException("索引中没有匹配的结果");
		}else{
			Document doc = createDocument(66,"JavaScript Replace 全部替换字符用法","update content");
			indexWriter.updateDocument(term, doc);		// 实现方式：先删除旧文档再先索引中写入新文档
			indexWriter.commit();
		}
	}

}
