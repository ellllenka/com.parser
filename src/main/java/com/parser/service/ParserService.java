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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lena on 21.07.16.
 */
@Service
public class ParserService {
    @Autowired
    private ParserRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

    public static int sumZerosInHT = 0;

    private java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

    private boolean checkClass(HtmlTableRow sr){
        return !sr.getAttribute("class").equals("sjt1") &&
                !sr.getAttribute("class").equals("sjt2") &&
                !sr.getAttribute("class").equals("sjt3") &&
                !sr.getAttribute("class").equals("sjt4");
    }


    private boolean checkYears(HtmlTableRow sr, int parseYears){
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        try {
            c.setTime(formatter.parse(sr.getCell(1).getTextContent()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        Calendar.getInstance().get(Calendar.YEAR);
        return (currentYear - parseYears) <= c.get(Calendar.YEAR) ;
    }



    private HashMap<String, Integer> calcZeroResults(List<HtmlTableRow> scoresRows){
        int zerosInFirstTime = 0, zerosInMatch = 0;

        for (HtmlTableRow srt : scoresRows) {
            if (checkClass(srt)) continue;
            //if (checkYears(srt, 3)) {
                if (srt.getCell(3).getTextContent().equals("0-0")) {
                    zerosInMatch++;
                }

                if (srt.getCell(10).getTextContent().equals("0-0")) {
                    zerosInFirstTime++;
                }
            //}
        }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("zerosInFirstTime", zerosInFirstTime);
        result.put("zerosInMatch", zerosInMatch);

        return result;
    }


    public void startParsing() {
        final WebClient webClient = createWebClient();

        try {
            logger.info("Start parser");
            HtmlPage page = webClient.getPage("http://live2.7msport.com/"); // заходим на сайт
            List<HtmlTableRow> scores = (List<HtmlTableRow>) page.getByXPath( "//tr[@class='tbg0' or @class='tbg1']");
            for (HtmlTableRow score : scores) {
                String id = score.getAttribute("id");

                String command1 = ((HtmlAnchor) score.getByXPath("td[@class='home']/a").get(0)).getTextContent();
                String command2 = ((HtmlAnchor) score.getByXPath("td[@class='away']/a").get(0)).getTextContent();

                if (checkParsedMatches(currentDate, command1, command2)){
                    logger.info("parse "+command1+" vs "+command2+" skip");
                    continue;
                }

                logger.info("parse "+command1+" vs "+command2+" pars");

                HtmlPage page1 = webClient.getPage("http://analyse.7msport.com/"+id.substring(2)+"/index.shtml");

                Integer sumZero1stTime1 = 0;
                List<HtmlTableRow> scoresRow = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='jfwj']//tr"));  // 1-й шаг
                for (HtmlTableRow sr: scoresRow) {
                    if (checkClass(sr)) continue;
                    if (checkYears(sr, 3)){ //исключили матчи старше 3 лет
                        if (!sr.getCell(3).getTextContent().equals("0-0")){ //исключили общ.результат 0-0
                            if (sr.getCell(9).getTextContent().equals("0-0")) {      //выбрали td без класса
                                sumZero1stTime1++;
                            }
                        }
                    }
                }

                if (sumZero1stTime1 > 1) {   // запись в базу данных
                    Match match = new Match(currentDate, command1, command2, sumZero1stTime1);
                    repository.save(match);
                    continue;
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
                        Match match = new Match(currentDate, command1, command2, zerosInHomeMatches);
                        if (match.getCategory() == 1) {   // запись в базу данных
                            repository.save(match);
                            continue;
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
                        Match match = new Match(currentDate, command1, command2, zerosInAwayMatches);
                        repository.save(match);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            logger.info("end parser");
        }
    }

    public boolean checkParsedMatches(Date date, String command1, String command2){
        List<Match> matchFromSQL = repository.findByDateAndCommand1AndCommand2(date, command1, command2) ;
        return !matchFromSQL.isEmpty();

    }

    public List<Match> getMatches (Integer category, Date date) {
        return repository.findByCategoryAndDate(category, date);
    }

    private WebClient createWebClient(){

        //java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

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
