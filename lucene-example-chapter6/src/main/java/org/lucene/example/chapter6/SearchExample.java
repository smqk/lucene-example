package org.lucene.example.chapter6;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
/**
 * 检索数据
 * 
 * 
 * @author 龙
 * @time 2017年3月30日 下午5:31:40
 */
public class SearchExample {
	
	private static final String indexPath = "F:\\SolrIndex";
	private static Directory directory = null;
	//检索数据
	private static IndexReader indexReader = null;
	private static IndexSearcher indexSearcher = null;
	
	
	static{
		try {
			directory = FSDirectory.open(Paths.get(indexPath));
			indexReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		searchIndex("title","托马斯");
	}

}
