package com.parser.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.parser.domain.Match;
import com.parser.domain.ParserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by lena on 21.07.16.
 */
@Service
public class ParserService {
    @Autowired
    ParserRepository repository;
    public static int sumZerosInHT = 0;

    ArrayList<Match> matches = new ArrayList<>();

    private boolean checkClass(HtmlTableRow sr){
        return !sr.getAttribute("class").equals("sjt1") &&
                !sr.getAttribute("class").equals("sjt2") &&
                !sr.getAttribute("class").equals("sjt3") &&
                !sr.getAttribute("class").equals("sjt4");
    }


    private boolean checkYears(HtmlTableRow sr, int parseYears){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        try {
            c.setTime(formatter.parse(sr.getCell(1).getTextContent()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return c.get(Calendar.YEAR) >= (c.get(Calendar.YEAR)-parseYears);
    }



    private HashMap<String, Integer> calcZeroResults(List<HtmlTableRow> scoresRows){
        int zerosInFirstTime = 0, zerosInMatch = 0;

        for (HtmlTableRow srt : scoresRows) {
            if (checkClass(srt)) continue;
            if (checkYears(srt, 3)) {
                if (srt.getCell(3).getTextContent().equals("0-0")) {
                    zerosInMatch++;
                }

                if (srt.getCell(10).getTextContent().equals("0-0")) {
                    zerosInFirstTime++;
                }
            }
        }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("zerosInFirstTime", zerosInFirstTime);
        result.put("zerosInMatch", zerosInMatch);

        return result;
    }


    public void startParsing() {

        final WebClient webClient = createWebClient();


        try {
            HtmlPage page = webClient.getPage("http://live2.7msport.com/"); // заходим на сайт
            List<HtmlAnchor> scores = ((List<HtmlAnchor>) page.getByXPath( "//a[@class='bflk']"));
            for (HtmlAnchor score : scores) {
                String id = score.getHrefAttribute().replace("javascript:ShowDetails_en(","").replace(")","");
                HtmlPage page1 = webClient.getPage("http://analyse.7msport.com/"+id+"/index.shtml");

//                String command1 = ((List<HtmlTableRow>) score.getByXPath("//tr[@id='bh1579889']//td[@id='t_at1579889']")).get(0).getTextContent().replace("]", "").replace("[", "").replaceAll("[0-9]", "");
//                String command2 = ((List<HtmlTableRow>) score.getByXPath("//tr[@id='bh1579889']//td[@id='t_bt1579889']")).get(0).getTextContent().replace("]", "").replace("[", "").replaceAll("[0-9]", "");
                String command1 = "first";
                String command2 = "second";
//                Date date = new Date();
//                Calendar c = Calendar.getInstance();
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
//                score.getByXPath("//td[@class='date']//span"). )
//
//                try {
//                    c.setTime(formatter.parse();
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                }

                Integer sumZero1stTime1 = 0;
                List<HtmlTableRow> scoresRow = ((List<HtmlTableRow>) page1.getByXPath("//Table[@class='qdwj1']//tr"));  // 1-й шаг
                for (HtmlTableRow sr: scoresRow) {
                    if (checkClass(sr)) continue;
                    if (checkYears(sr, 3)){ //исключили матчи старше 3 лет
                        if (!sr.getCell(3).getTextContent().equals("0-0")){ //исключили общ.результат 0-0
                            if (sr.getCell(9).getTextContent().equals("0-0")) {      //выбрали td без класса
                                sumZero1stTime1++;
                            }
                        }
                        if (sumZero1stTime1 > 1){
                            //System.out.println("матч " + sr.getCell(2).getTextContent() + " - " + sr.getCell(4).getTextContent() + " попадает в I категорию");
                        }
                        else if (sumZero1stTime1 <= 1){
                            //System.out.println("матч " + sr.getCell(2).getTextContent() + " - " + sr.getCell(4).getTextContent() + " попадает вo II категорию");
                        }
                    }
                }

                List<HtmlTableRow> scoresRowTotal1 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_A_all']//tr")); // 2-й шаг
                Map<String, Integer> zerosInAllMatches1 = calcZeroResults(scoresRowTotal1);
                HtmlPage pageWithHomeHistory = (HtmlPage) page1.executeJavaScript("showTS('A',1)").getNewPage();
                List<HtmlTableRow> scoresRowHome = ((List<HtmlTableRow>) pageWithHomeHistory.getByXPath("//Table[@id='tbTeamHistory_A_home']//tr"));
                Map<String, Integer> zerosInHomeMatches = calcZeroResults(scoresRowHome);
                if (zerosInAllMatches1.get("zerosInMatch") < 2) {
                    //Match match = new Match(command1, command2, zerosInAllMatches1);
                    if (zerosInHomeMatches.get("zerosInMatch") < 2) {
                        sumZerosInHT = zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime");
                        Match match = new Match(date, command1, command2, zerosInHomeMatches);
                        match.setCategory((zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime")) > 12 ? 1 : 2);
                        if (match.getCategory() == 1) {   // запись в базу данных
                            repository.save(match);
                            return;
                        }
                    }

                }




                List<HtmlTableRow> scoresRowTotal2 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_B_all']//tr")); // 2-й шаг
                Map<String, Integer> zerosInAllMatches2 = calcZeroResults(scoresRowTotal2);
                HtmlPage pageWithAwayHistory = (HtmlPage) page1.executeJavaScript("showTS('B',1)").getNewPage();
                List<HtmlTableRow> scoresRowAway = ((List<HtmlTableRow>) pageWithAwayHistory.getByXPath("//Table[@id='tbTeamHistory_B_away']//tr"));
                Map<String, Integer> zerosInAwayMatches = calcZeroResults(scoresRowAway);
                if (zerosInAllMatches2.get("zerosInMatch") < 2) {
                    //Match match = new Match(command1, command2, zerosInAllMatches2);
                    if (zerosInAwayMatches.get("zerosInMatch") < 2) {
                        sumZerosInHT = zerosInAllMatches2.get("zerosInFirstTime") + zerosInAwayMatches.get("zerosInFirstTime");
                        Match match = new Match(date, command1, command2, zerosInAwayMatches);
                        match.setCategory((zerosInAllMatches2.get("zerosInFirstTime") + zerosInAwayMatches.get("zerosInFirstTime")) > 12 ? 1 : 2);
                        repository.save(match);
                    }
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public List<Match> getMatches (Date date) {
        return repository.findByDate(date);
    }

    private WebClient createWebClient(){

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setCssErrorHandler(new SilentCssErrorHandler());

        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(true);
        webClient.getOptions().setTimeout(10000);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(true);
        webClient.waitForBackgroundJavaScript(5000);

        return webClient;
    }



}
