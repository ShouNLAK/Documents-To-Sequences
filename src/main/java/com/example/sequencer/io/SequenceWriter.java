package com.example.sequencer.io;

import com.example.sequencer.model.DocumentSequence;
import com.example.sequencer.model.SequenceVector;
import com.example.sequencer.vectorization.TFIDFCalculator;
import com.example.sequencer.vectorization.TFFormula;
import com.example.sequencer.vectorization.IDFFormula;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * SequenceWriter - Writes converted sequences to output files
 * Supports multiple output formats for different downstream tasks
 */
public class SequenceWriter {
    
    private final String outputPath;
    private final OutputFormat format;
    
    public enum OutputFormat {
        PLAIN_TEXT,          // Human-readable text format
        JSON,                // JSON format for structured data
        CSV,                 // CSV format for tabular data
        NUMERIC_SEQUENCES    // Pure numeric sequences
    }
    
    public SequenceWriter(String outputPath) {
        this(outputPath, OutputFormat.PLAIN_TEXT);
    }
    
    public SequenceWriter(String outputPath, OutputFormat format) {
        this.outputPath = outputPath;
        this.format = format;
    }
    
    /**
     * Write document sequences to file
     * @param sequences List of document sequences
     * @throws IOException if writing fails
     */
    public void writeSequences(List<DocumentSequence> sequences) throws IOException {
        // Create parent directory if it exists and doesn't already exist
        if (Paths.get(outputPath).getParent() != null) {
            Files.createDirectories(Paths.get(outputPath).getParent());
        }
        
        switch (format) {
            case PLAIN_TEXT:
                writeAsPlainText(sequences);
                break;
            case NUMERIC_SEQUENCES:
                writeAsNumericSequences(sequences);
                break;
            case CSV:
                writeAsCsv(sequences);
                break;
            case JSON:
                writeAsJson(sequences);
                break;
            default:
                throw new IllegalStateException("Unknown format: " + format);
        }
    }
    
    /**
     * Write sequence vectors to file
     * @param vectors List of sequence vectors
     * @throws IOException if writing fails
     */
    public void writeVectors(List<SequenceVector> vectors) throws IOException {
        // Create parent directory if it exists and doesn't already exist
        if (Paths.get(outputPath).getParent() != null) {
            Files.createDirectories(Paths.get(outputPath).getParent());
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("=== DOCUMENT SEQUENCE VECTORS ===\n\n");
            
            for (SequenceVector vector : vectors) {
                writer.write(vector.toFormattedString(10));
                writer.write("\n" + "=".repeat(80) + "\n\n");
            }
            
            writer.write("Total Documents: " + vectors.size() + "\n");
        }
    }
    
    /**
     * Write TF-IDF values with all formulas
     * @param calculator TFIDFCalculator containing all computed values
     * @throws IOException if writing fails
     */
    public void writeTFIDFAllFormulas(TFIDFCalculator calculator) throws IOException {
        // Create parent directory if it exists and doesn't already exist
        if (Paths.get(outputPath).getParent() != null) {
            Files.createDirectories(Paths.get(outputPath).getParent());
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("=".repeat(100) + "\n");
            writer.write("TF-IDF ANALYSIS WITH ALL FORMULAS\n");
            writer.write("Based on: https://en.wikipedia.org/wiki/Tf-idf\n");
            writer.write("=".repeat(100) + "\n\n");
            
            writer.write("Vocabulary Size: " + calculator.getVocabularySize() + "\n");
            writer.write("Total Documents: " + calculator.getTotalDocuments() + "\n\n");
            
            // Write TF formulas information
            writer.write("=== TERM FREQUENCY (TF) FORMULAS ===\n\n");
            for (TFFormula formula : TFFormula.values()) {
                writer.write(String.format("%-30s : %s\n", 
                    formula.getDisplayName(), formula.getFormula()));
            }
            writer.write("\n");
            
            // Write IDF formulas information
            writer.write("=== INVERSE DOCUMENT FREQUENCY (IDF) FORMULAS ===\n\n");
            for (IDFFormula formula : IDFFormula.values()) {
                writer.write(String.format("%-45s : %s\n", 
                    formula.getDisplayName(), formula.getFormula()));
            }
            writer.write("\n");
            
            writer.write("=".repeat(100) + "\n\n");
            
            // Write detailed results for each document
            for (int docIdx = 0; docIdx < calculator.getTotalDocuments(); docIdx++) {
                writer.write("╔" + "═".repeat(98) + "╗\n");
                writer.write(String.format("║ DOCUMENT %d%s║\n", 
                    docIdx + 1, " ".repeat(88 - String.valueOf(docIdx + 1).length())));
                writer.write("╚" + "═".repeat(98) + "╝\n\n");
                
                writer.write(String.format("Total Terms in Document: %d\n", 
                    calculator.getTotalTermsInDocument(docIdx)));
                writer.write(String.format("Max Term Count: %d\n\n", 
                    calculator.getMaxTermCountInDocument(docIdx)));
                
                // For each term that appears in this document
                boolean hasTerms = false;
                for (int termIdx = 0; termIdx < calculator.getVocabularySize(); termIdx++) {
                    String token = calculator.getToken(termIdx);
                    int termCount = calculator.getTermCount(docIdx, token);
                    
                    if (termCount > 0) {
                        hasTerms = true;
                        writer.write("─".repeat(100) + "\n");
                        writer.write(String.format("Term: \"%s\" (Index: %d)\n", token, termIdx));
                        writer.write(String.format("  Raw Count: %d\n", termCount));
                        writer.write(String.format("  Document Frequency: %d / %d documents\n\n", 
                            calculator.getDocumentFrequency(termIdx), 
                            calculator.getTotalDocuments()));
                        
                        // Write all TF values
                        writer.write("  TERM FREQUENCY (TF) VALUES:\n");
                        for (TFFormula tfFormula : TFFormula.values()) {
                            double tfValue = calculator.getTF(docIdx, termIdx, tfFormula);
                            writer.write(String.format("    %-35s : %.6f\n", 
                                tfFormula.getDisplayName(), tfValue));
                        }
                        writer.write("\n");
                        
                        // Write all IDF values (same for all documents)
                        writer.write("  INVERSE DOCUMENT FREQUENCY (IDF) VALUES:\n");
                        for (IDFFormula idfFormula : IDFFormula.values()) {
                            double idfValue = calculator.getIDF(termIdx, idfFormula);
                            writer.write(String.format("    %-45s : %.6f\n", 
                                idfFormula.getDisplayName(), idfValue));
                        }
                        writer.write("\n");
                    }
                }
                
                if (!hasTerms) {
                    writer.write("  (No terms in this document)\n");
                }
                
                writer.write("\n");
            }
            
            writer.write("=".repeat(100) + "\n");
            writer.write("END OF TF-IDF ANALYSIS\n");
            writer.write("=".repeat(100) + "\n");
        }
    }
    
    /**
     * Write as human-readable plain text
     */
    private void writeAsPlainText(List<DocumentSequence> sequences) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("=== DOCUMENT TO SEQUENCE CONVERSION RESULTS ===\n\n");
            
            for (int i = 0; i < sequences.size(); i++) {
                DocumentSequence seq = sequences.get(i);
                writer.write("--- Document " + (i + 1) + " ---\n");
                writer.write(seq.toFormattedString());
                writer.write("\n" + "=".repeat(80) + "\n\n");
            }
            
            writer.write("Total Documents Processed: " + sequences.size() + "\n");
        }
    }
    
    /**
     * Write as numeric sequences only (for ML models)
     */
    private void writeAsNumericSequences(List<DocumentSequence> sequences) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (DocumentSequence seq : sequences) {
                List<Integer> intSeq = seq.getIntegerSequence();
                for (int i = 0; i < intSeq.size(); i++) {
                    writer.write(String.valueOf(intSeq.get(i)));
                    if (i < intSeq.size() - 1) {
                        writer.write(" ");
                    }
                }
                writer.write("\n");
            }
        }
    }
    
    /**
     * Write as CSV format
     */
    private void writeAsCsv(List<DocumentSequence> sequences) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // Header
            writer.write("document_id,token_count,sequence_length,tokens,integer_sequence\n");
            
            for (DocumentSequence seq : sequences) {
                writer.write(escapeCsv(seq.getDocumentId()) + ",");
                writer.write(seq.getTokenCount() + ",");
                writer.write(seq.getSequenceLength() + ",");
                writer.write(escapeCsv(seq.getTokens().toString()) + ",");
                writer.write(escapeCsv(seq.getIntegerSequence().toString()));
                writer.write("\n");
            }
        }
    }
    
    /**
     * Write as JSON format
     */
    private void writeAsJson(List<DocumentSequence> sequences) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("{\n");
            writer.write("  \"documents\": [\n");
            
            for (int i = 0; i < sequences.size(); i++) {
                DocumentSequence seq = sequences.get(i);
                writer.write("    {\n");
                writer.write("      \"id\": \"" + escapeJson(seq.getDocumentId()) + "\",\n");
                writer.write("      \"token_count\": " + seq.getTokenCount() + ",\n");
                writer.write("      \"sequence_length\": " + seq.getSequenceLength() + ",\n");
                writer.write("      \"tokens\": " + listToJsonArray(seq.getTokens()) + ",\n");
                writer.write("      \"integer_sequence\": " + seq.getIntegerSequence() + ",\n");
                writer.write("      \"metadata\": " + mapToJson(seq.getMetadata()) + "\n");
                writer.write("    }");
                
                if (i < sequences.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("  ],\n");
            writer.write("  \"total_documents\": " + sequences.size() + "\n");
            writer.write("}\n");
        }
    }
    
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
    
    private String listToJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String mapToJson(Map<String, Object> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (count > 0) sb.append(", ");
            sb.append("\"").append(escapeJson(entry.getKey())).append("\": ");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(escapeJson(value.toString())).append("\"");
            } else {
                sb.append(value);
            }
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
}
