package com.example.sequencer.vectorization;

/**
 * TF (Term Frequency) calculation formulas based on Wikipedia
 * https://en.wikipedia.org/wiki/Tf%E2%80%93idf
 */
public enum TFFormula {
    /**
     * Binary: tf(t,d) = 1 if t occurs in d and 0 otherwise
     */
    BINARY("Binary", "1 if t ∈ d else 0"),
    
    /**
     * Raw count: tf(t,d) = f(t,d)
     * where f(t,d) is the raw count of term t in document d
     */
    RAW_COUNT("Raw Count", "f(t,d)"),
    
    /**
     * Term frequency: tf(t,d) = f(t,d) / Σ(f(t',d))
     * Normalized by total number of terms in document
     */
    TERM_FREQUENCY("Term Frequency", "f(t,d) / Σf(t',d)"),
    
    /**
     * Log normalization: tf(t,d) = log(1 + f(t,d))
     */
    LOG_NORMALIZATION("Log Normalization", "log(1 + f(t,d))"),
    
    /**
     * Double normalization 0.5: tf(t,d) = 0.5 + 0.5 × (f(t,d) / max{f(t',d)})
     * Prevents bias towards longer documents
     */
    DOUBLE_NORMALIZATION_05("Double Normalization 0.5", "0.5 + 0.5 × f(t,d) / max{f(t',d)}"),
    
    /**
     * Double normalization K: tf(t,d) = K + (1-K) × (f(t,d) / max{f(t',d)})
     * K is typically 0.5 but can be adjusted
     */
    DOUBLE_NORMALIZATION_K("Double Normalization K", "K + (1-K) × f(t,d) / max{f(t',d)}");
    
    private final String displayName;
    private final String formula;
    
    TFFormula(String displayName, String formula) {
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
     * Calculate TF value based on this formula
     * 
     * @param termCount Raw count of term in document
     * @param totalTerms Total number of terms in document
     * @param maxTermCount Maximum term count in document
     * @param k Parameter for DOUBLE_NORMALIZATION_K (typically 0.5)
     * @return Calculated TF value
     */
    public double calculate(int termCount, int totalTerms, int maxTermCount, double k) {
        switch (this) {
            case BINARY:
                return termCount > 0 ? 1.0 : 0.0;
                
            case RAW_COUNT:
                return (double) termCount;
                
            case TERM_FREQUENCY:
                return totalTerms > 0 ? (double) termCount / totalTerms : 0.0;
                
            case LOG_NORMALIZATION:
                return Math.log(1.0 + termCount);
                
            case DOUBLE_NORMALIZATION_05:
                return maxTermCount > 0 ? 0.5 + 0.5 * ((double) termCount / maxTermCount) : 0.0;
                
            case DOUBLE_NORMALIZATION_K:
                return maxTermCount > 0 ? k + (1.0 - k) * ((double) termCount / maxTermCount) : 0.0;
                
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculate TF value with default K=0.5 for DOUBLE_NORMALIZATION_K
     */
    public double calculate(int termCount, int totalTerms, int maxTermCount) {
        return calculate(termCount, totalTerms, maxTermCount, 0.5);
    }
}
