package com.frank0631.SemanticWebScraper;

import java.util.*;

public class semanticWebScraper {

   String url_file_name = "testURLs_mega.txt";
   String badurl_file_name = "testURLs_mega_bad.txt";
   String distribution_file_name = "testURLs_mega_distribution.csv";
   String matrix_file_name = "testURLs_mega_matrix.csv";

   ArrayList<String> URLs;
   ArrayList<String> Bad_URLs; 
   Map url_text_map = new LinkedHashMap();
   
   ArrayList<String> keywords_list;

   public semanticWebScraper(){
   
      try{
         ArrayList<String> URLs;
         ArrayList<String> BadURLs;
         URLHandeler urlHandel = new URLHandeler();
         URLs = urlHandel.readURLrows(url_file_name);
      
         BadURLs = urlHandel.readURLrows(badurl_file_name);  
         Map summeryMap = new LinkedHashMap();
         ArticleHandeler ae = new ArticleHandeler("URLs/");
         boolean skipBad = false;
         boolean cacheOnly = true;
         //ae.cacheArticles(URLs);
         summeryMap = ae.summeryMap(URLs, 50, badurl_file_name, skipBad,cacheOnly); //badurl_file_name
         System.out.println("Number of URL Summeries "+summeryMap.size());
          
         Map keywordMap = new LinkedHashMap();
         Map keywordDistro = new LinkedHashMap();
         Map keywordMatricMap = new LinkedHashMap();
         ArrayList<String> keywords;
         KeywordHandeler kh = new KeywordHandeler();
         //kh.BuildTrainingDir("frank_train",summeryMap);
         kh.keywordsLimit = 40;
         keywordMap = kh.ExtractKeywords(summeryMap);
         keywords = kh.ListKeywords(keywordMap);
         
         keywordDistro = kh.KeywordDistribution(keywordMap, keywords, distribution_file_name);
         ArrayList<String> keywords_sorted = new ArrayList<String>(keywordDistro.keySet());
         keywordMatricMap = kh.KeywordMatricMap(keywordMap, keywords_sorted, matrix_file_name);
         
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   
   
   //build feature matrix
   
   //loop urls
      //plot in point cloud
      
   //k_means
   //graph representation
   //
   
   }


   public static void main(String[] args) {
      semanticWebScraper sws = new semanticWebScraper();
      
               
   }
   
   public static void printMapString(Map<String, String> stringMap){
      Iterator text_itt = stringMap.entrySet().iterator();
      while (text_itt.hasNext()) {
         Map.Entry<String, String>  pairs = (Map.Entry)text_itt.next();
         String key = pairs.getKey();
         String txt = pairs.getValue();
         System.out.println(key+","+txt);
      }
   }
            
   public static void printArrayString(ArrayList<String> arryStr){
      for(String str : arryStr){
         System.out.print( str+",");
      }
      
   }               
            
   public static void printMapArray(Map<String,ArrayList<String>> arrayMap){
      Iterator text_itt = arrayMap.entrySet().iterator();
      while (text_itt.hasNext()) {
         Map.Entry<String, ArrayList<String>>  pairs = (Map.Entry)text_itt.next();
         String key = pairs.getKey();
         System.out.print(key+",");
         ArrayList<String> strings = pairs.getValue();
         for(String str : strings){
            System.out.print( str+",");
         }
         System.out.println();
      }
   }
   
}