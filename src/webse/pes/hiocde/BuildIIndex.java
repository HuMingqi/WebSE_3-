package webse.pes.hiocde;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import webse.pes.hiocde.RegExp;


public class BuildIIndex {
	
	public static int pid=0;
	public static int wid=0;
	private static String segword_api = "http://api.bosonnlp.com/tag/analysis?space_mode=0&oov_level=3&t2s=0&&special_char_conv=0";
	private static JSONArray words_dic= new JSONArray();
	private static JSONArray inv_file = new JSONArray();
	private static JSONArray page_info = new JSONArray();
	
	
	static{
	      pid=0;											
		  File amountFile=new File(Webse.cur_index);
	      String s = null;
			try {
				BufferedReader br = new BufferedReader(new FileReader(amountFile));       //构造一个BufferedReader类来读取文件
				if((s=br.readLine())!=null) 
					pid=Integer.parseInt(s); 								//***用于续接，id为最近一次获取成功的服饰页面id***
				br.close();				
			} catch (FileNotFoundException e1) {			
				e1.printStackTrace();
			} catch (IOException e1) {				
				e1.printStackTrace();
			}
			
		  //load words_dic and inversed file
			File dic = new File(Webse.words_dic);
			File invfile = new File(Webse.inversed_file);		
			File pageinfo = new File(Webse.page_info);
			try {
				if(dic.length()!=0&&invfile.length()!=0){
					words_dic = JSONArray.fromObject(FileUtils.readFileToString(dic));
					inv_file =  JSONArray.fromObject(FileUtils.readFileToString(invfile));
					page_info = JSONArray.fromObject(FileUtils.readFileToString(pageinfo));
					
					wid= words_dic.size();
					System.out.println("Load success : cur word id : "+ wid);
				}							
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void build(String page , String url){
		
		++pid;
		RegExp.submit("(<title>)([\\w\\W]*?)(</title>)",page);
		String title = RegExp.find(2).get(0);
		RegExp.submit("([价格:|售价:][\\w\\W]*?￥)(\\d+)",page);
		String price = RegExp.find(2).get(0);
		
		//******filter html to get content text , discard tags
		Document doc = Jsoup.parse(page);
		String text = doc.text();
		
		//***discard Punctuations and partial special signs in text
		String reg= "[^%\\.0-9a-zA-Z\\u4e00-\\u9fa5]";			//***another : [`~!@#$%^&*()+=|{}':;',\"\\[\\]\\.\\\<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]
		text=text.replaceAll(reg," ");
		
		//***normalization	upper --> lower
		text = text.toLowerCase();
		
//		FileWriter fw;
//		try {
//			fw = new FileWriter("C:\\Users\\mingq\\Desktop\\Temp\\src_text.txt");
//			fw.write(text);
//			fw.close();	
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//******segment words including Chinese, English, number
		JSONObject seg_words = (JSONObject) JSONArray.fromObject(Brower.post(segword_api, text)).get(0);		//brower return json array format [ { "tag" : [...] , "word" : [...]} ] 
				
//		FileWriter fw;
//		try {
//			fw = new FileWriter("C:\\Users\\mingq\\Desktop\\Temp\\text_seg_words.txt");
//			fw.write(Brower.post(segword_api, text));
//			fw.close();	
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
						
		//******build inversed index
		for(Object wordstr : seg_words.getJSONArray("word")){
			boolean flag = false;
			
			for(Object  word : words_dic){ 
				if(((String)wordstr).equals(((JSONObject)word).getString("word"))){		//word matching
					int wwid = ((JSONObject)word).getInt("id");	flag=true;
					
					for(Object pages : inv_file){
						if(((JSONObject)pages).getInt("id")==wwid){			//must be ture
							boolean flagg=false;
							
							for(Object pagee : ((JSONObject)pages).getJSONArray("pages")){
								if(((JSONObject)pagee).getInt("pid")==pid){
									int old_f = ((JSONObject)pagee).getInt("f");
									((JSONObject)pagee).put("f", old_f + 1);			//update word frequency
									
									//descend sort
																										
									flagg = true;
								}																
							}
							if(!flagg){													// add new page
								JSONObject pagee = new JSONObject();
								pagee.put("pid", pid);
//								pagee.put("url", url);
//								pagee.put("title", title);
//								pagee.put("price", price);
								pagee.put("f", 1);
								
								((JSONObject)pages).getJSONArray("pages").add(pagee);
							}
						}
					}									
				}
			}
			if(!flag){			//word not matching ,  a new word
				JSONObject word = new JSONObject();
				word.put("id", ++wid);
				word.put("word", wordstr);
				
				words_dic.add(word);
				
				JSONObject pagee = new JSONObject();
				pagee.put("pid", pid);
//				pagee.put("url", url);
//				pagee.put("title", title);
//				pagee.put("price", price);
				pagee.put("f", 1);
				
				JSONArray pageArray = new JSONArray();
				pageArray.add(pagee);
				
				JSONObject pages = new JSONObject();
				pages.put("id", wid);				
				pages.put("pages", pageArray);
				
				inv_file.add(pages);
			}			
		}						
		
		//***add page info
		JSONObject page_infoo = new JSONObject();
		page_infoo.put("pid", pid);
		page_infoo.put("url", url);
		page_infoo.put("title", title);
		page_infoo.put("price", price);
		page_info.add(page_infoo);		
		
		//***save inversed index and src html
		FileWriter fw;
		try {
			fw = new FileWriter(Webse.words_dic);
			fw.write(words_dic.toString());
			fw.close();
			
			fw = new FileWriter(Webse.inversed_file);
			fw.write(inv_file.toString());
			fw.close();
			
			fw = new FileWriter(Webse.page_info);			
			fw.write(page_info.toString());
			fw.close();
			
			fw = new FileWriter(Webse.webpages_folder+pid+".html");			
			fw.write(page);			//store paths		
			fw.close();
			
			fw=new FileWriter(Webse.cur_index);
			fw.write(pid+"");		//write(id) will save ascii code
			fw.close();
			
			System.out.println("Page "+pid+" output ok\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

}
