package com.example.sequencer.pipeline;

import com.example.sequencer.encoding.IntegerEncoder;
import com.example.sequencer.encoding.Vocabulary;
import com.example.sequencer.model.DocumentSequence;
import com.example.sequencer.model.SequenceVector;
import com.example.sequencer.preprocessing.PorterStemmer;
import com.example.sequencer.preprocessing.StopWordFilter;
import com.example.sequencer.preprocessing.TextPreprocessor;
import com.example.sequencer.preprocessing.Tokenizer;
import com.example.sequencer.vectorization.BagOfWordsVectorizer;
import com.example.sequencer.vectorization.TfidfVectorizer;
import com.example.sequencer.vectorization.TFIDFCalculator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SequencingPipeline - Orchestrates the complete document-to-sequence conversion
 * Automatically discovers and applies preprocessing, encoding, and vectorization
 * 
 * This implements the full pipeline described in the research literature:
 * Text → Preprocessing → Tokenization → Filtering → Stemming → Encoding → Vectorization
 */
public class SequencingPipeline {
    
    private final TextPreprocessor preprocessor;
    private final Tokenizer tokenizer;
    private final StopWordFilter stopWordFilter;
    private final PorterStemmer stemmer;
    private final Vocabulary vocabulary;
    private final IntegerEncoder encoder;
    private final BagOfWordsVectorizer bowVectorizer;
    private final TfidfVectorizer tfidfVectorizer;
    private final TFIDFCalculator tfidfCalculator;
    
    private final PipelineConfiguration config;
    
    public SequencingPipeline(PipelineConfiguration config) {
        this.config = config;
        
        // Initialize preprocessing components
        this.preprocessor = new TextPreprocessor.Builder()
                .convertToLowercase(config.lowercase)
                .removeHtmlTags(config.removeHtml)
                .removeUrls(config.removeUrls)
                .removeEmails(config.removeEmails)
                .removeNonWords(config.removePunctuation)
                .build();
        
        this.tokenizer = new Tokenizer(config.minTokenLength);
        this.stopWordFilter = new StopWordFilter();
        this.stemmer = new PorterStemmer();
        this.vocabulary = new Vocabulary(config.minFrequency, "<UNK>", "<PAD>");
        this.encoder = new IntegerEncoder(vocabulary);
        this.bowVectorizer = new BagOfWordsVectorizer(config.binaryBoW);
        this.tfidfVectorizer = new TfidfVectorizer(config.sublinearTf);
        this.tfidfCalculator = new TFIDFCalculator();
    }
    
    /**
     * Execute the complete pipeline on documents
     * @param rawDocuments List of raw document strings
     * @return Pipeline results containing sequences and vectors
     */
    public PipelineResult execute(List<String> rawDocuments) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("DOCUMENT-TO-SEQUENCE CONVERSION PIPELINE");
        System.out.println("=".repeat(80));
        System.out.println("Processing " + rawDocuments.size() + " documents...\n");
        
        // Step 1: Preprocessing
        System.out.println("[Step 1/7] Text Preprocessing...");
        List<String> preprocessedDocs = preprocessor.preprocessAll(rawDocuments);
        
        // Truyền danh sách từ bảo vệ từ preprocessor sang stemmer
        if (config.applyStemming) {
            stemmer.setProtectedWords(preprocessor.getProtectedWords());
        }
        
        System.out.println("  ✓ Completed: Applied cleaning and normalization\n");
        
        // Step 2: Tokenization
        System.out.println("[Step 2/7] Tokenization...");
        List<List<String>> tokenizedDocs = tokenizer.tokenizeAll(preprocessedDocs);
        int totalTokens = tokenizedDocs.stream().mapToInt(List::size).sum();
        System.out.println("  ✓ Completed: Generated " + totalTokens + " tokens\n");

        // Cập nhật danh sách từ đã biết để logic stemming giữ nguyên nghĩa
        Set<String> knownWords = tokenizedDocs.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        stemmer.setKnownWords(knownWords);
        
        // Step 3: Stop word filtering
        System.out.println("[Step 3/7] Stop Word Filtering...");
        List<List<String>> filteredDocs = config.removeStopWords 
                ? stopWordFilter.filterAll(tokenizedDocs) 
                : tokenizedDocs;
        int tokensAfterFilter = filteredDocs.stream().mapToInt(List::size).sum();
        System.out.println("  ✓ Completed: Retained " + tokensAfterFilter + " tokens\n");
        
        // Step 4: Stemming
        System.out.println("[Step 4/7] Stemming...");
        List<List<String>> stemmedDocs = config.applyStemming 
                ? stemmer.stemDocuments(filteredDocs) 
                : filteredDocs;
        System.out.println("  ✓ Completed: Applied Porter Stemmer\n");
        
        // Step 5: Vocabulary building and integer encoding
        System.out.println("[Step 5/7] Vocabulary Construction & Integer Encoding...");
        vocabulary.buildFromDocuments(stemmedDocs);
        List<List<Integer>> integerSequences = encoder.encodeAll(stemmedDocs);
        System.out.println("  ✓ Completed: Encoded to integer sequences\n");
        
        // Step 6: BoW vectorization
        System.out.println("[Step 6/7] Bag-of-Words Vectorization...");
        bowVectorizer.fit(stemmedDocs);
        List<Map<Integer, Double>> bowVectors = bowVectorizer.transform(stemmedDocs);
        System.out.println("  ✓ Completed: Generated BoW vectors\n");
        
        // Step 7: TF-IDF vectorization
        System.out.println("[Step 7/7] TF-IDF Vectorization...");
        tfidfVectorizer.fit(stemmedDocs);
        List<Map<Integer, Double>> tfidfVectors = tfidfVectorizer.transform(stemmedDocs);
        
        // Also calculate all TF-IDF formulas using the shared vocabulary
        tfidfCalculator.fit(stemmedDocs, vocabulary);
        System.out.println("  ✓ Completed: Generated TF-IDF vectors and all formula calculations\n");
        
        // Build DocumentSequence objects
        List<DocumentSequence> sequences = new ArrayList<>(rawDocuments.size());
        for (int i = 0; i < rawDocuments.size(); i++) {
            DocumentSequence seq = new DocumentSequence.Builder()
                    .documentId("doc_" + i)
                    .originalText(rawDocuments.get(i))
                    .tokens(stemmedDocs.get(i))
                    .integerSequence(integerSequences.get(i))
                    .metadata("preprocessed", preprocessedDocs.get(i))
                    .metadata("original_token_count", tokenizedDocs.get(i).size())
                    .metadata("filtered_token_count", stemmedDocs.get(i).size())
                    .build();
            sequences.add(seq);
        }
        
        // Build SequenceVector objects
        List<SequenceVector> bowSequenceVectors = new ArrayList<>(rawDocuments.size());
        List<SequenceVector> tfidfSequenceVectors = new ArrayList<>(rawDocuments.size());
        
        for (int i = 0; i < rawDocuments.size(); i++) {
            SequenceVector bowVec = new SequenceVector.Builder()
                    .documentId("doc_" + i)
                    .sparseVector(bowVectors.get(i))
                    .type(SequenceVector.VectorizationType.BAG_OF_WORDS)
                    .metadata("vocabulary_size", bowVectorizer.getVocabularySize())
                    .build();
            bowSequenceVectors.add(bowVec);
            
            SequenceVector tfidfVec = new SequenceVector.Builder()
                    .documentId("doc_" + i)
                    .sparseVector(tfidfVectors.get(i))
                    .type(SequenceVector.VectorizationType.TF_IDF)
                    .metadata("vocabulary_size", tfidfVectorizer.getVocabularySize())
                    .build();
            tfidfSequenceVectors.add(tfidfVec);
        }
        
        System.out.println("=".repeat(80));
        System.out.println("PIPELINE EXECUTION COMPLETED SUCCESSFULLY");
        System.out.println("=".repeat(80) + "\n");
        
        return new PipelineResult(sequences, bowSequenceVectors, tfidfSequenceVectors, 
                                  vocabulary, tfidfCalculator, config);
    }
    
    /**
     * Configuration for the sequencing pipeline
     */
    public static class PipelineConfiguration {
        private boolean lowercase = true;
        private boolean removeHtml = true;
        private boolean removeUrls = true;
        private boolean removeEmails = true;
        private boolean removePunctuation = true;
        private boolean removeStopWords = true;
        private boolean applyStemming = true;
        private int minTokenLength = 1;
        private int minFrequency = 1;
        private boolean binaryBoW = false;
        private boolean sublinearTf = false;
        
        public PipelineConfiguration setLowercase(boolean lowercase) {
            this.lowercase = lowercase;
            return this;
        }
        
        public PipelineConfiguration setRemoveStopWords(boolean removeStopWords) {
            this.removeStopWords = removeStopWords;
            return this;
        }
        
        public PipelineConfiguration setApplyStemming(boolean applyStemming) {
            this.applyStemming = applyStemming;
            return this;
        }
        
        public PipelineConfiguration setMinFrequency(int minFrequency) {
            this.minFrequency = minFrequency;
            return this;
        }
        
        public PipelineConfiguration setMinTokenLength(int minTokenLength) {
            this.minTokenLength = minTokenLength;
            return this;
        }
        
        public Map<String, Object> toMap() {
            Map<String, Object> configMap = new LinkedHashMap<>();
            configMap.put("lowercase", lowercase);
            configMap.put("remove_html", removeHtml);
            configMap.put("remove_urls", removeUrls);
            configMap.put("remove_emails", removeEmails);
            configMap.put("remove_punctuation", removePunctuation);
            configMap.put("remove_stopwords", removeStopWords);
            configMap.put("apply_stemming", applyStemming);
            configMap.put("min_token_length", minTokenLength);
            configMap.put("min_frequency", minFrequency);
            configMap.put("binary_bow", binaryBoW);
            configMap.put("sublinear_tf", sublinearTf);
            return configMap;
        }
    }
    
    /**
     * Results from pipeline execution
     */
    public static class PipelineResult {
        private final List<DocumentSequence> sequences;
        private final List<SequenceVector> bowVectors;
        private final List<SequenceVector> tfidfVectors;
        private final Vocabulary vocabulary;
        private final TFIDFCalculator tfidfCalculator;
        private final PipelineConfiguration configuration;
        
        public PipelineResult(List<DocumentSequence> sequences,
                            List<SequenceVector> bowVectors,
                            List<SequenceVector> tfidfVectors,
                            Vocabulary vocabulary,
                            TFIDFCalculator tfidfCalculator,
                            PipelineConfiguration configuration) {
            this.sequences = sequences;
            this.bowVectors = bowVectors;
            this.tfidfVectors = tfidfVectors;
            this.vocabulary = vocabulary;
            this.tfidfCalculator = tfidfCalculator;
            this.configuration = configuration;
        }
        
        public List<DocumentSequence> getSequences() {
            return sequences;
        }
        
        public List<SequenceVector> getBowVectors() {
            return bowVectors;
        }
        
        public List<SequenceVector> getTfidfVectors() {
            return tfidfVectors;
        }
        
        public Vocabulary getVocabulary() {
            return vocabulary;
        }
        
        public TFIDFCalculator getTfidfCalculator() {
            return tfidfCalculator;
        }
        
        public PipelineConfiguration getConfiguration() {
            return configuration;
        }
        
        public void printSummary() {
            System.out.println("\n=== PIPELINE RESULTS SUMMARY ===");
            System.out.println("Documents processed: " + sequences.size());
            System.out.println("Vocabulary size: " + vocabulary.getSize());
            System.out.println("\nConfiguration:");
            configuration.toMap().forEach((key, value) -> 
                System.out.println("  " + key + ": " + value));
        }
    }
}
