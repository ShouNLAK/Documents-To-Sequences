package com.example.sequencer.io;

import com.example.sequencer.model.DocumentSequence;
import com.example.sequencer.model.SequenceVector;
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
               ".dtc{background:#fff;padding:15px;border-radius:4px;font-family:monospace;white-space:pre-wrap;max-height:300px;overflow-y:auto;font-size:.9em;border:1px solid #dee2e6}" +
               ".formula-sel{background:#f8f9fa;padding:20px;border-radius:8px;margin-bottom:20px;display:flex;gap:20px;flex-wrap:wrap}" +
               ".form-group{flex:1;min-width:300px}" +
               ".form-group label{display:block;font-weight:600;color:#667eea;margin-bottom:8px;font-size:.95em}" +
               ".form-group select{width:100%;padding:10px;border:2px solid #667eea;border-radius:6px;font-size:.95em;background:#fff;cursor:pointer;transition:.3s}" +
               ".form-group select:hover{border-color:#764ba2;box-shadow:0 2px 8px rgba(102,126,234,.2)}" +
               ".form-group select:focus{outline:none;border-color:#764ba2;box-shadow:0 0 0 3px rgba(102,126,234,.1)}" +
               ".formula-display{margin-top:10px;padding:12px;background:#fff;border-left:4px solid #667eea;border-radius:4px;font-family:'Courier New',monospace;color:#2c3e50;font-size:.9em;min-height:40px;display:flex;align-items:center}";
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
        TFIDFCalculator calculator = result.getTfidfCalculator();
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
        
        // For each token in vocabulary, send all TF and IDF values
        for (int i = 0; i < vocab.getSize(); i++) {
            if (i > 0) sb.append(",");
            String tok = vocab.getToken(i);
            int docFreq = calculator.getDocumentFrequency(i);
            
            sb.append("{i:").append(i).append(",t:'").append(escJs(tok)).append("',");
            sb.append("df:").append(docFreq).append(",");
            
            // Add all IDF values for all formulas
            sb.append("idf:{");
            int idfIdx = 0;
            for (IDFFormula idfFormula : IDFFormula.values()) {
                if (idfIdx > 0) sb.append(",");
                double idf = calculator.getIDF(i, idfFormula);
                sb.append("'").append(idfFormula.name()).append("':").append(String.format("%.6f", idf));
                idfIdx++;
            }
            sb.append("}");
            
            // Add all TF values for each document and each formula
            sb.append(",tf:[");
            for (int docIdx = 0; docIdx < totalDocs; docIdx++) {
                if (docIdx > 0) sb.append(",");
                sb.append("{");
                int tfIdx = 0;
                for (TFFormula tfFormula : TFFormula.values()) {
                    if (tfIdx > 0) sb.append(",");
                    double tf = calculator.getTF(docIdx, i, tfFormula);
                    sb.append("'").append(tfFormula.name()).append("':").append(String.format("%.6f", tf));
                    tfIdx++;
                }
                sb.append("}");
            }
            sb.append("]}");
        }
        sb.append("]");
        
        // Add formula metadata
        sb.append(",tfFormulas:[");
        int tfIdx = 0;
        for (TFFormula formula : TFFormula.values()) {
            if (tfIdx > 0) sb.append(",");
            sb.append("{id:'").append(formula.name()).append("',");
            sb.append("name:'").append(escJs(formula.getDisplayName())).append("',");
            sb.append("formula:'").append(escJs(formula.getFormula())).append("'}");
            tfIdx++;
        }
        sb.append("],idfFormulas:[");
        int idfIdx = 0;
        for (IDFFormula formula : IDFFormula.values()) {
            if (idfIdx > 0) sb.append(",");
            sb.append("{id:'").append(formula.name()).append("',");
            sb.append("name:'").append(escJs(formula.getDisplayName())).append("',");
            sb.append("formula:'").append(escJs(formula.getFormula())).append("'}");
            idfIdx++;
        }
        sb.append("]};");
        return sb.toString();
    }
    
    private String getJS() {
        return "let mp=0,vp=0,selTF='TERM_FREQUENCY',selIDF='IDF_SMOOTH';" +
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
               "let h='<div class=\"formula-sel\">';" +
               "h+='<div class=\"form-group\">';" +
               "h+='<label>TF Formula:</label>';" +
               "h+='<select id=\"tfSel\" onchange=\"chgFormula()\">';" +
               "D.tfFormulas.forEach(f=>h+='<option value=\"'+f.id+'\"'+(f.id===selTF?' selected':'')+'>'+f.name+'</option>');" +
               "h+='</select>';" +
               "h+='<div class=\"formula-display\" id=\"tfFormula\"></div>';" +
               "h+='</div>';" +
               "h+='<div class=\"form-group\">';" +
               "h+='<label>IDF Formula:</label>';" +
               "h+='<select id=\"idfSel\" onchange=\"chgFormula()\">';" +
               "D.idfFormulas.forEach(f=>h+='<option value=\"'+f.id+'\"'+(f.id===selIDF?' selected':'')+'>'+f.name+'</option>');" +
               "h+='</select>';" +
               "h+='<div class=\"formula-display\" id=\"idfFormula\"></div>';" +
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
               "h+='<div class=\"tbl-wrap\"><table><thead><tr><th>ID</th><th>Token</th><th>Doc Freq</th><th>Avg TF</th><th>IDF</th><th>Avg TF-IDF</th></tr></thead><tbody>';" +
               "for(let i=s;i<e;i++){" +
               "const v=D.v[i];" +
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
               "h+='<tr>';" +
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
               "h+='</tr>';" +
               "}" +
               "h+='</tbody></table></div>';" +
               "document.getElementById('vt').innerHTML=h;" +
               "document.getElementById('vp').textContent='Page '+(vp+1)+' / '+Math.ceil(D.v.length/50);" +
               "document.querySelector('[onclick=\"prevPage(\\\"v\\\")\"]').disabled=vp===0;" +
               "document.querySelector('[onclick=\"nextPage(\\\"v\\\")\"]').disabled=(vp+1)*50>=D.v.length;" +
               "updFormula();" +
               "}" +
               "function chgFormula(){" +
               "selTF=document.getElementById('tfSel').value;" +
               "selIDF=document.getElementById('idfSel').value;" +
               "rV();" +
               "}" +
               "function updFormula(){" +
               "const tfF=D.tfFormulas.find(f=>f.id===selTF);" +
               "const idfF=D.idfFormulas.find(f=>f.id===selIDF);" +
               "if(tfF)document.getElementById('tfFormula').textContent=tfF.formula;" +
               "if(idfF)document.getElementById('idfFormula').textContent=idfF.formula;" +
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
