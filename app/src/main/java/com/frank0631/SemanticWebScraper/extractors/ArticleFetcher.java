package com.frank0631.SemanticWebScraper.extractors;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Created by frank on 1/18/15.
 */
public class ArticleFetcher implements Runnable {

    public static String regexURL = "[^A-Za-z0-9()]";
    public static int reachTimeoutSec = 3;
    public static int readTimeoutSec = 6;
    public static String[] skipExt = (new String[]{".pdf", ".doc", ".ppt"});

    public static ArticleExtractor article_extractor;
    public static HTMLHighlighter html_highlighter;
    public String urlstr;
    public String storePath;

    public ArticleFetcher(String storePath, String urlstr) {
        //TODO pass in these two objects when threadsafe
        article_extractor = new ArticleExtractor();
        html_highlighter = HTMLHighlighter.newHighlightingInstance();

        this.storePath = storePath;
        this.urlstr = urlstr;
    }

    @Override
    public void run() {
        fetch();
    }

    private void fetch() {
        try {
            htmlHilight(urlstr, true);
            extractorArticle(urlstr, true, false, storePath);
            System.out.println("cached " + urlstr);
        } catch (IOException ioe) {
            System.out.println("could not cache " + urlstr);
            //ioe.printStackTrace();
        } catch (UnsupportedExtension ioe) {
            System.out.println("could not cache " + urlstr);
            //ioe.printStackTrace();
        }
    }

    public String htmlHilight(String url_str, boolean write) throws IOException, UnsupportedExtension {

        for (String skip : skipExt) {
            if (url_str.toLowerCase().endsWith(skip))
                throw new UnsupportedExtension("UnsupportedExtension " + skip);
        }

        String url_path = url_str.replaceAll(regexURL, "").toLowerCase();
        String article_html_file = storePath + url_path + "/highlighted.html";
        String article_html = null;

        URL url = new URL(url_str);
        File html_file = new File(article_html_file);

        if (html_file.exists())//read
            article_html = fileToString(html_file);
        else {
            if (URLFetcher.ping(url)) {
                try {
                    article_html = html_highlighter.process(url, article_extractor);
                } catch (BoilerpipeProcessingException bpe) {
                    article_html = "";
                    bpe.printStackTrace();
                } catch (SAXException saxe) {
                    article_html = "";
                    saxe.printStackTrace();
                }

                if (write) {
                    new File(storePath + url_path).mkdir();
                    PrintWriter out_html = new PrintWriter(article_html_file, "UTF-8");
                    out_html.println("<base href=\"" + url + "\" >");
                    out_html.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
                    out_html.println(article_html);
                    out_html.close();
                }
            } else
                return "";
        }

        return article_html;
    }

    public static String extractorArticle(String url_str, boolean write, boolean cachedArticle, String storePath) throws IOException, UnsupportedExtension {

        for (String skip : skipExt) {
            if (url_str.toLowerCase().endsWith(skip))
                throw new UnsupportedExtension("UnsupportedExtension " + skip);
        }

        String url_path = url_str.replaceAll(regexURL, "").toLowerCase();
        String article_html_file = storePath + url_path + "/highlighted.html";
        String article_html = null;

        File html_file;
        String article_text_file = storePath + url_path + "/text.txt";
        String article_text;
        File text_file = new File(article_text_file);
        if (text_file.exists())//read
            article_text = fileToString(text_file);
        else {
            html_file = new File(article_html_file);
            if (html_file.exists()) { //read if html exist
                article_html = fileToString(html_file);
                try {
                    article_text = article_extractor.getText(article_html);
                } catch (BoilerpipeProcessingException bpe) {
                    article_text = "";
                    bpe.printStackTrace();
                }
            } else if (cachedArticle == false) { //read if no html exist
                URL url = new URL(url_str);
                if (URLFetcher.ping(url))
                    try {
                        article_text = article_extractor.getText(url);
                    } catch (BoilerpipeProcessingException bpe) {
                        article_text = "";
                        bpe.printStackTrace();
                    }
                else
                    article_text = ""; //if no html exist set to blank
            } else
                article_text = "";

            if (write) {
                new File(storePath + url_path).mkdir();
                PrintWriter out_text = new PrintWriter(article_text_file, "US-ASCII");
                out_text.println(article_text);
                out_text.close();
            }
        }
        return article_text;
    }

    public static class UnsupportedExtension extends Exception {
        public UnsupportedExtension(String msg) {
            super(msg);
        }
    }

    public static String fileToString(File in) throws IOException {
        FileInputStream is = new FileInputStream(in);
        byte[] b = new byte[(int) in.length()];
        is.read(b, 0, (int) in.length());
        String contents = new String(b);
        is.close();
        return contents;
    }

    public static int countWords(String summery) {
        String trimmed = summery.trim();
        if (trimmed.isEmpty())
            return 0;
        return trimmed.split("\\s+").length;
    }

}