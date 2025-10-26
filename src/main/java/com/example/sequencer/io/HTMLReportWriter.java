package com.example.sequencer.io;

import com.example.sequencer.model.DocumentSequence;
import com.example.sequencer.model.SequenceVector;
import com.example.sequencer.pipeline.SequencingPipeline.PipelineResult;
import com.example.sequencer.encoding.Vocabulary;
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
        html.append("<div class='ctrl'><button onclick='downloadCSV(\"m\")'>📥 Download CSV</button>");
        html.append("<button onclick='prevPage(\"m\")'>⬅️ Prev</button>");
        html.append("<span class='pg' id='mp'>Page 1</span>");
        html.append("<button onclick='nextPage(\"m\")'>Next ➡️</button></div>");
        html.append("<div id='mt'></div></div>");
        html.append("<div id='v' class='tc'>");
        html.append("<h2>Vocabulary Statistics</h2>");
        html.append("<div class='ctrl'><button onclick='downloadCSV(\"v\")'>📥 Download CSV</button>");
        html.append("<button onclick='prevPage(\"v\")'>⬅️ Prev</button>");
        html.append("<span class='pg' id='vp'>Page 1</span>");
        html.append("<button onclick='nextPage(\"v\")'>Next ➡️</button></div>");
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
               ".tbl-wrap{overflow-x:auto;max-height:600px;border-radius:8px;border:1px solid #dee2e6}" +
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
               ".dtc{background:#fff;padding:15px;border-radius:4px;font-family:monospace;white-space:pre-wrap;max-height:300px;overflow-y:auto;font-size:.9em;border:1px solid #dee2e6}";
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
        StringBuilder sb = new StringBuilder();
        Vocabulary vocab = result.getVocabulary();
        List<DocumentSequence> seqs = result.getSequences();
        int totalDocs = seqs.size();
        
        sb.append("const D={m:[");
        for (int i = 0; i < seqs.size(); i++) {
            if (i > 0) sb.append(",");
            Map<Integer, Integer> fm = new HashMap<>();
            for (String t : seqs.get(i).getTokens()) {
                int id = vocab.getIndex(t);
                fm.put(id, fm.getOrDefault(id, 0) + 1);
            }
            sb.append("[");
            for (int j = 0; j < vocab.getSize(); j++) {
                if (j > 0) sb.append(",");
                sb.append(fm.getOrDefault(j, 0));
            }
            sb.append("]");
        }
        sb.append("],v:[");
        
        // Calculate TF-IDF statistics for each token
        for (int i = 0; i < vocab.getSize(); i++) {
            if (i > 0) sb.append(",");
            String tok = vocab.getToken(i);
            
            // Calculate document frequency (number of documents containing this token)
            int docFreq = 0;
            double totalTF = 0;  // Sum of all term frequencies across documents
            
            for (DocumentSequence seq : seqs) {
                int count = 0;
                for (String t : seq.getTokens()) {
                    if (vocab.getIndex(t) == i) count++;
                }
                if (count > 0) {
                    docFreq++;
                    totalTF += (double) count / seq.getTokens().size();  // TF for this doc
                }
            }
            
            // Calculate average TF across all documents
            double avgTF = totalTF / totalDocs;
            
            // Calculate IDF: log((N + 1) / (df + 1)) + 1 (smooth-idf, sklearn default)
            double idf = Math.log((double) (totalDocs + 1) / (docFreq + 1)) + 1.0;
            
            // Calculate average TF-IDF (sum of normalized TF-IDF from all vectors / total docs)
            double avgTFIDF = 0;
            for (SequenceVector vec : result.getTfidfVectors()) {
                avgTFIDF += vec.getSparseVector().getOrDefault(i, 0.0);
            }
            avgTFIDF /= totalDocs;
            
            sb.append("{i:").append(i).append(",t:'").append(escJs(tok)).append("',");
            sb.append("df:").append(docFreq).append(",");
            sb.append("tf:").append(String.format("%.4f", avgTF)).append(",");
            sb.append("idf:").append(String.format("%.4f", idf)).append(",");
            sb.append("tfidf:").append(String.format("%.4f", avgTFIDF)).append("}");
        }
        sb.append("]};");
        return sb.toString();
    }
    
    private String getJS() {
        return "let mp=0,vp=0;" +
               "function openTab(t){" +
               "document.querySelectorAll('.tab').forEach(e=>e.classList.remove('active'));" +
               "document.querySelectorAll('.tc').forEach(e=>e.classList.remove('active'));" +
               "event.target.classList.add('active');" +
               "document.getElementById(t).classList.add('active');" +
               "if(t==='m'&&!document.getElementById('mt').innerHTML)rM();" +
               "if(t==='v'&&!document.getElementById('vt').innerHTML)rV();" +
               "}" +
               "function rM(){" +
               "const s=mp*10,e=Math.min(s+10,D.m.length);" +
               "let h='<div class=\"tbl-wrap\"><table><thead><tr><th>Doc</th>';" +
               "for(let i=0;i<D.v.length;i++)h+='<th>'+i+'</th>';" +
               "h+='</tr></thead><tbody>';" +
               "for(let i=s;i<e;i++){" +
               "h+='<tr><th>Doc '+(i+1)+'</th>';" +
               "for(let j=0;j<D.m[i].length;j++){" +
               "const v=D.m[i][j];" +
               "h+='<td class=\"f'+(v===0?0:v<=2?1:v<=5?2:3)+'\">'+v+'</td>';" +
               "}" +
               "h+='</tr>';" +
               "}" +
               "h+='</tbody></table></div>';" +
               "document.getElementById('mt').innerHTML=h;" +
               "document.getElementById('mp').textContent='Page '+(mp+1)+' / '+Math.ceil(D.m.length/10);" +
               "document.querySelector('[onclick=\"prevPage(\\\"m\\\")\"]').disabled=mp===0;" +
               "document.querySelector('[onclick=\"nextPage(\\\"m\\\")\"]').disabled=(mp+1)*10>=D.m.length;" +
               "}" +
               "function rV(){" +
               "const s=vp*50,e=Math.min(s+50,D.v.length);" +
               "const maxIdf=Math.max(...D.v.map(x=>x.idf));" +
               "const maxTfidf=Math.max(...D.v.map(x=>x.tfidf));" +
               "let h='<div class=\"tbl-wrap\"><table><thead><tr><th>ID</th><th>Token</th><th>Doc Freq</th><th>Avg TF</th><th>IDF</th><th>Avg TF-IDF</th></tr></thead><tbody>';" +
               "for(let i=s;i<e;i++){" +
               "const v=D.v[i];" +
               "const idfW=Math.round((v.idf/maxIdf)*100);" +
               "const tfW=Math.round((v.tfidf/maxTfidf)*100);" +
               "h+='<tr>';" +
               "h+='<td class=\"num\">'+v.i+'</td>';" +
               "h+='<td><span class=\"tok\">'+v.t+'</span></td>';" +
               "h+='<td class=\"num\">'+v.df+'</td>';" +
               "h+='<td class=\"num\">'+v.tf.toFixed(4)+'</td>';" +
               "h+='<td class=\"num'+(v.idf>1.5?' hi':'')+'\">'+v.idf.toFixed(4)+'<span class=\"vbar\" style=\"width:'+idfW+'px\"></span></td>';" +
               "h+='<td class=\"num'+(v.tfidf>0.01?' hi':'')+'\">'+v.tfidf.toFixed(4)+'<span class=\"vbar\" style=\"width:'+tfW+'px\"></span></td>';" +
               "h+='</tr>';" +
               "}" +
               "h+='</tbody></table></div>';" +
               "document.getElementById('vt').innerHTML=h;" +
               "document.getElementById('vp').textContent='Page '+(vp+1)+' / '+Math.ceil(D.v.length/50);" +
               "document.querySelector('[onclick=\"prevPage(\\\"v\\\")\"]').disabled=vp===0;" +
               "document.querySelector('[onclick=\"nextPage(\\\"v\\\")\"]').disabled=(vp+1)*50>=D.v.length;" +
               "}" +
               "function prevPage(t){if(t==='m'&&mp>0){mp--;rM();}if(t==='v'&&vp>0){vp--;rV();}}" +
               "function nextPage(t){if(t==='m'&&(mp+1)*10<D.m.length){mp++;rM();}if(t==='v'&&(vp+1)*50<D.v.length){vp++;rV();}}" +
               "function tog(i,t){document.getElementById('o'+i).style.display=t===0?'block':'none';document.getElementById('p'+i).style.display=t===1?'block':'none';}" +
               "function downloadCSV(t){" +
               "let c='';" +
               "if(t==='m'){" +
               "c='Doc/ID';" +
               "for(let i=0;i<D.v.length;i++)c+=','+i;" +
               "c+='\\n';" +
               "D.m.forEach((d,i)=>c+='Doc '+(i+1)+','+d.join(',')+('\\n'));" +
               "dl(c,'frequency_matrix.csv');" +
               "}else{" +
               "c='ID,Token,DocFreq,AvgTF,IDF,AvgTF-IDF\\n';" +
               "D.v.forEach(v=>c+=v.i+','+v.t+','+v.df+','+v.tf.toFixed(4)+','+v.idf.toFixed(4)+','+v.tfidf.toFixed(4)+'\\n');" +
               "dl(c,'vocabulary.csv');" +
               "}" +
               "}" +
               "function dl(c,f){const b=new Blob([c],{type:'text/csv'});const a=document.createElement('a');a.href=URL.createObjectURL(b);a.download=f;a.click();}" +
               "window.onload=()=>rM();";
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
