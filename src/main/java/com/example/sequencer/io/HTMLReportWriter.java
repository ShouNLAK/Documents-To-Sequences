package com.example.sequencer.io;

import com.example.sequencer.model.DocumentSequence;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineResult;
import com.example.sequencer.encoding.Vocabulary;
import com.example.sequencer.vectorization.TFIDFCalculator;
import com.example.sequencer.vectorization.TFFormula;
import com.example.sequencer.vectorization.IDFFormula;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HTMLReportWriter {
    
    private final String outputPath;
    private final PipelineResult result;
    private final List<String> originalDocs;
    
    public HTMLReportWriter(String outputPath, PipelineResult result, List<String> originalDocs) {
        this.outputPath = outputPath;
        this.result = result;
        this.originalDocs = originalDocs;
    }
    
    public void write() throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Report</title>");
        html.append("<style>").append(getCSS()).append("</style></head><body>");
        html.append("<div class='c'>");
        html.append("<h1>Document-to-Sequence Report</h1>");
        html.append(getStats());
        html.append("<div class='tabs'>");
        html.append("<button class='tab active' onclick='openTab(\"m\")'>");
        html.append("<span class='tab-icon'>📊</span>");
        html.append("<span class='tab-label'>Frequency Matrix</span>");
        html.append("</button>");
        html.append("<button class='tab' onclick='openTab(\"v\")'>");
        html.append("<span class='tab-icon'>📖</span>");
        html.append("<span class='tab-label'>Vocabulary</span>");
        html.append("</button>");
        html.append("<button class='tab' onclick='openTab(\"d\")'>");
        html.append("<span class='tab-icon'>📄</span>");
        html.append("<span class='tab-label'>Documents</span>");
        html.append("</button>");
        html.append("</div>");
        html.append("<div id='m' class='tc active'>");
        html.append("<h2>Frequency Matrix</h2>");
        html.append("<div class='ctrl'>");
        html.append("<button onclick='downloadCSV(\"m\")'>📥 Download CSV</button>");
        html.append("<button onclick='prevPage(\"m\")'>⬅️ Prev</button>");
        html.append("<span class='pg' id='mp'>Page 1</span>");
        html.append("<button onclick='nextPage(\"m\")'>Next ➡️</button>");
        html.append("<span style='margin-left:20px;color:#6c757d;'>Rows per page:</span>");
        html.append("<select id='mPageSize' onchange='chgPageSize(\"m\")' style='padding:8px;border:2px solid #667eea;border-radius:6px;margin-left:8px;'>");
        html.append("<option value='10' selected>10</option>");
        html.append("<option value='25'>25</option>");
        html.append("<option value='50'>50</option>");
        html.append("<option value='100'>100</option>");
        html.append("</select>");
        html.append("</div>");
        html.append("<div id='mt'></div></div>");
        html.append("<div id='v' class='tc'>");
        html.append("<h2>Vocabulary Statistics</h2>");
        html.append("<div class='ctrl'>");
        html.append("<button onclick='downloadCSV(\"v\")'>📥 Download CSV</button>");
        html.append("<button onclick='prevPage(\"v\")'>⬅️ Prev</button>");
        html.append("<span class='pg' id='vp'>Page 1</span>");
        html.append("<button onclick='nextPage(\"v\")'>Next ➡️</button>");
        html.append("<span style='margin-left:20px;color:#6c757d;'>Rows per page:</span>");
        html.append("<select id='vPageSize' onchange='chgPageSize(\"v\")' style='padding:8px;border:2px solid #667eea;border-radius:6px;margin-left:8px;'>");
        html.append("<option value='25'>25</option>");
        html.append("<option value='50' selected>50</option>");
        html.append("<option value='100'>100</option>");
        html.append("<option value='200'>200</option>");
        html.append("</select>");
        html.append("</div>");
        html.append("<div id='vt'></div></div>");
        html.append("<div id='d' class='tc'>").append(getDocCards()).append("</div>");
        html.append("</div>");
        html.append("<script>").append(getData()).append(getJS()).append("</script>");
        html.append("</body></html>");
        Files.write(Paths.get(outputPath), html.toString().getBytes("UTF-8"));
    }
    
    private String getCSS() {
        return "*{margin:0;padding:0;box-sizing:border-box}" +
               "body{font-family:'Segoe UI',Arial,sans-serif;background:linear-gradient(135deg,#667eea,#764ba2);padding:20px}" +
               ".c{max-width:1400px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 10px 40px rgba(0,0,0,.2)}" +
               "h1{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;padding:30px;text-align:center;margin:0;font-size:2em}" +
               ".st{display:flex;justify-content:space-around;padding:20px;background:#f8f9fa}" +
               ".stat{text-align:center}.stat .l{color:#6c757d;font-size:.9em;margin-bottom:8px}.stat .val{font-size:2.2em;font-weight:bold;color:#667eea}" +
               ".tabs{display:flex;gap:15px;background:#f8f9fa;padding:20px 20px 0 20px;position:relative}" +
               ".tabs::after{content:'';position:absolute;bottom:0;left:20px;right:20px;height:2px;background:#e0e0e0}" +
               ".tab{position:relative;flex:1;padding:15px 20px;background:transparent;border:none;cursor:pointer;font-size:1em;transition:all .3s ease;font-weight:500;border-radius:8px 8px 0 0;display:flex;flex-direction:column;align-items:center;gap:5px;color:#6c757d}" +
               ".tab-icon{font-size:1.8em;transition:transform .3s ease}" +
               ".tab-label{font-size:.95em;letter-spacing:.3px}" +
               ".tab:hover{background:rgba(102,126,234,.05);color:#667eea}" +
               ".tab:hover .tab-icon{transform:scale(1.1)}" +
               ".tab.active{background:#fff;color:#667eea;box-shadow:0 -2px 10px rgba(0,0,0,.08)}" +
               ".tab.active::after{content:'';position:absolute;bottom:0;left:0;right:0;height:3px;background:linear-gradient(90deg,#667eea,#764ba2);border-radius:3px 3px 0 0}" +
               ".tab.active .tab-icon{transform:scale(1.15)}" +
               ".tc{display:none;padding:30px;animation:fadeIn .4s ease}" +
               ".tc.active{display:block}" +
               "@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}" +
               "h2{color:#667eea;margin-bottom:20px;border-bottom:2px solid #e9ecef;padding-bottom:10px;font-size:1.5em}" +
               ".ctrl{margin:20px 0;display:flex;gap:10px;flex-wrap:wrap;align-items:center}" +
               "button{background:#667eea;color:#fff;padding:10px 20px;border:none;border-radius:6px;cursor:pointer;transition:.3s;font-weight:500}" +
               "button:hover{background:#764ba2;transform:translateY(-2px);box-shadow:0 4px 8px rgba(0,0,0,.2)}" +
               "button:disabled{opacity:.5;cursor:not-allowed;transform:none}" +
               ".pg{color:#667eea;font-weight:bold;padding:10px 15px;background:#f8f9fa;border-radius:6px}" +
               "table{border-collapse:collapse;width:100%;font-size:.9em;background:#fff;box-shadow:0 2px 8px rgba(0,0,0,.1)}" +
               ".tbl-wrap{overflow-x:auto;overflow-y:auto;max-height:600px;border-radius:8px;border:1px solid #dee2e6;position:relative}" +
               ".tbl-wrap::-webkit-scrollbar{width:12px;height:12px}" +
               ".tbl-wrap::-webkit-scrollbar-track{background:#f1f1f1;border-radius:6px}" +
               ".tbl-wrap::-webkit-scrollbar-thumb{background:#667eea;border-radius:6px}" +
               ".tbl-wrap::-webkit-scrollbar-thumb:hover{background:#764ba2}" +
               "th,td{border:1px solid#dee2e6;padding:12px 8px;text-align:center}" +
               "thead th{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;position:sticky;top:0;z-index:10;font-weight:600;text-transform:uppercase;font-size:.85em;letter-spacing:.5px}" +
               "tbody th{background:#764ba2;color:#fff;font-weight:600}" +
               "tbody td{transition:.2s}" +
               "tbody tr:hover{background:#e7f3ff}" +
               "tr:nth-child(even){background:#f8f9fa}" +
               ".f0{color:#ccc}.f1{background:#d1ecf1;font-weight:500}.f2{background:#fff3cd;font-weight:600}.f3{background:#f8d7da;font-weight:700}" +
               ".vbar{display:inline-block;background:linear-gradient(90deg,#667eea,#764ba2);height:8px;border-radius:4px;margin-left:8px;vertical-align:middle}" +
               ".tok{font-family:'Courier New',monospace;font-weight:600;color:#2c3e50;background:#f0f0f0;padding:4px 8px;border-radius:4px}" +
               ".num{font-family:'Courier New',monospace;font-size:.95em}" +
               ".hi{background:#fffbea;font-weight:600}" +
               ".dc{background:#f8f9fa;border-left:4px solid#667eea;padding:20px;margin-bottom:20px;border-radius:8px}" +
               ".dc h3{color:#667eea;margin-bottom:15px}" +
               ".dt{margin-bottom:15px}" +
               ".dtc{background:#fff;padding:15px;border-radius:4px;font-family:monospace;white-space:pre-wrap;max-height:300px;overflow-y:auto;font-size:.9em;border:1px solid #dee2e6}" +
               ".formula-sel{background:#f8f9fa;padding:20px;border-radius:8px;margin-bottom:20px;display:flex;gap:20px;flex-wrap:wrap}" +
               ".form-group{flex:1;min-width:300px}" +
               ".form-group label{display:block;font-weight:600;color:#667eea;margin-bottom:8px;font-size:.95em}" +
               ".form-group select{width:100%;padding:10px;border:2px solid #667eea;border-radius:6px;font-size:.95em;background:#fff;cursor:pointer;transition:.3s}" +
               ".form-group select:hover{border-color:#764ba2;box-shadow:0 2px 8px rgba(102,126,234,.2)}" +
               ".form-group select:focus{outline:none;border-color:#764ba2;box-shadow:0 0 0 3px rgba(102,126,234,.1)}" +
               ".formula-display{margin-top:10px;padding:12px;background:#fff;border-left:4px solid #667eea;border-radius:4px;font-family:'Courier New',monospace;color:#2c3e50;font-size:.9em;min-height:40px;display:flex;align-items:center}" +
               ".search-box{margin:15px 0;display:flex;gap:10px;align-items:center}" +
               ".search-box input{flex:1;padding:10px 15px;border:2px solid #667eea;border-radius:6px;font-size:.95em;transition:.3s}" +
               ".search-box input:focus{outline:none;border-color:#764ba2;box-shadow:0 0 0 3px rgba(102,126,234,.1)}" +
               ".search-box button{min-width:100px}" +
               "thead th.sortable{cursor:pointer;user-select:none;position:relative;padding-right:25px}" +
               "thead th.sortable:hover{background:linear-gradient(135deg,#764ba2,#667eea)}" +
               "thead th.sortable::after{content:'⇅';position:absolute;right:8px;opacity:0.3;font-size:0.9em}" +
               "thead th.sortable.asc::after{content:'↑';opacity:1;color:#ffc107}" +
               "thead th.sortable.desc::after{content:'↓';opacity:1;color:#ffc107}" +
               "tbody tr{cursor:pointer}" +
               "tbody tr.selected{background:#fff3cd!important;border:2px solid #ffc107}" +
               ".modal{display:none;position:fixed;z-index:1000;left:0;top:0;width:100%;height:100%;background:rgba(0,0,0,.5);animation:fadeIn .3s}" +
               ".modal-content{background:#fff;margin:5% auto;padding:0;width:90%;max-width:800px;border-radius:12px;box-shadow:0 10px 40px rgba(0,0,0,.3);animation:slideDown .3s}" +
               "@keyframes slideDown{from{transform:translateY(-50px);opacity:0}to{transform:translateY(0);opacity:1}}" +
               ".modal-header{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;padding:20px 30px;border-radius:12px 12px 0 0;display:flex;justify-content:space-between;align-items:center}" +
               ".modal-header h3{margin:0;font-size:1.5em}" +
               ".close{color:#fff;font-size:2em;font-weight:bold;cursor:pointer;transition:.3s;line-height:1}" +
               ".close:hover{color:#ffc107;transform:scale(1.2)}" +
               ".modal-body{padding:30px;max-height:70vh;overflow-y:auto}" +
               ".detail-section{margin-bottom:25px;padding:20px;background:#f8f9fa;border-radius:8px;border-left:4px solid #667eea}" +
               ".detail-section h4{color:#667eea;margin-bottom:15px;font-size:1.2em}" +
               ".detail-row{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid #dee2e6}" +
               ".detail-row:last-child{border-bottom:none}" +
               ".detail-label{font-weight:600;color:#6c757d}" +
               ".detail-value{font-family:'Courier New',monospace;color:#2c3e50;font-weight:600}" +
               ".formula-box{background:#fff;padding:15px;border-radius:6px;margin:10px 0;border:2px solid #667eea}" +
               ".formula-title{font-weight:600;color:#667eea;margin-bottom:8px}" +
               ".formula-expr{font-family:'Courier New',monospace;font-size:1.1em;color:#2c3e50;padding:10px;background:#f0f0f0;border-radius:4px;margin:8px 0}" +
               ".calc-step{background:#fff;padding:12px;margin:8px 0;border-left:3px solid #28a745;border-radius:4px}" +
               ".calc-step code{background:#f0f0f0;padding:2px 6px;border-radius:3px;font-family:'Courier New',monospace}" +
               ".result-box{background:linear-gradient(135deg,#d4edda,#c3e6cb);padding:15px;border-radius:6px;margin:15px 0;border:2px solid #28a745}" +
               ".result-box .label{font-weight:600;color:#155724;margin-bottom:5px}" +
               ".result-box .value{font-size:1.5em;font-weight:bold;color:#155724;font-family:'Courier New',monospace}" +
               ".doc-freq-row{background:#fff3cd;font-weight:bold}" +
               ".doc-freq-row th{background:#ffc107!important;color:#000!important}" +
               "details{border:1px solid #dee2e6;border-radius:6px;margin:10px 0}" +
               "details[open]{border-color:#667eea}" +
               "summary{cursor:pointer;font-weight:600;color:#667eea;padding:12px;background:#f8f9fa;border-radius:6px;user-select:none;transition:.3s}" +
               "summary:hover{background:#e9ecef;color:#764ba2}" +
               "summary::marker{color:#667eea;font-size:1.2em}";
    }
    
    private String getStats() {
        int tot = 0;
        for (DocumentSequence s : result.getSequences()) tot += s.getTokens().size();
        return "<div class='st'>" +
               "<div class='stat'><div class='l'>Documents</div><div class='val'>" + result.getSequences().size() + "</div></div>" +
               "<div class='stat'><div class='l'>Vocabulary</div><div class='val'>" + result.getVocabulary().getSize() + "</div></div>" +
               "<div class='stat'><div class='l'>Tokens</div><div class='val'>" + tot + "</div></div>" +
               "</div>";
    }
    
    private String getDocCards() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Documents</h2>");
        int lim = Math.min(50, result.getSequences().size());
        for (int i = 0; i < lim; i++) {
            DocumentSequence seq = result.getSequences().get(i);
            String orig = i < originalDocs.size() ? esc(originalDocs.get(i)) : "";
            String proc = esc((String) seq.getMetadata().getOrDefault("preprocessed", ""));
            sb.append("<div class='dc'><h3>Doc ").append(i+1).append("</h3>");
            sb.append("<div class='dt'>");
            sb.append("<label><input type='radio' name='v").append(i).append("' checked onclick='tog(").append(i).append(",0)'>Original</label> ");
            sb.append("<label><input type='radio' name='v").append(i).append("' onclick='tog(").append(i).append(",1)'>Preprocessed</label>");
            sb.append("</div>");
            sb.append("<div class='dtc' id='o").append(i).append("'>").append(orig).append("</div>");
            sb.append("<div class='dtc' id='p").append(i).append("' style='display:none'>").append(proc).append("</div>");
            sb.append("</div>");
        }
        if (result.getSequences().size() > 50) {
            sb.append("<p style='text-align:center;color:#6c757d'>Showing first 50 of ").append(result.getSequences().size()).append("</p>");
        }
        return sb.toString();
    }
    
    private String getData() {
        StringBuilder sb = new StringBuilder(50000); // Pre-allocate capacity
        Vocabulary vocab = result.getVocabulary();
        List<DocumentSequence> seqs = result.getSequences();
        TFIDFCalculator calculator = result.getTfidfCalculator();
        int totalDocs = seqs.size();
        int vocabSize = vocab.getSize();
        
        // Cache enum arrays to avoid repeated calls
        TFFormula[] tfFormulas = TFFormula.values();
        IDFFormula[] idfFormulas = IDFFormula.values();
        
        sb.append("const D={m:[");
        for (int i = 0; i < totalDocs; i++) {
            if (i > 0) sb.append(",");
            Map<Integer, Integer> fm = new HashMap<>(vocabSize);
            for (String t : seqs.get(i).getTokens()) {
                int id = vocab.getIndex(t);
                fm.merge(id, 1, Integer::sum);
            }
            sb.append("[");
            for (int j = 0; j < vocabSize; j++) {
                if (j > 0) sb.append(",");
                sb.append(fm.getOrDefault(j, 0));
            }
            sb.append("]");
        }
        sb.append("],docTotals:[");
        for (int i = 0; i < totalDocs; i++) {
            if (i > 0) sb.append(",");
            sb.append(calculator.getTotalTermsInDocument(i));
        }
        sb.append("],termDocFreq:[");
        for (int i = 0; i < vocabSize; i++) {
            if (i > 0) sb.append(",");
            sb.append(calculator.getDocumentFrequency(i));
        }
        sb.append("],termMaxCount:[");
        for (int docIdx = 0; docIdx < totalDocs; docIdx++) {
            if (docIdx > 0) sb.append(",");
            sb.append(calculator.getMaxTermCountInDocument(docIdx));
        }
        sb.append("],v:[");
        
        // For each token in vocabulary, send all TF and IDF values
        for (int i = 0; i < vocabSize; i++) {
            if (i > 0) sb.append(",");
            String tok = vocab.getToken(i);
            int docFreq = calculator.getDocumentFrequency(i);
            
            sb.append("{i:").append(i).append(",t:'").append(escJs(tok)).append("',");
            sb.append("df:").append(docFreq).append(",");
            
            // Add all IDF values for all formulas
            sb.append("idf:{");
            for (int idfIdx = 0; idfIdx < idfFormulas.length; idfIdx++) {
                if (idfIdx > 0) sb.append(",");
                IDFFormula idfFormula = idfFormulas[idfIdx];
                double idf = calculator.getIDF(i, idfFormula);
                sb.append("'").append(idfFormula.name()).append("':").append(String.format("%.6f", idf));
            }
            sb.append("}");
            
            // Add all TF values for each document and each formula
            sb.append(",tf:[");
            for (int docIdx = 0; docIdx < totalDocs; docIdx++) {
                if (docIdx > 0) sb.append(",");
                sb.append("{");
                for (int tfIdx = 0; tfIdx < tfFormulas.length; tfIdx++) {
                    if (tfIdx > 0) sb.append(",");
                    TFFormula tfFormula = tfFormulas[tfIdx];
                    double tf = calculator.getTF(docIdx, i, tfFormula);
                    sb.append("'").append(tfFormula.name()).append("':").append(String.format("%.6f", tf));
                }
                sb.append("}");
            }
            sb.append("]}");
        }
        sb.append("]");
        
        // Add formula metadata
        sb.append(",tfFormulas:[");
        for (int tfIdx = 0; tfIdx < tfFormulas.length; tfIdx++) {
            if (tfIdx > 0) sb.append(",");
            TFFormula formula = tfFormulas[tfIdx];
            sb.append("{id:'").append(formula.name()).append("',");
            sb.append("name:'").append(escJs(formula.getDisplayName())).append("',");
            sb.append("formula:'").append(escJs(formula.getFormula())).append("'}");
        }
        sb.append("],idfFormulas:[");
        for (int idfIdx = 0; idfIdx < idfFormulas.length; idfIdx++) {
            if (idfIdx > 0) sb.append(",");
            IDFFormula formula = idfFormulas[idfIdx];
            sb.append("{id:'").append(formula.name()).append("',");
            sb.append("name:'").append(escJs(formula.getDisplayName())).append("',");
            sb.append("formula:'").append(escJs(formula.getFormula())).append("'}");
        }
        sb.append("]};");
        return sb.toString();
    }
    
    private String getJS() {
        return "let mp=0,vp=0,mPageSize=10,vPageSize=50,selTF='TERM_FREQUENCY',selIDF='IDF_SMOOTH',sortBy='id',sortDir='asc',searchTerm='';" +
               "const els={};" +
               "function openTab(t){" +
               "document.querySelectorAll('.tab').forEach(e=>e.classList.remove('active'));" +
               "document.querySelectorAll('.tc').forEach(e=>e.classList.remove('active'));" +
               "event.target.classList.add('active');" +
               "document.getElementById(t).classList.add('active');" +
               "if(t==='m'&&!document.getElementById('mt').innerHTML)rM();" +
               "if(t==='v'&&!document.getElementById('vt').innerHTML)rV();" +
               "}" +
               "function calcTFIDF(v,docIdx){" +
               "const tf=v.tf[docIdx][selTF];" +
               "const idf=v.idf[selIDF];" +
               "return tf*idf;" +
               "}" +
               "function chgPageSize(t){" +
               "if(t==='m'){" +
               "mPageSize=parseInt(document.getElementById('mPageSize').value);" +
               "mp=0;rM();" +
               "}else{" +
               "vPageSize=parseInt(document.getElementById('vPageSize').value);" +
               "vp=0;rV();" +
               "}" +
               "}" +
               "function rM(){" +
               "const totalPages=Math.ceil(D.m.length/mPageSize);" +
               "const s=mp*mPageSize,e=Math.min(s+mPageSize,D.m.length);" +
               "const vocabSize=D.v.length;" +
               "let h='<div style=\"margin:15px 0;padding:15px;background:#f8f9fa;border-radius:8px;\">';" +
               "h+='<div style=\"display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;\">';" +
               "h+='<div><strong>📊 Matrix Info:</strong> '+D.m.length+' documents × '+vocabSize+' terms = '+(D.m.length*vocabSize)+' cells</div>';" +
               "h+='<div><strong>Page:</strong> '+(mp+1)+' / '+totalPages+' <span style=\"color:#6c757d;margin-left:10px;\">(showing docs '+(s+1)+'-'+e+')</span></div>';" +
               "h+='</div>';" +
               "h+='<div style=\"color:#6c757d;font-size:0.9em;\">💡 Tip: Scroll horizontally to see all terms. Yellow row shows document frequency.</div>';" +
               "h+='</div>';" +
               "h+='<div class=\"tbl-wrap\" style=\"max-height:'+(D.m.length>5?'500px':'800px')+';\"><table><thead><tr><th style=\"position:sticky;left:0;z-index:20;background:linear-gradient(135deg,#667eea,#764ba2);\">Doc</th>';" +
               "for(let i=0;i<vocabSize;i++){" +
               "const token=D.v[i].t;" +
               "h+='<th title=\"ID: '+i+' | Token: '+token+' | Doc Freq: '+D.termDocFreq[i]+'\">'+i+'</th>';" +
               "}" +
               "h+='<th style=\"background:#28a745;font-weight:bold;position:sticky;right:0;z-index:20;\">Total</th>';" +
               "h+='</tr></thead><tbody>';" +
               "for(let i=s;i<e;i++){" +
               "h+='<tr><th style=\"position:sticky;left:0;z-index:10;background:#764ba2;\">Doc '+(i+1)+'</th>';" +
               "for(let j=0;j<D.m[i].length;j++){" +
               "const v=D.m[i][j];" +
               "const title='Doc '+(i+1)+' | Token: '+D.v[j].t+' | Count: '+v;" +
               "h+='<td class=\"f'+(v===0?0:v<=2?1:v<=5?2:3)+'\" title=\"'+title+'\">'+v+'</td>';" +
               "}" +
               "h+='<td style=\"background:#d4edda;font-weight:bold;color:#155724;position:sticky;right:0;z-index:10;\">'+D.docTotals[i]+'</td>';" +
               "h+='</tr>';" +
               "}" +
               "h+='<tr class=\"doc-freq-row\"><th style=\"position:sticky;left:0;z-index:10;\">Doc Freq</th>';" +
               "for(let j=0;j<D.termDocFreq.length;j++){" +
               "const df=D.termDocFreq[j];" +
               "const pct=D.m.length>0?((df/D.m.length)*100).toFixed(1):0;" +
               "h+='<td title=\"'+df+' / '+D.m.length+' docs ('+pct+'%)\">'+df+'</td>';" +
               "}" +
               "h+='<td style=\"background:#ffc107;position:sticky;right:0;z-index:10;\">-</td></tr>';" +
               "h+='</tbody></table></div>';" +
               "document.getElementById('mt').innerHTML=h;" +
               "document.getElementById('mp').textContent='Page '+(mp+1)+' / '+totalPages;" +
               "document.querySelector('[onclick=\"prevPage(\\\"m\\\")\"]').disabled=mp===0;" +
               "document.querySelector('[onclick=\"nextPage(\\\"m\\\")\"]').disabled=(mp+1)*mPageSize>=D.m.length;" +
               "}" +
               "function rV(){" +
               "let filtered=D.v.filter(v=>{" +
               "if(!searchTerm)return true;" +
               "const term=searchTerm.toLowerCase();" +
               "return v.t.toLowerCase().includes(term)||v.i.toString()===term;" +
               "});" +
               "filtered.sort((a,b)=>{" +
               "let av,bv;" +
               "if(sortBy==='id'){av=a.i;bv=b.i;}" +
               "else if(sortBy==='token'){av=a.t.toLowerCase();bv=b.t.toLowerCase();}" +
               "else if(sortBy==='df'){av=a.df;bv=b.df;}" +
               "else if(sortBy==='tf'){" +
               "av=a.tf.reduce((s,t)=>s+t[selTF],0)/D.m.length;" +
               "bv=b.tf.reduce((s,t)=>s+t[selTF],0)/D.m.length;" +
               "}" +
               "else if(sortBy==='idf'){av=a.idf[selIDF];bv=b.idf[selIDF];}" +
               "else if(sortBy==='tfidf'){" +
               "av=a.tf.reduce((s,t,i)=>s+t[selTF]*a.idf[selIDF],0)/D.m.length;" +
               "bv=b.tf.reduce((s,t,i)=>s+t[selTF]*b.idf[selIDF],0)/D.m.length;" +
               "}" +
               "if(typeof av==='string')return sortDir==='asc'?av.localeCompare(bv):bv.localeCompare(av);" +
               "return sortDir==='asc'?av-bv:bv-av;" +
               "});" +
               "const s=vp*vPageSize,e=Math.min(s+vPageSize,filtered.length);" +
               "const tfCurr=D.tfFormulas.find(f=>f.id===selTF);" +
               "const idfCurr=D.idfFormulas.find(f=>f.id===selIDF);" +
               "let h='<div class=\"search-box\">';" +
               "h+='<input type=\"text\" id=\"searchInput\" placeholder=\"🔍 Search by Token or ID...\" value=\"'+searchTerm+'\" onkeyup=\"if(event.key===\\'Enter\\')doSearch()\">';" +
               "h+='<button onclick=\"doSearch()\">Search</button>';" +
               "h+='<button onclick=\"clearSearch()\">Clear</button>';" +
               "h+='</div>';" +
               "h+='<div class=\"formula-sel\">';" +
               "h+='<div class=\"form-group\">';" +
               "h+='<label>TF Formula:</label>';" +
               "h+='<select id=\"tfSel\" onchange=\"chgFormula()\">';" +
               "D.tfFormulas.forEach(f=>h+='<option value=\"'+f.id+'\"'+(f.id===selTF?' selected':'')+'>'+f.name+'</option>');" +
               "h+='</select>';" +
               "h+='<div class=\"formula-display\" id=\"tfFormula\">'+(tfCurr?tfCurr.formula:'')+'</div>';" +
               "h+='</div>';" +
               "h+='<div class=\"form-group\">';" +
               "h+='<label>IDF Formula:</label>';" +
               "h+='<select id=\"idfSel\" onchange=\"chgFormula()\">';" +
               "D.idfFormulas.forEach(f=>h+='<option value=\"'+f.id+'\"'+(f.id===selIDF?' selected':'')+'>'+f.name+'</option>');" +
               "h+='</select>';" +
               "h+='<div class=\"formula-display\" id=\"idfFormula\">'+(idfCurr?idfCurr.formula:'')+'</div>';" +
               "h+='</div>';" +
               "h+='</div>';" +
               "let maxTFIDF=0,maxAvgTF=0;" +
               "D.v.forEach(v=>{" +
               "let sumTF=0;" +
               "for(let i=0;i<D.m.length;i++){" +
               "const tf=v.tf[i][selTF];" +
               "sumTF+=tf;" +
               "const tfidf=calcTFIDF(v,i);" +
               "if(tfidf>maxTFIDF)maxTFIDF=tfidf;" +
               "}" +
               "const avgTF=sumTF/D.m.length;" +
               "if(avgTF>maxAvgTF)maxAvgTF=avgTF;" +
               "});" +
               "h+='<div style=\"margin:10px 0;color:#6c757d;display:flex;justify-content:space-between;align-items:center;\">';" +
               "h+='<span>📊 Showing '+filtered.length+' token(s)'+(searchTerm?' matching \"'+searchTerm+'\"':'')+'</span>';" +
               "h+='<span>Page '+(vp+1)+' / '+Math.ceil(filtered.length/vPageSize)+'</span></div>';" +
               "h+='<div class=\"tbl-wrap\"><table><thead><tr>';" +
               "h+='<th class=\"sortable '+(sortBy==='id'?sortDir:''+'')+'\" onclick=\"setSort(\\'id\\')\">ID</th>';" +
               "h+='<th class=\"sortable '+(sortBy==='token'?sortDir:''+'')+'\" onclick=\"setSort(\\'token\\')\">Token</th>';" +
               "h+='<th class=\"sortable '+(sortBy==='df'?sortDir:''+'')+'\" onclick=\"setSort(\\'df\\')\">Doc Freq</th>';" +
               "h+='<th class=\"sortable '+(sortBy==='tf'?sortDir:''+'')+'\" onclick=\"setSort(\\'tf\\')\">Avg TF</th>';" +
               "h+='<th class=\"sortable '+(sortBy==='idf'?sortDir:''+'')+'\" onclick=\"setSort(\\'idf\\')\">IDF</th>';" +
               "h+='<th class=\"sortable '+(sortBy==='tfidf'?sortDir:''+'')+'\" onclick=\"setSort(\\'tfidf\\')\">Avg TF-IDF</th>';" +
               "h+='<th>Action</th></tr></thead><tbody>';" +
               "for(let i=s;i<e;i++){" +
               "const v=filtered[i];" +
               "let sumTF=0,sumTFIDF=0;" +
               "for(let d=0;d<D.m.length;d++){" +
               "sumTF+=v.tf[d][selTF];" +
               "sumTFIDF+=calcTFIDF(v,d);" +
               "}" +
               "const avgTF=sumTF/D.m.length;" +
               "const avgTFIDF=sumTFIDF/D.m.length;" +
               "const idf=v.idf[selIDF];" +
               "const tfW=maxAvgTF>0?Math.round((avgTF/maxAvgTF)*100):0;" +
               "const tfidfW=maxTFIDF>0?Math.round((avgTFIDF/maxTFIDF)*100):0;" +
               "h+='<tr onclick=\"showDetail('+v.i+')\">';" +
               "h+='<td class=\"num\">'+v.i+'</td>';" +
               "h+='<td><span class=\"tok\">'+v.t+'</span></td>';" +
               "h+='<td class=\"num\">'+v.df+'</td>';" +
               "h+='<td class=\"num'+(avgTF>0?' hi':'')+'\">'+avgTF.toFixed(6);" +
               "if(avgTF>0)h+='<span class=\"vbar\" style=\"width:'+tfW+'px\"></span>';" +
               "h+='</td>';" +
               "h+='<td class=\"num'+(idf>1?' hi':'')+'\">'+idf.toFixed(6)+'</td>';" +
               "h+='<td class=\"num'+(avgTFIDF>0.01?' hi':'')+'\">'+avgTFIDF.toFixed(6);" +
               "if(avgTFIDF>0)h+='<span class=\"vbar\" style=\"width:'+tfidfW+'px\"></span>';" +
               "h+='</td>';" +
               "h+='<td><button onclick=\"event.stopPropagation();showDetail('+v.i+')\">📊 Details</button></td>';" +
               "h+='</tr>';" +
               "}" +
               "h+='</tbody></table></div>';" +
               "document.getElementById('vt').innerHTML=h;" +
               "document.getElementById('vp').textContent='Page '+(vp+1)+' / '+Math.ceil(filtered.length/vPageSize);" +
               "document.querySelector('[onclick=\"prevPage(\\\"v\\\")\"]').disabled=vp===0;" +
               "document.querySelector('[onclick=\"nextPage(\\\"v\\\")\"]').disabled=(vp+1)*vPageSize>=filtered.length;" +
               "}" +
               "function chgFormula(){" +
               "selTF=document.getElementById('tfSel').value;" +
               "selIDF=document.getElementById('idfSel').value;" +
               "vp=0;" +
               "rV();" +
               "}" +
               "function doSearch(){" +
               "searchTerm=document.getElementById('searchInput').value.trim();" +
               "vp=0;" +
               "rV();" +
               "}" +
               "function clearSearch(){" +
               "searchTerm='';" +
               "vp=0;" +
               "rV();" +
               "}" +
               "function setSort(by){" +
               "if(sortBy===by)sortDir=sortDir==='asc'?'desc':'asc';" +
               "else{sortBy=by;sortDir='asc';}" +
               "vp=0;" +
               "rV();" +
               "}" +
               "function showDetail(termIdx){" +
               "const v=D.v.find(x=>x.i===termIdx);" +
               "if(!v)return;" +
               "const tfF=D.tfFormulas.find(f=>f.id===selTF);" +
               "const idfF=D.idfFormulas.find(f=>f.id===selIDF);" +
               "let h='<div id=\"detailModal\" class=\"modal\" style=\"display:block\">';" +
               "h+='<div class=\"modal-content\">';" +
               "h+='<div class=\"modal-header\"><h3>📊 Token Details: <span class=\"tok\" style=\"background:#fff;color:#667eea;padding:6px 12px\">'+v.t+'</span></h3><span class=\"close\" onclick=\"closeModal()\">&times;</span></div>';" +
               "h+='<div class=\"modal-body\">';" +
               "h+='<div class=\"detail-section\"><h4>🔢 Basic Information</h4>';" +
               "h+='<div class=\"detail-row\"><span class=\"detail-label\">Token ID:</span><span class=\"detail-value\">'+v.i+'</span></div>';" +
               "h+='<div class=\"detail-row\"><span class=\"detail-label\">Token:</span><span class=\"detail-value\">'+v.t+'</span></div>';" +
               "h+='<div class=\"detail-row\"><span class=\"detail-label\">Document Frequency:</span><span class=\"detail-value\">'+v.df+' / '+D.m.length+' documents</span></div>';" +
               "h+='</div>';" +
               "h+='<div class=\"detail-section\">';" +
               "h+='<h4>📐 TF Calculation ('+tfF.name+')</h4>';" +
               "h+='<div class=\"formula-expr\" style=\"margin-bottom:15px;font-size:1.1em;\">'+tfF.formula+'</div>';" +
               "let sumTF=0,countDocs=0;" +
               "h+='<details style=\"margin-bottom:15px;\" open>';" +
               "h+='<summary style=\"cursor:pointer;font-weight:600;color:#667eea;padding:10px;background:#f8f9fa;border-radius:6px;margin-bottom:10px;\">📄 Document Calculations (click to expand/collapse)</summary>';" +
               "h+='<div style=\"padding:10px;\">';" +
               "for(let docIdx=0;docIdx<D.m.length;docIdx++){" +
               "const termCount=D.m[docIdx][termIdx];" +
               "if(termCount===0)continue;" +
               "const totalTerms=D.docTotals[docIdx];" +
               "const maxCount=D.termMaxCount[docIdx];" +
               "const tf=v.tf[docIdx][selTF];" +
               "sumTF+=tf;" +
               "countDocs++;" +
               "h+='<div class=\"calc-step\" style=\"margin:8px 0;\">';" +
               "h+='<strong>Doc '+(docIdx+1)+':</strong> ';" +
               "h+=getTFSteps(selTF,termCount,totalTerms,maxCount);" +
               "h+=' → <strong>'+tf.toFixed(6)+'</strong>';" +
               "h+='</div>';" +
               "}" +
               "h+='</div></details>';" +
               "const avgTF=countDocs>0?sumTF/countDocs:0;" +
               "h+='<div class=\"result-box\">';" +
               "h+='<div class=\"label\">Average TF across all documents:</div>';" +
               "h+='<div class=\"value\">'+avgTF.toFixed(6)+'</div>';" +
               "h+='<div style=\"font-size:0.9em;color:#155724;margin-top:5px;\">Sum: '+sumTF.toFixed(6)+' ÷ Documents: '+countDocs+' = '+avgTF.toFixed(6)+'</div>';" +
               "h+='</div>';" +
               "h+='</div>';" +
               "h+='<div class=\"detail-section\">';" +
               "h+='<h4>🌍 IDF Calculation ('+idfF.name+')</h4>';" +
               "h+='<div class=\"detail-row\"><span class=\"detail-label\">Total Documents N:</span><span class=\"detail-value\">'+D.m.length+'</span></div>';" +
               "h+='<div class=\"detail-row\"><span class=\"detail-label\">Documents with term n(t):</span><span class=\"detail-value\">'+v.df+'</span></div>';" +
               "h+='<div class=\"formula-box\">';" +
               "h+='<div class=\"formula-title\">📐 IDF Calculation</div>';" +
               "h+='<div class=\"formula-expr\">'+idfF.formula+'</div>';" +
               "h+='<div class=\"calc-step\">'+getIDFSteps(selIDF,D.m.length,v.df)+'</div>';" +
               "h+='<div class=\"result-box\"><div class=\"label\">IDF Result:</div><div class=\"value\">'+v.idf[selIDF].toFixed(6)+'</div></div>';" +
               "h+='</div>';" +
               "h+='</div>';" +
               "h+='<div class=\"detail-section\">';" +
               "h+='<h4>✨ TF-IDF Calculations</h4>';" +
               "let sumTFIDF=0,countTFIDF=0;" +
               "h+='<details style=\"margin-bottom:15px;\">';" +
               "h+='<summary style=\"cursor:pointer;font-weight:600;color:#667eea;padding:10px;background:#f8f9fa;border-radius:6px;margin-bottom:10px;\">📄 Document TF-IDF Results (click to expand/collapse)</summary>';" +
               "h+='<div style=\"padding:10px;\">';" +
               "for(let docIdx=0;docIdx<D.m.length;docIdx++){" +
               "const termCount=D.m[docIdx][termIdx];" +
               "if(termCount===0)continue;" +
               "const tf=v.tf[docIdx][selTF];" +
               "const idf=v.idf[selIDF];" +
               "const tfidf=tf*idf;" +
               "sumTFIDF+=tfidf;" +
               "countTFIDF++;" +
               "h+='<div class=\"calc-step\" style=\"margin:8px 0;\">';" +
               "h+='<strong>Doc '+(docIdx+1)+':</strong> ';" +
               "h+='TF × IDF = <code>'+tf.toFixed(6)+'</code> × <code>'+idf.toFixed(6)+'</code> = <strong>'+tfidf.toFixed(6)+'</strong>';" +
               "h+='</div>';" +
               "}" +
               "h+='</div></details>';" +
               "const avgTFIDF=countTFIDF>0?sumTFIDF/countTFIDF:0;" +
               "h+='<div class=\"result-box\">';" +
               "h+='<div class=\"label\">Average TF-IDF:</div>';" +
               "h+='<div class=\"value\">'+avgTFIDF.toFixed(6)+'</div>';" +
               "h+='<div style=\"font-size:0.9em;color:#155724;margin-top:5px;\">Sum: '+sumTFIDF.toFixed(6)+' ÷ Documents: '+countTFIDF+' = '+avgTFIDF.toFixed(6)+'</div>';" +
               "h+='</div>';" +
               "h+='</div>';" +
               "h+='</div></div></div>';" +
               "document.body.insertAdjacentHTML('beforeend',h);" +
               "}" +
               "function getTFSteps(formula,count,total,maxCount){" +
               "switch(formula){" +
               "case 'BINARY':return 'Since term occurs in document: <code>tf = 1</code>';" +
               "case 'RAW_COUNT':return 'Raw count: <code>tf = '+count+'</code>';" +
               "case 'TERM_FREQUENCY':return 'tf = f(t,d) / Σf(t\\',d) = <code>'+count+'</code> / <code>'+total+'</code> = <code>'+(count/total).toFixed(6)+'</code>';" +
               "case 'LOG_NORMALIZATION':return 'tf = log(1 + f(t,d)) = log(1 + <code>'+count+'</code>) = <code>'+Math.log(1+count).toFixed(6)+'</code>';" +
               "case 'DOUBLE_NORMALIZATION_05':return 'tf = 0.5 + 0.5 × (f(t,d) / max{f(t\\',d)}) = 0.5 + 0.5 × (<code>'+count+'</code> / <code>'+maxCount+'</code>) = <code>'+(0.5+0.5*count/maxCount).toFixed(6)+'</code>';" +
               "case 'DOUBLE_NORMALIZATION_K':return 'tf = K + (1-K) × (f(t,d) / max{f(t\\',d)}) with K=0.5 = 0.5 + 0.5 × (<code>'+count+'</code> / <code>'+maxCount+'</code>) = <code>'+(0.5+0.5*count/maxCount).toFixed(6)+'</code>';" +
               "default:return '';}" +
               "}" +
               "function getIDFSteps(formula,N,df){" +
               "if(df===0)df=1;" +
               "switch(formula){" +
               "case 'UNARY':return 'Unary (constant): <code>idf = 1</code>';" +
               "case 'IDF':return 'idf = log(N / n(t)) = log(<code>'+N+'</code> / <code>'+df+'</code>) = <code>'+Math.log(N/df).toFixed(6)+'</code>';" +
               "case 'IDF_SMOOTH':return 'idf = log(N / (1 + n(t))) + 1 = log(<code>'+N+'</code> / <code>'+(1+df)+'</code>) + 1 = <code>'+(Math.log(N/(1+df))+1).toFixed(6)+'</code>';" +
               "case 'IDF_MAX':return 'idf = log(max{n(t\\')}/n(t)) (requires max doc freq from data)';" +
               "case 'IDF_PROBABILISTIC':return 'idf = log((N - n(t)) / n(t)) = log((<code>'+N+'</code> - <code>'+df+'</code>) / <code>'+df+'</code>) = <code>'+Math.log((N-df)/df).toFixed(6)+'</code>';" +
               "default:return '';}" +
               "}" +
               "function closeModal(){" +
               "const m=document.getElementById('detailModal');" +
               "if(m)m.remove();" +
               "}" +
               "function prevPage(t){if(t==='m'&&mp>0){mp--;rM();}if(t==='v'&&vp>0){vp--;rV();}}" +
               "function nextPage(t){if(t==='m'&&(mp+1)*mPageSize<D.m.length){mp++;rM();}if(t==='v'){vp++;rV();}}" +
               "function tog(i,t){document.getElementById('o'+i).style.display=t===0?'block':'none';document.getElementById('p'+i).style.display=t===1?'block':'none';}" +
               "function downloadCSV(t){" +
               "let c='';" +
               "if(t==='m'){" +
               "c='Doc/ID';" +
               "for(let i=0;i<D.v.length;i++)c+=','+i;" +
               "c+=',Total\\n';" +
               "D.m.forEach((d,i)=>c+='Doc '+(i+1)+','+d.join(',')+','+D.docTotals[i]+'\\n');" +
               "c+='Doc Freq';" +
               "for(let i=0;i<D.termDocFreq.length;i++)c+=','+D.termDocFreq[i];" +
               "c+=',-\\n';" +
               "dl(c,'frequency_matrix.csv');" +
               "}else{" +
               "c='ID,Token,DocFreq,AvgTF,IDF,AvgTFIDF\\n';" +
               "D.v.forEach(v=>{" +
               "let sumTF=0,sumTFIDF=0;" +
               "for(let d=0;d<D.m.length;d++){" +
               "sumTF+=v.tf[d][selTF];" +
               "sumTFIDF+=calcTFIDF(v,d);" +
               "}" +
               "const avgTF=sumTF/D.m.length;" +
               "const avgTFIDF=sumTFIDF/D.m.length;" +
               "c+=v.i+','+v.t+','+v.df+','+avgTF.toFixed(6)+','+v.idf[selIDF].toFixed(6)+','+avgTFIDF.toFixed(6)+'\\n';" +
               "});" +
               "dl(c,'vocabulary_'+selTF+'_'+selIDF+'.csv');" +
               "}" +
               "}" +
               "function dl(c,f){const b=new Blob([c],{type:'text/csv'});const a=document.createElement('a');a.href=URL.createObjectURL(b);a.download=f;a.click();}" +
               "window.onload=()=>rM();" +
               "window.onclick=e=>{const m=document.getElementById('detailModal');if(m&&e.target===m)closeModal();};";
    }
    
    private String esc(String t) {
        if (t == null) return "";
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    
    private String escJs(String t) {
        if (t == null) return "";
        return t.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    }
}
