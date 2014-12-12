package com.frank0631.SemanticWebScraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

//Scrapes articles from URLs
public class ArticleHandeler {

   URLHandeler urlHandeler;
   ArticleExtractor article_extractor;
   HTMLHighlighter html_highlighter;
   String regexURL = "[^A-Za-z0-9()]";
   String storePath= "URLs/";
   
   String[] skipExt;
   public class UnsupportedExtension extends Exception {
      public UnsupportedExtension(String msg) {
         super(msg);
      }
   }
   
   public ArticleHandeler(String path){
      storePath=path;
      new File(storePath).mkdir();
      urlHandeler = new URLHandeler();
      article_extractor = new ArticleExtractor();
      html_highlighter = HTMLHighlighter.newHighlightingInstance();
      System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
      skipExt = (new String[]{".pdf", ".doc", ".ppt"});
   }
   
   public String htmlHilight(String url_str, boolean write) throws IOException,UnsupportedExtension{
   
      for (String skip: skipExt) {
         if(url_str.toLowerCase().endsWith(skip))
            throw new UnsupportedExtension("UnsupportedExtension "+skip);
      }
   
      String url_path = url_str.replaceAll(regexURL, "").toLowerCase();   
      String article_html_file = storePath+url_path+"/highlighted.html";
      String article_html = null;
   
      URL url = new URL(url_str);
      File html_file = new File(article_html_file);
      
      if(html_file.exists())//read
         article_html =  fileToString(html_file);
      else{
         if(urlHandeler.ping(url)){
            try{
               article_html =  html_highlighter.process(url, article_extractor);
            }
            catch(BoilerpipeProcessingException bpe){
               article_html="";
               bpe.printStackTrace();
            }
            catch(SAXException saxe){
               article_html="";
               saxe.printStackTrace();
            }
         
            if(write){    
               new File(storePath+url_path).mkdir();       
               PrintWriter out_html = new PrintWriter(article_html_file, "UTF-8");
               out_html.println("<base href=\"" + url + "\" >");
               out_html.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
               out_html.println(article_html);
               out_html.close();
            }  
         }
         else
            return ""; 
      }
              
      return article_html;
   }
   
   public String extractorArticle(String url_str, boolean write, boolean cacheOnly) throws IOException,UnsupportedExtension{
            
      for (String skip: skipExt) {
         if(url_str.toLowerCase().endsWith(skip))
            throw new UnsupportedExtension("UnsupportedExtension "+skip);
      }       
            
      String url_path = url_str.replaceAll(regexURL, "").toLowerCase();
      String article_html_file = storePath+url_path+"/highlighted.html";
      String article_html = null;
   
      File html_file;
      String article_text_file = storePath+url_path+"/text.txt";
      String article_text;
      File text_file = new File(article_text_file);
      if(text_file.exists())//read
         article_text = fileToString(text_file);
      else{
         html_file = new File(article_html_file);
         if(html_file.exists()){//read
            article_html = fileToString(html_file);
            try{
               article_text = article_extractor.getText(article_html);
            }
            catch(BoilerpipeProcessingException bpe){
               article_text = "";
               bpe.printStackTrace();
            }
         }
         else if(cacheOnly==false){
            URL url = new URL(url_str);
            if(urlHandeler.ping(url))
               try{
                  article_text = article_extractor.getText(url);
               }
               catch(BoilerpipeProcessingException bpe){
                  article_text = "";
                  bpe.printStackTrace();
               }
            else
               article_text = "";     
         } 
         else
            article_text = "";
         
         if(write){     
            new File(storePath+url_path).mkdir();      
            PrintWriter out_text = new PrintWriter(article_text_file, "US-ASCII");
            out_text.println(article_text);
            out_text.close();
         }                    
      }
      return article_text;
   }

   int countWords (String summery) {
      String trimmed = summery.trim();
      if (trimmed.isEmpty()) 
         return 0;
      return trimmed.split("\\s+").length;
   }

   String fileToString(File in) throws IOException{
      FileInputStream is = new FileInputStream(in);
      byte[] b = new byte[(int) in.length()];  
      is.read(b, 0, (int) in.length());
      String contents = new String(b);
      return contents;
   }

   public void cacheArticles(ArrayList<String> urls){
      Iterator<String> url_itt = urls.iterator();
      while (url_itt.hasNext()) {
         String url = url_itt.next();
         try{
         
            htmlHilight(url, true);
            extractorArticle(url, true, false);
            System.out.println("cached "+url);
         }
         catch(IOException ioe) {
             System.out.println("could not cache "+url);
             //ioe.printStackTrace();
         }
         catch(UnsupportedExtension ioe){
            System.out.println("could not cache "+url);
            //ioe.printStackTrace();
         }   
      }
   }

   public Map summeryMap(ArrayList<String> urls, int wordCountLimit, String badURLFilename, boolean skipBad, boolean cacheOnly){
   
      Map url_text_map = new LinkedHashMap();  
      ArrayList<String> bad_URLs = new ArrayList<String>();
      
      if(skipBad){
         URLHandeler urlHandel = new URLHandeler();
         bad_URLs = urlHandel.readURLrows(badURLFilename);   
      }
      
   //loop urls
      Iterator<String> url_itt = urls.iterator();
      while (url_itt.hasNext()) {
          String url = url_itt.next();
          System.out.println("extracting " + url);
          try {

              if (skipBad && bad_URLs.contains(url))
                  continue;

              String txt_summery = extractorArticle(url, false, cacheOnly);
              if (txt_summery != null) {
                  int summery_words = countWords(txt_summery);
                  if (summery_words < wordCountLimit)
                      bad_URLs.add(url);
                  else
                      url_text_map.put(url, txt_summery);
              }
          }
          //ioe.printStackTrace();
          catch (IOException ioe) {
              bad_URLs.add(url);
              //ioe.printStackTrace();
          } catch (UnsupportedExtension ioe) {
              bad_URLs.add(url);
              //ioe.printStackTrace();
          }
      }

   //print bad urls
      if(!skipBad && badURLFilename!=null && !badURLFilename.isEmpty()){
         try{
            PrintWriter badurl_writter = new PrintWriter(badURLFilename, "US-ASCII");
            for(String badurl : bad_URLs)
               badurl_writter.println(badurl);
            badurl_writter.close();
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
      
      return url_text_map;
   }
}