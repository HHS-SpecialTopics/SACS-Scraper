import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;


/**
 * @author Christopher Lutz
 */
public class Scraper {
    
    public static void main(String[] args) {
        
        try {
            
            UserAgent userAgent = new UserAgent();
            userAgent.visit("http://www.sacs.k12.in.us/site/default.aspx?PageType=14&DomainID=4&PageID=1&ModuleInstanceID=74&ViewID=7070de72-c6ac-43a7-b8eb-103562708ba5&IsMoreExpandedView=True");
            
            Elements links = userAgent.doc.findEvery("<h1 class='ui-article-title'>").findEvery("<a>");
            
            new File("json").mkdir();
            
            int i = 0;
            
            for (Element link : links) {
                
                userAgent.visit(link.getAt("href"));
                
                Element title = userAgent.doc.findFirst("<div class='ui-widget-header'>");
                Element content = userAgent.doc.findFirst("<div class='ui-widget-detail'>");
                
                JSONObject obj = new JSONObject();
                obj.put("status", "publish");
                obj.put("title", title.innerText().trim());
                obj.put("content", content.innerHTML().trim());
    
                FileWriter file = new FileWriter("json/"+(i++)+".json");
                file.write(obj.toJSONString());
                file.close();
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
