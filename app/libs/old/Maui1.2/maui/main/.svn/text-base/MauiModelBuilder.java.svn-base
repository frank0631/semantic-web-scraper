package maui.main;

/*
 *    MauiModelBuilder.java
 *    Copyright (C) 2001-2009 Eibe Frank, Olena Medelyan
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Wikipedia;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import maui.filters.MauiFilter;
import maui.stemmers.*;
import maui.stopwords.*;

/**
 * Builds a topic indexing model from the documents in a given
 * directory.  Assumes that the file names for the documents end with
 * ".txt".  Assumes that files containing corresponding
 * author-assigned keyphrases end with ".key". Optionally an encoding
 * for the documents/keyphrases can be defined (e.g. for Chinese
 * text).
 *
 * Valid options are:<p>
 *
 * -l "directory name"<br>
 * Specifies name of directory.<p>
 *
 * -m "model name"<br>
 * Specifies name of model.<p>
 *
 * -e "encoding"<br>
 * Specifies encoding.<p>
 * 
 *  -w "WikipediaDatabase@WikipediaServer" <br>
 * Specifies wikipedia data.<p>
 * 
 * -v "vocabulary name" <br>
 * Specifies vocabulary name (e.g. agrovoc or none).<p>
 * 
 * -f "vocabulary format" <br>
 * Specifies vocabulary format (txt or skos).<p>
 *
 * -i "document language" <br>
 * Specifies document language (en, es, de, fr).<p>
 *
 * -d<br>
 * Turns debugging mode on.<p>
 * 
 * -x "length"<br>
 * Sets maximum phrase length (default: 3).<p>
 *
 * -y "length"<br>
 * Sets minimum phrase length (default: 1).<p>
 *
 * -o "number"<br>
 * Sets the minimum number of times a phrase needs to occur (default: 2). <p>
 *
 * -s "stopwords class"<br>
 * Sets the name of the class implementing the stop words (default: StopwordsEnglish).<p>
 *
 * -t "stemmer class "<br>
 * Sets stemmer to use (default: PorterStemmer). <p>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz), Olena Medelyan (olena@cs.waikato.ac.nz)
 * @version 1.0
 */
public class MauiModelBuilder implements OptionHandler {

	/** Name of directory */
	String inputDirectoryName = null;

	/** Name of model */
	String modelName = null;

	/** Vocabulary name */
	String vocabularyName = "none";

	/** Format of the vocabulary {skos,text} */
	String vocabularyFormat = null;

	/** Document language {en,es,de,fr,...} */
	String documentLanguage = "en";

	/** Document encoding */
	String documentEncoding = "default";

	/** Debugging mode? */
	boolean debugMode = false;

	/** Maximum length of phrases */
	private int maxPhraseLength = 5;

	/** Minimum length of phrases */
	private int minPhraseLength = 1;

	/** Minimum number of occurences of a phrase */
	private int minNumOccur = 1;
	
	/** Wikipedia object */
	private Wikipedia wikipedia = null;
	
	/** Classifier */
	private Classifier classifier = null;
	
	/** Name of the server with the mysql Wikipedia data */ 
	private String wikipediaServer = "localhost"; 
	
	/** Name of the database with Wikipedia data */
	private String wikipediaDatabase = "database";
	
	/** Name of the directory with Wikipedia data in files */
	private String wikipediaDataDirectory = null;
	
	/** Should Wikipedia data be cached first? */
	private boolean cacheWikipediaData = false;
	
	/** Minimum keyphraseness of a string */
	private double minKeyphraseness = 0.01;

	/** Minimum sense probability or commonness */
	private double minSenseProbability = 0.005;

	/** Minimum number of the context articles */
	private int contextSize = 5;

	/** Use basic features  
	 * TFxIDF & First Occurrence */
	boolean useBasicFeatures = true;

	/** Use domain keyphraseness feature */
	boolean useKeyphrasenessFeature = true;

	/** Use frequency features
	 * TF & IDF additionally */
	boolean useFrequencyFeatures = true;

	/** Use occurrence position features
	 * LastOccurrence & Spread */
	boolean usePositionsFeatures = true;

	/** Use thesaurus features
	 * Node degree  */
	boolean useNodeDegreeFeature = true;

	/** Use length feature */
	boolean useLengthFeature = true;

	/** Use basic Wikipedia features 
	 *  Wikipedia keyphraseness & Total Wikipedia keyphraseness */
	boolean useBasicWikipediaFeatures = false;

	/** Use all Wikipedia features 
	 * Inverse Wikipedia frequency & Semantic relatedness*/
	boolean useAllWikipediaFeatures = false;

	/** Maui filter object */
	private MauiFilter mauiFilter = null;

	/** Stemmer to be used */
	private Stemmer stemmer = new PorterStemmer();

	/** Llist of stopwords to be used */
	private Stopwords stopwords = new StopwordsEnglish();

	public Stopwords getStopwords() {
		return stopwords;
	}

	public void setStopwords(Stopwords stopwords) {
		this.stopwords = stopwords;
	}

	public Stemmer getStemmer() {
		return stemmer;
	}

	public void setStemmer(Stemmer stemmer) {
		this.stemmer = stemmer;
	}
	
	public void setWikipedia(Wikipedia wikipedia) {
		this.wikipedia = wikipedia;
	}

	public String getWikipediaDatabase() {
		return wikipediaDatabase;
	}

	public void setWikipediaDatabase(String wikipediaDatabase) {
		this.wikipediaDatabase = wikipediaDatabase;
	}
	
	public void setWikipediaServer(String wikipediaServer) {
		this.wikipediaServer = wikipediaServer;
	}
	
	public void setWikipediaConnection(String wikipediaConnection) {
		int at = wikipediaConnection.indexOf("@");
		setWikipediaDatabase(wikipediaConnection.substring(0,at));
		setWikipediaServer(wikipediaConnection.substring(at+1));
	}
	
	public void setWikipediaDataDirectory(String wikipediaDataDirectory) {
		this.wikipediaDataDirectory = wikipediaDataDirectory;
	}
	public void setCachWikipediaData(boolean cacheWikipediaData) {
		this.cacheWikipediaData = cacheWikipediaData;
	}
	
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}


	
	public int getMinNumOccur() {
		return minNumOccur;
	}

	public void setMinNumOccur(int minNumOccur) {
		this.minNumOccur = minNumOccur;
	}

	public int getMaxPhraseLength() {
		return maxPhraseLength;
	}

	public void setMaxPhraseLength(int maxPhraseLength) {
		this.maxPhraseLength = maxPhraseLength;
	}

	public int getMinPhraseLength() {
		return minPhraseLength;
	}

	public void setMinPhraseLength(int minPhraseLength) {
		this.minPhraseLength = minPhraseLength;
	}

	public boolean getDebug() {
		return debugMode;
	}

	public void setDebug(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getEncoding() {
		return documentEncoding;
	}

	public void setEncoding(String documentEncoding) {
		this.documentEncoding = documentEncoding;
	}

	public String getVocabularyName() {
		return vocabularyName;
	}

	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}

	public String getDocumentLanguage() {
		return documentLanguage;
	}

	public void setDocumentLanguage(String documentLanguage) {
		this.documentLanguage = documentLanguage;
	}

	public String getVocabularyFormat() {
		return vocabularyFormat;
	}

	public void setVocabularyFormat(String vocabularyFormat) {
		this.vocabularyFormat = vocabularyFormat;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getDirName() {
		return inputDirectoryName;
	}

	public void setDirName(String inputDirectoryName) {
		this.inputDirectoryName = inputDirectoryName;
	}

	public void setBasicFeatures(boolean useBasicFeatures) {
		this.useBasicFeatures = useBasicFeatures;
	}

	public void setKeyphrasenessFeature(boolean useKeyphrasenessFeature) {
		this.useKeyphrasenessFeature = useKeyphrasenessFeature;
	}

	public void setFrequencyFeatures(boolean useFrequencyFeatures) {
		this.useFrequencyFeatures = useFrequencyFeatures;
	}

	public void setPositionsFeatures(boolean usePositionsFeatures) {
		this.usePositionsFeatures = usePositionsFeatures;
	}

	public void setNodeDegreeFeature(boolean useNodeDegreeFeature) {
		this.useNodeDegreeFeature = useNodeDegreeFeature;
	}

	public void setLengthFeature(boolean useLengthFeature) {
		this.useLengthFeature = useLengthFeature;
	}

	public void setBasicWikipediaFeatures(boolean useBasicWikipediaFeatures) {
		this.useBasicWikipediaFeatures = useBasicWikipediaFeatures;
	}

	public void setAllWikipediaFeatures(boolean useAllWikipediaFeatures) {
		this.useAllWikipediaFeatures = useAllWikipediaFeatures;
	}
	
	public void setContextSize(int contextSize) {
		this.contextSize = contextSize;
	}
	
	public void setMinSenseProbability(double minSenseProbability) {
		this.minSenseProbability = minSenseProbability;
	}
	
	public void setMinKeyphraseness(double minKeyphraseness) {
		this.minKeyphraseness = minKeyphraseness;
	}
	

	/**
	 * Parses a given list of options controlling the behaviour of this object.
	 * Valid options are:<p>
	 *
	 * -l "directory name" <br>
	 * Specifies name of directory.<p>
	 *
	 * -m "model name" <br>
	 * Specifies name of model.<p>
	 *
	 * -v "vocabulary name" <br>
	 * Specifies vocabulary name.<p>
	 * 
	 * -f "vocabulary format" <br>
	 * Specifies vocabulary format.<p>
	 *    
	 * -i "document language" <br>
	 * Specifies document language.<p>
	 * 
	 * -e "encoding" <br>
	 * Specifies encoding.<p>
	 * 
	 *  -w "WikipediaDatabase@WikipediaServer" <br>
	 * Specifies wikipedia data.<p>
	 * 
	 * -d<br>
	 * Turns debugging mode on.<p>
	 *
	 * -x "length"<br>
	 * Sets maximum phrase length (default: 3).<p>
	 *
	 * -y "length"<br>
	 * Sets minimum phrase length (default: 3).<p>
	 *
	 * -o "number"<br>
	 * The minimum number of times a phrase needs to occur (default: 2). <p>
	 *
	 * -s "name of class implementing list of stop words"<br>
	 * Sets list of stop words to used (default: StopwordsEnglish).<p>
	 *
	 * -t "name of class implementing stemmer"<br>
	 * Sets stemmer to use (default: IteratedLovinsStemmer). <p>
	 *
	 * @param options the list of options as an array of strings
	 * @exception Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {

		String dirName = Utils.getOption('l', options);
		if (dirName.length() > 0) {
			setDirName(dirName);
		} else {
			setDirName(null);
			throw new Exception("Name of directory required argument.");
		}

		String modelName = Utils.getOption('m', options);
		if (modelName.length() > 0) {
			setModelName(modelName);
		} else {
			setModelName(null);
			throw new Exception("Name of model required argument.");
		}

		String vocabularyName = Utils.getOption('v', options);
		if (vocabularyName.length() > 0) {
			setVocabularyName(vocabularyName);
		} 

		String vocabularyFormat = Utils.getOption('f', options);

		if (!getVocabularyName().equals("none") && !getVocabularyName().equals("wikipedia")) {
			if (vocabularyFormat.length() > 0) {
				if (vocabularyFormat.equals("skos")
						|| vocabularyFormat.equals("text")) {
					setVocabularyFormat(vocabularyFormat);
				} else {
					throw new Exception(
							"Unsupported format of vocabulary. It should be either \"skos\" or \"text\".");
				}
			} else {
				setVocabularyFormat(null);
				throw new Exception(
						"If a controlled vocabulary is used, format of vocabulary required argument (skos or text).");
			}
		} else {
			setVocabularyFormat(null);
		}

		String encoding = Utils.getOption('e', options);
		if (encoding.length() > 0) {
			setEncoding(encoding);
		} else {
			setEncoding("default");
		}
		
		String wikipediaConnection = Utils.getOption('w', options);
		if (wikipediaConnection.length() > 0) {
			setWikipediaConnection(wikipediaConnection);
		} 

		String documentLanguage = Utils.getOption('i', options);
		if (documentLanguage.length() > 0) {
			setDocumentLanguage(documentLanguage);
		} else {
			setDocumentLanguage("en");
		}

		String maxPhraseLengthString = Utils.getOption('x', options);
		if (maxPhraseLengthString.length() > 0) {
			setMaxPhraseLength(Integer.parseInt(maxPhraseLengthString));
		} else {
			setMaxPhraseLength(5);
		}
		String minPhraseLengthString = Utils.getOption('y', options);
		if (minPhraseLengthString.length() > 0) {
			setMinPhraseLength(Integer.parseInt(minPhraseLengthString));
		} else {
			setMinPhraseLength(1);
		}
		String minNumOccurString = Utils.getOption('o', options);
		if (minNumOccurString.length() > 0) {
			setMinNumOccur(Integer.parseInt(minNumOccurString));
		} else {
			setMinNumOccur(2);
		}

		String stopwordsString = Utils.getOption('s', options);
		if (stopwordsString.length() > 0) {
			stopwordsString = "kea.stopwords.".concat(stopwordsString);
			setStopwords((Stopwords) Class.forName(stopwordsString)
					.newInstance());
		}

		String stemmerString = Utils.getOption('t', options);
		if (stemmerString.length() > 0) {
			stemmerString = "kea.stemmers.".concat(stemmerString);
			setStemmer((Stemmer) Class.forName(stemmerString).newInstance());
		}
		setDebug(Utils.getFlag('d', options));
		Utils.checkForRemainingOptions(options);
	}

	/**
	 * Gets the current option settings.
	 *
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String[] getOptions() {

		String[] options = new String[23];
		int current = 0;

		options[current++] = "-l";
		options[current++] = "" + (getDirName());
		options[current++] = "-m";
		options[current++] = "" + (getModelName());
		options[current++] = "-v";
		options[current++] = "" + (getVocabularyName());
		options[current++] = "-f";
		options[current++] = "" + (getVocabularyFormat());
		options[current++] = "-e";
		options[current++] = "" + (getEncoding());
		options[current++] = "-i";
		options[current++] = "" + (getDocumentLanguage());

		if (getDebug()) {
			options[current++] = "-d";
		}
		options[current++] = "-x";
		options[current++] = "" + (getMaxPhraseLength());
		options[current++] = "-y";
		options[current++] = "" + (getMinPhraseLength());
		options[current++] = "-o";
		options[current++] = "" + (getMinNumOccur());
		options[current++] = "-s";
		options[current++] = "" + (getStopwords().getClass().getName());
		options[current++] = "-t";
		options[current++] = "" + (getStemmer().getClass().getName());

		while (current < options.length) {
			options[current++] = "";
		}
		return options;
	}

	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options
	 */
	public Enumeration<Option> listOptions() {

		Vector<Option> newVector = new Vector<Option>(12);

		newVector.addElement(new Option("\tSpecifies name of directory.", "l",
				1, "-l <directory name>"));
		newVector.addElement(new Option("\tSpecifies name of model.", "m", 1,
				"-m <model name>"));
		newVector.addElement(new Option("\tSpecifies vocabulary name.", "v", 1,
				"-v <vocabulary name>"));
		newVector.addElement(new Option(
				"\tSpecifies vocabulary format (text or skos or none).", "f",
				1, "-f <vocabulary format>"));
		newVector.addElement(new Option(
				"\tSpecifies document language (en (default), es, de, fr).",
				"i", 1, "-i <document language>"));
		newVector.addElement(new Option("\tSpecifies encoding.", "e", 1,
				"-e <encoding>"));
		newVector.addElement(new Option("\tSpecifies wikipedia database and server.", "w", 1,
		"-w <wikipediaDatabase@wikipediaServer>"));
		newVector.addElement(new Option("\tTurns debugging mode on.", "d", 0,
				"-d"));
		newVector.addElement(new Option(
				"\tSets the maximum phrase length (default: 5).", "x", 1,
				"-x <length>"));
		newVector.addElement(new Option(
				"\tSets the minimum phrase length (default: 1).", "y", 1,
				"-y <length>"));
		newVector.addElement(new Option(
				"\tSet the minimum number of occurences (default: 2).", "o", 1,
				"-o"));
		newVector
				.addElement(new Option(
						"\tSets the list of stopwords to use (default: StopwordsEnglish).",
						"s", 1, "-s <name of stopwords class>"));
		newVector.addElement(new Option(
				"\tSet the stemmer to use (default: SremovalStemmer).", "t", 1,
				"-t <name of stemmer class>"));

		return newVector.elements();
	}

	/**
	 * Collects the file names
	 */
	public HashSet<String> collectStems() throws Exception {

		HashSet<String> stems = new HashSet<String>();

		try {
			File dir = new File(inputDirectoryName);

			for (String file : dir.list()) {
				if (file.endsWith(".txt")) {
					String stem = file.substring(0, file.length() - 4);

					File keys = new File(inputDirectoryName + "/" + stem
							+ ".key");
					if (keys.exists()) {
						stems.add(stem);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Problem reading directory "
					+ inputDirectoryName);
		}
		return stems;
	}

	/**
	 * Builds the model from the training data
	 */
	public void buildModel(HashSet<String> fileNames) throws Exception {

		// Check whether there is actually any data
		if (fileNames.size() == 0) {
			throw new Exception("Couldn't find any data in "
					+ inputDirectoryName);
		}

		System.err.println("-- Building the model... ");
		
		FastVector atts = new FastVector(3);
		atts.addElement(new Attribute("filename", (FastVector) null));
		atts.addElement(new Attribute("document", (FastVector) null));
		atts.addElement(new Attribute("keyphrases", (FastVector) null));
		Instances data = new Instances("keyphrase_training_data", atts, 0);

		// Build model
		mauiFilter = new MauiFilter();

		mauiFilter.setDebug(getDebug());
		mauiFilter.setMaxPhraseLength(getMaxPhraseLength());
		mauiFilter.setMinPhraseLength(getMinPhraseLength());
		mauiFilter.setMinNumOccur(getMinNumOccur());
		mauiFilter.setStemmer(getStemmer());
		mauiFilter.setDocumentLanguage(getDocumentLanguage());
		mauiFilter.setVocabularyName(getVocabularyName());
		mauiFilter.setVocabularyFormat(getVocabularyFormat());
		mauiFilter.setStopwords(getStopwords());
		
	
		if (wikipedia != null) {
			mauiFilter.setWikipedia(wikipedia);
		} else if (wikipediaServer.equals("localhost") && wikipediaDatabase.equals("database")) {
			mauiFilter.setWikipedia(wikipedia);		
		} else {
			mauiFilter.setWikipedia(wikipediaServer, wikipediaDatabase, cacheWikipediaData, wikipediaDataDirectory);
		}
		
		if (classifier != null) {
			mauiFilter.setClassifier(classifier);
		}
		
		mauiFilter.setInputFormat(data);
		
		// set features configurations
		mauiFilter.setBasicFeatures(useBasicFeatures);
		mauiFilter.setKeyphrasenessFeature(useKeyphrasenessFeature);
		mauiFilter.setFrequencyFeatures(useFrequencyFeatures);
		mauiFilter.setPositionsFeatures(usePositionsFeatures);
		mauiFilter.setLengthFeature(useLengthFeature);
		mauiFilter.setThesaurusFeatures(useNodeDegreeFeature);
		mauiFilter.setBasicWikipediaFeatures(useBasicWikipediaFeatures);
		mauiFilter.setAllWikipediaFeatures(useAllWikipediaFeatures);
		mauiFilter.setThesaurusFeatures(useNodeDegreeFeature);
		
		mauiFilter.setClassifier(classifier);
		
		mauiFilter.setContextSize(contextSize);
		mauiFilter.setMinKeyphraseness(minKeyphraseness);
		mauiFilter.setMinSenseProbability(minSenseProbability);
		
		if (!vocabularyName.equals("none") && !vocabularyName.equals("wikipedia") ) {
			mauiFilter.loadThesaurus(getStemmer(), getStopwords());
		}

		

		System.err.println("-- Reading the input documents... ");

		for (String fileName : fileNames) {

			double[] newInst = new double[3];

			newInst[0] = (double) data.attribute(0).addStringValue(fileName);
			;

			File documentTextFile = new File(inputDirectoryName + "/"
					+ fileName + ".txt");
			File documentTopicsFile = new File(inputDirectoryName + "/"
					+ fileName + ".key");

			try {

				InputStreamReader is;
				if (!documentEncoding.equals("default")) {
					is = new InputStreamReader(new FileInputStream(
							documentTextFile), documentEncoding);
				} else {
					is = new InputStreamReader(new FileInputStream(
							documentTextFile));
				}

				// Reading the file content
				StringBuffer txtStr = new StringBuffer();
				int c;
				while ((c = is.read()) != -1) {
					txtStr.append((char) c);
				}
				is.close();

				// Adding the text of the document to the instance
				newInst[1] = (double) data.attribute(1).addStringValue(
						txtStr.toString());

			} catch (Exception e) {

				System.err.println("Problem with reading " + documentTextFile);
				e.printStackTrace();
				newInst[1] = Instance.missingValue();
			}

			try {

				InputStreamReader is;
				if (!documentEncoding.equals("default")) {
					is = new InputStreamReader(new FileInputStream(
							documentTopicsFile), documentEncoding);
				} else {
					is = new InputStreamReader(new FileInputStream(
							documentTopicsFile));
				}

				// Reading the content of the keyphrase file
				StringBuffer keyStr = new StringBuffer();
				int c;
				while ((c = is.read()) != -1) {
					keyStr.append((char) c);
				}

				// Adding the topics to the file
				newInst[2] = (double) data.attribute(2).addStringValue(
						keyStr.toString());

			} catch (Exception e) {

				System.err
						.println("Problem with reading " + documentTopicsFile);
				e.printStackTrace();
				newInst[2] = Instance.missingValue();
			}

			data.add(new Instance(1.0, newInst));

			mauiFilter.input(data.instance(0));
			data = data.stringFreeStructure();
		}
		mauiFilter.batchFinished();

		while ((mauiFilter.output()) != null) {
		}
		;
	}

	
	/** 
	 * Saves the extraction model to the file.
	 */
	public void saveModel() throws Exception {

		BufferedOutputStream bufferedOut = new BufferedOutputStream(
				new FileOutputStream(modelName));
		ObjectOutputStream out = new ObjectOutputStream(bufferedOut);
		out.writeObject(mauiFilter);
		out.flush();
		out.close();
	}

	/**
	 * The main method.  
	 */
	public static void main(String[] ops) {

		MauiModelBuilder modelBuilder = new MauiModelBuilder();

		try {

			modelBuilder.setOptions(ops);

			// Output what options are used
			if (modelBuilder.getDebug() == true) {
				System.err.print("Building model with options: ");
				String[] optionSettings = modelBuilder.getOptions();
				for (String optionSetting : optionSettings) {
					System.err.print(optionSetting + " ");
				}
				System.err.println();
			}

			HashSet<String> fileNames = modelBuilder.collectStems();
			modelBuilder.buildModel(fileNames);

			if (modelBuilder.getDebug() == true) {
				System.err.print("Model built. Saving the model...");
			}

			modelBuilder.saveModel();

			System.err.print("Done!");
			
		} catch (Exception e) {

			// Output information on how to use this class
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("\nOptions:\n");
			Enumeration<Option> en = modelBuilder.listOptions();
			while (en.hasMoreElements()) {
				Option option = (Option) en.nextElement();
				System.err.println(option.synopsis());
				System.err.println(option.description());
			}
		}
	}
}
