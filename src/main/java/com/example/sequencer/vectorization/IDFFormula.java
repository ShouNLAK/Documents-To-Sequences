package com.example.sequencer.vectorization;

/**
 * IDF (Inverse Document Frequency) calculation formulas based on Wikipedia
 * https://en.wikipedia.org/wiki/Tf%E2%80%93idf
 */
public enum IDFFormula {
    /**
     * Unary: idf(t,D) = 1
     * No weighting applied
     */
    UNARY("Unary", "1"),
    
    /**
     * Inverse document frequency: idf(t,D) = log(N / n(t))
     * where N = total documents, n(t) = documents containing term t
     */
    IDF("Inverse Document Frequency", "log(N / n(t))"),
    
    /**
     * Inverse document frequency smooth: idf(t,D) = log((N + 1) / (n(t) + 1)) + 1
     * Smoothed version to avoid division by zero
     */
    IDF_SMOOTH("Inverse Document Frequency Smooth", "log((N+1) / (n(t)+1)) + 1"),
    
    /**
     * Inverse document frequency max: idf(t,D) = log(max(n(t')) / n(t))
     * where max(n(t')) is the maximum document frequency among all terms
     */
    IDF_MAX("Inverse Document Frequency Max", "log(max{n(t')} / n(t))"),
    
    /**
     * Probabilistic inverse document frequency: idf(t,D) = log((N - n(t)) / n(t))
     * Based on probability estimates
     */
    IDF_PROBABILISTIC("Probabilistic Inverse Document Frequency", "log((N - n(t)) / n(t))");
    
    private final String displayName;
    private final String formula;
    
    IDFFormula(String displayName, String formula) {
        this.displayName = displayName;
        this.formula = formula;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getFormula() {
        return formula;
    }
    
    /**
     * Calculate IDF value based on this formula
     * 
     * @param totalDocuments Total number of documents (N)
     * @param documentFrequency Number of documents containing the term (n(t))
     * @param maxDocumentFrequency Maximum document frequency among all terms
     * @return Calculated IDF value
     */
    public double calculate(int totalDocuments, int documentFrequency, int maxDocumentFrequency) {
        // Avoid division by zero
        if (documentFrequency == 0) {
            documentFrequency = 1;
        }
        
        switch (this) {
            case UNARY:
                return 1.0;
                
            case IDF:
                return Math.log((double) totalDocuments / documentFrequency);
                
            case IDF_SMOOTH:
                return Math.log((double) (totalDocuments + 1) / (documentFrequency + 1)) + 1.0;
                
            case IDF_MAX:
                if (maxDocumentFrequency > 0) {
                    return Math.log((double) maxDocumentFrequency / documentFrequency);
                }
                return 0.0;
                
            case IDF_PROBABILISTIC:
                int numerator = totalDocuments - documentFrequency;
                if (numerator > 0) {
                    return Math.log((double) numerator / documentFrequency);
                }
                return 0.0;
                
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculate IDF value without max document frequency (for formulas that don't need it)
     */
    public double calculate(int totalDocuments, int documentFrequency) {
        return calculate(totalDocuments, documentFrequency, 0);
    }
}
