package com.example.sequencer.preprocessing;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Tokenizer - Splits text into discrete tokens (words)
 * Implements word-level tokenization as described in NLP literature
 * 
 * References:
 * - Document-to-Sequence Conversion in Data Mining
 * - Tokenization techniques in NLP
 */
public class Tokenizer {
    
    private final Pattern tokenPattern;
    private final int minTokenLength;
    
    public Tokenizer() {
        this(1);
    }
    
    public Tokenizer(int minTokenLength) {
        this.minTokenLength = minTokenLength;
        // Pattern to split on whitespace
        this.tokenPattern = Pattern.compile("\\s+");
    }
    
    /**
     * Tokenize a single document into words
     * @param text Input text
     * @return List of tokens
     */
    public List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] rawTokens = tokenPattern.split(text.trim());
        List<String> tokens = new ArrayList<>(rawTokens.length);
        for (String token : rawTokens) {
            if (token.length() >= minTokenLength) {
                tokens.add(token);
            }
        }
        return tokens;
    }
    
    /**
     * Tokenize multiple documents
     * @param documents List of documents
     * @return List of tokenized documents
     */
    public List<List<String>> tokenizeAll(List<String> documents) {
        List<List<String>> tokenizedDocs = new ArrayList<>(documents.size());
        for (String doc : documents) {
            tokenizedDocs.add(tokenize(doc));
        }
        return tokenizedDocs;
    }
    
    /**
     * Get unique tokens from a document
     * @param text Input text
     * @return Set-like list of unique tokens
     */
    public List<String> getUniqueTokens(String text) {
    List<String> tokens = tokenize(text);
    Set<String> unique = new LinkedHashSet<>(tokens);
    return new ArrayList<>(unique);
    }
}
