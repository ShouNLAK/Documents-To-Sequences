package com.example.sequencer.encoding;

import java.util.ArrayList;
import java.util.List;

/**
 * IntegerEncoder - Converts token sequences to integer sequences
 * Implements the fundamental transformation from symbolic to numerical representation
 * 
 * References:
 * - Document-to-Sequence Conversion in Data Mining
 * - Integer Encoding and One-Hot Encoding section
 */
public class IntegerEncoder {
    
    private final Vocabulary vocabulary;
    
    public IntegerEncoder(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }
    
    /**
     * Encode a single document to integer sequence
     * @param tokens List of tokens
     * @return List of integer indices
     */
    public List<Integer> encode(List<String> tokens) {
        List<Integer> encoded = new ArrayList<>();
        for (String token : tokens) {
            encoded.add(vocabulary.getIndex(token));
        }
        return encoded;
    }
    
    /**
     * Encode multiple documents
     * @param tokenizedDocuments List of tokenized documents
     * @return List of encoded sequences
     */
    public List<List<Integer>> encodeAll(List<List<String>> tokenizedDocuments) {
        List<List<Integer>> allEncoded = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            allEncoded.add(encode(tokens));
        }
        return allEncoded;
    }
    
    /**
     * Decode an integer sequence back to tokens
     * @param indices List of integer indices
     * @return List of tokens
     */
    public List<String> decode(List<Integer> indices) {
        List<String> decoded = new ArrayList<>();
        for (Integer index : indices) {
            decoded.add(vocabulary.getToken(index));
        }
        return decoded;
    }
    
    /**
     * Decode multiple sequences
     * @param encodedSequences List of encoded sequences
     * @return List of decoded token sequences
     */
    public List<List<String>> decodeAll(List<List<Integer>> encodedSequences) {
        List<List<String>> allDecoded = new ArrayList<>();
        for (List<Integer> sequence : encodedSequences) {
            allDecoded.add(decode(sequence));
        }
        return allDecoded;
    }
    
    /**
     * Pad or truncate sequence to fixed length
     * @param sequence Input sequence
     * @param maxLength Target length
     * @return Padded/truncated sequence
     */
    public List<Integer> padSequence(List<Integer> sequence, int maxLength) {
        List<Integer> padded = new ArrayList<>(sequence);
        
        // Truncate if too long
        if (padded.size() > maxLength) {
            return padded.subList(0, maxLength);
        }
        
        // Pad if too short
        while (padded.size() < maxLength) {
            padded.add(vocabulary.getPaddingIndex());
        }
        
        return padded;
    }
    
    /**
     * Pad all sequences to the same length
     * @param sequences List of sequences
     * @param maxLength Target length (if null, uses longest sequence)
     * @return Padded sequences
     */
    public List<List<Integer>> padAll(List<List<Integer>> sequences, Integer maxLength) {
        if (maxLength == null) {
            maxLength = sequences.stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);
        }
        
        List<List<Integer>> padded = new ArrayList<>();
        for (List<Integer> sequence : sequences) {
            padded.add(padSequence(sequence, maxLength));
        }
        
        return padded;
    }
    
    /**
     * Get vocabulary used by this encoder
     * @return Vocabulary instance
     */
    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}
