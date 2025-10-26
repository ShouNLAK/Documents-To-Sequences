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

/**
 * AutoRunner - Batch processing for entire folder
 */
public class AutoRunner {
    
    private static final String INPUT_DIR = "Data/Input";
    private static final String OUTPUT_DIR = "Data/Output";
    private static final String OUTPUT_BASE = OUTPUT_DIR + "/output";
    
    public static void main(String[] args) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("  AUTO RUNNER - BATCH DOCUMENT PROCESSING");
            System.out.println("=".repeat(80) + "\n");
            
            File inputDir = new File(INPUT_DIR);
            File[] txtFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (txtFiles == null || txtFiles.length == 0) {
                System.err.println("No .txt files found in " + INPUT_DIR);
                return;
            }
            
            System.out.println("Found " + txtFiles.length + " file(s) in " + INPUT_DIR);
            
            List<String> allDocuments = new ArrayList<>();
            for (File file : txtFiles) {
                System.out.println("  Reading: " + file.getName());
                DocumentReader reader = new DocumentReader(file.getPath(), 
                        DocumentReader.DocumentFormat.SINGLE_DOCUMENT);
                allDocuments.addAll(reader.readDocuments());
            }
            System.out.println("Loaded total " + allDocuments.size() + " document(s)\n");
            
            PipelineConfiguration config = new PipelineConfiguration()
                    .setLowercase(true)
                    .setRemoveStopWords(true)
                    .setApplyStemming(true)
                    .setMinFrequency(1)
                    .setMinTokenLength(1);
            
            SequencingPipeline pipeline = new SequencingPipeline(config);
            PipelineResult result = pipeline.execute(allDocuments);
            
            writeOutputFiles(result);
            
            String htmlPath = OUTPUT_DIR + "/report.html";
            HTMLReportWriter htmlWriter = new HTMLReportWriter(htmlPath, result, allDocuments);
            htmlWriter.write();
            System.out.println("HTML Report written to: " + htmlPath);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("COMPLETED! Results saved to Data/Output");
            System.out.println("Open: Data/Output/report.html");
            System.out.println("=".repeat(80) + "\n");
            
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
        
        String tfidfPath = OUTPUT_BASE + "_tfidf_vectors.txt";
        SequenceWriter tfidfWriter = new SequenceWriter(tfidfPath, 
                SequenceWriter.OutputFormat.PLAIN_TEXT);
        tfidfWriter.writeVectors(result.getTfidfVectors());
        System.out.println("TF-IDF vectors written to: " + tfidfPath);
        
        String numericPath = OUTPUT_BASE + "_numeric.txt";
        SequenceWriter numericWriter = new SequenceWriter(numericPath, 
                SequenceWriter.OutputFormat.NUMERIC_SEQUENCES);
        numericWriter.writeSequences(result.getSequences());
        System.out.println("Numeric sequences written to: " + numericPath);
    }
}
