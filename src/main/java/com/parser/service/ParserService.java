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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@Service
public class ParserService {
    @Autowired
    private ParserRepository repository;

    private static boolean isRunning = false;

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

    private java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
    private int totalNumber;
    private int currentNumber;

    private boolean checkClass(HtmlTableRow sr) {
        return !sr.getAttribute("class").equals("sjt1") &&
                !sr.getAttribute("class").equals("sjt2") &&
                !sr.getAttribute("class").equals("sjt3") &&
                !sr.getAttribute("class").equals("sjt4");
    }


    private boolean checkYears(HtmlTableRow sr, int parseYears) throws ParseException {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");

        c.setTime(formatter.parse(sr.getCell(1).getTextContent()));


        Calendar.getInstance().get(Calendar.YEAR);
        return (currentYear - parseYears) <= c.get(Calendar.YEAR);
    }


    private HashMap<String, Integer> calcZeroResults(List<HtmlTableRow> scoresRows) {
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

    @Async
    public Future<Void> startParsing() throws ParseException {
        if (isRunning)
            return null;

        isRunning = true;

        while (totalNumber < currentNumber || currentNumber == 0) {
            try {
                parse();
            } catch (IOException e) {
                logger.error("Error when parsing", e);
            } finally {
                isRunning = false;
                logger.info("end parser");
            }


        }

        return null;
    }

    private void parse() throws IOException, ParseException {
        final WebClient webClient = createWebClient();

        logger.info("Start parser");
        HtmlPage page = webClient.getPage("http://live2.7msport.com/"); // заходим на сайт
        List<HtmlTableRow> scores = (List<HtmlTableRow>) page.getByXPath("//tr[@class='tbg0' or @class='tbg1']");
        totalNumber = scores.size();
        logger.info("Total number of matches is " + scores.size());
        for (HtmlTableRow score : scores) {
            String id = score.getAttribute("id");

            //if (!id.substring(2).equals("1623419")) continue; //TODO DEBUG

            String command1 = ((HtmlAnchor) score.getByXPath("td[@class='home']/a").get(0)).getTextContent();
            String command2 = ((HtmlAnchor) score.getByXPath("td[@class='away']/a").get(0)).getTextContent();

            currentNumber++;
            if (checkParsedMatches(currentDate, command1, command2)) {
                logger.info("Skip \"" + command1 + "\" vs \"" + command2 + "\"");
                continue;
            }

            logger.info("Parse \"" + command1 + "\" vs \"" + command2 + "\" number " + currentNumber + " of " + scores.size());

            HtmlPage page1 = webClient.getPage("http://analyse.7msport.com/" + id.substring(2) + "/index.shtml");

            int sumZero1stTime1 = 0;
            int sumZeroMatch = 0;
            List<HtmlTableRow> scoresRow = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='jfwj']//tr"));  // 1-й шаг
            for (HtmlTableRow sr : scoresRow) {
                if (checkClass(sr)) continue;
                if (checkYears(sr, 3)) { //исключили матчи старше 3 лет
                    if (!sr.getCell(3).getTextContent().equals("0-0")) {
                        sumZeroMatch++;
                        break;
                    }
                    if (sr.getCell(9).getTextContent().equals("0-0")) {
                        sumZero1stTime1++;
                    }
                }
            }

            if (sumZeroMatch > 0) {
                repository.save(new Match(currentDate, command1, command2, 0));
                continue;
            }

            int category = sumZero1stTime1 > 1 ? 1 : 2;

            List<HtmlTableRow> scoresRowTotal1 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_A_all']//tr")); // 2-й шаг
            Map<String, Integer> zerosInAllMatches1 = calcZeroResults(scoresRowTotal1);

            HtmlPage pageWithHomeHistory = (HtmlPage) page1.executeJavaScript("showTS('A',1)").getNewPage();
            List<HtmlTableRow> scoresRowHome = ((List<HtmlTableRow>) pageWithHomeHistory.getByXPath("//Table[@id='tbTeamHistory_A_home']//tr"));
            Map<String, Integer> zerosInHomeMatches = calcZeroResults(scoresRowHome);

            if (zerosInHomeMatches.get("zerosInMatch") + zerosInAllMatches1.get("zerosInMatch") > 2) {
                repository.save(new Match(currentDate, command1, command2, 0));
                continue;
            }

            int sumZeros = zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime");

            int curCategory = sumZeros > 12 ? 1 : 2;
            category = Math.min(category, curCategory);

            List<HtmlTableRow> scoresRowTotal2 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_B_all']//tr")); // 2-й шаг
            Map<String, Integer> zerosInAllMatches2 = calcZeroResults(scoresRowTotal2);

            HtmlPage pageWithAwayHistory = (HtmlPage) page1.executeJavaScript("showTS('B',1)").getNewPage();
            List<HtmlTableRow> scoresRowAway = ((List<HtmlTableRow>) pageWithAwayHistory.getByXPath("//Table[@id='tbTeamHistory_B_away']//tr"));
            Map<String, Integer> zerosInAwayMatches = calcZeroResults(scoresRowAway);
            if (zerosInAwayMatches.get("zerosInMatch") + zerosInAllMatches2.get("zerosInMatch") > 2) {
                repository.save(new Match(currentDate, command1, command2, 0));
                continue;
            }

            sumZeros = zerosInAllMatches2.get("zerosInFirstTime") + zerosInAwayMatches.get("zerosInFirstTime");

            int totalZeros = zerosInAllMatches1.get("zerosInMatch") + zerosInHomeMatches.get("zerosInMatch") +
                    zerosInAllMatches2.get("zerosInMatch") + zerosInAwayMatches.get("zerosInMatch");

            int totaZerosHT = zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime") +
                    zerosInAllMatches2.get("zerosInFirstTime") + zerosInAwayMatches.get("zerosInFirstTime") + sumZero1stTime1;

            curCategory = sumZeros > 12 ? 1 : 2;
            category = Math.min(category, curCategory);

            Match match = new Match(currentDate, command1, command2, category, totalZeros, totaZerosHT);
            repository.save(match);
        }
    }


    private boolean checkParsedMatches(Date date, String command1, String command2) {
        List<Match> matchFromSQL = repository.findByDateAndCommand1AndCommand2(date, command1, command2);
        return !matchFromSQL.isEmpty();

    }

    public List<Match> getMatches(Integer category, Date date) {
        return repository.findByCategoryAndDate(category, date);
    }

    private WebClient createWebClient() {

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
