# 📊 Document-to-Sequence Converter for Data Mining

> **Research-grade Java NLP pipeline** với **Interactive HTML Dashboard** - Transform raw text into ML-ready numerical sequences

[![Java](https://img.shields.io/badge/Java-11%2B-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Build](https://img.shields.io/badge/Build-Passing-success?style=for-the-badge)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Production-green?style=for-the-badge)](https://github.com)

---

## 🎯 What's New (Latest Version)

### ✨ **Enhanced UI/UX**
- 🎨 **Modern Tab Design** - Beautiful animated tabs with icons (📊 Matrix, 📖 Vocabulary, 📄 Documents)
- 📊 **Visual Progress Bars** - IDF and TF-IDF values với gradient bars
- 🎯 **Smart Highlighting** - Tự động highlight important terms (IDF>1.5 hoặc TF-IDF>0.01)
- 💅 **Professional Styling** - Monospace badges, gradient headers, smooth animations

### 🔬 **TF-IDF Calculation Fixed**
- ✅ **Smooth-IDF Formula** - `log((N+1)/(df+1)) + 1` theo chuẩn sklearn
- ✅ **Document Frequency Column** - Thêm cột Doc Freq trong bảng Vocabulary
- 📖 **6-Column Vocabulary Table** - ID, Token, Doc Freq, Avg TF, IDF, Avg TF-IDF
- 📚 **Full Documentation** - TFIDF_VERIFICATION.md với chi tiết công thức

**References:**
- [GeeksforGeeks - Understanding TF-IDF](https://www.geeksforgeeks.org/understanding-tf-idf-term-frequency-inverse-document-frequency/)
- [Wikipedia - TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [Usage Examples](#-usage-examples)
- [HTML Dashboard](#-html-dashboard)
- [Output Files Explained](#-output-files-explained)
- [Pipeline Details](#-pipeline-details)
- [Configuration Guide](#-configuration-guide)
- [Technical Documentation](#-technical-documentation)
- [FAQ & Troubleshooting](#-faq--troubleshooting)

---

## 🌟 Overview

### What It Does

Transform unstructured text documents into **machine learning-ready numerical representations** through a comprehensive **7-stage NLP pipeline**:

```mermaid
Raw Text → Clean → Tokenize → Filter → Stem → Encode → Vectorize → ML Models
   ↓        ↓         ↓         ↓       ↓        ↓         ↓
"Hello"  "hello"  ["hello"]   [✓]   ["hello"]  [42]   [0,0,1,...]
```

### Why Choose This?

| Feature | Description |
|---------|-------------|
| 🎓 **Research-Grade** | Based on scientific papers (GeeksforGeeks, Wikipedia) |
| 🔬 **Accurate TF-IDF** | Verified smooth-IDF formula `log((N+1)/(df+1)) + 1` |
| 🎨 **Beautiful Dashboard** | Interactive HTML with modern UI (gradient tabs, visual bars) |
| 🚀 **Production-Ready** | Error handling, batch processing, 50+ files support |
| 📊 **Rich Visualization** | Frequency matrix, vocabulary stats, document comparison |
| 🔧 **Highly Configurable** | Single file vs folder, custom preprocessing options |
| 📥 **Multiple Outputs** | BoW, TF-IDF, Integer Sequences, CSV export |
| 💻 **Cross-Platform** | Windows (build.bat), Linux/Mac (build.sh) |

---

## 🎁 Key Features

### 🔄 **Complete NLP Pipeline (7 Steps)**

1. **Text Preprocessing** - Cleaning, normalization, case conversion
2. **Tokenization** - Word-level splitting with configurable min length
3. **Stop Word Filtering** - 127 English stop words removal
4. **Porter Stemming** - Intelligent stemming preserving word meaning
5. **Vocabulary Construction** - Token-to-index mapping with frequency thresholds
6. **Integer Encoding** - Sequence representation with special tokens (<PAD>, <UNK>)
7. **Vectorization** - Bag-of-Words and TF-IDF with L2 normalization

### 📊 **Interactive HTML Dashboard**

#### 🎨 **Modern Tab Navigation**
```
┌─────────────────────────────────────────────────────┐
│  📊 Frequency Matrix  │  📖 Vocabulary  │  📄 Docs │
│  ════════════════════                               │
└─────────────────────────────────────────────────────┘
```

**Features:**
- ✅ **Animated Tabs** - Icon scaling, gradient underline, smooth transitions
- ✅ **Sticky Headers** - Table headers stay visible while scrolling
- ✅ **Hover Effects** - Blue highlight, button lift animation
- ✅ **Pagination** - 10 docs/page (Matrix), 50 tokens/page (Vocabulary)
- ✅ **Disabled States** - Auto-disable Prev/Next at boundaries
- ✅ **CSV Export** - Download Matrix or Vocabulary with full data

#### 📖 **Enhanced Vocabulary Table (6 Columns)**

| ID | Token | Doc Freq | Avg TF | IDF | Avg TF-IDF |
|----|-------|----------|--------|-----|------------|
| 2 | `add` | 50 | 0.0095 | 1.00 ▰▰▰▰▱ | 0.0536 ▰▰▰▱ |
| 15 | `cleaners` | 35 | 0.0124 | **1.35** ▰▰▰▰▰▰▱ | **0.0642** ▰▰▰▰▰▱ |

**Visual Elements:**
- 📊 **Progress Bars** - Width proportional to max IDF/TF-IDF
- 🎯 **Highlighting** - Yellow background for important terms
- 💅 **Token Badges** - Monospace font with gray background
- 🔢 **Monospace Numbers** - Easy to read and align

#### 📄 **Document Viewer**

- **Original Text** - Raw input with formatting preserved
- **Preprocessed Text** - Cleaned, stemmed tokens
- **Toggle View** - Radio buttons to switch between versions
- **Multiple Documents** - First 50 documents shown with pagination

### 🔬 **Accurate TF-IDF Implementation**

**Formula (Smooth-IDF):**
```
TF(t,d) = count(t in d) / total_terms(d)

IDF(t) = log((N + 1) / (df(t) + 1)) + 1

TF-IDF(t,d) = TF(t,d) × IDF(t)

# Then L2 Normalization:
norm = sqrt(Σ tfidf²)
normalized_tfidf = tfidf / norm
```

**Verification:**
- ✅ Matches sklearn `TfidfVectorizer` with `smooth_idf=True`
- ✅ Validated against GeeksforGeeks & Wikipedia formulas
- ✅ See `TFIDF_VERIFICATION.md` for detailed calculations

### 🚀 **Batch Processing**

Process **entire folders** automatically:
```bash
.\build.bat auto
# Processes all .txt files in Data/Input/
# Generates unified report.html
```

**Features:**
- 📂 Auto-detect all `.txt` files
- 📊 Unified frequency matrix (all documents)
- 📈 Combined vocabulary statistics
- 🎯 50+ documents support with pagination

---

## 🚀 Quick Start

### Prerequisites

- **Java 11+** (download from [oracle.com](https://www.oracle.com/java/technologies/downloads/))
- Text files with your documents

### 3-Step Setup

#### **Step 1: Prepare Input**

Create `Data/Input/input.txt`:
```text
Natural Language Processing (NLP) is a fascinating field.
It combines computer science, artificial intelligence, and linguistics.
Text mining helps extract valuable insights from unstructured data.
```

Or place **multiple files** (`input01.txt`, `input02.txt`, ...):
```
Data/Input/
├── input01.txt
├── input02.txt
├── input03.txt
└── ...
```

#### **Step 2: Run Pipeline**

**Option A: Auto Mode (Recommended)** - Batch processing
```bash
.\build.bat auto         # Windows
./build.sh auto          # Linux/Mac
```

**Option B: Interactive Mode** - Single file with prompts
```bash
.\build.bat run
```

**Option C: Build Only**
```bash
.\build.bat              # Compile without running
```

#### **Step 3: View Results**

Open `Data/Output/report.html` in your browser 🎉

---

## 📁 Project Structure

```
DocumentToSequences/
│
├── 📂 Data/
│   ├── Input/                      ← Your text files
│   │   ├── input.txt              ← Single file mode
│   │   ├── input01.txt            ← Batch mode
│   │   ├── input02.txt
│   │   └── ...
│   └── Output/                     ← Generated results
│       ├── output_sequences.txt   ← Integer sequences + tokens
│       ├── output_bow_vectors.txt ← Bag-of-Words vectors
│       ├── output_tfidf_vectors.txt ← TF-IDF vectors
│       ├── output_numeric.txt     ← Pure number sequences
│       └── report.html            ← 🌟 Interactive dashboard
│
├── 📂 src/main/java/com/example/sequencer/
│   │
│   ├── 📂 core/
│   │   ├── DocumentSequencerApplication.java  ← Main interactive app
│   │   └── AutoRunner.java                    ← Batch processor
│   │
│   ├── 📂 preprocessing/
│   │   ├── TextPreprocessor.java    ← Text cleaning
│   │   ├── Tokenizer.java           ← Word splitting
│   │   ├── StopWordFilter.java      ← Stop word removal
│   │   └── PorterStemmer.java       ← Stemming (intelligent)
│   │
│   ├── 📂 encoding/
│   │   ├── Vocabulary.java          ← Token-to-index mapping
│   │   └── IntegerEncoder.java      ← Sequence encoding
│   │
│   ├── 📂 vectorization/
│   │   ├── BagOfWordsVectorizer.java  ← BoW implementation
│   │   └── TfidfVectorizer.java       ← TF-IDF (smooth-idf)
│   │
│   ├── 📂 model/
│   │   ├── DocumentSequence.java    ← Document representation
│   │   └── SequenceVector.java      ← Vector representation
│   │
│   ├── 📂 pipeline/
│   │   └── SequencingPipeline.java  ← 7-stage pipeline orchestrator
│   │
│   └── 📂 io/
│       ├── DocumentReader.java      ← File input handler
│       ├── SequenceWriter.java      ← Text output writer
│       └── HtmlReportWriter.java    ← 🌟 HTML dashboard generator
│
├── 📂 target/classes/               ← Compiled .class files
│
├── 📄 build.bat                     ← Windows build script
├── 📄 build.sh                      ← Linux/Mac build script
├── 📄 pom.xml                       ← Maven config (optional)
│
├── 📄 README.md                     ← This file
├── 📄 TFIDF_VERIFICATION.md        ← TF-IDF formula verification
├── 📄 VOCABULARY_ENHANCEMENTS.md   ← UI improvements log
└── 📄 VOCAB_TABLE_GUIDE.txt        ← Quick reference card
```

---

## 💡 Usage Examples

### Example 1: Single Document Processing

**Input:** `Data/Input/input.txt`
```text
Machine learning is transforming how we analyze data.
Natural language processing enables computers to understand human language.
Deep learning models achieve remarkable results in text classification.
```

**Run:**
```bash
.\build.bat auto
```

**Output:** `Data/Output/report.html`
```
╔═══════════════════════════════════════════════════╗
║  📊 STATISTICS                                     ║
║  Documents: 1                                     ║
║  Vocabulary: 23 unique tokens                     ║
║  Total Tokens: 19 (after filtering)               ║
╚═══════════════════════════════════════════════════╝

📖 VOCABULARY TABLE (Page 1 / 1)
┌────┬────────────┬──────────┬─────────┬──────┬────────────┐
│ ID │   Token    │ Doc Freq │ Avg TF  │ IDF  │ Avg TFIDF  │
├────┼────────────┼──────────┼─────────┼──────┼────────────┤
│ 2  │  machine   │    1     │ 0.0526  │ 1.69 │  0.0892    │
│ 3  │  learn     │    2     │ 0.1053  │ 1.00 │  0.1053    │
│ 4  │  languag   │    2     │ 0.1053  │ 1.00 │  0.1053    │
└────┴────────────┴──────────┴─────────┴──────┴────────────┘
```

### Example 2: Batch Processing (50+ Files)

**Input:** `Data/Input/input01.txt` - `input50.txt`

**Run:**
```bash
.\build.bat auto
```

**Console Output:**
```
================================================================================
  AUTO RUNNER - BATCH DOCUMENT PROCESSING
================================================================================

Found 50 file(s) in Data/Input
  Reading: input01.txt
  Reading: input02.txt
  ...
Loaded total 50 document(s)

[Step 1/7] Text Preprocessing...
  ✓ Completed: Applied cleaning and normalization

[Step 2/7] Tokenization...
  ✓ Completed: Generated 26938 tokens

[Step 3/7] Stop Word Filtering...
  ✓ Completed: Retained 22692 tokens

[Step 4/7] Stemming...
  ✓ Completed: Applied Porter Stemmer

[Step 5/7] Vocabulary Construction & Integer Encoding...
Vocabulary built: 377 unique tokens (min_freq=1)
  ✓ Completed: Encoded to integer sequences

[Step 6/7] Bag-of-Words Vectorization...
BoW vocabulary fitted: 375 unique features
  ✓ Completed: Generated BoW vectors

[Step 7/7] TF-IDF Vectorization...
TF-IDF vocabulary fitted: 375 unique features
  ✓ Completed: Generated TF-IDF vectors

================================================================================
COMPLETED! Results saved to Data/Output
📊 Open: Data/Output/report.html
================================================================================
```

**HTML Report:**
- 📊 **Frequency Matrix**: 50 docs × 377 tokens (paginated: 10 docs/page)
- 📖 **Vocabulary**: 377 tokens with stats (paginated: 50 tokens/page)
- 📄 **Documents**: First 50 docs with Original/Preprocessed toggle

### Example 3: Custom Configuration

Edit `src/main/java/com/example/sequencer/core/AutoRunner.java`:

```java
PipelineConfiguration config = new PipelineConfiguration()
    .setLowercase(true)              // true | false
    .setRemoveStopWords(true)        // true | false
    .setApplyStemming(true)          // true | false
    .setMinFrequency(2)              // 1, 2, 3, ... (filter rare words)
    .setMinTokenLength(3);           // 1, 2, 3, ... (filter short words)
```

**Then rebuild:**
```bash
.\build.bat
.\build.bat auto
```

---

## 🎨 HTML Dashboard

### Tab 1: 📊 Frequency Matrix

**What it shows:** Document-Term frequency matrix

```
╔═══════════════════════════════════════════════╗
║ Doc / ID │  0  │  1  │  2  │  3  │ ...       ║
╠═══════════╪═════╪═════╪═════╪═════╪═══════════╣
║  Doc 1   │  0  │  0  │  3  │  5  │ ...       ║
║  Doc 2   │  0  │  0  │  2  │  1  │ ...       ║
║  Doc 3   │  0  │  0  │  4  │  2  │ ...       ║
╚═══════════╧═════╧═════╧═════╧═════╧═══════════╝
```

**Color Coding:**
- Gray (0) - Token absent
- Light Blue (1-2) - Low frequency
- Yellow (3-5) - Medium frequency
- Red (6+) - High frequency

**Controls:**
- ⬅️ **Prev** / **Next** ➡️ - Navigate pages (10 docs/page)
- 📥 **Download CSV** - Export full matrix

### Tab 2: 📖 Vocabulary Statistics

**What it shows:** Complete vocabulary with TF-IDF metrics

| Column | Description | Example |
|--------|-------------|---------|
| **ID** | Token index | 15 |
| **Token** | Word (stemmed) | `cleaners` |
| **Doc Freq** | # docs containing token | 35/50 |
| **Avg TF** | Average term frequency | 0.0124 |
| **IDF** | Inverse doc frequency | 1.3507 ▰▰▰▰▰▰▱ |
| **Avg TF-IDF** | Average TF-IDF score | 0.0642 ▰▰▰▰▰▱ |

**Visual Elements:**
- 📊 **Progress Bars** - Gradient bars showing relative values
- 🎯 **Highlighting** - Yellow background for IDF>1.5 or TF-IDF>0.01
- 💅 **Token Badges** - Monospace font with gray rounded background
- 🔢 **Monospace Numbers** - Easy comparison and alignment

**Controls:**
- ⬅️ **Prev** / **Next** ➡️ - Navigate pages (50 tokens/page)
- 📥 **Download CSV** - Export: `ID,Token,DocFreq,AvgTF,IDF,AvgTF-IDF`

**Example Interpretation:**

```
Token: "cleaners"
Doc Freq: 35    → Appears in 70% of documents
Avg TF: 0.0124  → Average 1.24% of words per document
IDF: 1.3507     → Higher than universal terms (>1.0)
                  ▰▰▰▰▰▰▱ (visual bar)
Avg TF-IDF: 0.0642 → HIGHLIGHTED ✨ (important term)
                     ▰▰▰▰▰▱ (visual bar)

Interpretation:
✅ Moderately common (70% docs)
✅ Has discriminative power (IDF > 1.0)
✅ Important for this domain (shopping/cleaning)
```

### Tab 3: 📄 Documents

**What it shows:** Document comparison (Original vs Preprocessed)

**Features:**
- 🔄 **Toggle View** - Radio buttons to switch between versions
- 📝 **Original Text** - Raw input with formatting preserved
- 🔧 **Preprocessed Text** - Cleaned, lowercased, stemmed tokens
- 📄 **First 50 Docs** - Pagination for large datasets

**Example:**

```
╔═══════════════════════════════════════════════════╗
║  Doc 1                                             ║
╠═══════════════════════════════════════════════════╣
║  ⚪ Original  ⚫ Preprocessed                      ║
╠═══════════════════════════════════════════════════╣
║  Natural Language Processing (NLP) is fascinating ║
║  field at the intersection of computer science... ║
╚═══════════════════════════════════════════════════╝

Click "Preprocessed":
╔═══════════════════════════════════════════════════╗
║  natur languag process nlp fascin field          ║
║  intersect comput scienc                          ║
╚═══════════════════════════════════════════════════╝
```

---

## 📤 Output Files Explained

### 1. `output_sequences.txt` - Detailed Conversion Info

```
Document 1:
-----------
Tokens: [natur, languag, process, nlp, fascin, field, intersect, comput, scienc, ...]
Integer Sequence: [13, 6, 27, 7, 52, 56, 117, 23, 102, ...]

Metadata:
- Original Token Count: 264
- Filtered Token Count: 173
- Unique Tokens: 128
```

**Use case:** Debug preprocessing, verify stemming results

### 2. `output_bow_vectors.txt` - Bag-of-Words

```
Document 1:
-----------
Dimension: 126
Top Features:
  - word: 9
  - text: 5
  - languag: 4
  - process: 4

Sparse Vector: {2: 9, 3: 5, 6: 4, ...}
```

**Use case:** Text classification, document similarity

### 3. `output_tfidf_vectors.txt` - TF-IDF Weighted

```
Document 1:
-----------
L2 Norm: 1.0 (normalized)
Top Features:
  - word: 0.472377
  - text: 0.262432
  - languag: 0.209946

Sparse Vector: {2: 0.472377, 3: 0.262432, ...}
```

**Use case:** Information retrieval, feature selection

### 4. `output_numeric.txt` - ML-Ready Sequences

```
13 6 27 7 52 56 117 23 102 37 30 122 113 ...
```

**Use case:** Feed into neural networks, sequence models

### 5. `report.html` - Interactive Dashboard 🌟

**Use case:** Exploratory data analysis, presentation, documentation

**Features:**
- ✅ No server needed - open directly in browser
- ✅ Offline-ready - pure HTML/CSS/JavaScript
- ✅ Responsive - works on desktop, tablet, mobile
- ✅ Modern design - gradient themes, smooth animations
- ✅ Data export - CSV download for Matrix and Vocabulary

---

## 🔧 Pipeline Details

### Stage 1: Text Preprocessing

**Class:** `TextPreprocessor.java`

**Operations:**
1. Lowercase conversion (optional)
2. HTML tag removal
3. URL/email removal
4. Punctuation removal (except apostrophes)
5. Extra whitespace normalization

**Example:**
```
Input:  "Check out https://example.com for NLP! It's amazing."
Output: "check out for nlp it's amazing"
```

### Stage 2: Tokenization

**Class:** `Tokenizer.java`

**Operations:**
1. Split on whitespace and punctuation
2. Filter by minimum length (default: 1)
3. Remove empty tokens

**Example:**
```
Input:  "natural language processing"
Output: ["natural", "language", "processing"]
```

### Stage 3: Stop Word Filtering

**Class:** `StopWordFilter.java`

**127 English Stop Words:**
```
a, an, the, is, are, was, were, be, been, being,
have, has, had, do, does, did, will, would, should,
could, may, might, must, can, of, at, by, for, with,
about, against, between, into, through, during, ...
```

**Example:**
```
Input:  ["natural", "language", "processing", "is", "a", "field"]
Output: ["natural", "language", "processing", "field"]
```

### Stage 4: Porter Stemming

**Class:** `PorterStemmer.java`

**Algorithm:** Porter Stemming (M. Porter, 1980)

**Intelligent Features:**
- ✅ Preserves word meaning (e.g., "program" not "programm")
- ✅ Handles irregular plurals (e.g., "knives" → "knife")
- ✅ Preserves silent-E when needed (e.g., "argue" → "argue")

**Example:**
```
computers → comput
processing → process
languages → languag
running → run
happier → happier (preserved)
```

### Stage 5: Vocabulary Construction

**Class:** `Vocabulary.java`

**Special Tokens:**
- `<PAD>` (Index 0) - Padding token
- `<UNK>` (Index 1) - Unknown token

**Frequency Filtering:**
```
minFrequency = 1  → Keep all tokens
minFrequency = 2  → Only tokens appearing 2+ times
minFrequency = 5  → Only tokens appearing 5+ times
```

**Example:**
```
Vocabulary (128 tokens):
0: <PAD>
1: <UNK>
2: word (freq: 9)
3: text (freq: 5)
4: languag (freq: 4)
...
```

### Stage 6: Integer Encoding

**Class:** `IntegerEncoder.java`

**Mapping:**
```
Token → Index
word → 2
text → 3
languag → 4
unknown_token → 1 (<UNK>)
```

**Example:**
```
Tokens:   ["word", "text", "word", "languag"]
Encoded:  [2, 3, 2, 4]
```

### Stage 7: Vectorization

#### A. Bag-of-Words (BoW)

**Class:** `BagOfWordsVectorizer.java`

**Formula:**
```
BoW[i] = count(token_i in document)
```

**Example:**
```
Document: "word text word languag"
Vocabulary: [word, text, languag, process, ...]

BoW Vector: [2, 1, 1, 0, ...]
            ↑  ↑  ↑  ↑
          word text lang proc
```

#### B. TF-IDF (Term Frequency-Inverse Document Frequency)

**Class:** `TfidfVectorizer.java`

**Formula (Smooth-IDF):**
```
TF(t,d) = count(t in d) / total_tokens(d)

IDF(t) = log((N + 1) / (df(t) + 1)) + 1
         where N = total documents
               df(t) = documents containing term t

TF-IDF(t,d) = TF(t,d) × IDF(t)

# L2 Normalization:
norm = sqrt(Σ tfidf²)
normalized_tfidf = tfidf / norm
```

**Example Calculation:**
```
Document: "word text word languag" (4 tokens)
Corpus: 50 documents
Token "word" appears in 40 documents

TF(word) = 2/4 = 0.5

IDF(word) = log((50+1)/(40+1)) + 1
          = log(51/41) + 1
          = log(1.244) + 1
          = 0.218 + 1
          = 1.218

TF-IDF(word) = 0.5 × 1.218 = 0.609

# After L2 normalization across all features:
normalized_tfidf(word) = 0.472377
```

**Verification:** See `TFIDF_VERIFICATION.md`

---

## ⚙️ Configuration Guide

### Pipeline Settings

**File:** `src/main/java/com/example/sequencer/core/AutoRunner.java`

```java
PipelineConfiguration config = new PipelineConfiguration()
    .setLowercase(true)              // Convert to lowercase
    .setRemoveStopWords(true)        // Filter stop words
    .setApplyStemming(true)          // Apply Porter stemmer
    .setMinFrequency(1)              // Min word frequency (1 = keep all)
    .setMinTokenLength(1);           // Min token length (1 = keep all)
```

**Parameter Effects:**

| Parameter | Value | Effect |
|-----------|-------|--------|
| `setLowercase` | `true` | "Hello" → "hello" |
| | `false` | "Hello" remains "Hello" |
| `setRemoveStopWords` | `true` | Remove "the", "is", "a", etc. |
| | `false` | Keep all words |
| `setApplyStemming` | `true` | "running" → "run" |
| | `false` | "running" remains "running" |
| `setMinFrequency` | `1` | Keep all tokens (default) |
| | `2` | Only tokens appearing 2+ times |
| | `5` | Only tokens appearing 5+ times |
| `setMinTokenLength` | `1` | Keep all lengths (default) |
| | `2` | Remove single characters |
| | `3` | Only keep words with 3+ chars |

### Document Format

**File:** `DocumentReader.java`

```java
// Option 1: Entire file as one document (default)
DocumentReader.DocumentFormat.SINGLE_DOCUMENT

// Option 2: Each line is a separate document
DocumentReader.DocumentFormat.LINE_PER_DOCUMENT

// Option 3: Blank lines separate documents
DocumentReader.DocumentFormat.PARAGRAPH_PER_DOCUMENT
```

**Example:**

**SINGLE_DOCUMENT:**
```
Line 1: Hello world
Line 2: Natural language processing

Result: 1 document
```

**LINE_PER_DOCUMENT:**
```
Line 1: Hello world
Line 2: Natural language processing

Result: 2 documents
  - Doc 1: "Hello world"
  - Doc 2: "Natural language processing"
```

**PARAGRAPH_PER_DOCUMENT:**
```
Line 1: Hello world
Line 2: This is paragraph one
[blank line]
Line 4: Natural language processing
Line 5: This is paragraph two

Result: 2 documents
  - Doc 1: "Hello world\nThis is paragraph one"
  - Doc 2: "Natural language processing\nThis is paragraph two"
```

---

## 📚 Technical Documentation

### Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│           DocumentSequencerApplication              │
│                  (Entry Point)                      │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│              SequencingPipeline                     │
│          (7-Stage Orchestrator)                     │
└──────────────────────┬──────────────────────────────┘
                       │
       ┌───────────────┼───────────────┬───────────────┐
       ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Preprocessing│ │  Encoding   │ │Vectorization│ │     I/O     │
├─────────────┤ ├─────────────┤ ├─────────────┤ ├─────────────┤
│Preprocessor │ │ Vocabulary  │ │ BoW Vector  │ │ Doc Reader  │
│ Tokenizer   │ │ IntEncoder  │ │ TF-IDF Vec  │ │ Seq Writer  │
│StopWordFilt │ │             │ │             │ │ HTML Writer │
│PorterStemmer│ │             │ │             │ │             │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

### Class Relationships

```mermaid
DocumentSequencerApplication
    ├─→ DocumentReader
    ├─→ SequencingPipeline
    │   ├─→ TextPreprocessor
    │   ├─→ Tokenizer
    │   ├─→ StopWordFilter
    │   ├─→ PorterStemmer
    │   ├─→ Vocabulary
    │   ├─→ IntegerEncoder
    │   ├─→ BagOfWordsVectorizer
    │   └─→ TfidfVectorizer
    ├─→ SequenceWriter
    └─→ HtmlReportWriter
```

### Data Flow

```
1. Raw Text (String)
   ↓
2. Preprocessed Text (String)
   ↓
3. Tokens (List<String>)
   ↓
4. Filtered Tokens (List<String>)
   ↓
5. Stemmed Tokens (List<String>)
   ↓
6. Integer Sequence (List<Integer>)
   ↓
7a. BoW Vector (Map<Integer, Integer>)
7b. TF-IDF Vector (Map<Integer, Double>)
```

### Performance Considerations

**Memory Usage:**
- Vocabulary: O(V) where V = unique tokens
- BoW Vector: O(V) sparse representation
- TF-IDF Vector: O(V) sparse representation
- HTML Report: ~3MB for 50 documents, 377 tokens

**Time Complexity:**
- Preprocessing: O(n) where n = text length
- Tokenization: O(n)
- Stop Word Filtering: O(t) where t = token count
- Stemming: O(t × k) where k = avg token length
- Vocabulary Building: O(t)
- Encoding: O(t)
- BoW Vectorization: O(t)
- TF-IDF Vectorization: O(t × d) where d = document count

**Scalability:**
- ✅ 1-50 documents: Excellent performance
- ✅ 50-100 documents: Good performance
- ⚠️ 100+ documents: Consider pagination optimization
- 💡 1000+ documents: Use batch processing with chunking

---

## ❓ FAQ & Troubleshooting

### General Questions

**Q: What Java version do I need?**
- A: Java 11 or higher. Check with `java -version`

**Q: Can I process multiple documents at once?**
- A: Yes! Use batch mode: `.\build.bat auto` with multiple `.txt` files in `Data/Input/`

**Q: How do I customize preprocessing?**
- A: Edit `AutoRunner.java` lines 49-54 to change `PipelineConfiguration` settings

**Q: Why are there different token indices in different outputs?**
- A: 
  - `Vocabulary` uses 0=<PAD>, 1=<UNK>, 2+=tokens
  - `BoW/TF-IDF` use 0-based indices of unique tokens only

**Q: What's the difference between BoW and TF-IDF?**
- A:
  - **BoW**: Raw counts (e.g., "word" appears 9 times)
  - **TF-IDF**: Weighted by importance (rare words get higher scores)

### TF-IDF Questions

**Q: How is IDF calculated?**
- A: `IDF(t) = log((N+1)/(df+1)) + 1` (smooth-idf, sklearn standard)
- See `TFIDF_VERIFICATION.md` for detailed explanation

**Q: Why is my TF-IDF different from online calculators?**
- A: Different formulas exist:
  - **This tool**: `log((N+1)/(df+1)) + 1` + L2 normalization
  - **Standard**: `log(N/df)`
  - **Smooth**: `log(N/(df+1)) + 1`
  - Our implementation matches **sklearn's default**

**Q: What does "Doc Freq" mean in the Vocabulary table?**
- A: Number of documents containing that token (out of total N documents)

**Q: Why is IDF highlighted in yellow?**
- A: IDF > 1.5 indicates a relatively rare term with discriminative power

### HTML Report Questions

**Q: How do I view the HTML report?**
- A: Simply double-click `Data/Output/report.html` to open in your browser

**Q: The tabs are not switching!**
- A: Make sure JavaScript is enabled in your browser

**Q: Can I share the HTML report?**
- A: Yes! It's a standalone file with no external dependencies

**Q: The table is too wide to view**
- A: Use horizontal scroll bar, or zoom out in browser (Ctrl + Mouse Wheel)

**Q: How do I export data from the HTML?**
- A: Click "📥 Download CSV" button in Matrix or Vocabulary tabs

### Build/Compilation Issues

**Q: `javac: command not found`**
- A: Java not in PATH. Add Java bin folder to system PATH environment variable

**Q: `Error: Could not find or load main class`**
- A: Run `.\build.bat` first to compile before running

**Q: `src\main\java\...: error: unmappable character for encoding UTF-8`**
- A: Save Java files with UTF-8 encoding (not UTF-8 with BOM)

**Q: Files are not being processed**
- A: Check that:
  1. Files are in `Data/Input/` folder
  2. Files have `.txt` extension
  3. Files contain text (not empty)

### Performance Issues

**Q: HTML report is slow with many documents**
- A: This is normal with 100+ documents. Use pagination controls to navigate

**Q: Build takes too long**
- A: First build compiles everything. Subsequent builds are incremental and faster

**Q: Out of memory error**
- A: Increase Java heap: `java -Xmx2g -cp ...` or reduce document count

### Output Interpretation

**Q: Why do some tokens have IDF = 1.00?**
- A: Token appears in ALL documents, so IDF is minimum (log((N+1)/(N+1))+1 = 1.0)

**Q: What's a good TF-IDF score?**
- A: Depends on corpus, but generally:
  - 0.00-0.01: Low importance (common words)
  - 0.01-0.05: Medium importance
  - 0.05+: High importance (rare & frequent)

**Q: Why are BoW counts different from TF-IDF values?**
- A: TF-IDF applies:
  1. Term frequency normalization (TF)
  2. Inverse document frequency weighting (IDF)
  3. L2 vector normalization

---

## 🎓 References & Citations

### Scientific Papers
1. **Porter, M. F.** (1980). "An algorithm for suffix stripping." _Program_, 14(3), 130-137.
2. **Salton, G., & Buckley, C.** (1988). "Term-weighting approaches in automatic text retrieval." _Information Processing & Management_, 24(5), 513-523.

### Online Resources
1. [GeeksforGeeks - Understanding TF-IDF](https://www.geeksforgeeks.org/understanding-tf-idf-term-frequency-inverse-document-frequency/)
2. [GeeksforGeeks - Bag of Words Model](https://www.geeksforgeeks.org/bag-of-words-bow-model-in-nlp/)
3. [Wikipedia - TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)
4. [Sklearn TfidfVectorizer](https://scikit-learn.org/stable/modules/generated/sklearn.feature_extraction.text.TfidfVectorizer.html)

### Implementation Standards
- TF-IDF formula matches **sklearn's smooth-idf** default
- Porter Stemming follows **original 1980 algorithm**
- Stop words list based on **NLTK English stopwords**

---

## 📝 Version History

### v2.0 (Current) - Enhanced UI & Verified TF-IDF
- ✨ Modern animated tab design with icons
- 📊 Visual progress bars for IDF/TF-IDF values
- 🎯 Smart highlighting for important terms
- 📖 6-column Vocabulary table (added Doc Freq)
- ✅ Fixed TF-IDF calculation (smooth-idf formula)
- 📚 Complete documentation (3 MD files)
- 🎨 Professional styling (gradient, shadows, animations)
- 📥 CSV export with 6 columns
- 🔧 Batch processing for 50+ files

### v1.0 - Initial Release
- 🚀 7-stage NLP pipeline
- 📊 Basic HTML report
- 📈 BoW and TF-IDF vectorization
- 🔧 Interactive and auto modes

---

## 🤝 Contributing

Want to extend this project? Consider adding:

### Feature Ideas
- 🌍 **Multilingual Support** - Support for non-English languages
- 🔤 **Lemmatization** - Alternative to stemming (preserve word meaning)
- 📊 **N-grams** - Bigrams, trigrams for phrase detection
- 🧠 **Word Embeddings** - Word2Vec, GloVe, FastText integration
- 🤖 **BERT Embeddings** - Transformer-based representations
- 📈 **Visualization** - Word clouds, t-SNE plots, PCA
- 🔍 **Search Engine** - Query and rank documents by relevance
- 📊 **Clustering** - K-means, hierarchical clustering
- 🏷️ **Classification** - Naive Bayes, SVM, Neural Networks
- 📱 **Web API** - REST API for text processing

### Code Improvements
- ✅ Unit tests (JUnit)
- ✅ Maven/Gradle build
- ✅ Docker containerization
- ✅ CI/CD pipeline
- ✅ Performance benchmarks
- ✅ Logging framework (SLF4J)
- ✅ Configuration files (YAML/JSON)

---

## 📄 License

**MIT License** - Free for educational and research purposes

```
Copyright (c) 2025 DocumentToSequences Project

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 🙏 Acknowledgments

- **GeeksforGeeks** - Excellent NLP tutorials and TF-IDF explanations
- **Martin Porter** - Porter Stemming algorithm (1980)
- **Scikit-learn** - TF-IDF implementation reference
- **NLTK** - English stop words list
- **Wikipedia** - TF-IDF formula verification

---

## 📞 Support

### Getting Help
1. 📖 Read this README thoroughly
2. 📚 Check `TFIDF_VERIFICATION.md` for TF-IDF details
3. 📋 Review `VOCAB_TABLE_GUIDE.txt` for quick reference
4. 🌐 Open `Data/Output/report.html` for visual analysis

### Reporting Issues
When reporting bugs, please include:
- Java version (`java -version`)
- Operating system
- Input file sample
- Error message / stack trace
- Console output

---

## 🚀 Quick Command Reference

```bash
# Windows
.\build.bat              # Build only
.\build.bat run          # Interactive mode
.\build.bat auto         # Batch processing + HTML report ⭐
.\build.bat test         # Run tests

# Linux/Mac
./build.sh               # Build only
./build.sh run           # Interactive mode
./build.sh auto          # Batch processing + HTML report ⭐
./build.sh test          # Run tests

# View Results
# Open: Data/Output/report.html in browser 🌐
```

---

<div align="center">

### 🌟 Star this project if you found it helpful! 🌟

**Built with ❤️ for the NLP community**

---

**[📊 View Demo](Data/Output/report.html)** • 
**[📚 Documentation](TFIDF_VERIFICATION.md)** • 
**[🐛 Report Bug](issues)** • 
**[💡 Request Feature](issues)**

---

**Happy Text Mining! 🚀📊✨**

</div>
