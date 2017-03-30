package org.lucene.example.chapter6;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 初始化索引数据
 * 
 * @author 龙
 * @time 2017年3月30日 下午5:31:57
 */
public class InitIndexDataExample {
	
	private static final String indexPath = "F:\\SolrIndex";
	private static Directory directory = null;
	//分词器
	private static Analyzer analyzer = null;
	private static IndexWriterConfig conf = null;
	private static IndexWriter indexWriter = null;
	
	//检索数据
	private static IndexReader indexReader = null;
	private static IndexSearcher indexSearcher = null;
	
	
	static{
		try {
			directory = FSDirectory.open(Paths.get(indexPath));
			
			//分词器
			analyzer = new SmartChineseAnalyzer();
			analyzer.setVersion(Version.LUCENE_5_2_0);
			
			conf = new IndexWriterConfig(analyzer);
			conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			indexWriter = new IndexWriter(directory, conf);
			
			indexReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建索引
	 * 
	 * @param url
	 * @param title
	 * @param author
	 * @param body
	 * @return
	 * @throws IOException 
	 */
	public static void createIndex(String url,String title,String author,String body) throws IOException{
		Document doc = new Document();
		doc.add(new StringField("url", url, Field.Store.YES));
		doc.add(new StringField("title", title, Field.Store.YES));
		doc.add(new StringField("author", author, Field.Store.YES));
		doc.add(new TextField("body", body, Field.Store.YES));
		indexWriter.addDocument(doc);
		indexWriter.commit();
//		indexWriter.close();
	}
	
	
	/**
	 * 检索数据
	 * 
	 * 
	 * @param fid
	 * @param keyword
	 * @throws IOException
	 */
	public static void searchIndex(String fid,String keyword) throws IOException{
		Term term = new Term(fid,keyword) ;
		Query query = new TermQuery(term);
		
		TopDocs topDocs = indexSearcher.search(query, 5);
		
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			System.out.println("scoreDoc: "+scoreDoc);
		}
		
	}
	

	public static void main(String[] args) throws IOException {
//		createIndex("http://nba.sina.com/","首节7分助勇士度过难关 绝境一哥再显灵","NBA官网","勇士客场22分逆转马刺，伊戈达拉8投6中，砍下14分，其中第一节末端连得7分，帮助勇士走出开局困境。");
//		createIndex("http://china.nba.com/","主场惜败 小托马斯：我们不会沉湎于失利","NBA官网","自从杜兰特因伤缺阵后，伊戈达拉在12场比赛里场均11.7分，投篮命中率60%，帮助勇士在经历三连败后取得7连胜，绝境就得看一哥。");
//		createIndex("http://www.nba.com/","沃尔空砍41+8 快船四人20+胜奇才","NBA官网","首节还剩2分31秒，底角三分命中；49.9秒，跳投得手；1.8秒，急停跳投得手。伊戈达拉用末节连得7分的方式使得勇士在首节结束时追到17-33，保留了继续追分的希望。");
//		createIndex("http://www.nba.com/","托马斯","NBA官网","首节还剩2分31秒，底角三分命中；49.9秒，跳投得手；1.8秒，急停跳投得手。伊戈达拉用末节连得7分的方式使得勇士在首节结束时追到17-33，保留了继续追分的希望。");
		searchIndex("title","托马斯");
	}

}
