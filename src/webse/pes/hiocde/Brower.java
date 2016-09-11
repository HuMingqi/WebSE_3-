package webse.pes.hiocde;


import java.io.IOException;
//import java.io.FileOutputStream;
//import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import net.sf.json.JSONArray;

//import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.*;

import webse.pes.hiocde.HttpClientUtils;

	//import sun.security.action.GetBooleanAction;

	//import com.sun.org.apache.xerces.internal.util.URI;

	public class Brower {								//获取网页正文
		private static String content=null;
		private static RequestConfig reqConfig=RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
//		private HashMap<String, String> headers = new HashMap<String , String>();
		private static CloseableHttpClient httpclient=HttpClients.createDefault();
		private static HttpGet httpget = new HttpGet();
		private static HttpPost httpPost = new HttpPost();
		private static CloseableHttpResponse httpResponse=null;
		
		public static String get(String url) {
			// TODO Auto-generated method stub
			
			
	        try {
	        //	URI uri=new URIBuilder().setPath("http://www.baidu.com/s").setParameter("wd","crawler").build();
	        	
	        //	URI uri=new URIBuilder().setPath(url).build();      //url中若带参，访问会失败！！！！！！以下直接用url构造即可
	        	URI uri=new URI(url);
	    		httpget.setURI(uri);
	    		httpget.setConfig(reqConfig);
	    		
	    		if(url.startsWith("https")){
	    			httpclient= HttpClientUtils.createSSLClientDefault();    					//******https get req			  		
	    		}else{
	    			httpclient=HttpClients.createDefault();
	    		}
	    		
	    		//System.out.println(httpget.getRequestLine());
	            //执行get请求	    		
	            httpResponse = httpclient.execute(httpget);
	            
	            //判断访问成功
	            if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
	            	System.out.println("status:" + httpResponse.getStatusLine());
	            
	            	//File file=new File("G:/crawlerTest/火影.jpg");
	            	//FileOutputStream output=new FileOutputStream(file);
	            
	            	//获取响应消息实体
	            	HttpEntity entity = httpResponse.getEntity();
	            	
	            	//判断响应实体是否为空
	            	if (entity != null) {
	            		//System.out.println("contentEncoding:" + entity.getContentEncoding());
	            		//System.out.println("内容长度："+entity.getContentLength());
	            		System.out.println();
	            		content=EntityUtils.toString(entity,"UTF-8");                               //获取正文（创建流副本）
	            		
	            		//图片写到文件中
	            		// entity.writeTo(output);
	            	}else{
	            		content=null;
	            	}
	            
	            	// httpget.releaseConnection();
	            
	            }else{
	            	content=null;
	            	System.out.println("Content组件访问失败，状态码: "+ httpResponse.getStatusLine().getStatusCode());
	            }
	          
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
					try {
						httpResponse.close();									//释放资源，丢弃链接
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("close failed");
						e.printStackTrace();
					}													 
	            	httpget.releaseConnection();								//清空session                	      
	        }
	        
	        return content;	        
		}
				
		public static String post(String url , String data){
			try {
		        //	URI uri=new URIBuilder().setPath("http://www.baidu.com/s").setParameter("wd","crawler").build();
		        	
		        //	URI uri=new URIBuilder().setPath(url).build();      //url中若带参，访问会失败！！！！！！以下直接用url构造即可
		        	URI uri=new URI(url);
		    		httpPost.setURI(uri);
		    		httpPost.setConfig(reqConfig);
		    		httpPost.setHeader("Content-Type","application/json");
		    		httpPost.setHeader("Accept","application/json");
		    		httpPost.setHeader("X-Token","Wcc5Oa2l.9407.AhdrWElCBHM7");		    			//***token of req api : http://api.bosonnlp.com/tag/analysis?space_mode=0&oov_level=3&t2s=0&&special_char_conv=0
		    		
		    		JSONArray jsonArray = new JSONArray();
		    		jsonArray.add(0,data);
		    		StringEntity body = new StringEntity(jsonArray.toString(),"utf-8");
		    		
//		    		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		    		list.add(new BasicNameValuePair("data","\""+data+"\""));		    		
//		    		UrlEncodedFormEntity body = new UrlEncodedFormEntity(list);  
	                httpPost.setEntity(body);  
		    		
		    		if(url.startsWith("https")){
		    			httpclient= HttpClientUtils.createSSLClientDefault();    					//******https get req			  		
		    		}else{
		    			httpclient=HttpClients.createDefault();
		    		}
		    		
		    		//System.out.println(httpget.getRequestLine());		            	
		            httpResponse = httpclient.execute(httpPost);
		            
		            //判断访问成功
		            if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
		            	System.out.println("status:" + httpResponse.getStatusLine());		            
		            
		            	//获取响应消息实体
		            	HttpEntity entity = httpResponse.getEntity();
		            	
		            	//判断响应实体是否为空
		            	if (entity != null) {
		            		//System.out.println("contentEncoding:" + entity.getContentEncoding());
		            		//System.out.println("内容长度："+entity.getContentLength());
		            		System.out.println();
		            		content=EntityUtils.toString(entity,"UTF-8");                               //获取正文（创建流副本）
		            		
		            		//图片写到文件中
		            		// entity.writeTo(output);
		            	}else{
		            		content=null;
		            	}		            		           
		            
		            }else{
		            	content=null;
		            	System.out.println("Content组件访问失败，状态码: "+ httpResponse.getStatusLine().getStatusCode());
		            }
			}catch (IOException e) {
	            e.printStackTrace();
	        } catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
					try {
						httpResponse.close();									//释放资源，丢弃链接
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("close failed");
						e.printStackTrace();
					}													 
	            	httpget.releaseConnection();								//清空session                	      
	        }
	        
	        return content;	 		
		}
		
	}
