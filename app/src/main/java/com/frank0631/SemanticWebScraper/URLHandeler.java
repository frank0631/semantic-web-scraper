package com.frank0631.SemanticWebScraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Scrapes list of URLs
public class URLHandeler {

   public URLHandeler() {
   }

   //TODO make this return URLCouples instead of writing to file
   public void readURLsFromBookmarks(String bookmarks_filename, String urlout_filename, boolean inHREF) {
      ArrayList<String> url_array = new ArrayList<String>();
      File bookmarks_file = new File(bookmarks_filename);

      try {
         Scanner scanner = new Scanner(bookmarks_file);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Pattern p;
            if (inHREF)
               p = Pattern.compile("HREF=\"(.*?)\"");
            else
               p = Pattern.compile("http(.*)");
            Matcher m = p.matcher(line);
            while (m.find()) {
               if (inHREF)
                  url_array.add(m.group(1));
               else
                  url_array.add("http" + m.group(1));
            }
            scanner.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         PrintWriter extracted_urls = new PrintWriter(urlout_filename, "US-ASCII");
         for (String url : url_array)
            extracted_urls.println(url);
         extracted_urls.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public URLCouples readURLFromFiles(String validfilename, String invalidfilename) {
      File validfile = new File(validfilename);
      File invalidfile = new File(invalidfilename);
      URLCouples urlCouples = new URLCouples();

      try {
         BufferedReader validFileBufferedReader = new BufferedReader(new FileReader(validfile));
         BufferedReader invalidFileBufferedReader = new BufferedReader(new FileReader(invalidfile));

         for (String line; (line = validFileBufferedReader.readLine()) != null; )
            urlCouples.ValidURLs.add(line);

         for (String line; (line = invalidFileBufferedReader.readLine()) != null; )
            urlCouples.inValidURLs.add(line);

      } catch (Exception e) {
         e.printStackTrace();
      }
      return urlCouples;
   }

   public URLCouples readURLrows(String readfilename, int nThreads) {
      File readfile = new File(readfilename);
      URLCouples urlCouples = new URLCouples();

      if (readfile.exists())
         try {
            BufferedReader br = new BufferedReader(new FileReader(readfilename));
            ExecutorService executor = Executors.newFixedThreadPool(nThreads);

            //Multi Threaded URL Fetching
            for (String line; (line = br.readLine()) != null; ) {
               Runnable urlFetcher = new URLFetcher(urlCouples, line);
               executor.execute(urlFetcher);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
               Thread.sleep(100);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      System.out.println("Finished pinging URLs");
      return urlCouples;
   }



}