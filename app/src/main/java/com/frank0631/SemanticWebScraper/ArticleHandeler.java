package com.frank0631.SemanticWebScraper;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Scrapes articles from URLs
public class ArticleHandeler {

   String storePath;

   public ArticleHandeler(String storePath){
      System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
      this.storePath=storePath;
      new File(storePath).mkdir();

      ArticleExtractor articleExtractor = new ArticleExtractor();
      HTMLHighlighter htmlHighlighter = HTMLHighlighter.newHighlightingInstance();
   }


   public Map summeryMap(List<String> urls, int wordCountLimit, boolean cacheOnly) {

      Map url_text_map = new LinkedHashMap();

      //loop urls
      Iterator<String> url_itt = urls.iterator();
      while (url_itt.hasNext()) {
         String url = url_itt.next();
         System.out.println("extracting " + url);
         try {
            String txt_summery = ArticleFetcher.extractorArticle(url, false, cacheOnly, storePath);
            if (txt_summery != null) {
               int summery_words = ArticleFetcher.countWords(txt_summery);
               if (summery_words >= wordCountLimit)
                  url_text_map.put(url, txt_summery);
               else {
                  System.out.print("Not enough words in " + url);
                  url_text_map.put(url, null);
               }
            }
         }
         //ioe.printStackTrace();
         catch (IOException ioe) {
            System.out.print("Could not reach " + url);
            url_text_map.put(url, null);
            //ioe.printStackTrace();
         } catch (ArticleFetcher.UnsupportedExtension ioe) {
            System.out.print("Could not read " + url);
            url_text_map.put(url, null);
         }
      }

      return url_text_map;
   }

   public void cacheArticles(List<String> urls, int nThreads){

      try {
         //FIXME multiple threads writing to disk seems like a bad idea
         ExecutorService executor = Executors.newFixedThreadPool(nThreads);
         Iterator<String> url_itt = urls.iterator();
         while (url_itt.hasNext()) {
            String urlstr = url_itt.next();
            Runnable urlFetcher = new ArticleFetcher(storePath,urlstr);
            executor.execute(urlFetcher);
         }
         executor.shutdown();
         while (!executor.isTerminated()) {
            Thread.sleep(100);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      System.out.println("Finished caching URLs");
   }



}