package com.example.sequencer.utils;

import java.util.List;
import java.util.Map;

/**
 * MathUtils - Utility functions for mathematical operations
 * Used in vectorization and similarity calculations
 */
public class MathUtils {
    
    /**
     * Calculate cosine similarity between two sparse vectors
     * @param vector1 First sparse vector
     * @param vector2 Second sparse vector
     * @return Cosine similarity (0 to 1)
     */
    public double cosineSimilarity(Map<Integer, Double> vector1, Map<Integer, Double> vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        // Calculate dot product and norm for vector1
        for (Map.Entry<Integer, Double> entry : vector1.entrySet()) {
            int index = entry.getKey();
            double value1 = entry.getValue();
            
            norm1 += value1 * value1;
            
            if (vector2.containsKey(index)) {
                dotProduct += value1 * vector2.get(index);
            }
        }
        
        // Calculate norm for vector2
        for (double value2 : vector2.values()) {
            norm2 += value2 * value2;
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * Calculate cosine similarity between two dense vectors
     * @param vector1 First dense vector
     * @param vector2 Second dense vector
     * @return Cosine similarity (0 to 1)
     */
    public double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * Calculate Euclidean distance between two vectors
     * @param vector1 First vector
     * @param vector2 Second vector
     * @return Euclidean distance
     */
    public double euclideanDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
    
    /**
     * Normalize vector to unit length (L2 normalization)
     * @param vector Input vector
     * @return Normalized vector
     */
    public double[] normalizeL2(double[] vector) {
        double norm = 0.0;
        for (double value : vector) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);
        
        if (norm == 0.0) {
            return vector.clone();
        }
        
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / norm;
        }
        
        return normalized;
    }
    
    /**
     * Calculate mean of a list of numbers
     * @param values List of values
     * @return Mean value
     */
    public double mean(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        
        return sum / values.size();
    }
    
    /**
     * Calculate standard deviation
     * @param values List of values
     * @return Standard deviation
     */
    public double standardDeviation(List<Double> values) {
        if (values.size() < 2) {
            return 0.0;
        }
        
        double mean = mean(values);
        double variance = 0.0;
        
        for (double value : values) {
            double diff = value - mean;
            variance += diff * diff;
        }
        
        variance /= (values.size() - 1);
        return Math.sqrt(variance);
    }
    
    /**
     * Calculate entropy of a distribution
     * @param probabilities Probability distribution
     * @return Entropy value
     */
    public double entropy(double[] probabilities) {
        double entropy = 0.0;
        
        for (double p : probabilities) {
            if (p > 0) {
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }
        
        return entropy;
    }
}
