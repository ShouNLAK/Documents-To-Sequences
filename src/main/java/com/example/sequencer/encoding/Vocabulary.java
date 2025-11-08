package com.example.sequencer.encoding;

import java.util.*;

/**
 * Vocabulary - Manages the mapping between tokens and integer indices
 * Core component for converting symbolic tokens to numerical representations
 * 
 * References:
 * - Document-to-Sequence Conversion in Data Mining
 * - The Role of Vocabulary in sequence conversion
 */
public class Vocabulary {
    
    private final Map<String, Integer> tokenToIndex;
    private final Map<Integer, String> indexToToken;
    private final int minFrequency;
    private final String unknownToken;
    private final String paddingToken;
    private int nextIndex;
    
    public Vocabulary() {
        this(1, "<UNK>", "<PAD>");
    }
    
    public Vocabulary(int minFrequency, String unknownToken, String paddingToken) {
        this.tokenToIndex = new LinkedHashMap<>();
        this.indexToToken = new LinkedHashMap<>();
        this.minFrequency = minFrequency;
        this.unknownToken = unknownToken;
        this.paddingToken = paddingToken;
        this.nextIndex = 0;
        
        // Reserve special tokens
        addSpecialToken(paddingToken);
        addSpecialToken(unknownToken);
    }
    
    /**
     * Build vocabulary from tokenized documents
     * Optimized for large datasets
     * @param tokenizedDocuments List of tokenized documents
     */
    public void buildFromDocuments(List<List<String>> tokenizedDocuments) {
        // Count token frequencies - optimized with initial capacity
        int estimatedTokens = tokenizedDocuments.size() * 100; // estimate
        Map<String, Integer> tokenFrequencies = new HashMap<>(estimatedTokens);
        
        for (List<String> tokens : tokenizedDocuments) {
            for (String token : tokens) {
                tokenFrequencies.merge(token, 1, Integer::sum);
            }
        }
        
        // Add tokens that meet minimum frequency threshold
        // Sort by frequency descending for better cache locality
        tokenFrequencies.entrySet().stream()
                .filter(entry -> entry.getValue() >= minFrequency)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> addToken(entry.getKey()));
        
        System.out.println("Vocabulary built: " + getSize() + " unique tokens (min_freq=" + minFrequency + ")");
    }
    
    /**
     * Add a special token (padding, unknown, etc.)
     * @param token Special token string
     */
    private void addSpecialToken(String token) {
        if (!tokenToIndex.containsKey(token)) {
            tokenToIndex.put(token, nextIndex);
            indexToToken.put(nextIndex, token);
            nextIndex++;
        }
    }
    
    /**
     * Add a token to the vocabulary
     * @param token Token to add
     */
    public void addToken(String token) {
        if (!tokenToIndex.containsKey(token) && !token.equals(unknownToken) && !token.equals(paddingToken)) {
            tokenToIndex.put(token, nextIndex);
            indexToToken.put(nextIndex, token);
            nextIndex++;
        }
    }
    
    /**
     * Get index for a token
     * @param token Input token
     * @return Index (or unknown token index if not in vocabulary)
     */
    public int getIndex(String token) {
        return tokenToIndex.getOrDefault(token, tokenToIndex.get(unknownToken));
    }
    
    /**
     * Get token for an index
     * @param index Input index
     * @return Token string
     */
    public String getToken(int index) {
        return indexToToken.getOrDefault(index, unknownToken);
    }
    
    /**
     * Check if token exists in vocabulary
     * @param token Token to check
     * @return true if token exists
     */
    public boolean contains(String token) {
        return tokenToIndex.containsKey(token);
    }
    
    /**
     * Get vocabulary size
     * @return Number of unique tokens
     */
    public int getSize() {
        return tokenToIndex.size();
    }
    
    /**
     * Get unknown token index
     * @return Index of unknown token
     */
    public int getUnknownIndex() {
        return tokenToIndex.get(unknownToken);
    }
    
    /**
     * Get padding token index
     * @return Index of padding token
     */
    public int getPaddingIndex() {
        return tokenToIndex.get(paddingToken);
    }
    
    /**
     * Get all tokens in vocabulary
     * @return Set of all tokens
     */
    public Set<String> getAllTokens() {
        return tokenToIndex.keySet();
    }
    
    /**
     * Get vocabulary statistics
     * @return Map of statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("vocabulary_size", getSize());
        stats.put("min_frequency", minFrequency);
        stats.put("unknown_token", unknownToken);
        stats.put("padding_token", paddingToken);
        return stats;
    }
    
    @Override
    public String toString() {
        return "Vocabulary{size=" + getSize() + ", minFreq=" + minFrequency + "}";
    }
}
