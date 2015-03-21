package com.frank0631.SemanticWebScraper.utils;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by frank0631 on 2/21/15.
 */
public class Printer {

    public static String regexCSV = "[^A-Za-z0-9() ]";

    public static void printKeywordDistribution(Map<String, Integer> keywords_distribution, PrintWriter distribution_file_writer) {

        //print keyword distribution
        for (Map.Entry<String, Integer> keyword_entry : keywords_distribution.entrySet()) {
            String keyword_str = keyword_entry.getKey();
            int keyword_count = keyword_entry.getValue();

            distribution_file_writer.print(keyword_str.replaceAll(regexCSV, "") + ",");
            distribution_file_writer.print(keyword_count + ",");
            for (int i = 0; i < keyword_count; i++)
                distribution_file_writer.print("|");
            distribution_file_writer.println();
        }
    }

    public static void printKeywordJGIDDLDA(Map<String, List<String>> keywordMap, PrintWriter printWriter) {

        int blankrows = 0;
        Iterator keywordsMap_itt = keywordMap.entrySet().iterator();
        while (keywordsMap_itt.hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Map.Entry) keywordsMap_itt.next();
            if (pairs.getValue().size() == 0)
                blankrows++;
        }
        printWriter.println(keywordMap.size() - blankrows);

        keywordsMap_itt = keywordMap.entrySet().iterator();
        while (keywordsMap_itt.hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Map.Entry) keywordsMap_itt.next();

            String document = pairs.getKey();
            ArrayList<String> keywords = pairs.getValue();

            if (keywords.size() > 0) {
                printWriter.print("\t");
                for (String word : keywords)
                    printWriter.print(word.replaceAll(regexCSV, "").replaceAll(" ", "_") + " ");
                printWriter.println();
            }
        }
    }

    public static void printMatrixMap(ArrayList<String> keywordsList, Map keywordMatrixMap, PrintWriter keyword_matrix_writer, boolean printHeader, boolean printURLs) {
        //print keyword distribution

        if (printHeader) {
            keyword_matrix_writer.print("Keywords header,hostname,");
            for (int i = 0; i < keywordsList.size(); ++i) {
                String keyword = keywordsList.get(i);
                if (i + 1 == keywordsList.size())
                    keyword_matrix_writer.println(keyword);
                else
                    keyword_matrix_writer.print("\"" + keyword + "\"" + ",");
            }
        }

        Iterator keywordsMatrix_itt = keywordMatrixMap.entrySet().iterator();
        while (keywordsMatrix_itt.hasNext()) {
            Map.Entry<String, int[]> pairs = (Map.Entry) keywordsMatrix_itt.next();
            String urlstr = pairs.getKey();
            int[] keywordsArray = pairs.getValue();

            if (printURLs) {
                String hostname = "";
                try {
                    URL url = new URL(urlstr);
                    hostname = url.getHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                keyword_matrix_writer.print("\"" + urlstr + "\"" + "," + hostname + ",");
            }
            for (int i = 0; i < keywordsArray.length; ++i) {
                if (i + 1 == keywordsArray.length)
                    keyword_matrix_writer.println(keywordsArray[i]);
                else
                    keyword_matrix_writer.print(keywordsArray[i] + ",");
            }

        }
    }

    public static void printMapString(Map<String, String> stringMap, PrintWriter printWriter) {
        Iterator text_itt = stringMap.entrySet().iterator();
        while (text_itt.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry) text_itt.next();
            String key = pairs.getKey();
            String txt = pairs.getValue();
            printWriter.println(key + "," + txt);
        }
    }

    public static void printArrayString(List<String> arryStr, PrintWriter printWriter) {
        for (String str : arryStr) {
            printWriter.println(str);
        }

    }

    public static void printMapArray(Map<String, List<String>> arrayMap, PrintWriter printWriter) {
        Iterator text_itt = arrayMap.entrySet().iterator();
        while (text_itt.hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Map.Entry) text_itt.next();
            String key = pairs.getKey();
            printWriter.print(key + ",");
            ArrayList<String> strings = pairs.getValue();
            for (String str : strings) {
                printWriter.print(str + ",");
            }
            printWriter.println();
        }
    }

}
