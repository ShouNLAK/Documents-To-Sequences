package com.example.sequencer.model;

import java.util.*;

/**
 * DocumentSequence - Represents a document converted to sequence format
 * Contains both raw tokens and encoded integer sequences
 * 
 * This is the fundamental output structure for document-to-sequence conversion
 */
public class DocumentSequence {
    
    private final String documentId;
    private final String originalText;
    private final List<String> tokens;
    private final List<Integer> integerSequence;
    private final Map<String, Object> metadata;
    
    private DocumentSequence(Builder builder) {
        this.documentId = builder.documentId;
        this.originalText = builder.originalText;
        this.tokens = builder.tokens;
        this.integerSequence = builder.integerSequence;
        this.metadata = builder.metadata;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public List<String> getTokens() {
        return new ArrayList<>(tokens);
    }
    
    public List<Integer> getIntegerSequence() {
        return new ArrayList<>(integerSequence);
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public int getSequenceLength() {
        return integerSequence != null ? integerSequence.size() : 0;
    }
    
    public int getTokenCount() {
        return tokens != null ? tokens.size() : 0;
    }
    
    @Override
    public String toString() {
        return "DocumentSequence{" +
                "id='" + documentId + '\'' +
                ", tokenCount=" + getTokenCount() +
                ", sequenceLength=" + getSequenceLength() +
                '}';
    }
    
    /**
     * Convert to formatted string representation
     * @return Formatted string
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Document ID: ").append(documentId).append("\n");
        sb.append("Original Text: ").append(originalText).append("\n");
        sb.append("Token Count: ").append(getTokenCount()).append("\n");
        sb.append("Tokens: ").append(tokens).append("\n");
        sb.append("Sequence Length: ").append(getSequenceLength()).append("\n");
        sb.append("Integer Sequence: ").append(integerSequence).append("\n");
        
        if (!metadata.isEmpty()) {
            sb.append("Metadata:\n");
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Builder for DocumentSequence
     */
    public static class Builder {
        private String documentId;
        private String originalText;
        private List<String> tokens;
        private List<Integer> integerSequence;
        private Map<String, Object> metadata = new LinkedHashMap<>();
        
        public Builder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }
        
        public Builder originalText(String originalText) {
            this.originalText = originalText;
            return this;
        }
        
        public Builder tokens(List<String> tokens) {
            this.tokens = new ArrayList<>(tokens);
            return this;
        }
        
        public Builder integerSequence(List<Integer> integerSequence) {
            this.integerSequence = new ArrayList<>(integerSequence);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }
        
        public DocumentSequence build() {
            return new DocumentSequence(this);
        }
    }
}
