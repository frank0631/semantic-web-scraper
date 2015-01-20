package com.frank0631.SemanticWebScraper;

//import com.entopix.maui.main.MauiWrapper;
//import com.entopix.maui.util.Topic;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

import java.io.File;
import java.util.List;

public class DictionaryBuilder {

   public DictionaryBuilder(){

       String modelname = "SemEval2010";
       File modelfile = new File(modelname);
       if(!modelfile.exists()) {
      /*
            -l directory   (directory with the data)
             -m model       (model file)
             -v vocabulary  (vocabulary name)
             -f {skos|text} (vocabulary format)
             -w database@server (wikipedia location)
       */
           String[] trainOptions = new String[]{
                   "-l", "SemEval2010-Maui/maui-semeval2010-train",
                   "-v", "lcsh.rdf.gz",
                   "-f", "skos",
                   "-x", "2",
                   "-o", "2",
                   "-m", modelname};
           try {

               MauiModelBuilder mauiModelBuilder = new MauiModelBuilder();
               mauiModelBuilder.setBasicFeatures(true);
               mauiModelBuilder.setOptions(trainOptions);
               System.out.println(mauiModelBuilder.getOptions());

               MauiFilter frankModel = mauiModelBuilder.buildModel();
               mauiModelBuilder.saveModel(frankModel);

           } catch (Exception ex) {
               ex.printStackTrace();
           }
       }

       String[] testOptions = new String[]{"-l", "SemEval2010-Maui/maui-semeval2010-test", "-v", "lcsh.rdf.gz", "-f", "skos", "-m", modelname};
       try {

           MauiTopicExtractor mauiTopicExtractor = new MauiTopicExtractor();
           mauiTopicExtractor.setOptions(testOptions);
           mauiTopicExtractor.loadModel();
           List<MauiDocument> testFiles = mauiTopicExtractor.loadDocuments();
           List<MauiTopics> testTopics = mauiTopicExtractor.extractTopics(testFiles);

           for (MauiTopics tts : testTopics) {
               System.out.println(tts.getFilePath());

               for (Topic topic : tts.getTopics())
                System.out.print(topic.getTitle()+":"+topic.getProbability()+", ");
           }

       } catch (Exception ex) {
           ex.printStackTrace();
       }

   }

   public static void main(String[] args){
      new DictionaryBuilder();
   }


}