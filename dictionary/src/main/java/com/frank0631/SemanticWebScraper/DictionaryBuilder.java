package com.frank0631.SemanticWebScraper;

//import com.entopix.maui.main.MauiWrapper;
//import com.entopix.maui.util.Topic;

import com.entopix.maui.main.MauiModelBuilder;

public class DictionaryBuilder {

   public DictionaryBuilder(){

      MauiModelBuilder mauiModelBuilder = new MauiModelBuilder();
      mauiModelBuilder.listOptions();

   }

   public static void main(String[] args){
      new DictionaryBuilder();
   }


}