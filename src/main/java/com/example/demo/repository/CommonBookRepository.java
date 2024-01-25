package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.CommonBook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class CommonBookRepository {
	@Autowired
	JdbcTemplate jdbcTemplate ;

	RestTemplate restTemplate ; // JDBCみたいにautowiredできないので
	
	// コンストラクタでrestTemplateインスタンスをつくる
	public CommonBookRepository(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	// CommonBook表から共通本のオブジェクトを取得
	public CommonBook findById(String isbn) {
		String sql = "SELECT * FROM COMMONBOOK WHERE COMMONBOOKID = ?";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,isbn);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				// TODO:本情報取得
				String CommonBookId = (String) result.get("CommonBookId");
				String title = (String) result.get("title");
				String authors = (String) result.get("authors");
				String publisher = (String) result.get("publisher");
				int pageCount = (int)(result.get("pageCount"));
				String thummail = (String) result.get("thumbnail");
				CommonBook commonBook = new CommonBook(CommonBookId, title, authors, publisher, pageCount, thummail);
				return commonBook;
			}
		}
		return null;
	}
	
	// グーグルブックスのISBNコードから共通本を取得
	public CommonBook findByIdFromWebAPI(String isbn) {
		final String URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn;//GoogleBooksAPIへのURL
		if(findCommonBookListByURL(URL).size() >=1) {
			CommonBook commonBook = findCommonBookListByURL(URL).get(0);
			return commonBook;
		}else {
			return null; //見つからない場合
		}
	}
	
	// URLから共通本のリストを返す
	public List<CommonBook> findCommonBookListByURL(String URL){
		String json = restTemplate.getForObject(URL, String.class);
		System.out.println(json);
		List<CommonBook> commonBookList = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(json);
			JsonNode items = node.path("items");
//			System.out.println("size:"+items.size());
			for(int i=0,n=items.size();i<n;i++) {
				JsonNode volumeInfo = items.path(i).path("volumeInfo");
				// TODO: ISBNコードが2種類あるのでISBN_13のほうを読むように13桁にする！
				String CommonBookId = volumeInfo.path("industryIdentifiers").path(0).path("identifier").asText("");
				if(CommonBookId.startsWith("97") == false) {
					CommonBookId = volumeInfo.path("industryIdentifiers").path(1).path("identifier").asText("");
				}
				String title = volumeInfo.path("title").asText("");
				String authors = volumeInfo.path("authors").path(0).asText("");
				String publisher = volumeInfo.path("publisher").asText("");
				int pageCount = Integer.parseInt(volumeInfo.path("pageCount").asText("0"));//ページ数が取得できない場合もある
				if( pageCount ==0 ) continue; // ページ数が0のときは登録しない
//				System.out.println(i+":"+title+","+pageCount+"page");
				String thummail = volumeInfo.path("imageLinks").path("thumbnail").asText("");
				CommonBook commonBook = new CommonBook(CommonBookId, title, authors, publisher, pageCount, thummail);
				commonBookList.add(commonBook);
			};
		}catch(Exception e) {
			e.printStackTrace();
		}
		return commonBookList;
	}

	// APIに取得したデータに欠損した場合、データが取得できない事についてChatGPTに訊いた。
	// 
	// 問題の根本的な原因は、APIが返すJSONにおいて、一部の本においてはpublisherなどの必要な情報が欠落していることです。このような場合、エラーが発生して処理が中断されてしまいます。
	/*
	問題の根本的な原因は、APIが返すJSONにおいて、一部の本においてはpublisherなどの必要な情報が欠落していることです。このような場合、エラーが発生して処理が中断されてしまいます。
	改善するために、以下のアプローチが考えられます。
	
	・Null チェックとデフォルト値の設定: JsonNode からデータを取り出す前に、null チェックを追加し、データが存在しない場合にはデフォルト値を設定することで、欠落したデータに対処できます。
	・オプショナルなデータの扱い: JsonNode からデータを取り出す際に、asText() メソッドを使用すると、データが存在しない場合に例外が発生します。代わりに、asText() の代わりに asText("") を使用して、デフォルト値を指定することで例外を回避できます。
	・オプショナルなデータの取得: JsonNode の path メソッドを使用して、データが存在しない場合にデフォルト値を返すようにできます。これにより、欠落しているデータがあってもエラーが発生せずに処理を続行できます。
	
	上記改善を取り入れたら動いた。
	https://chat.openai.com/share/31c8591e-442e-4968-a3a0-d3247a1a49af
	*/	
	
	
	// 共通本の表に本登録
	public boolean insert(CommonBook book) {
		String sql ="INSERT INTO COMMONBOOK(COMMONBOOKID,TITLE,AUTHORS,PUBLISHER,PAGECOUNT,THUMBNAIL) "
				+ " VALUES(?,?,?,?,?,?)";
		// System.out.println(sql);
		// System.out.println(book);
		int result = 0 ;
		try {
			result= jdbcTemplate.update(
					sql,
					book.getCommonBookId(),
					book.getTitle(),
					book.getAuthors(),
					book.getPublisher(),
					book.getPageCount(),
					book.getThumbnail());
		}catch(Exception e) {
			e.printStackTrace();
		}
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
}
