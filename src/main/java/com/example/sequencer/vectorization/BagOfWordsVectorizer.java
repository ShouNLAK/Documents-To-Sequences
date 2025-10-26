package com.example.sequencer.vectorization;

import java.util.*;

/**
 * BagOfWordsVectorizer - Implements the Bag-of-Words (BoW) model
 * Converts documents into vectors based on word occurrence counts
 * 
 * References:
 * - Bag of Words (BoW) model in NLP (GeeksforGeeks)
 * - Document-to-Sequence Conversion in Data Mining
 * 
 * The BoW model represents documents as unordered collections of words,
 * focusing on frequency while ignoring grammar and word order.
 */
public class BagOfWordsVectorizer {
    
    private final List<String> vocabulary;
    private final Map<String, Integer> vocabularyIndex;
    private final boolean binary;
    
    public BagOfWordsVectorizer() {
        this(false);
    }
    
    public BagOfWordsVectorizer(boolean binary) {
        this.vocabulary = new ArrayList<>();
        this.vocabularyIndex = new HashMap<>();
        this.binary = binary;
    }
    
    /**
     * Fit the vectorizer to build vocabulary from documents
     * @param tokenizedDocuments List of tokenized documents
     */
    public void fit(List<List<String>> tokenizedDocuments) {
        Set<String> uniqueTokens = new LinkedHashSet<>();
        
        for (List<String> tokens : tokenizedDocuments) {
            uniqueTokens.addAll(tokens);
        }
        
        int index = 0;
        for (String token : uniqueTokens) {
            vocabulary.add(token);
            vocabularyIndex.put(token, index++);
        }
        
        System.out.println("BoW vocabulary fitted: " + vocabulary.size() + " unique features");
    }
    
    /**
     * Transform documents to BoW vectors
     * @param tokenizedDocuments List of tokenized documents
     * @return List of BoW vectors (each vector is a map of feature indices to counts)
     */
    public List<Map<Integer, Double>> transform(List<List<String>> tokenizedDocuments) {
        List<Map<Integer, Double>> bowVectors = new ArrayList<>();
        
        for (List<String> tokens : tokenizedDocuments) {
            bowVectors.add(transformSingle(tokens));
        }
        
        return bowVectors;
    }
    
    /**
     * Transform a single document to BoW vector
     * @param tokens List of tokens
     * @return BoW vector as sparse map
     */
    public Map<Integer, Double> transformSingle(List<String> tokens) {
        Map<Integer, Double> bowVector = new HashMap<>();
        
        for (String token : tokens) {
            if (vocabularyIndex.containsKey(token)) {
                int index = vocabularyIndex.get(token);
                if (binary) {
                    bowVector.put(index, 1.0);
                } else {
                    bowVector.put(index, bowVector.getOrDefault(index, 0.0) + 1.0);
                }
            }
        }
        
        return bowVector;
    }
    
    /**
     * Fit and transform in one step
     * @param tokenizedDocuments List of tokenized documents
     * @return List of BoW vectors
     */
    public List<Map<Integer, Double>> fitTransform(List<List<String>> tokenizedDocuments) {
        fit(tokenizedDocuments);
        return transform(tokenizedDocuments);
    }
    
    /**
     * Convert sparse vector to dense array
     * @param sparseVector Sparse vector representation
     * @return Dense array
     */
    public double[] toDenseArray(Map<Integer, Double> sparseVector) {
        double[] dense = new double[vocabulary.size()];
        for (Map.Entry<Integer, Double> entry : sparseVector.entrySet()) {
            dense[entry.getKey()] = entry.getValue();
        }
        return dense;
    }
    
    /**
     * Get feature names (vocabulary)
     * @return List of feature names
     */
    public List<String> getFeatureNames() {
        return new ArrayList<>(vocabulary);
    }
    
    /**
     * Get vocabulary size
     * @return Size of vocabulary
     */
    public int getVocabularySize() {
        return vocabulary.size();
    }
    
    /**
     * Get statistics about the BoW model
     * @return Statistics map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("vocabulary_size", vocabulary.size());
        stats.put("binary_mode", binary);
        stats.put("vectorization_type", "Bag-of-Words");
        return stats;
    }
}
