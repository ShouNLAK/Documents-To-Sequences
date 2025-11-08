package com.example.sequencer.vectorization;

import com.example.sequencer.encoding.Vocabulary;
import java.util.*;

/**
 * TFIDFCalculator - Comprehensive TF-IDF calculator with all formulas from Wikipedia
 * https://en.wikipedia.org/wiki/Tf%E2%80%93idf
 * 
 * This calculator computes TF and IDF separately using various formulas,
 * and provides the ability to combine them for TF-IDF scores.
 */
public class TFIDFCalculator {
    
    private Vocabulary vocabulary;
    private final Map<String, Integer> vocabularyIndex;
    
    // Store all TF values for each document and each formula
    private Map<TFFormula, List<Map<Integer, Double>>> allTFValues;
    
    // Store all IDF values for each formula
    private Map<IDFFormula, Map<Integer, Double>> allIDFValues;
    
    // Document statistics
    private List<Map<String, Integer>> documentTermCounts;
    private List<Integer> documentTotalTerms;
    private List<Integer> documentMaxTermCounts;
    private Map<String, Integer> documentFrequencies;
    private int totalDocuments;
    private int maxDocumentFrequency;
    
    public TFIDFCalculator() {
        this.vocabulary = null;
        this.vocabularyIndex = new HashMap<>();
        this.allTFValues = new HashMap<>();
        this.allIDFValues = new HashMap<>();
        this.documentTermCounts = new ArrayList<>();
        this.documentTotalTerms = new ArrayList<>();
        this.documentMaxTermCounts = new ArrayList<>();
        this.documentFrequencies = new HashMap<>();
    }
    
    /**
     * Fit the calculator to compute all TF and IDF values for all formulas
     * @param tokenizedDocuments The tokenized documents to fit on
     * @param sharedVocabulary The vocabulary to use (shared from pipeline)
     */
    public void fit(List<List<String>> tokenizedDocuments, Vocabulary sharedVocabulary) {
        this.vocabulary = sharedVocabulary;
        this.totalDocuments = tokenizedDocuments.size();
        
        // Build vocabulary and collect statistics
        buildVocabularyAndStatistics(tokenizedDocuments);
        
        // Calculate all TF values for all formulas
        calculateAllTFValues(tokenizedDocuments);
        
        // Calculate all IDF values for all formulas
        calculateAllIDFValues();
        
        System.out.println("TF-IDF Calculator fitted:");
        System.out.println("  Vocabulary size: " + vocabulary.getSize());
        System.out.println("  Total documents: " + totalDocuments);
        System.out.println("  TF formulas computed: " + TFFormula.values().length);
        System.out.println("  IDF formulas computed: " + IDFFormula.values().length);
    }
    
    /**
     * Build vocabulary index and collect document statistics
     * Note: Vocabulary is already built by the pipeline, we just need to index it
     */
    private void buildVocabularyAndStatistics(List<List<String>> tokenizedDocuments) {
        // Build vocabulary index from the shared vocabulary
        int index = 0;
        for (String token : vocabulary.getAllTokens()) {
            vocabularyIndex.put(token, index++);
        }
        
        // Collect document statistics
        maxDocumentFrequency = 0;
        
        for (List<String> tokens : tokenizedDocuments) {
            // Count terms in this document
            Map<String, Integer> termCounts = new HashMap<>();
            int maxCount = 0;
            
            for (String token : tokens) {
                int count = termCounts.getOrDefault(token, 0) + 1;
                termCounts.put(token, count);
                maxCount = Math.max(maxCount, count);
            }
            
            documentTermCounts.add(termCounts);
            documentTotalTerms.add(tokens.size());
            documentMaxTermCounts.add(maxCount);
            
            // Update document frequencies
            Set<String> uniqueInDoc = new HashSet<>(tokens);
            for (String token : uniqueInDoc) {
                int df = documentFrequencies.getOrDefault(token, 0) + 1;
                documentFrequencies.put(token, df);
                maxDocumentFrequency = Math.max(maxDocumentFrequency, df);
            }
        }
    }
    
    /**
     * Calculate TF values for all documents using all formulas
     */
    private void calculateAllTFValues(List<List<String>> tokenizedDocuments) {
        for (TFFormula tfFormula : TFFormula.values()) {
            List<Map<Integer, Double>> tfVectors = new ArrayList<>();
            
            for (int docIdx = 0; docIdx < tokenizedDocuments.size(); docIdx++) {
                Map<String, Integer> termCounts = documentTermCounts.get(docIdx);
                int totalTerms = documentTotalTerms.get(docIdx);
                int maxTermCount = documentMaxTermCounts.get(docIdx);
                
                Map<Integer, Double> tfVector = new HashMap<>();
                
                for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
                    String token = entry.getKey();
                    int count = entry.getValue();
                    
                    if (vocabularyIndex.containsKey(token)) {
                        int tokenIndex = vocabularyIndex.get(token);
                        double tf = tfFormula.calculate(count, totalTerms, maxTermCount);
                        tfVector.put(tokenIndex, tf);
                    }
                }
                
                tfVectors.add(tfVector);
            }
            
            allTFValues.put(tfFormula, tfVectors);
        }
    }
    
    /**
     * Calculate IDF values for all terms using all formulas
     */
    private void calculateAllIDFValues() {
        for (IDFFormula idfFormula : IDFFormula.values()) {
            Map<Integer, Double> idfVector = new HashMap<>();
            
            for (int i = 0; i < vocabulary.getSize(); i++) {
                String token = vocabulary.getToken(i);
                int df = documentFrequencies.getOrDefault(token, 0);
                
                double idf = idfFormula.calculate(totalDocuments, df, maxDocumentFrequency);
                idfVector.put(i, idf);
            }
            
            allIDFValues.put(idfFormula, idfVector);
        }
    }
    
    /**
     * Get TF value for a specific term in a specific document using a specific formula
     */
    public double getTF(int documentIndex, int termIndex, TFFormula formula) {
        List<Map<Integer, Double>> tfVectors = allTFValues.get(formula);
        if (tfVectors == null || documentIndex >= tfVectors.size()) {
            return 0.0;
        }
        return tfVectors.get(documentIndex).getOrDefault(termIndex, 0.0);
    }
    
    /**
     * Get IDF value for a specific term using a specific formula
     */
    public double getIDF(int termIndex, IDFFormula formula) {
        Map<Integer, Double> idfVector = allIDFValues.get(formula);
        if (idfVector == null) {
            return 0.0;
        }
        return idfVector.getOrDefault(termIndex, 0.0);
    }
    
    /**
     * Get TF-IDF value by combining TF and IDF formulas
     */
    public double getTFIDF(int documentIndex, int termIndex, TFFormula tfFormula, IDFFormula idfFormula) {
        double tf = getTF(documentIndex, termIndex, tfFormula);
        double idf = getIDF(termIndex, idfFormula);
        return tf * idf;
    }
    
    /**
     * Get all TF values for a document using a specific formula
     */
    public Map<Integer, Double> getTFVector(int documentIndex, TFFormula formula) {
        List<Map<Integer, Double>> tfVectors = allTFValues.get(formula);
        if (tfVectors == null || documentIndex >= tfVectors.size()) {
            return new HashMap<>();
        }
        return new HashMap<>(tfVectors.get(documentIndex));
    }
    
    /**
     * Get all IDF values using a specific formula
     */
    public Map<Integer, Double> getIDFVector(IDFFormula formula) {
        Map<Integer, Double> idfVector = allIDFValues.get(formula);
        if (idfVector == null) {
            return new HashMap<>();
        }
        return new HashMap<>(idfVector);
    }
    
    /**
     * Get all TF values for all formulas for a specific document
     */
    public Map<TFFormula, Map<Integer, Double>> getAllTFVectorsForDocument(int documentIndex) {
        Map<TFFormula, Map<Integer, Double>> result = new HashMap<>();
        for (TFFormula formula : TFFormula.values()) {
            result.put(formula, getTFVector(documentIndex, formula));
        }
        return result;
    }
    
    /**
     * Get all IDF values for all formulas
     */
    public Map<IDFFormula, Map<Integer, Double>> getAllIDFVectors() {
        Map<IDFFormula, Map<Integer, Double>> result = new HashMap<>();
        for (IDFFormula formula : IDFFormula.values()) {
            result.put(formula, getIDFVector(formula));
        }
        return result;
    }
    
    /**
     * Calculate TF-IDF vector for a document using specific formulas
     */
    public Map<Integer, Double> calculateTFIDFVector(int documentIndex, TFFormula tfFormula, IDFFormula idfFormula) {
        Map<Integer, Double> tfVector = getTFVector(documentIndex, tfFormula);
        Map<Integer, Double> idfVector = getIDFVector(idfFormula);
        
        Map<Integer, Double> tfidfVector = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : tfVector.entrySet()) {
            int termIndex = entry.getKey();
            double tf = entry.getValue();
            double idf = idfVector.getOrDefault(termIndex, 0.0);
            tfidfVector.put(termIndex, tf * idf);
        }
        
        return tfidfVector;
    }
    
    /**
     * Get vocabulary
     */
    public List<String> getVocabulary() {
        return new ArrayList<>(vocabulary.getAllTokens());
    }
    
    /**
     * Get vocabulary size
     */
    public int getVocabularySize() {
        return vocabulary.getSize();
    }
    
    /**
     * Get total number of documents
     */
    public int getTotalDocuments() {
        return totalDocuments;
    }
    
    /**
     * Get token by index
     */
    public String getToken(int index) {
        if (index >= 0 && index < vocabulary.getSize()) {
            return vocabulary.getToken(index);
        }
        return null;
    }
    
    /**
     * Get index of token
     */
    public int getTokenIndex(String token) {
        return vocabularyIndex.getOrDefault(token, -1);
    }
    
    /**
     * Get document frequency for a term
     */
    public int getDocumentFrequency(String token) {
        return documentFrequencies.getOrDefault(token, 0);
    }
    
    /**
     * Get document frequency for a term by index
     */
    public int getDocumentFrequency(int termIndex) {
        if (termIndex >= 0 && termIndex < vocabulary.getSize()) {
            return getDocumentFrequency(vocabulary.getToken(termIndex));
        }
        return 0;
    }
    
    /**
     * Get raw term count in a specific document
     */
    public int getTermCount(int documentIndex, String token) {
        if (documentIndex >= 0 && documentIndex < documentTermCounts.size()) {
            return documentTermCounts.get(documentIndex).getOrDefault(token, 0);
        }
        return 0;
    }
    
    /**
     * Get total terms in a document
     */
    public int getTotalTermsInDocument(int documentIndex) {
        if (documentIndex >= 0 && documentIndex < documentTotalTerms.size()) {
            return documentTotalTerms.get(documentIndex);
        }
        return 0;
    }
    
    /**
     * Get max term count in a document
     */
    public int getMaxTermCountInDocument(int documentIndex) {
        if (documentIndex >= 0 && documentIndex < documentMaxTermCounts.size()) {
            return documentMaxTermCounts.get(documentIndex);
        }
        return 0;
    }
}
