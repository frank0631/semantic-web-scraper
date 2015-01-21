package com.frank0631.SemanticWebScraper;

import jgibblda.Estimator;
import jgibblda.LDACmdOption;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class semanticWebScraper {

    public static String regexCSV = "[^A-Za-z0-9() ]";

    //hardcode for now
    String base_file_name = "frank_mega";
    String url_file_name = base_file_name + "_urls.txt";
    String valid_file_name = base_file_name + "_valid.txt";
    String invalid_file_name = base_file_name + "_invalid.txt";
    String distribution_file_name = base_file_name + "_distribution.csv";
    String matrix_file_name = base_file_name + "_matrix.csv";
    String arff_file_name = base_file_name + "_matrix.arff";
    String LDADir = System.getProperty("user.dir")+File.separator+"LDA";
    String JGIDDLDA_file_name = base_file_name + "_JGIDDLDA.txt";

    ArrayList<String> URLs;
    ArrayList<String> Bad_URLs;
    Map url_text_map;

    int numClusters = 50;
    boolean cachedArticle = true;
    int wordCountLimit = 50;

    ArrayList<String> keywords_list;

    public semanticWebScraper() {

        try {
            URLCouples URLs;
            Map summeryMap;

            //get URLs
            URLHandeler urlHandel = new URLHandeler();
            //URLs = urlHandel.readURLrows(url_file_name, 20);
            URLs = urlHandel.readURLFromFiles(valid_file_name, invalid_file_name);

            PrintWriter valid_file_writer = new PrintWriter(valid_file_name, "US-ASCII");
            PrintWriter invalid_file_writer = new PrintWriter(invalid_file_name, "US-ASCII");
            printArrayString(URLs.ValidURLs, valid_file_writer);
            printArrayString(URLs.inValidURLs, invalid_file_writer);
            valid_file_writer.close();
            invalid_file_writer.close();

            //get Summeries
            ArticleHandeler articleHandeler = new ArticleHandeler("URLs/");
            if (!cachedArticle)
                articleHandeler.cacheArticles(URLs.ValidURLs, 10);
            summeryMap = articleHandeler.summeryMap(URLs.ValidURLs, wordCountLimit, cachedArticle);
            System.out.println("Number of URL Summeries " + summeryMap.size());

            Map keywordMap;
            Map keywordDistro;
            Map keywordMatrixMap;
            ArrayList<String> keywords;
            ArrayList<String> keywords_sorted;

            KeywordHandeler keywordHandeler = new KeywordHandeler();
            //keywordHandeler.BuildTrainingDir("frank_train",summeryMap);
            keywordHandeler.keywordsLimit = 40;
            keywordMap = keywordHandeler.ExtractKeywords(summeryMap);
            keywords = keywordHandeler.ListKeywords(keywordMap);
            keywordDistro = keywordHandeler.KeywordDistribution(keywordMap, keywords);
            keywords_sorted = new ArrayList<String>(keywordDistro.keySet());

            keywordMatrixMap = keywordHandeler.KeywordMatricMap(keywordMap, keywords_sorted);

            PrintWriter distribution_file_writer = new PrintWriter(distribution_file_name, "US-ASCII");
            PrintWriter matrix_file_writer = new PrintWriter(matrix_file_name, "US-ASCII");
            PrintWriter JGIDDLDA_file_writer = new PrintWriter(LDADir+File.separator+JGIDDLDA_file_name, "US-ASCII");
            printKeywordDistribution(keywordDistro, distribution_file_writer);
            printMatrixMap(keywords_sorted,keywordMatrixMap, matrix_file_writer, true, true);
            printKeywordJGIDDLDA(keywordMap,JGIDDLDA_file_writer);
            distribution_file_writer.close();
            matrix_file_writer.close();
            JGIDDLDA_file_writer.close();

            CSVtoARFF(matrix_file_name, arff_file_name);

            //LDA
            File LDAFile = new File(LDADir);
            if(!LDAFile.exists() || !LDAFile.isDirectory())
                LDAFile.mkdir();

            LDACmdOption ldaOption = new LDACmdOption();
//            ldaOption.inf = true;
            ldaOption.modelName = base_file_name+"model";
            ldaOption.est = true;
            ldaOption.K = numClusters;
            ldaOption.dfile = JGIDDLDA_file_name;
            ldaOption.dir = LDADir;
            ldaOption.twords = keywords_sorted.size();
            ldaOption.niters = 500;
            ldaOption.savestep = 500 + 1;

            Estimator e = new Estimator();
            e.init(ldaOption);
            e.estimate();

//            Inferencer inferencer = new Inferencer();
//            inferencer.init(ldaOption);
//
//            String [] test = {"politics bill clinton", "law court", "football match"};
//            Model newModel = inferencer.inference(test);

            //LDADataset.readDataSet(matrix_file_name);



        } catch (Exception e) {
            e.printStackTrace();
        }

        //build feature matrix

        //loop urls
        //plot in point cloud

        //k_means
        //graph representation

    }

    public static void main(String[] args) {
        semanticWebScraper sws = new semanticWebScraper();
    }

    private static void printKeywordDistribution(Map<String, Integer> keywords_distribution, PrintWriter distribution_file_writer) {

        //print keyword distribution
        for (Map.Entry<String, Integer> keyword_entry : keywords_distribution.entrySet())
        {
            String keyword_str = keyword_entry.getKey();
            int keyword_count = keyword_entry.getValue();

            distribution_file_writer.print(keyword_str.replaceAll(regexCSV, "") + ",");
            distribution_file_writer.print( keyword_count+ ",");
            for (int i = 0; i < keyword_count; i++)
                distribution_file_writer.print("|");
            distribution_file_writer.println();
        }
    }

    private static void printKeywordJGIDDLDA(Map<String, List<String>>  keywordMap, PrintWriter printWriter) {

        int blankrows = 0;
        Iterator keywordsMap_itt = keywordMap.entrySet().iterator();
        while (keywordsMap_itt .hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Map.Entry) keywordsMap_itt.next();
            if(pairs.getValue().size()==0)
                blankrows++;
        }
        printWriter.println(keywordMap.size()-blankrows);

        keywordsMap_itt = keywordMap.entrySet().iterator();
        while (keywordsMap_itt .hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Map.Entry) keywordsMap_itt .next();

            String document = pairs.getKey();
            ArrayList<String> keywords = pairs.getValue();

            if(keywords.size()>0) {
                printWriter.print("\t");
                for (String word : keywords)
                    printWriter.print(word.replaceAll(regexCSV, "").replaceAll(" ","_")+" ");
                printWriter.println();
            }
        }
    }

    private static void printMatrixMap(ArrayList<String> keywordsList, Map keywordMatrixMap, PrintWriter keyword_matrix_writer, boolean printHeader, boolean printURLs) {
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

    public static void CSVtoARFF(String csvfilename, String arfffilename) {
        try {
            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(csvfilename));
            Instances data = loader.getDataSet();

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(arfffilename));
            saver.setDestination(new File(arfffilename));
            saver.writeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}