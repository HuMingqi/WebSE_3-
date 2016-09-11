package webse.pes.hiocde;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;

public class Webse {
	
	private static final String init_or_again_url="./conf/init_or_again_url.txt";				//card page url , auto-update after it's initialized to prevent being breaked
    public static final String cur_index="./conf/cur_index.txt";									//used this index to start again when being breaked
    private static final String output_paths="./conf/output_paths.txt";								//indlude webpages_folder words_dic inversed_index
    private static String url="";
    public static String webpages_folder="";		//folder path ends by '/'
    public static String words_dic="";				//determined by users
    public static String inversed_file="";			//determined by users , (pid , f)
    public static String page_info="";				//(pid , url , title , price) , avoid data redundancy
    public static int shutdown=48;					//need to get amount of pages , defined by user , shutdown = 0 exit
    private static boolean normal_exit=true;
    
	private Set<String> visitedURL=new HashSet<String>();
	private Queue<String> queue=new PriorityQueue<String>();	
//	private LinkedList<String> detail_pages=new LinkedList<String>();

	
	public static void main(String[] args) {
//		//***Test cpde
//		File page = new File("F:/DataAdapter/Eclipse_workspace/_DataAdapter/SearchEngine_3+/webpages/1.html");
//		String page_str;
//		try {
//			page_str = FileUtils.readFileToString(page);
//			
////			FileWriter fw= new FileWriter("C:\\Users\\mingq\\Desktop\\Temp\\src_html.txt");
////			fw.write(page_str);
////			fw.close();					
//			
//			words_dic = "F:\\DataAdapter\\Eclipse_workspace\\_DataAdapter\\SearchEngine_3+\\words_dic.txt";
//			inversed_file = "F:\\DataAdapter\\Eclipse_workspace\\_DataAdapter\\SearchEngine_3+\\inversed_file.txt";
//			page_info= "F:\\DataAdapter\\Eclipse_workspace\\_DataAdapter\\SearchEngine_3+\\page_info.txt";
//			
//			BuildIIndex.build(page_str,url);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		// TODO Auto-generated method stub
		System.out.println("-------------Web Search Engine 3+ Launching-------------\n");
		
		boolean again=false;
		BufferedReader br;
		
		//***input seed url
		try {
			br= new BufferedReader(new FileReader(init_or_again_url));       
			
			if((url=br.readLine())==null){		//file is empty, ***must assign url //if use File ob , judge empty through ob.length()==0***
				System.out.println("Please input card page link of products from Amazon website, strike ENTER to end input everytime.");
				char ch;
				url="";
				while((ch=(char)System.in.read())!='\r'){
					url+=ch;
				}				
				System.in.read();//read '\n'
			}else{				
				System.out.println("Using breaked URL last time to continue?(Y/N)");
				
				char ok=(char)System.in.read();
				System.in.read();	//read '\r'
				System.in.read();	//read '\n'
				if(ok=='Y'||ok=='y'){				
					again=true;	
					
				}else{					
					System.out.println("\nPlease input card page link of products from Amazon website, strike ENTER to end input everytime.");
					char ch;
					url="";
					while((ch=(char)System.in.read())!='\r'){
						url+=ch;
					}
					System.in.read();//read '\n'
				}				
			}	
			
			br.close();
		} catch (FileNotFoundException e1) {			
			e1.printStackTrace();
		} catch (IOException e1) {				
			e1.printStackTrace();
		}
		
		//***input object path
		if(again){						
			try {
				br = new BufferedReader(new FileReader(output_paths));
				webpages_folder=br.readLine();
				words_dic=br.readLine();
				inversed_file=br.readLine();
				page_info = br.readLine();
				
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}																
			
		}else{
			System.out.println("\nPlease input the path of web pages folder, you should end it by '/'");
			char ch;			
			try {
				while((ch=(char)System.in.read())!='\r'){
					webpages_folder+=ch;
				}
				System.in.read();//read '\n'
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("\nPlease input the path of words dictionary file.");
			try {
				while((ch=(char)System.in.read())!='\r'){
					words_dic+=ch;
				}
				System.in.read();//read '\n'
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("\nPlease input the path of inversed index file.");
			try {
				while((ch=(char)System.in.read())!='\r'){
					inversed_file+=ch;
				}
				System.in.read();//read '\n'
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("\nPlease input the path of page info file.");
			try {
				while((ch=(char)System.in.read())!='\r'){
					page_info+=ch;
				}
				System.in.read();//read '\n'
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				FileWriter	writer = new FileWriter(output_paths);
				writer.write(webpages_folder+'\n'+words_dic+'\n'+inversed_file+"\n"+page_info);			//store paths
				writer.flush();
				writer.close();
			} 
			catch (IOException e) {				
				e.printStackTrace();
			}
						
		}
				
		
		System.out.println("\nWeb Search Engine 3+ has launched......\n");
		
		new Webse().run(url);					
		
		if(normal_exit==true){
			FileWriter fw=null;
			try {
				fw=new FileWriter(new File(init_or_again_url));					//clear conf files when ends normally
				fw.write("");
				fw.close();
				fw=new FileWriter(new File(cur_index));
				fw.write("");
				fw.close();
//				fw=new FileWriter(new File(output_paths));
//				fw.write("");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
			System.out.println("Web SE3+ has ended normally, to see your outputs now! \nprovided by hiocde.\nEmail: hiocde@gamil.com");
		}else{
			System.out.println("Web SE3+ has ended accidently, to check it! \nprovided by hiocde.\nEmail: hiocde@gamil.com");
		}
		
	}	
	
	public void run(String url){
		System.out.println(url);				
		
		queue.add(url);
		
		while(!queue.isEmpty()){
			if(shutdown==0){									
				System.out.println("Task completed\n");
				return;
			}
			
			url=queue.poll();      			        //取URL			
			if(isVisited(url)) continue;            //判断是否已经访问
			else{
				visitedURL.add(url);									 
				try {
					FileWriter	writer = new FileWriter(init_or_again_url);
					writer.write(url);				//auto-update card url to start from there again when fail to visit
					writer.flush();
					writer.close();
				} 
				catch (IOException e) {				
					e.printStackTrace();
				}				
			}
			
			String con=Brower.get(url);               								 //获取页面正文			
				
			if(con!=null){
				System.out.println("访问成功，卡片页URL： "+url);
					
				ArrayList<String> urls=null;
				RegExp.submit("(\"下一页\"[\\w\\W]+?)(href=\")(.+?)(\")", con);								
				urls=RegExp.findAllLink();								//***下一页链接			
				
				if(urls.isEmpty()){											    //页面无超链接，终端节点
					System.out.println("下一页不存在！这是最后一页");					
				}
				else{																	//否则下一卡片页URL入队
					for(String URL:urls){																		
						if((URL.substring(0,20)).equals("http://www.amazon.cn"))
						queue.add(URL);															//下一页超链接入队												
					}
					System.out.println("下一卡片页超链接入队成功！");
				}							
				
				RegExp.submit("(id=\"result_\\d+\"[\\w\\W]*?)(href=\")(.+?)(\")",con);		//获得详情页地址
				urls=RegExp.find(3);
				
				if(urls.isEmpty()){
					System.out.println("未匹配到详情页地址");
					continue;
				}
				System.out.println("详情页个数："+urls.size());	
								
				//******download details_page and resolve, build inversed index one by one 
				int counter = 0;
				int break_pid=BuildIIndex.pid;					//although no breaking , no problem!
				shutdown-=break_pid%48;							//
				for(String obj_url:urls){
					if(++counter <= (break_pid%48))	continue;	//if breaked , accurately restart and continue!! 
					if(shutdown-- > 0){
						BuildIIndex.build(Brower.get(obj_url) , obj_url);
					}else{									
						System.out.println("Task completed\n");
						return;
					}
				}								
															
			}
			else{
					System.out.println("抓取卡片页失败，URL是："+url+"\n");
					normal_exit=false;
			}
		}			
	}		
	
	public boolean  isVisited(String url) {		
		Iterator<String> iterator=visitedURL.iterator();
		String vurl=null;
		while(iterator.hasNext()){
			vurl=(String)iterator.next();
			if(url.equals(vurl))	return true; 
		}
		return false;
	}
}
