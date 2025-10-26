# 📊 Document-to-Sequence Converter

> **Research-Grade NLP Implementation for Data Mining & Text Vectorization**

[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Maven-red.svg)](pom.xml)
[![Status](https://img.shields.io/badge/Status-Production-brightgreen.svg)]()

---

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🚀 Quick Start](#-quick-start)
- [📖 Usage Guide](#-usage-guide)
- [🔬 Technical Details](#-technical-details)
- [📊 Output Formats](#-output-formats)
- [🧪 Example Workflow](#-example-workflow)
- [📚 Documentation](#-documentation)
- [🤝 Contributing](#-contributing)

---

## 🎯 Overview

**Document-to-Sequence Converter** is a comprehensive Java implementation of state-of-the-art Natural Language Processing (NLP) techniques for transforming unstructured text documents into numerical sequences suitable for machine learning and data mining applications.

### 🎓 Academic Foundation

This project implements methodologies documented in academic research and industry best practices:

- **Bag-of-Words (BoW)** - Frequency-based sparse vector representation
- **TF-IDF** - Statistical measure of word importance across documents
- **Text Preprocessing Pipeline** - Industry-standard cleaning and normalization
- **Porter Stemming** - Morphological word reduction
- **Integer Encoding** - Vocabulary-based sequence generation

### 🎯 Use Cases

- 📈 **Text Classification** - Categorize documents by topic or sentiment
- 🔍 **Information Retrieval** - Search engines and document ranking
- 🤖 **Machine Learning Pipelines** - Feature extraction for ML models
- 📊 **Data Mining** - Pattern discovery in text corpora
- 🧠 **Research & Education** - Learning NLP fundamentals

---

## ✨ Features

### 🔧 Text Preprocessing Pipeline

| Feature | Description | Configuration |
|---------|-------------|---------------|
| **HTML Tag Removal** | Strip HTML markup from web-scraped content | ✅ Enabled by default |
| **URL Removal** | Eliminate web addresses and links | ✅ Enabled by default |
| **Email Removal** | Remove email addresses | ✅ Enabled by default |
| **Case Normalization** | Convert to lowercase | ⚙️ Configurable |
| **Stop Word Filtering** | Remove common words (a, the, is, etc.) | ⚙️ Configurable |
| **Porter Stemming** | Reduce words to root form (running → run) | ⚙️ Configurable |
| **Punctuation Removal** | Clean non-alphanumeric characters | ✅ Enabled by default |

### 🎨 Vectorization Methods

#### 1️⃣ **Integer Encoding**
- Maps each unique word to a numerical index
- Creates vocabulary from corpus
- Generates ordered sequences of integers
- Supports minimum frequency filtering
- Handles out-of-vocabulary (OOV) words with `<UNK>` token

#### 2️⃣ **Bag-of-Words (BoW)**
- Word frequency counting per document
- Sparse vector representation
- Configurable binary mode (presence/absence)
- Efficient storage for high-dimensional vectors

#### 3️⃣ **TF-IDF Vectorization**

**Multiple TF (Term Frequency) Formulas:**
| Formula | Description | Mathematical Expression |
|---------|-------------|------------------------|
| Binary | Presence indicator | `1 if t ∈ d else 0` |
| Raw Count | Simple frequency | `f(t,d)` |
| Term Frequency | Normalized by document length | `f(t,d) / Σf(t',d)` |
| Log Normalization | Logarithmic scaling | `log(1 + f(t,d))` |
| Double Norm 0.5 | Bias prevention | `0.5 + 0.5 × f(t,d) / max{f(t',d)}` |
| Double Norm K | Parameterized normalization | `K + (1-K) × f(t,d) / max{f(t',d)}` |

**Multiple IDF (Inverse Document Frequency) Formulas:**
| Formula | Description | Mathematical Expression |
|---------|-------------|------------------------|
| Unary | No weighting | `1` |
| IDF | Standard inverse frequency | `log(N / n(t))` |
| IDF Smooth | Smoothed version | `log((N+1) / (n(t)+1)) + 1` |
| IDF Max | Maximum-based | `log(max{n(t')} / n(t))` |
| IDF Probabilistic | Probability-based | `log((N - n(t)) / n(t))` |

### 📊 Advanced Features

- **🔄 Batch Processing** - Process entire folders of documents
- **📈 HTML Reports** - Beautiful interactive visualization
- **💾 Multiple Output Formats** - Plain text, CSV, JSON-compatible
- **🎯 Smart Configuration** - Auto-discovery with manual override
- **📱 Responsive UI** - Professional HTML/CSS reporting
- **⚡ Performance Optimized** - Efficient sparse matrix operations

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    DOCUMENT INPUT LAYER                     │
│  📄 Raw Text • HTML Content • File System • Batch Input     │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│              TEXT PREPROCESSING PIPELINE                    │
│  🧹 Clean → 🔤 Normalize → ✂️ Tokenize → 🚫 Filter        │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│                LINGUISTIC PROCESSING                        │
│  📚 Stop Word Removal → 🌱 Porter Stemming                 │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│              ENCODING & VECTORIZATION                       │
│  🔢 Integer Encoding • 👜 Bag-of-Words • 📊 TF-IDF         │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│                   OUTPUT GENERATION                         │
│  💾 Sequences • 📊 Vectors • 📈 Reports • 📉 Statistics   │
└─────────────────────────────────────────────────────────────┘
```

### 📦 Package Structure

```
src/main/java/com/example/sequencer/
├── 🎯 core/                          # Main applications
│   ├── DocumentSequencerApplication  # Interactive console app
│   └── AutoRunner                    # Batch processing mode
├── 🔧 preprocessing/                 # Text cleaning
│   ├── TextPreprocessor             # HTML, URL, email removal
│   ├── Tokenizer                    # Word splitting
│   ├── StopWordFilter               # Common word removal
│   └── PorterStemmer               # Morphological analysis
├── 🔢 encoding/                      # Sequence generation
│   ├── Vocabulary                   # Word-to-index mapping
│   └── IntegerEncoder              # Sequence encoder
├── 📊 vectorization/                 # Vector representations
│   ├── BagOfWordsVectorizer        # BoW implementation
│   ├── TfidfVectorizer             # TF-IDF implementation
│   ├── TFIDFCalculator             # All formula calculator
│   ├── TFFormula                   # TF formula enums
│   └── IDFFormula                  # IDF formula enums
├── 🎨 io/                            # Input/Output handlers
│   ├── DocumentReader              # File reading
│   ├── SequenceWriter              # Results writing
│   └── HTMLReportWriter            # Report generation
├── 🏗️ pipeline/                      # Workflow orchestration
│   └── SequencingPipeline          # End-to-end processing
├── 📦 model/                         # Data models
│   ├── DocumentSequence            # Document representation
│   └── SequenceVector              # Vector representation
└── 🛠️ utils/                         # Utility functions
    └── MathUtils                   # Mathematical operations
```

---

## 🚀 Quick Start

### ⚡ Prerequisites

- ☕ **Java 8+** - [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- 📦 **Maven** (optional) - For dependency management
- 💻 **Command Line** - Terminal/PowerShell/CMD

### 🎯 Installation

#### **Option 1: Using Build Scripts (Recommended)**

**Windows:**
```powershell
# Clone or download the repository
cd Documents-To-Sequences-main

# Build the project
.\build.bat

# Run interactive mode
.\build.bat run

# Run batch mode with HTML report
.\build.bat auto
```

**Linux/Mac:**
```bash
# Clone or download the repository
cd Documents-To-Sequences-main

# Make script executable
chmod +x build.sh

# Build the project
./build.sh

# Run interactive mode
./build.sh run

# Run batch mode with HTML report
./build.sh auto
```

#### **Option 2: Using Maven**

```bash
# Build project
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="com.example.sequencer.core.DocumentSequencerApplication"

# Run auto mode
mvn exec:java -Dexec.mainClass="com.example.sequencer.core.AutoRunner"
```

#### **Option 3: Manual Compilation**

```bash
# Compile
javac -d target/classes -encoding UTF-8 src/main/java/com/example/sequencer/**/*.java

# Run
java -cp target/classes com.example.sequencer.core.DocumentSequencerApplication
```

---

## 📖 Usage Guide

### 🎮 Interactive Mode

Perfect for **single-file processing** and **experimentation**:

```bash
.\build.bat run
```

**Step-by-Step Workflow:**

1. **📁 Select Input Mode**
   ```
   1. Single input file
   2. Entire folder (all .txt files)
   ```

2. **📄 Choose Document Format**
   ```
   1. Single Document (entire file)
   2. Line per Document
   3. Paragraph per Document
   ```

3. **⚙️ Configure Pipeline**
   ```
   Press ENTER for defaults or 'custom' for manual configuration
   ```

4. **🎯 Custom Configuration Options**
   - Convert to lowercase? (y/n)
   - Remove stop words? (y/n)
   - Apply Porter stemming? (y/n)
   - Minimum word frequency (default: 1)
   - Minimum token length (default: 1)

5. **📊 View Results**
   - Sequences written to `Data/Output/output_sequences.txt`
   - BoW vectors → `Data/Output/output_bow_vectors.txt`
   - TF-IDF vectors → `Data/Output/output_tfidf_vectors.txt`
   - HTML Report → `Data/Output/output_report.html`

### 🔄 Batch Processing Mode

Ideal for **processing multiple documents** with **HTML visualization**:

```bash
.\build.bat auto
```

**Features:**
- ✅ Automatically processes all `.txt` files in `Data/Input/`
- ✅ Generates comprehensive HTML report
- ✅ Produces all output formats
- ✅ No user interaction required
- ✅ Perfect for scheduled jobs

**Output Files:**
```
Data/Output/
├── output_sequences.txt       # Integer-encoded sequences
├── output_bow_vectors.txt     # Bag-of-Words vectors
├── output_tfidf_vectors.txt   # TF-IDF with all formulas
├── output_numeric.txt         # Pure numeric sequences (ML-ready)
└── report.html                # Interactive visualization 🌟
```

### 📊 HTML Report Features

The generated HTML report includes:

#### 🎨 **Interactive Tabs**
- **📊 Frequency Matrix** - Term-document frequency table
- **📖 Vocabulary** - Word statistics and IDF scores
- **📄 Documents** - Original vs. preprocessed comparison

#### 🔍 **Features**
- ✨ Responsive design with gradient styling
- 📱 Mobile-friendly layout
- 📥 CSV download capability
- ⏭️ Pagination for large datasets
- 🎯 Visual frequency indicators (bar charts)
- 🔄 Toggle between original and preprocessed text
- 📈 Summary statistics dashboard

#### 🎯 **Statistics Display**
```
┌─────────────┬──────────────┬────────────┐
│ Documents   │ Vocabulary   │ Tokens     │
│     50      │     377      │   22,692   │
└─────────────┴──────────────┴────────────┘
```

---

## 🔬 Technical Details

### 🧮 TF-IDF Implementation

This implementation provides **30 different TF-IDF combinations** (6 TF formulas × 5 IDF formulas), allowing comprehensive analysis of text importance.

#### **Example: Standard TF-IDF Calculation**

**Input Document:** `"the cat sat on the mat"`

**Step 1: Term Frequency (TF)**
```
Term    | Count | TF (normalized)
--------|-------|----------------
the     |   2   |  2/6 = 0.333
cat     |   1   |  1/6 = 0.167
sat     |   1   |  1/6 = 0.167
on      |   1   |  1/6 = 0.167
mat     |   1   |  1/6 = 0.167
```

**Step 2: Inverse Document Frequency (IDF)**
```
Given: 10 total documents
Term "the" appears in 8 documents
Term "cat" appears in 3 documents

IDF("the") = log(10/8) = 0.097
IDF("cat") = log(10/3) = 0.522
```

**Step 3: TF-IDF Score**
```
TF-IDF("the") = 0.333 × 0.097 = 0.032 (common word)
TF-IDF("cat") = 0.167 × 0.522 = 0.087 (more important)
```

### 📊 Vectorization Comparison

| Feature | BoW | TF-IDF | Integer Encoding |
|---------|-----|--------|------------------|
| **Vector Type** | Sparse | Sparse | Sequential |
| **Dimensionality** | Vocabulary Size | Vocabulary Size | Variable Length |
| **Word Order** | ❌ Lost | ❌ Lost | ✅ Preserved |
| **Word Importance** | Equal weight | Weighted | N/A |
| **Use Case** | Classification | Information Retrieval | Neural Networks |
| **Memory** | Medium | Medium | Low |

### 🛡️ Porter Stemming Examples

```
Original    →  Stemmed
─────────────────────────
running     →  run
computers   →  comput
beautiful   →  beauti
connection  →  connect
studying    →  studi
```

**Smart Features:**
- ✅ Protects protected words (brand names, acronyms)
- ✅ Preserves known words to avoid over-stemming
- ✅ Configurable enable/disable

---

## 📊 Output Formats

### 1️⃣ **Integer Sequences** (`output_sequences.txt`)

```
Document #1: [45, 123, 67, 89, 12, 45, 90]
Document #2: [23, 56, 78, 12, 90, 34]
Document #3: [89, 90, 12, 45, 67, 123]

Vocabulary Mapping:
0: <PAD>
1: <UNK>
12: the
23: cat
34: sat
...
```

### 2️⃣ **Bag-of-Words Vectors** (`output_bow_vectors.txt`)

```
Document #1 - BoW Vector (Sparse)
  Index    Token          Count
  ─────────────────────────────
    12     the              2
    45     cat              1
    67     sat              1
    89     on               1
    90     mat              1

Vector dimension: 377
Non-zero elements: 5
Sparsity: 98.67%
```

### 3️⃣ **TF-IDF Vectors** (`output_tfidf_vectors.txt`)

```
═══════════════════════════════════════════════════════════
Document #1 - TF-IDF Analysis
═══════════════════════════════════════════════════════════

╔════════════════════════════════════════════════════════╗
║           TERM FREQUENCY (TF) FORMULAS                 ║
╚════════════════════════════════════════════════════════╝

Formula: TERM_FREQUENCY = f(t,d) / Σf(t',d)

  Token          Count    TF Value
  ───────────────────────────────
  the              2      0.333
  cat              1      0.167
  sat              1      0.167
  ...

╔════════════════════════════════════════════════════════╗
║     INVERSE DOCUMENT FREQUENCY (IDF) FORMULAS          ║
╚════════════════════════════════════════════════════════╝

Formula: IDF_SMOOTH = log((N+1) / (n(t)+1)) + 1

  Token      Doc Freq    IDF Value
  ──────────────────────────────
  the           45       1.105
  cat           12       2.234
  sat            8       2.526
  ...

╔════════════════════════════════════════════════════════╗
║              TF-IDF FINAL SCORES                       ║
╚════════════════════════════════════════════════════════╝

  Token          TF-IDF Score
  ────────────────────────────
  cat              0.373
  sat              0.422
  the              0.368
  ...
```

### 4️⃣ **Numeric Sequences** (`output_numeric.txt`)

**ML-Ready Format** - Direct input for neural networks:

```
45 123 67 89 12 45 90
23 56 78 12 90 34
89 90 12 45 67 123
```

### 5️⃣ **HTML Report** (`report.html`)

Interactive web-based visualization - **open in any browser!**

---

## 🧪 Example Workflow

### 📝 Complete Processing Example

**Input Files:**
```
Data/Input/
├── review1.txt  "The product quality is excellent!"
├── review2.txt  "Poor quality, not recommended."
└── review3.txt  "Excellent product, highly recommended!"
```

**Execution:**
```bash
.\build.bat auto
```

**Pipeline Processing:**

```
[1/7] Text Preprocessing...
  ✓ HTML tags removed
  ✓ Lowercase conversion
  ✓ Punctuation cleaned

[2/7] Tokenization...
  ✓ Generated 18 tokens

[3/7] Stop Word Filtering...
  ✓ Retained 12 tokens

[4/7] Stemming...
  ✓ Applied Porter Stemmer
  • quality → qualiti
  • excellent → excel
  • recommended → recommend

[5/7] Vocabulary Construction...
  ✓ Vocabulary size: 7 unique terms

[6/7] Bag-of-Words Vectorization...
  ✓ Generated BoW vectors

[7/7] TF-IDF Vectorization...
  ✓ Generated TF-IDF vectors
```

**Vocabulary Built:**
```
Index | Token      | Document Frequency
─────────────────────────────────────
  0   | product    | 2
  1   | qualiti    | 2
  2   | excel      | 2
  3   | poor       | 1
  4   | recommend  | 2
  5   | high       | 1
```

**Integer Sequences:**
```
Document 1: [0, 1, 2]           # product quality excellent
Document 2: [3, 1, 4]           # poor quality recommend
Document 3: [2, 0, 5, 4]        # excellent product highly recommend
```

**TF-IDF Insights:**
- ⭐ **"poor"** has highest TF-IDF in Doc 2 (unique discriminator)
- ⭐ **"excel"** is important but appears in 2 docs (moderate weight)
- ⭐ **"product"** common across docs (lower weight)

---

## 📚 Documentation

### 📖 Additional Resources

| Document | Description |
|----------|-------------|
| [`TFIDF-VERIFICATION.md`](TFIDF-VERIFICATION.md) | 🔬 Complete TF-IDF formula verification guide |
| [`Document-to-Sequence Conversion in Data Mining.txt`](Document-to-Sequence%20Conversion%20in%20Data%20Mining.txt) | 📚 Comprehensive research documentation |
| [Source Code JavaDoc](src/main/java/) | 💻 Inline API documentation |

### 🎓 Academic References

This implementation is based on established NLP literature:

1. **Porter Stemmer Algorithm** - Porter, M.F. (1980)
2. **TF-IDF** - Salton, G. & McGill, M. (1983)
3. **Bag-of-Words Model** - Harris, Z. (1954)
4. **Text Preprocessing** - Manning & Schütze (1999)

### 🔗 External References

- [TF-IDF Wikipedia](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)
- [Porter Stemming Algorithm](https://tartarus.org/martin/PorterStemmer/)
- [Text Vectorization Guide](https://scikit-learn.org/stable/modules/feature_extraction.html)

---

## 🎯 Configuration Reference

### Pipeline Configuration Options

```java
PipelineConfiguration config = new PipelineConfiguration()
    .setLowercase(true)              // Convert to lowercase
    .setRemoveStopWords(true)        // Filter common words
    .setApplyStemming(true)          // Apply Porter stemmer
    .setMinFrequency(1)              // Minimum word frequency
    .setMinTokenLength(1);           // Minimum token length
```

### Document Format Options

| Format | Description | Use Case |
|--------|-------------|----------|
| `SINGLE_DOCUMENT` | Entire file as one document | Books, articles |
| `LINE_PER_DOCUMENT` | Each line is a document | Reviews, tweets |
| `PARAGRAPH_PER_DOCUMENT` | Each paragraph is a document | Essays, reports |

---

## 🛠️ Troubleshooting

### Common Issues

**❌ Compilation Error: `javac: command not found`**
```bash
# Solution: Install JDK 8+ and add to PATH
# Windows: Set JAVA_HOME environment variable
# Linux/Mac: export JAVA_HOME=/path/to/jdk
```

**❌ OutOfMemoryError for large datasets**
```bash
# Solution: Increase heap size
java -Xmx4g -cp target/classes com.example.sequencer.core.AutoRunner
```

**❌ UnsupportedEncodingException**
```bash
# Solution: Ensure UTF-8 encoding
javac -encoding UTF-8 ...
```

**❌ Empty output files**
- ✅ Check input file encoding (must be UTF-8)
- ✅ Verify input folder path
- ✅ Ensure input files are not empty
- ✅ Check file permissions

---

## 🚀 Performance Tips

### ⚡ Optimization Strategies

1. **Batch Processing**: Use `AutoRunner` for multiple files
2. **Memory Management**: Adjust JVM heap size for large corpora
3. **Preprocessing**: Disable unnecessary steps for speed
4. **Vocabulary Size**: Use `minFrequency` to reduce dimensionality
5. **Stop Words**: Remove them early to reduce processing time

### 📊 Benchmarks

| Corpus Size | Documents | Vocab | Processing Time |
|-------------|-----------|-------|----------------|
| Small | 10 | 500 | < 1s |
| Medium | 100 | 5,000 | 2-5s |
| Large | 1,000 | 50,000 | 10-30s |
| Very Large | 10,000 | 100,000 | 1-3min |

*Tested on Intel i7, 16GB RAM*

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### 🐛 Bug Reports
- Use GitHub Issues
- Include Java version, OS, and error logs
- Provide minimal reproducible example

### ✨ Feature Requests
- Describe use case and benefits
- Check existing issues first
- Consider implementation complexity

### 💻 Code Contributions
1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Follow existing code style
4. Add JavaDoc comments
5. Test thoroughly
6. Submit pull request

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

**Research Implementation Team**
- Comprehensive NLP Pipeline Design
- TF-IDF Multi-Formula Implementation
- HTML Report Visualization

---

## 🙏 Acknowledgments

- Porter Stemmer algorithm by Martin Porter
- Stop words list from NLTK project
- TF-IDF formulas from Wikipedia
- Inspired by scikit-learn's text vectorization

---

## 📞 Support

- 📧 **Email**: Open an issue on GitHub
- 💬 **Discussions**: GitHub Discussions
- 📖 **Documentation**: See `docs/` folder
- 🐛 **Bug Reports**: GitHub Issues

---

## 🎓 Citation

If you use this software in your research, please cite:

```bibtex
@software{document_to_sequence_converter,
  title = {Document-to-Sequence Converter for Data Mining},
  author = {Research Implementation Team},
  year = {2025},
  version = {1.0},
  url = {https://github.com/yourusername/Documents-To-Sequences}
}
```

---

<div align="center">

### ⭐ Star this repository if you find it helpful!

**Made with ❤️ for the NLP & Data Mining Community**

[⬆ Back to Top](#-document-to-sequence-converter)

</div>
