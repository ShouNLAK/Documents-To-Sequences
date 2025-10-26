package com.example.sequencer.model;

import java.util.*;

/**
 * SequenceVector - Represents a document as a numerical vector
 * Supports both sparse and dense representations
 * 
 * Used for BoW, TF-IDF, and other vectorization outputs
 */
public class SequenceVector {
    
    private final String documentId;
    private final Map<Integer, Double> sparseVector;
    private final double[] denseVector;
    private final VectorizationType type;
    private final Map<String, Object> metadata;
    
    public enum VectorizationType {
        BAG_OF_WORDS,
        TF_IDF,
        WORD_EMBEDDING,
        CUSTOM
    }
    
    private SequenceVector(Builder builder) {
        this.documentId = builder.documentId;
        this.sparseVector = builder.sparseVector;
        this.denseVector = builder.denseVector;
        this.type = builder.type;
        this.metadata = builder.metadata;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public Map<Integer, Double> getSparseVector() {
        return sparseVector != null ? new HashMap<>(sparseVector) : null;
    }
    
    public double[] getDenseVector() {
        return denseVector != null ? Arrays.copyOf(denseVector, denseVector.length) : null;
    }
    
    public VectorizationType getType() {
        return type;
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    /**
     * Get vector dimensionality
     * @return Dimension count
     */
    public int getDimension() {
        if (denseVector != null) {
            return denseVector.length;
        } else if (sparseVector != null && metadata.containsKey("vocabulary_size")) {
            return (Integer) metadata.get("vocabulary_size");
        }
        return 0;
    }
    
    /**
     * Get sparsity ratio (for sparse vectors)
     * @return Sparsity ratio (0.0 to 1.0)
     */
    public double getSparsity() {
        if (sparseVector != null && metadata.containsKey("vocabulary_size")) {
            int vocabSize = (Integer) metadata.get("vocabulary_size");
            int nonZeroCount = sparseVector.size();
            return 1.0 - ((double) nonZeroCount / vocabSize);
        }
        return 0.0;
    }
    
    /**
     * Calculate L2 norm of the vector
     * @return L2 norm
     */
    public double getL2Norm() {
        double norm = 0.0;
        
        if (denseVector != null) {
            for (double value : denseVector) {
                norm += value * value;
            }
        } else if (sparseVector != null) {
            for (double value : sparseVector.values()) {
                norm += value * value;
            }
        }
        
        return Math.sqrt(norm);
    }
    
    /**
     * Get count of non-zero elements
     * @return Non-zero count
     */
    public int getNonZeroCount() {
        if (sparseVector != null) {
            return sparseVector.size();
        } else if (denseVector != null) {
            int count = 0;
            for (double val : denseVector) {
                if (val != 0.0) count++;
            }
            return count;
        }
        return 0;
    }
    
    /**
     * Get top N features sorted by value
     * @param n Number of top features
     * @return Map of top features
     */
    public Map<Integer, Double> getTopFeatures(int n) {
        if (sparseVector != null) {
            List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(sparseVector.entrySet());
            sortedEntries.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());
            
            Map<Integer, Double> topFeatures = new LinkedHashMap<>();
            int count = 0;
            for (Map.Entry<Integer, Double> entry : sortedEntries) {
                if (count >= n) break;
                topFeatures.put(entry.getKey(), entry.getValue());
                count++;
            }
            return topFeatures;
        }
        return new LinkedHashMap<>();
    }
    
    /**
     * Convert sparse to dense representation
     * @param vocabularySize Total vocabulary size
     * @return Dense array
     */
    public double[] toDenseArray(int vocabularySize) {
        if (denseVector != null) {
            return getDenseVector();
        }
        
        if (sparseVector != null) {
            double[] dense = new double[vocabularySize];
            for (Map.Entry<Integer, Double> entry : sparseVector.entrySet()) {
                if (entry.getKey() < vocabularySize) {
                    dense[entry.getKey()] = entry.getValue();
                }
            }
            return dense;
        }
        
        return new double[vocabularySize];
    }
    
    @Override
    public String toString() {
        return "SequenceVector{" +
                "id='" + documentId + '\'' +
                ", type=" + type +
                ", dimension=" + getDimension() +
                ", sparsity=" + String.format("%.2f%%", getSparsity() * 100) +
                '}';
    }
    
    /**
     * Convert to formatted string representation
     * @param maxFeatures Maximum features to display
     * @return Formatted string
     */
    public String toFormattedString(int maxFeatures) {
        StringBuilder sb = new StringBuilder();
        sb.append("Document ID: ").append(documentId).append("\n");
        sb.append("Vectorization Type: ").append(type).append("\n");
        sb.append("Dimension: ").append(getDimension()).append("\n");
        sb.append("L2 Norm: ").append(String.format("%.6f", getL2Norm())).append("\n");
        
        if (sparseVector != null) {
            sb.append("Sparsity: ").append(String.format("%.2f%%", getSparsity() * 100)).append("\n");
            sb.append("Non-zero features: ").append(sparseVector.size()).append("\n");
            
            List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(sparseVector.entrySet());
            sortedEntries.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());
            
            sb.append("Top features (index: value):\n");
            int count = 0;
            for (Map.Entry<Integer, Double> entry : sortedEntries) {
                if (count >= maxFeatures) break;
                sb.append("  ").append(entry.getKey()).append(": ")
                  .append(String.format("%.6f", entry.getValue())).append("\n");
                count++;
            }
        } else if (denseVector != null) {
            sb.append("Dense vector (first ").append(Math.min(maxFeatures, denseVector.length))
              .append(" values):\n  ");
            for (int i = 0; i < Math.min(maxFeatures, denseVector.length); i++) {
                sb.append(String.format("%.6f", denseVector[i]));
                if (i < Math.min(maxFeatures, denseVector.length) - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        
        if (!metadata.isEmpty()) {
            sb.append("Metadata:\n");
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Builder for SequenceVector
     */
    public static class Builder {
        private String documentId;
        private Map<Integer, Double> sparseVector;
        private double[] denseVector;
        private VectorizationType type = VectorizationType.CUSTOM;
        private Map<String, Object> metadata = new LinkedHashMap<>();
        
        public Builder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }
        
        public Builder sparseVector(Map<Integer, Double> sparseVector) {
            this.sparseVector = new HashMap<>(sparseVector);
            return this;
        }
        
        public Builder denseVector(double[] denseVector) {
            this.denseVector = Arrays.copyOf(denseVector, denseVector.length);
            return this;
        }
        
        public Builder type(VectorizationType type) {
            this.type = type;
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
        
        public SequenceVector build() {
            return new SequenceVector(this);
        }
    }
}
