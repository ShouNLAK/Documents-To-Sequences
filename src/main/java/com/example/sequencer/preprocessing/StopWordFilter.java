package com.example.sequencer.preprocessing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * StopWordFilter - Removes common stop words from token sequences
 * Implements stop word removal as described in NLP preprocessing literature
 * 
 * References:
 * - Text Preprocessing in NLP
 * - Bag of Words model preprocessing steps
 */
public class StopWordFilter {
    
    private final Set<String> stopWords;
    
    public StopWordFilter() {
        this.stopWords = getDefaultEnglishStopWords();
    }
    
    public StopWordFilter(Set<String> customStopWords) {
        this.stopWords = new HashSet<>(customStopWords);
    }
    
    /**
     * Filter stop words from a token list
     * @param tokens List of tokens
     * @return Filtered list without stop words
     */
    public List<String> filter(List<String> tokens) {
        return tokens.stream()
                .filter(token -> !stopWords.contains(token.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter stop words from multiple documents
     * Optimized for large batches
     * @param tokenizedDocuments List of tokenized documents
     * @return Filtered documents
     */
    public List<List<String>> filterAll(List<List<String>> tokenizedDocuments) {
        // Pre-allocate list with known size
        List<List<String>> filtered = new ArrayList<>(tokenizedDocuments.size());
        for (List<String> tokens : tokenizedDocuments) {
            filtered.add(filter(tokens));
        }
        return filtered;
    }
    
    /**
     * Add custom stop words
     * @param words Words to add to stop word list
     */
    public void addStopWords(String... words) {
        stopWords.addAll(Arrays.asList(words));
    }
    
    /**
     * Default English stop words based on common NLP libraries
     * @return Set of stop words
     */
    private Set<String> getDefaultEnglishStopWords() {
        return new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
            "any", "are", "as", "at", "be", "because", "been", "before", "being", "below",
            "between", "both", "but", "by", "can", "did", "do", "does", "doing", "down",
            "during", "each", "few", "for", "from", "further", "had", "has", "have",
            "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
            "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me",
            "might", "more", "most", "my", "myself", "no", "nor", "not", "now", "of",
            "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out",
            "over", "own", "same", "she", "should", "so", "some", "such", "than", "that",
            "the", "their", "theirs", "them", "themselves", "then", "there", "these",
            "they", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "we", "were", "what", "when", "where", "which", "while",
            "who", "whom", "why", "will", "with", "would", "you", "your", "yours",
            "yourself", "yourselves"
        ));
    }
}
