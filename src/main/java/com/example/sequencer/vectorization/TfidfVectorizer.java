package com.example.sequencer.vectorization;

import java.util.*;

/**
 * TfidfVectorizer - Implements TF-IDF (Term Frequency-Inverse Document Frequency)
 * Statistical measure to evaluate word importance in documents
 * 
 * References:
 * - Understanding TF-IDF (GeeksforGeeks)
 * - Document-to-Sequence Conversion in Data Mining
 * 
 * TF-IDF = TF(t,d) × IDF(t,D)
 * where:
 *   TF(t,d) = (Number of times term t appears in document d) / (Total terms in document d)
 *   IDF(t,D) = log(Total documents / Documents containing term t)
 */
public class TfidfVectorizer {
    
    private final List<String> vocabulary;
    private final Map<String, Integer> vocabularyIndex;
    private final Map<String, Double> idfScores;
    private final boolean useSublinearTf;
    
    public TfidfVectorizer() {
        this(false);
    }
    
    public TfidfVectorizer(boolean useSublinearTf) {
        this.vocabulary = new ArrayList<>();
        this.vocabularyIndex = new HashMap<>();
        this.idfScores = new HashMap<>();
        this.useSublinearTf = useSublinearTf;
    }
    
    /**
     * Fit the vectorizer to calculate IDF scores
     * @param tokenizedDocuments List of tokenized documents
     */
    public void fit(List<List<String>> tokenizedDocuments) {
        vocabulary.clear();
        vocabularyIndex.clear();
        idfScores.clear();

        if (tokenizedDocuments.isEmpty()) {
            System.out.println("TF-IDF vocabulary fitted: " + vocabulary.size() + " unique features");
            return;
        }

        // Build vocabulary
        Set<String> uniqueTokens = new LinkedHashSet<>();
        for (List<String> tokens : tokenizedDocuments) {
            uniqueTokens.addAll(tokens);
        }
        
        int index = 0;
        for (String token : uniqueTokens) {
            vocabulary.add(token);
            vocabularyIndex.put(token, index++);
        }
        
        // Calculate IDF scores
        int totalDocuments = tokenizedDocuments.size();
        Map<String, Integer> documentFrequency = new HashMap<>(uniqueTokens.size());
        
        for (List<String> tokens : tokenizedDocuments) {
            Set<String> uniqueInDoc = new HashSet<>(tokens);
            for (String token : uniqueInDoc) {
                documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
            }
        }
        
        for (String token : vocabulary) {
            int docFreq = documentFrequency.getOrDefault(token, 0);
            // IDF formula: log(N / df) with smoothing to avoid division by zero
            // Using log((N + 1) / (df + 1)) + 1 for smooth-idf (sklearn default)
            double idf = Math.log((double) (totalDocuments + 1) / (docFreq + 1)) + 1.0;
            idfScores.put(token, idf);
        }
        
        System.out.println("TF-IDF vocabulary fitted: " + vocabulary.size() + " unique features");
    }
    
    /**
     * Transform documents to TF-IDF vectors
     * @param tokenizedDocuments List of tokenized documents
     * @return List of TF-IDF vectors
     */
    public List<Map<Integer, Double>> transform(List<List<String>> tokenizedDocuments) {
        List<Map<Integer, Double>> tfidfVectors = new ArrayList<>(tokenizedDocuments.size());
        
        for (List<String> tokens : tokenizedDocuments) {
            tfidfVectors.add(transformSingle(tokens));
        }
        
        return tfidfVectors;
    }
    
    /**
     * Transform a single document to TF-IDF vector
     * @param tokens List of tokens
     * @return TF-IDF vector as sparse map
     */
    public Map<Integer, Double> transformSingle(List<String> tokens) {
        Map<String, Integer> termCounts = new HashMap<>(Math.max(tokens.size(), 8));
        for (String token : tokens) {
            termCounts.merge(token, 1, Integer::sum);
        }

        Map<Integer, Double> tfidfVector = new HashMap<>(termCounts.size());
        
        int totalTerms = tokens.size();
        
        // Calculate TF-IDF scores
        for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
            String token = entry.getKey();
            int count = entry.getValue();

            Integer index = vocabularyIndex.get(token);
            if (index == null) {
                continue;
            }

            // Calculate TF
            double tf;
            if (useSublinearTf) {
                tf = 1.0 + Math.log(count);
            } else {
                tf = totalTerms == 0 ? 0.0 : (double) count / totalTerms;
            }

            // Get IDF
            double idf = idfScores.getOrDefault(token, 1.0);

            // Calculate TF-IDF
            double tfidf = tf * idf;
            tfidfVector.put(index, tfidf);
        }
        
        // L2 normalization
        return normalizeL2(tfidfVector);
    }
    
    /**
     * Fit and transform in one step
     * @param tokenizedDocuments List of tokenized documents
     * @return List of TF-IDF vectors
     */
    public List<Map<Integer, Double>> fitTransform(List<List<String>> tokenizedDocuments) {
        fit(tokenizedDocuments);
        return transform(tokenizedDocuments);
    }
    
    /**
     * Apply L2 normalization to vector
     * @param vector Input vector
     * @return Normalized vector
     */
    private Map<Integer, Double> normalizeL2(Map<Integer, Double> vector) {
        // Calculate L2 norm
        double norm = 0.0;
        for (Double value : vector.values()) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);

        if (norm == 0.0) {
            return vector;
        }

        for (Map.Entry<Integer, Double> entry : vector.entrySet()) {
            entry.setValue(entry.getValue() / norm);
        }

        return vector;
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
     * Get IDF scores for all terms
     * @return Map of term to IDF score
     */
    public Map<String, Double> getIdfScores() {
        return new HashMap<>(idfScores);
    }
    
    /**
     * Get vocabulary size
     * @return Size of vocabulary
     */
    public int getVocabularySize() {
        return vocabulary.size();
    }
    
    /**
     * Get statistics about the TF-IDF model
     * @return Statistics map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("vocabulary_size", vocabulary.size());
        stats.put("sublinear_tf", useSublinearTf);
        stats.put("vectorization_type", "TF-IDF");
        
        if (!idfScores.isEmpty()) {
            double sum = 0.0;
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (double value : idfScores.values()) {
                sum += value;
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
            stats.put("idf_mean", sum / idfScores.size());
            stats.put("idf_min", min);
            stats.put("idf_max", max);
        }
        
        return stats;
    }
}
