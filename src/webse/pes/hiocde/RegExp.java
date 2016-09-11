package webse.pes.hiocde;

import java.util.ArrayList;
import java.util.regex.*;

public class RegExp {
	private static Pattern pattern=null;
	private static Matcher matcher=null;
	private static String content=null;	
	
	public static void submit(String regex,String target){
		try{
			pattern=Pattern.compile(regex);
		}catch(PatternSyntaxException ex){
			System.out.println("正则表达式语法错误！");
			ex.printStackTrace();
		}
		content=target;
		matcher=pattern.matcher(content);
		//return new RegExp();
		//return matcher;
	}
	
	public static ArrayList<String> findAllLink(){		
		ArrayList<String> urls=new ArrayList<String>(100);			
		if(matcher!=null){
			while(matcher.find()){
				String s=matcher.group(3);                             //若urls==null,提示空指针异常(urls定义为一个数组时）
				if("http".equals(s.substring(0,4))==false){
					s="http://www.amazon.cn"+s;						   //写成s+=错误！！
				}
				s=s.replace("&amp;","&");							   //去掉&amp; ！！！！！！！！！！！！
				urls.add(s);
				
				System.out.println("下一页URL:"+urls.get(urls.size()-1));
			}	
		}
		return urls;
	}
	
	public static ArrayList<String> findAllImage(){
		ArrayList<String> urls=new ArrayList<String>(100);			
		if(matcher!=null){
			int i=0;
			while(matcher.find()){
				urls.add(matcher.group(3));
				System.out.println("图片URL:"+urls.get(i));
				++i;
			}	
		}
		return urls;
	}
	
	public static ArrayList<String> find(int groupIndex){
		ArrayList<String> target=new ArrayList<String>(100);			
		if(matcher!=null){
			int i=0;
			while(matcher.find()){
				target.add(matcher.group(groupIndex));
				System.out.println("正则捕获:"+target.get(i));
				++i;
			}	
		}
		return target;
	}
	
	public static String replace(int groupIndex, String newstr){
		
		return null;
	}
}