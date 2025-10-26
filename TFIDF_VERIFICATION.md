# TF-IDF Calculation Verification

## Công thức chuẩn (theo GeeksforGeeks & Wikipedia)

### Term Frequency (TF)
```
TF(t, d) = (Số lần term t xuất hiện trong doc d) / (Tổng số terms trong doc d)
```

### Inverse Document Frequency (IDF)
**Smooth IDF (sklearn default)**:
```
IDF(t) = log((N + 1) / (df(t) + 1)) + 1
```
Trong đó:
- N = Tổng số documents
- df(t) = Document frequency (số documents chứa term t)
- +1 smoothing để tránh division by zero và log(0)

**Standard IDF**:
```
IDF(t) = log(N / df(t))
```

### TF-IDF
```
TF-IDF(t, d) = TF(t, d) × IDF(t)
```

## Implementation trong project

### File: `TfidfVectorizer.java`
**Line 67-71** (ĐÃ SỬA):
```java
// IDF formula: log((N + 1) / (df + 1)) + 1 for smooth-idf (sklearn default)
double idf = Math.log((double) (totalDocuments + 1) / (docFreq + 1)) + 1.0;
```

**✅ ĐÚNG** - Sử dụng smooth-idf như sklearn

### File: `HTMLReportWriter.java` 
**getData() method** (ĐÃ SỬA):

1. **Document Frequency (df)**:
```java
int docFreq = 0;
for (DocumentSequence seq : seqs) {
    int count = 0;
    for (String t : seq.getTokens()) {
        if (vocab.getIndex(t) == i) count++;
    }
    if (count > 0) docFreq++;  // Đếm số docs chứa token
}
```

2. **Average TF**:
```java
double totalTF = 0;
for (DocumentSequence seq : seqs) {
    int count = (số lần token xuất hiện trong seq);
    if (count > 0) {
        totalTF += (double) count / seq.getTokens().size();
    }
}
double avgTF = totalTF / totalDocs;
```

3. **IDF Calculation**:
```java
double idf = Math.log((double) (totalDocs + 1) / (docFreq + 1)) + 1.0;
```

4. **Average TF-IDF**:
```java
// Lấy từ normalized TF-IDF vectors đã tính
double avgTFIDF = 0;
for (SequenceVector vec : result.getTfidfVectors()) {
    avgTFIDF += vec.getSparseVector().getOrDefault(i, 0.0);
}
avgTFIDF /= totalDocs;
```

## Ví dụ tính toán

Giả sử:
- **50 documents** (N = 50)
- Token "cleaners" xuất hiện trong **35 documents** (df = 35)
- Trong 1 doc có 100 tokens, "cleaners" xuất hiện 3 lần

### Tính TF
```
TF = 3 / 100 = 0.03
```

### Tính IDF (smooth-idf)
```
IDF = log((50 + 1) / (35 + 1)) + 1
    = log(51 / 36) + 1
    = log(1.4167) + 1
    = 0.3507 + 1
    = 1.3507
```

### Tính TF-IDF
```
TF-IDF = 0.03 × 1.3507 = 0.0405
```

Sau đó **L2 normalization** trong `TfidfVectorizer`:
```java
norm = sqrt(sum(tfidf_i^2))
normalized_tfidf = tfidf / norm
```

## Cải thiện UI trong HTML Report

### Bảng Vocabulary hiện có 6 cột:
1. **ID** - Index của token trong vocabulary
2. **Token** - Token text (styled với monospace font)
3. **Doc Freq** - Số documents chứa token này
4. **Avg TF** - Term Frequency trung bình across all docs
5. **IDF** - Inverse Document Frequency (highlight nếu > 1.5)
6. **Avg TF-IDF** - TF-IDF trung bình (highlight nếu > 0.01)

### Visual Features:
- ✅ **Progress bars** cho IDF và TF-IDF values
- ✅ **Highlighting** cho high-importance terms
- ✅ **Monospace font** cho tokens
- ✅ **Hover effects** trên table rows
- ✅ **Gradient headers** với sticky positioning
- ✅ **Responsive pagination** với disabled states
- ✅ **CSV export** với đầy đủ 6 columns

### CSS Classes:
- `.tok` - Token styling (monospace, background)
- `.num` - Number formatting
- `.hi` - Highlight important values
- `.vbar` - Visual bar chart inline
- `.pg` - Pagination indicator

## References
1. [GeeksforGeeks - Understanding TF-IDF](https://www.geeksforgeeks.org/understanding-tf-idf-term-frequency-inverse-document-frequency/)
2. [Wikipedia - TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)
3. [Sklearn TfidfVectorizer](https://scikit-learn.org/stable/modules/generated/sklearn.feature_extraction.text.TfidfVectorizer.html)
