import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
public class Scraper {
    
    public static void main(String[] args) {
        
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("config/info.properties");
            props.load(in);
            in.close();
            
            if (!props.containsKey("username") || !props.containsKey("password") || !props.containsKey("url")) {
                System.err.println("Invalid properties!");
                System.exit(1);
            }
            
            int prior = 0;
            
            File cache = new File("config/cache.dat");
            
            if (cache.exists()) {
                
                Scanner sc = new Scanner(cache);
                
                if (sc.hasNextInt()) {
                    prior = sc.nextInt();
                }
                
                sc.close();
            }
            
            UserAgent userAgent = new UserAgent();
            userAgent.visit("http://www.sacs.k12.in.us/site/default.aspx?PageType=14&DomainID=4&PageID=1&ModuleInstanceID=74&ViewID=7070de72-c6ac-43a7-b8eb-103562708ba5&IsMoreExpandedView=True");
            
            Elements links = userAgent.doc.findEvery("<h1 class='ui-article-title'>").findEvery("<a>");
            
            new File("json").mkdir();
            
            int index = 0;
            
            for (Element link : links) {
                
                userAgent.visit(link.getAt("href"));
                
                Element title = userAgent.doc.findFirst("<div class='ui-widget-header'>");
                Element content = userAgent.doc.findFirst("<div class='ui-widget-detail'>");
                
                JSONObject obj = new JSONObject();
                obj.put("status", "publish");
                obj.put("title", title.innerText().trim());
                obj.put("content", content.innerHTML().trim());
                
                FileWriter file = new FileWriter("json/" + (index++) + ".json");
                file.write(obj.toJSONString());
                file.close();
            }
            
            File f = new File("jsontodb-config.yml");
            f.createNewFile();
            PrintWriter pw = new PrintWriter(f);
            pw.println("User: " + props.get("username"));
            pw.println("Pass: " + props.get("password"));
            int num = index - prior;
            if (num > 0) {
                pw.println("Matrix:");
                
                for (int i = 0; i < num; i++) {
                    pw.println("  - Files: json/" + (num - i - 1) + ".json");
                    pw.println("    Command: post");
                    pw.println("    Url: " + props.get("url"));
                }
            }
            pw.close();
            
            cache.createNewFile();
            pw = new PrintWriter(cache);
            pw.println(index);
            pw.close();
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
