package com.example.sequencer.core;

import com.example.sequencer.io.DocumentReader;
import com.example.sequencer.io.SequenceWriter;
import com.example.sequencer.io.HTMLReportWriter;
import com.example.sequencer.pipeline.SequencingPipeline;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineConfiguration;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * DocumentSequencerApplication - Main console application
 * Implements document-to-sequence conversion for data mining
 * 
 * Based on comprehensive research from:
 * - "Document-to-Sequence Conversion in Data Mining" (Research Report)
 * - Bag-of-Words Model (GeeksforGeeks)
 * - TF-IDF (GeeksforGeeks)
 * 
 * This application transforms unstructured text documents into numerical sequences
 * suitable for machine learning and data mining applications.
 * 
 * @author Research Implementation
 * @version 1.0
 */
public class DocumentSequencerApplication {
    
    private final Scanner scanner;

    private enum ProcessingMode {
        SINGLE_FILE,
        ENTIRE_FOLDER
    }
    
    public DocumentSequencerApplication() {
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        DocumentSequencerApplication app = new DocumentSequencerApplication();
        app.run();
    }
    
    /**
     * Main application loop
     */
    public void run() {
        printHeader();
        
        try {
            ProcessingMode mode = selectProcessingMode();
            DocumentReader.DocumentFormat format = configureDocumentFormat();
            
            List<String> documents;
            if (mode == ProcessingMode.ENTIRE_FOLDER) {
                String folderPath = getInputFolderPath();
                documents = readDocumentsFromFolder(folderPath, format);
                System.out.println("Successfully loaded " + documents.size() + " document(s) from folder");
            } else {
                String inputPath = getInputPath();
                System.out.println("\nReading documents from: " + inputPath);
                DocumentReader reader = new DocumentReader(inputPath, format);
                documents = reader.readDocuments();
                System.out.println("Successfully loaded " + documents.size() + " document(s)");
            }
            
            PipelineConfiguration config = configurePipeline();
            
            SequencingPipeline pipeline = new SequencingPipeline(config);
            PipelineResult result = pipeline.execute(documents);
            
            result.printSummary();
            
            String outputPath = getOutputPath();
            writeResults(result, outputPath);
            
            // Generate HTML report
            String htmlPath = outputPath.replace(".txt", "_report.html");
            System.out.println("\nGenerating HTML report...");
            HTMLReportWriter htmlWriter = new HTMLReportWriter(htmlPath, result, documents);
            htmlWriter.write();
            System.out.println("✓ HTML report written to: " + htmlPath);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("CONVERSION COMPLETED SUCCESSFULLY!");
            System.out.println("Output written to: " + outputPath);
            System.out.println("HTML Report: " + htmlPath);
            System.out.println("=".repeat(80) + "\n");
            
        } catch (IOException e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    private ProcessingMode selectProcessingMode() {
        System.out.println("Select processing mode:");
        System.out.println("  1. Single input file");
        System.out.println("  2. Entire folder (all .txt files)");
        System.out.print("Choice (default: 1): ");
        String choice = "";
        if (scanner.hasNextLine()) {
            choice = scanner.nextLine().trim();
        }
        if ("2".equals(choice)) {
            return ProcessingMode.ENTIRE_FOLDER;
        }
        return ProcessingMode.SINGLE_FILE;
    }
    
    
    /**
     * Print application header
     */
    private void printHeader() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("  DOCUMENT-TO-SEQUENCE CONVERTER FOR DATA MINING");
        System.out.println("  Research-Grade Implementation of NLP Text Vectorization");
        System.out.println("=".repeat(80));
        System.out.println("\nSupported Techniques:");
        System.out.println("  • Text Preprocessing Pipeline (Cleaning, Normalization)");
        System.out.println("  • Tokenization (Word-level)");
        System.out.println("  • Stop Word Filtering");
        System.out.println("  • Porter Stemming");
        System.out.println("  • Integer Encoding (Vocabulary-based)");
        System.out.println("  • Bag-of-Words (BoW) Vectorization");
        System.out.println("  • TF-IDF Vectorization");
        System.out.println("\n" + "=".repeat(80) + "\n");
    }
    
    /**
     * Get input file path from user
     */
    private String getInputPath() {
        System.out.print("Enter input file path (default: Data/Input/input.txt): ");
        String input = "";
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim();
        }
        return input.isEmpty() ? "Data/Input/input.txt" : input;
    }
    
    private String getInputFolderPath() {
        System.out.print("Enter folder path (default: Data/Input): ");
        String input = "";
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim();
        }
        return input.isEmpty() ? "Data/Input" : input;
    }
    
    /**
     * Get output file path from user
     */
    private String getOutputPath() {
        System.out.print("\nEnter output file path (default: Data/Output/output.txt): ");
        String input = "";
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim();
        }
        return input.isEmpty() ? "Data/Output/output.txt" : input;
    }
    
    private List<String> readDocumentsFromFolder(String folderPath, DocumentReader.DocumentFormat format) throws IOException {
        File directory = new File(folderPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Folder not found or not a directory: " + folderPath);
        }
        File[] txtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) {
            throw new IOException("No .txt files found in folder: " + folderPath);
        }
        System.out.println("\nReading documents from folder: " + folderPath);
        List<String> documents = new ArrayList<>();
        for (File file : txtFiles) {
            System.out.println("  • " + file.getName());
            DocumentReader reader = new DocumentReader(file.getAbsolutePath(), format);
            documents.addAll(reader.readDocuments());
        }
        return documents;
    }

    /**
     * Configure document format
     */
    private DocumentReader.DocumentFormat configureDocumentFormat() {
        System.out.println("\nDocument Format:");
        System.out.println("  1. Single Document (entire file)");
        System.out.println("  2. Line per Document");
        System.out.println("  3. Paragraph per Document");
        System.out.print("Select format (default: 1): ");
        
        String input = "";
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim();
        }
        
        switch (input) {
            case "2":
                return DocumentReader.DocumentFormat.LINE_PER_DOCUMENT;
            case "3":
                return DocumentReader.DocumentFormat.PARAGRAPH_PER_DOCUMENT;
            default:
                return DocumentReader.DocumentFormat.SINGLE_DOCUMENT;
        }
    }
    
    /**
     * Configure pipeline parameters (automatic discovery with user override)
     */
    private PipelineConfiguration configurePipeline() {
        System.out.println("\nPipeline Configuration:");
        System.out.println("Press ENTER to use recommended defaults or type 'custom' for manual configuration");
        System.out.print("Choice: ");
        
        String choice = "";
        if (scanner.hasNextLine()) {
            choice = scanner.nextLine().trim().toLowerCase();
        }
        
        PipelineConfiguration config = new PipelineConfiguration();
        
        if ("custom".equals(choice)) {
            System.out.println("\nCustom Configuration:");
            
            config.setLowercase(askYesNo("Convert to lowercase?", true));
            config.setRemoveStopWords(askYesNo("Remove stop words?", true));
            config.setApplyStemming(askYesNo("Apply Porter stemming?", true));
            
            System.out.print("Minimum word frequency (default: 1): ");
            String freqInput = "";
            if (scanner.hasNextLine()) {
                freqInput = scanner.nextLine().trim();
            }
            if (!freqInput.isEmpty()) {
                try {
                    config.setMinFrequency(Integer.parseInt(freqInput));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, using default: 1");
                }
            }
            
            System.out.print("Minimum token length (default: 1): ");
            String lenInput = "";
            if (scanner.hasNextLine()) {
                lenInput = scanner.nextLine().trim();
            }
            if (!lenInput.isEmpty()) {
                try {
                    config.setMinTokenLength(Integer.parseInt(lenInput));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, using default: 1");
                }
            }
        } else {
            System.out.println("Using recommended configuration (optimized for general text mining)");
        }
        
        return config;
    }
    
    /**
     * Ask yes/no question
     */
    private boolean askYesNo(String question, boolean defaultValue) {
        System.out.print(question + " (y/n, default: " + (defaultValue ? "y" : "n") + "): ");
        String input = "";
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim().toLowerCase();
        }
        
        if (input.isEmpty()) {
            return defaultValue;
        }
        
        return input.startsWith("y");
    }
    
    /**
     * Write results to output files
     */
    private void writeResults(PipelineResult result, String basePath) throws IOException {
        // Write sequences (integer encoding)
        String sequencePath = basePath.replace(".txt", "_sequences.txt");
        SequenceWriter sequenceWriter = new SequenceWriter(sequencePath, 
                                                           SequenceWriter.OutputFormat.PLAIN_TEXT);
        sequenceWriter.writeSequences(result.getSequences());
        System.out.println("\n✓ Integer sequences written to: " + sequencePath);
        
        // Write BoW vectors
        String bowPath = basePath.replace(".txt", "_bow_vectors.txt");
        SequenceWriter bowWriter = new SequenceWriter(bowPath, 
                                                      SequenceWriter.OutputFormat.PLAIN_TEXT);
        bowWriter.writeVectors(result.getBowVectors());
        System.out.println("✓ Bag-of-Words vectors written to: " + bowPath);
        
        // Write TF-IDF vectors
        String tfidfPath = basePath.replace(".txt", "_tfidf_vectors.txt");
        SequenceWriter tfidfWriter = new SequenceWriter(tfidfPath, 
                                                        SequenceWriter.OutputFormat.PLAIN_TEXT);
        tfidfWriter.writeVectors(result.getTfidfVectors());
        System.out.println("✓ TF-IDF vectors written to: " + tfidfPath);
        
        // Write numeric sequences only (for ML models)
        String numericPath = basePath.replace(".txt", "_numeric.txt");
        SequenceWriter numericWriter = new SequenceWriter(numericPath, 
                                                          SequenceWriter.OutputFormat.NUMERIC_SEQUENCES);
        numericWriter.writeSequences(result.getSequences());
        System.out.println("✓ Numeric sequences written to: " + numericPath);
    }
}
