import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;


/**
 * @author Christopher Lutz
 */
public class Scraper {
    
    public static void main(String[] args) {
        
        try {
            
            UserAgent userAgent = new UserAgent();
            userAgent.visit("http://www.sacs.k12.in.us/site/default.aspx?PageType=14&DomainID=4&PageID=1&ModuleInstanceID=74&ViewID=7070de72-c6ac-43a7-b8eb-103562708ba5&IsMoreExpandedView=True");
            
            Elements links = userAgent.doc.findEvery("<h1 class='ui-article-title'>").findEvery("<a>");
            
            for (Element link : links){
                userAgent.visit(link.getAt("href"));
                Elements contents = userAgent.doc.findEvery("<div class='ui-widget app headlines detail'>");
                
                for (Element content : contents){
                    
                    System.out.println(content.innerHTML());
                }
                
                System.out.println("====================================================\n\n\n\n\n");
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
