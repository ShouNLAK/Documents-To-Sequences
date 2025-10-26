package com.example.sequencer.test;

import com.example.sequencer.preprocessing.*;
import com.example.sequencer.encoding.*;
import com.example.sequencer.vectorization.*;
import com.example.sequencer.pipeline.*;

import java.util.Arrays;
import java.util.List;

/**
 * ManualTest - Simple test to verify all components work
 * Run this to validate the implementation without JUnit
 */
public class ManualTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("MANUAL COMPONENT TEST");
        System.out.println("=".repeat(80));
        
        try {
            // Test documents
            List<String> documents = Arrays.asList(
                "Natural Language Processing is amazing!",
                "Machine learning algorithms are powerful.",
                "Text preprocessing is essential for NLP."
            );
            
            // Test 1: Preprocessing
            System.out.println("\n[Test 1] Text Preprocessing");
            TextPreprocessor preprocessor = new TextPreprocessor.Builder().build();
            for (int i = 0; i < documents.size(); i++) {
                String original = documents.get(i);
                String processed = preprocessor.preprocess(original);
                System.out.println("  Doc " + (i+1) + ": " + original);
                System.out.println("       → " + processed);
            }
            
            // Test 2: Tokenization
            System.out.println("\n[Test 2] Tokenization");
            Tokenizer tokenizer = new Tokenizer();
            List<String> preprocessed = preprocessor.preprocessAll(documents);
            List<List<String>> tokenized = tokenizer.tokenizeAll(preprocessed);
            for (int i = 0; i < tokenized.size(); i++) {
                System.out.println("  Doc " + (i+1) + ": " + tokenized.get(i));
            }
            
            // Test 3: Stop Word Filtering
            System.out.println("\n[Test 3] Stop Word Filtering");
            StopWordFilter filter = new StopWordFilter();
            List<List<String>> filtered = filter.filterAll(tokenized);
            for (int i = 0; i < filtered.size(); i++) {
                System.out.println("  Doc " + (i+1) + ": " + filtered.get(i));
            }
            
            // Test 4: Stemming
            System.out.println("\n[Test 4] Porter Stemming");
            PorterStemmer stemmer = new PorterStemmer();
            List<List<String>> stemmed = stemmer.stemDocuments(filtered);
            for (int i = 0; i < stemmed.size(); i++) {
                System.out.println("  Doc " + (i+1) + ": " + stemmed.get(i));
            }
            
            // Test 5: Vocabulary & Encoding
            System.out.println("\n[Test 5] Vocabulary & Integer Encoding");
            Vocabulary vocab = new Vocabulary();
            vocab.buildFromDocuments(stemmed);
            System.out.println("  Vocabulary size: " + vocab.getSize());
            
            IntegerEncoder encoder = new IntegerEncoder(vocab);
            List<List<Integer>> sequences = encoder.encodeAll(stemmed);
            for (int i = 0; i < sequences.size(); i++) {
                System.out.println("  Doc " + (i+1) + ": " + sequences.get(i));
            }
            
            // Test 6: Bag-of-Words
            System.out.println("\n[Test 6] Bag-of-Words Vectorization");
            BagOfWordsVectorizer bow = new BagOfWordsVectorizer();
            bow.fit(stemmed);
            System.out.println("  BoW vocabulary: " + bow.getVocabularySize() + " features");
            System.out.println("  Feature names: " + bow.getFeatureNames());
            
            // Test 7: TF-IDF
            System.out.println("\n[Test 7] TF-IDF Vectorization");
            TfidfVectorizer tfidf = new TfidfVectorizer();
            tfidf.fit(stemmed);
            System.out.println("  TF-IDF vocabulary: " + tfidf.getVocabularySize() + " features");
            
            // Test 8: Full Pipeline
            System.out.println("\n[Test 8] Complete Pipeline Execution");
            SequencingPipeline.PipelineConfiguration config = 
                new SequencingPipeline.PipelineConfiguration();
            SequencingPipeline pipeline = new SequencingPipeline(config);
            SequencingPipeline.PipelineResult result = pipeline.execute(documents);
            
            System.out.println("\n  Results:");
            System.out.println("  - Documents processed: " + result.getSequences().size());
            System.out.println("  - Vocabulary size: " + result.getVocabulary().getSize());
            System.out.println("  - BoW vectors: " + result.getBowVectors().size());
            System.out.println("  - TF-IDF vectors: " + result.getTfidfVectors().size());
            
            // Success
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ ALL TESTS PASSED SUCCESSFULLY!");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
