package cn.xuexiyuan.lucene.demo;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

public class HelloLucene {
	
	private static final String indexPath = "F:\\SolrIndex";
	private static Directory directory = null;
	
	static{
		try {
			directory = FSDirectory.open(Paths.get(indexPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createIndex() throws IOException{
		//分词器
		Analyzer analyzer = new StandardAnalyzer();
		analyzer.setVersion(Version.LUCENE_5_2_0);
		IndexWriterConfig conf =new IndexWriterConfig(analyzer);
		conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		IndexWriter indexWriter = new IndexWriter(directory, conf);
		
		Document doc = new Document();
		doc.add(new StringField("path", "test path", Field.Store.YES));
		doc.add(new TextField("contents", "lucene hello world lucene lucene", Field.Store.YES));
		indexWriter.addDocument(doc);
		
		Document doc1 = new Document();
		doc1.add(new StringField("path", "test path 1", Field.Store.YES));
		doc1.add(new TextField("contents", "lucene hello world 1", Field.Store.YES));
		indexWriter.addDocument(doc1);
		
		indexWriter.commit();
		indexWriter.close();
	}
	
	public static void searchIndex(String fid,String keyword) throws IOException{
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Term term = new Term(fid,keyword) ;
		Query query = new TermQuery(term);
		
		TopDocs topDocs = indexSearcher.search(query, 5);
		
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			System.out.println("scoreDoc: "+scoreDoc);
		}
		
	}
	

	public static void main(String[] args) throws IOException {
		createIndex();
		searchIndex("contents","lucene");
	}

}
