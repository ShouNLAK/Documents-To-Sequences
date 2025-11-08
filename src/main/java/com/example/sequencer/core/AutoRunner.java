package com.example.sequencer.core;

import com.example.sequencer.io.DocumentReader;
import com.example.sequencer.io.SequenceWriter;
import com.example.sequencer.io.HTMLReportWriter;
import com.example.sequencer.pipeline.SequencingPipeline;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineConfiguration;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineResult;
import com.example.sequencer.utils.PerformanceMonitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AutoRunner - Batch processing for entire folder
 * Optimized for handling large numbers of files and documents
 */
public class AutoRunner {
    
    private static final String INPUT_DIR = "Data/Input";
    private static final String OUTPUT_DIR = "Data/Output";
    private static final String OUTPUT_BASE = OUTPUT_DIR + "/output";
    
    public static void main(String[] args) {
        PerformanceMonitor monitor = new PerformanceMonitor();
        
        try {
            monitor.startProcessing();
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("  AUTO RUNNER - BATCH DOCUMENT PROCESSING");
            System.out.println("=".repeat(80) + "\n");
            
            // Step 1: Read files
            monitor.startOperation("1. Đọc file");
            File inputDir = new File(INPUT_DIR);
            File[] txtFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (txtFiles == null || txtFiles.length == 0) {
                System.err.println("No .txt files found in " + INPUT_DIR);
                return;
            }
            
            System.out.println("Found " + txtFiles.length + " file(s) in " + INPUT_DIR);
            
            // Optimized: Use ArrayList with initial capacity
            List<String> allDocuments = new ArrayList<>(txtFiles.length);
            int totalLines = 0;
            
            for (File file : txtFiles) {
                System.out.println("  Reading: " + file.getName());
                DocumentReader reader = new DocumentReader(file.getPath(), 
                        DocumentReader.DocumentFormat.SINGLE_DOCUMENT);
                List<String> docs = reader.readDocuments();
                allDocuments.addAll(docs);
                totalLines += docs.size();
            }
            
            System.out.println("Loaded total " + allDocuments.size() + " document(s)");
            System.out.println("Total lines: " + totalLines + "\n");
            monitor.endOperation("1. Đọc file");
            
            // Step 2: Configure and execute pipeline
            monitor.startOperation("2. Xử lý Pipeline");
            PipelineConfiguration config = new PipelineConfiguration()
                    .setLowercase(true)
                    .setRemoveStopWords(true)
                    .setApplyStemming(false)
                    .setMinFrequency(1)
                    .setMinTokenLength(1);
            
            SequencingPipeline pipeline = new SequencingPipeline(config);
            PipelineResult result = pipeline.execute(allDocuments);
            monitor.endOperation("2. Xử lý Pipeline");
            
            // Step 3: Write output files (excluding HTML)
            monitor.startOperation("3. Ghi file kết quả (.txt)");
            writeOutputFiles(result);
            monitor.endOperation("3. Ghi file kết quả (.txt)");
            
            // Step 4: Generate HTML report (not counted in main processing time)
            String htmlPath = OUTPUT_DIR + "/report.html";
            HTMLReportWriter htmlWriter = new HTMLReportWriter(htmlPath, result, allDocuments);
            htmlWriter.write();
            System.out.println("HTML Report written to: " + htmlPath);
            
            // End monitoring before HTML generation time
            monitor.endProcessing();
            
            // Print results
            System.out.println("\n" + "=".repeat(80));
            System.out.println("COMPLETED! Results saved to Data/Output");
            System.out.println("Open: Data/Output/report.html");
            System.out.println("=".repeat(80));
            
            // Print performance report
            monitor.printReport();
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void writeOutputFiles(PipelineResult result) throws IOException {
        String sequencePath = OUTPUT_BASE + "_sequences.txt";
        SequenceWriter sequenceWriter = new SequenceWriter(sequencePath, 
                SequenceWriter.OutputFormat.PLAIN_TEXT);
        sequenceWriter.writeSequences(result.getSequences());
        System.out.println("Sequences written to: " + sequencePath);
        
        String bowPath = OUTPUT_BASE + "_bow_vectors.txt";
        SequenceWriter bowWriter = new SequenceWriter(bowPath, 
                SequenceWriter.OutputFormat.PLAIN_TEXT);
        bowWriter.writeVectors(result.getBowVectors());
        System.out.println("BoW vectors written to: " + bowPath);
        
        // Write TF-IDF with all formulas instead of just vectors
        String tfidfPath = OUTPUT_BASE + "_tfidf_vectors.txt";
        SequenceWriter tfidfWriter = new SequenceWriter(tfidfPath, 
                SequenceWriter.OutputFormat.PLAIN_TEXT);
        tfidfWriter.writeTFIDFAllFormulas(result.getTfidfCalculator());
        System.out.println("TF-IDF all formulas written to: " + tfidfPath);
        
        String numericPath = OUTPUT_BASE + "_numeric.txt";
        SequenceWriter numericWriter = new SequenceWriter(numericPath, 
                SequenceWriter.OutputFormat.NUMERIC_SEQUENCES);
        numericWriter.writeSequences(result.getSequences());
        System.out.println("Numeric sequences written to: " + numericPath);
    }
}
