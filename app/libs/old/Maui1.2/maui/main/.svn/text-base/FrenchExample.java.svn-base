package maui.main;

import java.util.HashSet;

import maui.stemmers.FrenchStemmer;
import maui.stemmers.Stemmer;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsFrench;

import org.wikipedia.miner.model.Wikipedia;

public class FrenchExample {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		// location of the data
		String trainDir = "data/term_assignment/train_fr";
		String testDir = "data/term_assignment/test_fr";
		
		// name of the file for storing the model
		String modelName = "french_model";
		
		// language specific settings
		Stemmer stemmer = new FrenchStemmer();
		Stopwords stopwords = new StopwordsFrench();
		String language = "fr";
		String encoding = "UTF-8";
		
		// vocabulary to use for term assignment
		String vocabulary = "agrovoc_fr";
		String format = "skos";
		
		// how many topics per document to extract
		int numTopicsToExtract = 8;
		
		// maui objects
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		Wikipedia wikipedia = new Wikipedia("localhost", "enwiki_20090306", "root", null);
		
		// Settings for the model builder
		modelBuilder.setDirName(trainDir);
		modelBuilder.setModelName(modelName);
		modelBuilder.setVocabularyFormat(format);
		modelBuilder.setVocabularyName(vocabulary);
		modelBuilder.setStemmer(stemmer);
		modelBuilder.setStopwords(stopwords);
		modelBuilder.setDocumentLanguage(language);
		modelBuilder.setEncoding(encoding);
		modelBuilder.setDebug(true);
		modelBuilder.setWikipedia(wikipedia);
		
		// Which features to use?
		modelBuilder.setBasicFeatures(true);
		modelBuilder.setKeyphrasenessFeature(true);
		modelBuilder.setFrequencyFeatures(false);
		modelBuilder.setPositionsFeatures(true);
		modelBuilder.setLengthFeature(true);
		modelBuilder.setNodeDegreeFeature(true);
		modelBuilder.setBasicWikipediaFeatures(true);
		modelBuilder.setAllWikipediaFeatures(false);
		
		// Run model builder
		modelBuilder.buildModel(modelBuilder.collectStems());
		modelBuilder.saveModel();
		
		// Settings for the topic extractor
		topicExtractor.setDirName(testDir);
		topicExtractor.setModelName(modelName);
		topicExtractor.setVocabularyName(vocabulary);
		topicExtractor.setVocabularyFormat(format);
		topicExtractor.setStemmer(stemmer);
		topicExtractor.setStopwords(stopwords);
		topicExtractor.setDocumentLanguage(language);
		topicExtractor.setDebug(true);
		topicExtractor.setNumTopics(numTopicsToExtract); 
		topicExtractor.setWikipedia(wikipedia);
		
		// Run topic extractor
		topicExtractor.loadModel();
		topicExtractor.extractKeyphrases(topicExtractor.collectStems());
	}

}
