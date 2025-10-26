# Document-to-Sequence Conversion Suite

> Transform raw narratives into beautiful, data-rich sequences for analytics, search, and modeling. Built in Java, designed for humans.

---
[![Java](https://img.shields.io/badge/Java-11%2B-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Build](https://img.shields.io/badge/Build-Passing-success?style=for-the-badge)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Production-green?style=for-the-badge)](https://github.com)
---

## Highlights At A Glance
- Works out of the box with Java 11+ and zero external dependencies
- Presents insights through a polished, single-page HTML experience
- Delivers BoW, TF-IDF, and integer sequence outputs in one run
- Aligns TF-IDF math with `sklearn` (smooth IDF + L2 normalization)
- Handles single documents or whole folders with the same command

---

## Table Of Contents
1. [Product Story](#product-story)
2. [Experience Design](#experience-design)
3. [System Architecture](#system-architecture)
4. [Data Pipeline](#data-pipeline)
5. [Outputs & File Formats](#outputs--file-formats)
6. [Quick Start Guide](#quick-start-guide)
7. [Configuration Playbook](#configuration-playbook)
8. [Operations & Automation](#operations--automation)
9. [Quality & Validation](#quality--validation)
10. [Troubleshooting](#troubleshooting)
11. [Roadmap & Ideas](#roadmap--ideas)
12. [Credits & License](#credits--license)

---

## Product Story
The Document-to-Sequence Conversion Suite was created for analysts who need explainable text features, teams who value delightful UX, and engineers who demand traceability from byte to vector. The project combines a seven-stage natural language pipeline with a report that stakeholders can understand in seconds.

### Value Pillars
- **Trustworthy math** – TF-IDF implementation mirrors industry standards and documents the exact calculus in `TFIDF_VERIFICATION.md`.
- **Explainable UX** – Every transformation is observable. You can read the original sentence, inspect the stemmed tokens, and watch IDF bars glow as rarity increases.
- **Operational clarity** – Batch scripts, folder conventions, and logging make the workflow predictable for Windows and Unix operators alike.

---

## Experience Design
The HTML dashboard is crafted as a mini analytics app with emphasis on clarity, motion, and accessibility.

### Navigation & Layout
- **Tri-tab navigation** with animated icons (`📊 Matrix`, `📖 Vocabulary`, `📄 Documents`).
- **Sticky statistic panel** summarizing document count, vocabulary size, and token totals.
- **Peek-friendly typography** using Segoe UI for headings, Menlo for metrics.

### Visual Language
- **Color Palette**: twilight gradient (#667eea → #764ba2) for headers, neutral charcoal for content, accent blues for active states.
- **Micro-interactions**: scale-up animations on active tabs, smooth easing on hover transitions, and drop shadows that communicate depth without noise.
- **Information density control**: pagination (10 docs/page, 50 tokens/page) keeps tables fast, while CSV export exposes full detail for analysts.

### Accessibility Touchpoints
- Focus rings on interactive elements.
- Tooltips for icon-only buttons.
- Color choices checked for ≥4.5 contrast ratio, ensuring readability on projectors.

---

## System Architecture

```
 ┌─────────────────────────────┐
 │ DocumentSequencerApplication│  primary entry point
 └──────────────┬──────────────┘
                │ orchestrates pipeline + reporting
 ┌──────────────▼──────────────┐
 │      SequencingPipeline     │ 7 deterministic stages
 └──────┬────────────┬─────────┘
        │            │
  ┌─────▼───┐   ┌────▼─────┐   additional services
  │preprocess│   │encoding │   ────────────────
  │normalize │   │vocabulary│  DocumentReader (input)
  │tokenize  │   │tf-idf    │  SequenceWriter (text outputs)
  │filter    │   │bow       │  HtmlReportWriter (UI)
  │stem      │   │vectors   │
  └──────────┘   └──────────┘
```

The source tree under `src/main/java/com/example/sequencer/` mirrors these responsibilities. Each package has a narrow focus and exposes simple APIs, making it easy to swap components or enrich the flow.

---

## Data Pipeline
Seven stages convert raw text into structured representations. Each stage logs its progress and feeds metrics to the dashboard.

| Stage | Class | Purpose | Notes |
|-------|-------|---------|-------|
| 1. Normalize | `TextPreprocessor` | Cleans whitespace, punctuation, HTML, URLs | Optional lowercase toggle |
| 2. Tokenize | `Tokenizer` | Breaks text into tokens respecting minimum length | Regex tuned for natural text |
| 3. Filter | `StopWordFilter` | Removes 127 English stop words | Easy to extend list |
| 4. Stem | `PorterStemmer` | Reduces words to stems | Classic Porter 1980 algorithm |
| 5. Build Vocab | `Vocabulary` | Maps tokens to indices, registers `<PAD>` + `<UNK>` | Supports frequency thresholds |
| 6. Encode | `IntegerEncoder` | Creates integer sequences for each document | Unknown tokens gracefully fallback |
| 7. Vectorize | `BagOfWordsVectorizer`, `TfidfVectorizer` | Produces BoW counts and TF-IDF weights | TF-IDF L2-normalized, smooth IDF |

**TF-IDF Definition**

```
tf(t, d) = occurrences of term t in document d ÷ total tokens in document d
idf(t) = log((N + 1) / (df(t) + 1)) + 1
tfidf(t, d) = tf(t, d) × idf(t)
vector_norm = √Σ tfidf²
normalized_score = tfidf / vector_norm
```

This matches `sklearn.feature_extraction.text.TfidfVectorizer` with `smooth_idf=True`, ensuring consistent results across ecosystems.

---

## Outputs & File Formats

| File | Location | Description | Sample Use |
|------|----------|-------------|------------|
| `Data/Output/report.html` | Browser-ready dashboard | Interactive exploration of metrics, vocabulary, and documents | Share with stakeholders |
| `output_sequences.txt` | Token + integer mapping per document | Shows original tokens beside encoded IDs with metadata | Debug feature engineering |
| `output_numeric.txt` | Bare integer sequences | Minimal format for neural network inputs | Feed sequence models |
| `output_bow_vectors.txt` | Sparse BoW vectors with feature summary | Counts how often each token appears | Classical classification |
| `output_tfidf_vectors.txt` | Sparse TF-IDF vectors | Weighted representation with normalization | Retrieval or clustering |

CSV export buttons in the dashboard mirror the data found in the text files, eliminating guesswork when aligning visuals with downstream processing.

---

## Quick Start Guide

### 1. Install Requirements
- Install Java 11 or later (`java -version` should show 11+).
- Place input `.txt` documents under `Data/Input/`.

### 2. Choose Your Mode
| Command | Platform | Behavior |
|---------|----------|----------|
| `./build.sh auto` | Linux / macOS | Build + process every `.txt` in `Data/Input/` |
| `./build.sh run` | Linux / macOS | Build + prompt for a single file |
| `.uild.bat auto` | Windows | Same as above with PowerShell or CMD |
| `.uild.bat run` | Windows | Interactive guided run |
| `build.(bat|sh)` | Cross-platform | Compile only |

### 3. Inspect Results
- Review console logs for pipeline metrics.
- Open `Data/Output/report.html` and explore the tabs.
- Check `output_tfidf_vectors.txt` for downstream integration.

---

## Configuration Playbook

### PipelineConfiguration Options
Located in `src/main/java/com/example/sequencer/core/AutoRunner.java`.

```java
PipelineConfiguration config = new PipelineConfiguration()
    .setLowercase(true)
    .setRemoveStopWords(true)
    .setApplyStemming(true)
    .setMinFrequency(1)
    .setMinTokenLength(1);
```

#### Tuning Guide
- `setLowercase(false)` if case sensitivity matters (e.g., named entities).
- Increase `setMinFrequency` to suppress noise or domain-specific typos.
- Raise `setMinTokenLength` to exclude short tokens such as IDs or stopwords in uppercase data.

### Document Segmentation Styles
Configure in `DocumentReader`:
- `SINGLE_DOCUMENT` – Entire file becomes one document (default).
- `LINE_PER_DOCUMENT` – Treat each line as an individual document.
- `PARAGRAPH_PER_DOCUMENT` – Split on blank lines for paragraph-level analysis.

---

## Operations & Automation

### Batch Pipeline (Auto Mode)
1. Scan `Data/Input/` and list every `.txt` file.
2. Process documents sequentially while streaming metrics.
3. Aggregate vocabulary across the whole batch.
4. Generate outputs and refresh the HTML dashboard.

### Logging & Observability
- Each stage prints `[Step x/7]` style logs with token counts.
- Errors bubble with actionable messages (missing file, empty corpus, etc.).
- Build scripts exit with non-zero status on failure, enabling CI integration.

### Integration Hooks
- Add the build script to a scheduled task or cron job.
- Parse output vectors directly with Python/R via sparse format.
- Host the HTML report on an internal portal for asynchronous review.

---

## Quality & Validation

### Mathematical Guarantees
- Smooth IDF prevents divide-by-zero and stabilizes rare-term weights.
- L2 normalization keeps vector lengths consistent, improving similarity comparisons.
- Supporting document `TFIDF_VERIFICATION.md` reproduces calculations step-by-step.

### User Interface QA Checklist
- Test pagination boundaries (first/last page) to ensure controls disable correctly.
- Toggle between Original and Preprocessed views to verify token consistency.
- Confirm CSV exports contain headers `ID,Token,DocFreq,AvgTF,IDF,AvgTF-IDF`.

### Performance Notes
- 50 medium-length documents process in seconds on modern hardware.
- HTML report size remains under ~3 MB for 400 tokens × 50 documents.
- For corpora above several hundred documents, consider chunking inputs or adapting the UI pagination size.

---

## Troubleshooting

| Symptom | Likely Cause | Resolution |
|---------|--------------|------------|
| `javac` not recognized | Java not on PATH | Add Java `bin/` directory to PATH or install JDK |
| Empty outputs | No `.txt` files discovered | Verify file names, extensions, and directory placement |
| HTML tabs do not switch | Browser JavaScript disabled | Enable scripts or try a different browser |
| TF-IDF scores all near zero | Extreme min frequency or aggressive preprocessing | Reduce `minFrequency`, allow lowercase, or turn off stemming |
| Non-English corpus looks odd | Stemming tuned for English | Disable stemming, adjust stop words, or extend pipeline |

If issues persist, include Java version, OS information, input samples, and console logs when opening a ticket.

---

## Roadmap & Ideas
These initiatives guide future iterations and welcome contributions:
1. **Language Packs** – Expand stop-word lists and stemming strategies for additional languages.
2. **Vector Visuals** – Embed t-SNE or PCA previews directly in the dashboard.
3. **RESTful API** – Wrap the pipeline for microservice deployments.
4. **Interactive Filtering** – Allow users to adjust frequency thresholds from the HTML report.
5. **Model Starters** – Ship notebooks demonstrating classification and clustering atop the generated vectors.

Pull requests with tests, documentation, and screenshots (if UI related) are highly appreciated.

---

## Credits & License
- Project maintained by the DocumentToSequences community.
- Built on top of the Porter Stemmer (1980) and classic IR research by Salton & Buckley.
- UI craft inspired by modern data-ops dashboards and UX best practices.

Licensed under the MIT License – see `LICENSE` for the legal text.

---


Enjoy the blend of robust NLP engineering and thoughtful user experience! 
