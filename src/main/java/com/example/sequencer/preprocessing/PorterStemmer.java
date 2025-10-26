package com.example.sequencer.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PorterStemmer - Implementation of the Porter Stemming Algorithm
 * Reduces words to their root form (stem)
 * 
 * Based on: Porter, M. "An algorithm for suffix stripping." Program 14.3 (1980): 130-137.
 * 
 * This is a simplified implementation focusing on the most common rules.
 * For production use, consider using a full implementation or library.
 */
public class PorterStemmer {
    
    // Danh sách từ không nên stem (động, được cập nhật từ bên ngoài)
    private Set<String> protectedWords;
    // Tập các từ đã xuất hiện trong corpus (dùng để xác thực kết quả)
    private Set<String> knownWords;
    
    public PorterStemmer() {
        this.protectedWords = new HashSet<>();
        this.knownWords = new HashSet<>();
    }
    
    /**
     * Thiết lập danh sách từ được bảo vệ khỏi stemming
     */
    public void setProtectedWords(Set<String> words) {
        this.protectedWords = new HashSet<>(words);
    }
    
    /**
     * Thêm từ vào danh sách bảo vệ
     */
    public void addProtectedWords(Set<String> words) {
        this.protectedWords.addAll(words);
    }

    /**
     * Cung cấp danh sách từ đã biết trong corpus để kiểm tra tính hợp lệ sau khi stemming
     */
    public void setKnownWords(Set<String> words) {
        this.knownWords = new HashSet<>(words);
    }
    
    /**
     * Stem a single word
     * @param word Input word
     * @return Stemmed word
     */
    public String stem(String word) {
        if (word == null || word.length() < 3) {
            return word;
        }
        
        String lowerWord = word.toLowerCase();
        
        // Bỏ qua từ trong danh sách bảo vệ
        if (protectedWords.contains(lowerWord)) {
            return lowerWord;
        }
        
        word = lowerWord;
        
        // Step 1a: plural forms
        word = step1a(word);
        
        // Step 1b: past tense
        word = step1b(word);
        
        // Step 1c: y -> i
        word = step1c(word);
        
        // Step 2: double suffixes
        word = step2(word);
        
        // Step 3: more suffixes
        word = step3(word);
        
        // Step 4: -ic, -full, -ness etc
        word = step4(word);
        
        // Step 5: final cleanup
        word = step5(word);
        
        return ensureMeaningful(lowerWord, word);
    }
    
    /**
     * Stem a list of tokens
     * @param tokens List of tokens
     * @return List of stemmed tokens
     */
    public List<String> stemAll(List<String> tokens) {
        return tokens.stream()
                .map(this::stem)
                .collect(Collectors.toList());
    }
    
    /**
     * Stem multiple documents
     * @param tokenizedDocuments List of tokenized documents
     * @return List of stemmed documents
     */
    public List<List<String>> stemDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> stemmed = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            stemmed.add(stemAll(tokens));
        }
        return stemmed;
    }
    
    private String step1a(String word) {
        if (word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("ss")) {
            return word;
        } else if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
    
    private String step1b(String word) {
        if (word.endsWith("eed")) {
            if (measureConsonantSequence(word.substring(0, word.length() - 3)) > 0) {
                return word.substring(0, word.length() - 1);
            }
        } else if (word.endsWith("ed")) {
            String stem = word.substring(0, word.length() - 2);
            if (containsVowel(stem)) {
                return adjustStep1b(stem);
            }
        } else if (word.endsWith("ing")) {
            String stem = word.substring(0, word.length() - 3);
            if (containsVowel(stem)) {
                return adjustStep1b(stem);
            }
        }
        return word;
    }
    
    private String adjustStep1b(String word) {
        if (word.endsWith("at") || word.endsWith("bl") || word.endsWith("iz")) {
            return word + "e";
        } else if (endsWithDoubleConsonant(word) && 
                   !word.endsWith("l") && !word.endsWith("s") && !word.endsWith("z")) {
            return word.substring(0, word.length() - 1);
        } else if (measureConsonantSequence(word) == 1 && endsWithCVC(word)) {
            return word + "e";
        }
        return word;
    }
    
    private String step1c(String word) {
        if (word.endsWith("y") && containsVowel(word.substring(0, word.length() - 1))) {
            return word.substring(0, word.length() - 1) + "i";
        }
        return word;
    }
    
    private String step2(String word) {
        String[][] suffixes = {
            {"ational", "ate"}, {"tional", "tion"}, {"enci", "ence"}, {"anci", "ance"},
            {"izer", "ize"}, {"abli", "able"}, {"alli", "al"}, {"entli", "ent"},
            {"eli", "e"}, {"ousli", "ous"}, {"ization", "ize"}, {"ation", "ate"},
            {"ator", "ate"}, {"alism", "al"}, {"iveness", "ive"}, {"fulness", "ful"},
            {"ousness", "ous"}, {"aliti", "al"}, {"iviti", "ive"}, {"biliti", "ble"}
        };
        
        for (String[] suffix : suffixes) {
            if (word.endsWith(suffix[0])) {
                String stem = word.substring(0, word.length() - suffix[0].length());
                if (measureConsonantSequence(stem) > 0) {
                    return stem + suffix[1];
                }
            }
        }
        return word;
    }
    
    private String step3(String word) {
        String[][] suffixes = {
            {"icate", "ic"}, {"ative", ""}, {"alize", "al"},
            {"iciti", "ic"}, {"ical", "ic"}, {"ful", ""}, {"ness", ""}
        };
        
        for (String[] suffix : suffixes) {
            if (word.endsWith(suffix[0])) {
                String stem = word.substring(0, word.length() - suffix[0].length());
                if (measureConsonantSequence(stem) > 0) {
                    return stem + suffix[1];
                }
            }
        }
        return word;
    }
    
    private String step4(String word) {
        String[] suffixes = {
            "al", "ance", "ence", "er", "ic", "able", "ible", "ant",
            "ement", "ment", "ent", "ion", "ou", "ism", "ate", "iti", "ous", "ive", "ize"
        };
        
        for (String suffix : suffixes) {
            if (word.endsWith(suffix)) {
                String stem = word.substring(0, word.length() - suffix.length());
                if (measureConsonantSequence(stem) > 1) {
                    return stem;
                }
            }
        }
        return word;
    }
    
    private String step5(String word) {
        if (word.endsWith("e")) {
            String stem = word.substring(0, word.length() - 1);
            int measure = measureConsonantSequence(stem);
            if (measure > 1 || (measure == 1 && !endsWithCVC(stem))) {
                return stem;
            }
        }
        if (word.length() > 1 && endsWithDoubleConsonant(word) && word.endsWith("l")) {
            if (measureConsonantSequence(word) > 1) {
                return word.substring(0, word.length() - 1);
            }
        }
        return word;
    }
    
    private boolean isConsonant(char c) {
        return "aeiou".indexOf(c) == -1;
    }
    
    private boolean containsVowel(String word) {
        for (char c : word.toCharArray()) {
            if (!isConsonant(c)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean endsWithDoubleConsonant(String word) {
        if (word.length() < 2) return false;
        char last = word.charAt(word.length() - 1);
        char secondLast = word.charAt(word.length() - 2);
        return last == secondLast && isConsonant(last);
    }
    
    private boolean endsWithCVC(String word) {
        if (word.length() < 3) return false;
        char last = word.charAt(word.length() - 1);
        char middle = word.charAt(word.length() - 2);
        char first = word.charAt(word.length() - 3);
        return isConsonant(first) && !isConsonant(middle) && isConsonant(last) &&
               !"wxy".contains(String.valueOf(last));
    }
    
    private int measureConsonantSequence(String word) {
        int measure = 0;
        boolean prevWasVowel = false;
        
        for (char c : word.toCharArray()) {
            boolean isVowel = !isConsonant(c);
            if (isVowel && !prevWasVowel) {
                measure++;
            }
            prevWasVowel = isVowel;
        }
        
        return measure;
    }

    private String ensureMeaningful(String original, String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            return original;
        }
        if (candidate.equals(original)) {
            return candidate;
        }
        if (protectedWords.contains(candidate) || knownWords.contains(candidate)) {
            return candidate;
        }

        // Ưu tiên chuẩn hoá dạng số nhiều → số ít
        if (original.endsWith("ies") && original.length() > 4) {
            String stem = original.substring(0, original.length() - 3);
            String ieForm = stem + "ie";
            if (isMeaningfulString(ieForm)) {
                return ieForm;
            }
            String yForm = stem + "y";
            if (isMeaningfulString(yForm)) {
                return yForm;
            }
        }

        if (original.endsWith("ves") && original.length() > 4) {
            String stem = original.substring(0, original.length() - 3);
            String fForm = stem + "f";
            String veForm = stem + "ve";
            String feForm = stem + "fe";

            if (stem.endsWith("i") && isMeaningfulString(feForm)) {
                return feForm;
            }
            if ((fForm.endsWith("of") || fForm.endsWith("uf")) && isMeaningfulString(veForm)) {
                return veForm;
            }
            if (isMeaningfulString(fForm) && (fForm.endsWith("lf") || fForm.endsWith("rf")
                    || fForm.endsWith("af") || fForm.endsWith("ef") || fForm.endsWith("ff"))) {
                return fForm;
            }
            if (isMeaningfulString(veForm)) {
                return veForm;
            }
            if (isMeaningfulString(fForm)) {
                return fForm;
            }
            if (isMeaningfulString(feForm)) {
                return feForm;
            }
        }

        if (original.endsWith("es") && original.length() > 3) {
            String stem = original.substring(0, original.length() - 2);
            String stemWithE = stem + "e";
            if (shouldPreferSilentE(stem) && isMeaningfulString(stemWithE)) {
                return stemWithE;
            }
            if (isMeaningfulString(stem)) {
                return stem;
            }
            if (!shouldPreferSilentE(stem) && isMeaningfulString(stemWithE)) {
                return stemWithE;
            }
        }

        if (original.endsWith("s") && original.length() > 3 && !original.endsWith("ss")) {
            String stem = original.substring(0, original.length() - 1);
            if (isMeaningfulString(stem)) {
                return stem;
            }
            String stemWithE = stem + "e";
            if (isMeaningfulString(stemWithE)) {
                return stemWithE;
            }
        }

        if (original.endsWith("ing") && original.length() > 4) {
            String base = original.substring(0, original.length() - 3);
            if (base.length() > 2 && endsWithDoubleConsonant(base)) {
                String reduced = base.substring(0, base.length() - 1);
                if (isMeaningfulString(reduced)) {
                    return reduced;
                }
            }
            String baseWithE = base + "e";
            if (base.length() <= 3 && shouldPreferSilentE(base) && isMeaningfulString(baseWithE)) {
                return baseWithE;
            }
            if (isMeaningfulString(base)) {
                return base;
            }
            if (shouldPreferSilentE(base) && isMeaningfulString(baseWithE)) {
                return baseWithE;
            }
            if (isMeaningfulString(baseWithE)) {
                return baseWithE;
            }
        }

        if (original.endsWith("ed") && original.length() > 4) {
            String stem = original.substring(0, original.length() - 2);
            if (isMeaningfulString(stem)) {
                return stem;
            }
            if (stem.endsWith("i") && stem.length() > 2) {
                String yForm = stem.substring(0, stem.length() - 1) + "y";
                if (isMeaningfulString(yForm)) {
                    return yForm;
                }
            }
        }

        if (original.endsWith("e") && !candidate.endsWith("e") && isMeaningfulString(original)) {
            return original;
        }

        if ((original.equals(candidate + "er") || original.equals(candidate + "or"))
                && !knownWords.contains(candidate)) {
            return original;
        }

        if (original.endsWith("ll") && candidate.endsWith("l") && original.length() == candidate.length() + 1) {
            return original;
        }

        if (!isMeaningfulString(candidate)) {
            return isMeaningfulString(original) ? original : candidate;
        }

        return candidate;
    }

    private boolean isMeaningfulString(String word) {
        if (word == null) {
            return false;
        }
        if (word.length() < 3) {
            return false;
        }
        for (char c : word.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        if (!containsVowel(word)) {
            return false;
        }
        if (word.endsWith("i") && !(word.endsWith("ai") || word.endsWith("ei") || word.endsWith("oi"))) {
            return false;
        }
        return true;
    }

    private boolean shouldPreferSilentE(String stem) {
        if (stem == null || stem.isEmpty()) {
            return false;
        }
        if (stem.length() >= 2) {
            String lastTwo = stem.substring(stem.length() - 2);
            if ("ch".equals(lastTwo) || "sh".equals(lastTwo) || "ss".equals(lastTwo)) {
                return false;
            }
        }
        char lastChar = stem.charAt(stem.length() - 1);
        if (lastChar == 'x' || lastChar == 'z') {
            return false;
        }
        if (lastChar == 's') {
            if (stem.length() >= 2) {
                char prev = stem.charAt(stem.length() - 2);
                if (prev == 'u' || prev == 's') {
                    return false;
                }
            }
            return true;
        }
    return lastChar == 'd' || lastChar == 'g' || lastChar == 'k' || lastChar == 'l'
        || lastChar == 'm' || lastChar == 'p' || lastChar == 'r'
        || lastChar == 't' || lastChar == 'v' || lastChar == 'c';
    }
}
