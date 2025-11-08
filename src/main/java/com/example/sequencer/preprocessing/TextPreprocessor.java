package com.example.sequencer.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TextPreprocessor - Performs comprehensive text cleaning and normalization
 * Based on scientific literature on NLP preprocessing pipelines
 * 
 * References:
 * - Text Preprocessing in NLP (GeeksforGeeks)
 * - Document-to-Sequence Conversion in Data Mining (Research Report)
 */
public class TextPreprocessor {
    
    private final Pattern htmlTagPattern;
    private final Pattern urlPattern;
    private final Pattern emailPattern;
    private final Pattern nonWordPattern;
    private final Pattern multiSpacePattern;
    private final boolean convertToLowercase;
    private final boolean removeHtmlTags;
    private final boolean removeUrls;
    private final boolean removeEmails;
    private final boolean removeNonWords;
    
    // Danh sách từ được bảo vệ (tên riêng, từ chuyên ngành)
    private final Set<String> protectedWords;
    
    private TextPreprocessor(Builder builder) {
        this.convertToLowercase = builder.convertToLowercase;
        this.removeHtmlTags = builder.removeHtmlTags;
        this.removeUrls = builder.removeUrls;
        this.removeEmails = builder.removeEmails;
        this.removeNonWords = builder.removeNonWords;
        this.protectedWords = new HashSet<>();
        
        // Compile regex patterns for efficiency
        this.htmlTagPattern = Pattern.compile("<[^>]+>");
        this.urlPattern = Pattern.compile("https?://\\S+|www\\.\\S+");
        this.emailPattern = Pattern.compile("\\S+@\\S+");
        this.nonWordPattern = Pattern.compile("[^a-zA-Z0-9\\s]");
        this.multiSpacePattern = Pattern.compile("\\s+");
    }
    
    /**
     * Trích xuất tên riêng từ text gốc
     * @param text Input text (giữ nguyên hoa/thường)
     */
    private void extractProperNouns(String text) {
        Pattern properNounPattern = Pattern.compile("\\b([A-Z][a-z]+)\\b");
        Matcher matcher = properNounPattern.matcher(text);
        while (matcher.find()) {
            protectedWords.add(matcher.group(1).toLowerCase());
        }
    }
    
    /**
     * Lấy danh sách từ được bảo vệ
     */
    public Set<String> getProtectedWords() {
        return new HashSet<>(protectedWords);
    }
    
    /**
     * Preprocess a single document
     * @param text Raw input text
     * @return Cleaned and normalized text
     */
    public String preprocess(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        String processed = text;
        
        // Step 1: Remove HTML tags
        if (removeHtmlTags) {
            processed = htmlTagPattern.matcher(processed).replaceAll(" ");
        }
        
        // Step 2: Remove URLs
        if (removeUrls) {
            processed = urlPattern.matcher(processed).replaceAll(" ");
        }
        
        // Step 3: Remove email addresses
        if (removeEmails) {
            processed = emailPattern.matcher(processed).replaceAll(" ");
        }
        
        // Step 4: Remove non-word characters (punctuation) - TRƯỚC khi lowercase
        if (removeNonWords) {
            processed = nonWordPattern.matcher(processed).replaceAll(" ");
        }
        
        // Step 4.5: Trích xuất tên riêng và từ dài TRƯỚC KHI lowercase
        if (convertToLowercase) {
            extractProperNouns(processed);
        }
        
        // Step 5: Convert to lowercase
        if (convertToLowercase) {
            processed = processed.toLowerCase();
        }
        
        // Step 6: Normalize whitespace
        processed = multiSpacePattern.matcher(processed).replaceAll(" ");
        
        return processed.trim();
    }
    
    /**
     * Preprocess multiple documents
     * Optimized for large batches
     * @param documents List of raw documents
     * @return List of preprocessed documents
     */
    public List<String> preprocessAll(List<String> documents) {
        // Pre-allocate list with known size for better performance
        List<String> processed = new ArrayList<>(documents.size());
        for (String doc : documents) {
            processed.add(preprocess(doc));
        }
        return processed;
    }
    
    /**
     * Builder pattern for flexible configuration
     */
    public static class Builder {
        private boolean convertToLowercase = true;
        private boolean removeHtmlTags = true;
        private boolean removeUrls = true;
        private boolean removeEmails = true;
        private boolean removeNonWords = true;
        
        public Builder convertToLowercase(boolean value) {
            this.convertToLowercase = value;
            return this;
        }
        
        public Builder removeHtmlTags(boolean value) {
            this.removeHtmlTags = value;
            return this;
        }
        
        public Builder removeUrls(boolean value) {
            this.removeUrls = value;
            return this;
        }
        
        public Builder removeEmails(boolean value) {
            this.removeEmails = value;
            return this;
        }
        
        public Builder removeNonWords(boolean value) {
            this.removeNonWords = value;
            return this;
        }
        
        public TextPreprocessor build() {
            return new TextPreprocessor(this);
        }
    }
}
