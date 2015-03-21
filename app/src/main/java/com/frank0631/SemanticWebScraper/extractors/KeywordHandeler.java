package com.frank0631.SemanticWebScraper.extractors;

//import com.entopix.maui.main.MauiWrapper;
//import com.entopix.maui.util.Topic;

import com.entopix.maui.main.MauiWrapper;
import com.entopix.maui.util.Topic;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class KeywordHandeler {

   String vocabularyName = "lcsh.rdf.gz";
   String modelName = "SemEval2010";
   //String modelName = "indexing_model";
   //String vocabularyName = "wikipedia";
   MauiWrapper keywordsExtractor;
   public int keywordsLimit = 30;

   public KeywordHandeler(){
   
   //setup keywords extractor
      keywordsExtractor = new MauiWrapper(modelName,vocabularyName,"skos");
      keywordsExtractor.setModelParameters(vocabularyName,null,null,null);
   
   }

   public Map ExtractKeywords(Map urlSummeries){
   
      Map urlKeywords = new LinkedHashMap();
      int i=0;
             
      //loop urls text
      Iterator text_itt = urlSummeries.entrySet().iterator();
      while (text_itt.hasNext()) {
         Map.Entry<String, String>  pairs = (Map.Entry)text_itt.next();
         String url = pairs.getKey();
         String txtSummery = pairs.getValue();
         try{
            if(txtSummery!=null && !txtSummery.isEmpty()) {
               ArrayList<Topic> keytopics = keywordsExtractor.extractTopicsFromText(txtSummery, keywordsLimit);
               ArrayList<String> keywords = new ArrayList<String>();
               for (Topic topic : keytopics)
                  keywords.add(topic.getTitle());

               //ArrayList<String> keywords = keywordsExtractor.extractTopicsFromText(txtSummery, keywordsLimit);
               urlKeywords.put(url, keywords);
            }
            else
               urlKeywords.put(url, new ArrayList<String>());

            i=i++;
            //System.out.println(i);
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
      return urlKeywords;
   }
      
   public ArrayList<String> ListKeywords(Map urlKeywords){
      
   //pool all keywords
      ArrayList<String> keywordsList= new ArrayList<String>();
      Iterator keywords_itt = urlKeywords.entrySet().iterator();
      while (keywords_itt.hasNext()) {
         Map.Entry<String, ArrayList<String>> pairs = (Map.Entry)keywords_itt.next();
         ArrayList<String> keywords = pairs.getValue();
         for(String keyword : keywords){
            if(!keywordsList.contains(keyword))
               keywordsList.add(keyword);  
         }
      }   
      return keywordsList;
   }
         
   public Map KeywordDistribution(Map urlKeywords, ArrayList<String> keywordsList){

      Map<String,Integer> keywords_distribution = new TreeMap<String,Integer>();
      //String keyword_distribution_file = "testURLs_mega_distribution.csv";

   //setup keyword map
      for(String keyword : keywordsList)
         keywords_distribution.put(keyword, 0);       

   //get keyword distribution
      Iterator keywords_itt = urlKeywords.entrySet().iterator();
      keywords_itt = urlKeywords.entrySet().iterator();
      while (keywords_itt.hasNext()) {
         Map.Entry<String, ArrayList<String>> pairs = (Map.Entry)keywords_itt.next();
         ArrayList<String> keywords = pairs.getValue();
         for(String keyword : keywords){
            int count = (int)keywords_distribution.get(keyword) +1;
            keywords_distribution.put(keyword,count);
         }
      }

   //Sorted Treemap
      ValueComparator distoCompare =  new ValueComparator(keywords_distribution);
      TreeMap<String,Integer> keywords_distribution_sorted = new TreeMap<String,Integer>(distoCompare);
      keywords_distribution_sorted.putAll(keywords_distribution);
   
      return keywords_distribution_sorted;
   }
   
   //largest to smallest 
   class ValueComparator implements Comparator<String> {
      Map<String, Integer> base;
      public ValueComparator(Map<String, Integer> base) {
         this.base = base;
      }
    // Note: this comparator imposes orderings that are inconsistent with equals.    
      public int compare(String a, String b) {
         if (base.get(a) >= base.get(b)) {
            return -1;
         } 
         else {
            return 1;
         } // returning 0 would merge keys
      }
   }   
   
   public Map KeywordMatricMap(Map urlKeywords, ArrayList<String> keywordsList){
   
      Map keywordMatrixMap = new TreeMap(); 
   
   //for all urls
      Iterator keywords_itt = urlKeywords.entrySet().iterator();
      keywords_itt = urlKeywords.entrySet().iterator();
      while (keywords_itt.hasNext()) {
         Map.Entry<String, ArrayList<String>> pairs = (Map.Entry)keywords_itt.next();
         String url = pairs.getKey();
         ArrayList<String> urlkeywords = pairs.getValue();
      
         ArrayList<Integer> keywordVector= new ArrayList<Integer>();
      //for all keywords
         for(String keyword : keywordsList)
            if(urlkeywords.contains(keyword))
               keywordVector.add(1);
            else
               keywordVector.add(0);
               
         int[] keywordArray = new int[keywordsList.size()];
         for (int i=0; i < keywordsList.size(); i++)
            keywordArray[i] = keywordVector.get(i).intValue();
      
      //place array in Map
         keywordMatrixMap.put(url,keywordArray);
      }

      return keywordMatrixMap;
   }

   public int[][] MaptoMatrix(Map keywordMatrixMap){
   
      return null;
   }

   public void BuildTrainingDir(String dirPath, Map urlSummeries){
      new File(dirPath).mkdir();
      
      Iterator text_itt = urlSummeries.entrySet().iterator();
      while (text_itt.hasNext()) {            
         Map.Entry<String, String>  pairs = (Map.Entry)text_itt.next();
         String url = pairs.getKey();
         String summery = pairs.getValue();
         
         long urlHash = foldingHash(url,2147483647);
         String urlHashStr = String.format("%10d", urlHash)+".txt";
         
         try{
            String hashName = dirPath+File.separator+urlHashStr;
            if(new File(hashName).exists())
               System.out.println("collosion deteccted "+hashName);
            PrintWriter train_text = new PrintWriter(hashName, "US-ASCII");
            train_text.println(summery);
            train_text.close();
         }
         catch(Exception e){
            e.printStackTrace();
         }
      
      }
   }
   
   long foldingHash(String s, int M) {
      int intLength = s.length() / 4;
      long sum = 0;
      for (int j = 0; j < intLength; j++) {
         char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
         long mult = 1;
         for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
         }
      }
   
      char c[] = s.substring(intLength * 4).toCharArray();
      long mult = 1;
      for (int k = 0; k < c.length; k++) {
         sum += c[k] * mult;
         mult *= 256;
      }
   
      return(Math.abs(sum) % M);
   }
}