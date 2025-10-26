package com.example.sequencer.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentReader - Reads documents from various file formats
 * Supports single and multi-document reading
 */
public class DocumentReader {
    
    private final String filePath;
    private final DocumentFormat format;
    
    public enum DocumentFormat {
        SINGLE_DOCUMENT,      // Entire file is one document
        LINE_PER_DOCUMENT,    // Each line is a separate document
        PARAGRAPH_PER_DOCUMENT // Each paragraph (separated by blank lines) is a document
    }
    
    public DocumentReader(String filePath) {
        this(filePath, DocumentFormat.SINGLE_DOCUMENT);
    }
    
    public DocumentReader(String filePath, DocumentFormat format) {
        this.filePath = filePath;
        this.format = format;
    }
    
    /**
     * Read documents from file
     * @return List of document strings
     * @throws IOException if file reading fails
     */
    public List<String> readDocuments() throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        switch (format) {
            case SINGLE_DOCUMENT:
                return readAsSingleDocument();
            case LINE_PER_DOCUMENT:
                return readLinePerDocument();
            case PARAGRAPH_PER_DOCUMENT:
                return readParagraphPerDocument();
            default:
                throw new IllegalStateException("Unknown format: " + format);
        }
    }
    
    /**
     * Read entire file as single document
     * @return List containing single document
     * @throws IOException if reading fails
     */
    private List<String> readAsSingleDocument() throws IOException {
        List<String> documents = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        documents.add(content);
        return documents;
    }
    
    /**
     * Read file with each line as a separate document
     * @return List of documents (one per line)
     * @throws IOException if reading fails
     */
    private List<String> readLinePerDocument() throws IOException {
        List<String> documents = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    documents.add(line);
                }
            }
        }
        
        return documents;
    }
    
    /**
     * Read file with paragraphs as separate documents
     * @return List of documents (one per paragraph)
     * @throws IOException if reading fails
     */
    private List<String> readParagraphPerDocument() throws IOException {
        List<String> documents = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Empty line indicates paragraph break
                    if (currentParagraph.length() > 0) {
                        documents.add(currentParagraph.toString().trim());
                        currentParagraph = new StringBuilder();
                    }
                } else {
                    currentParagraph.append(line).append(" ");
                }
            }
            
            // Add last paragraph if exists
            if (currentParagraph.length() > 0) {
                documents.add(currentParagraph.toString().trim());
            }
        }
        
        return documents;
    }
    
    /**
     * Check if file exists
     * @return true if file exists
     */
    public boolean fileExists() {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Get file size in bytes
     * @return File size
     * @throws IOException if file access fails
     */
    public long getFileSize() throws IOException {
        return Files.size(Paths.get(filePath));
    }
}
