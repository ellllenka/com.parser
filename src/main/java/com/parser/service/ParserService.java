package com.parser.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.parser.domain.Match;
import com.parser.domain.ParserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lena on 21.07.16.
 */
@Service
public class ParserService {
    @Autowired
    ParserRepository repository;


    public boolean chooseClass(HtmlTableRow sr){
        return !sr.getAttribute("class").equals("sjt1") &&
                !sr.getAttribute("class").equals("sjt2") &&
                !sr.getAttribute("class").equals("sjt3") &&
                !sr.getAttribute("class").equals("sjt4");
    }
    public boolean chooseYears(HtmlTableRow sr){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        try {
            c.setTime(formatter.parse(sr.getCell(1).asText()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return c.get(Calendar.YEAR) >= (c.get(Calendar.YEAR)-3);
    }

//    public int countZeroResult(List<HtmlTableRow> scoresRow, Integer sumZero, Integer cellNumber) {
//        sumZero = 0;
//        for (HtmlTableRow srt : scoresRow) {
//            if (chooseClass(srt)) continue;
//            if (chooseYears(srt)) {
//                if (srt.getCell(cellNumber).asText().equals("0-0")) {
//                    sumZero++;
//                }
//            }
//        }
//        return sumZero;
//    }

    public void startParsing() {
        final String JS_CALL_HOME = "showTS('A',1)";
        final String JS_CALL_AWAY = "showTS('B',1)";

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

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


        try {
            HtmlPage page = webClient.getPage("http://live2.7msport.com/"); // заходим на сайт
            List<HtmlAnchor> scores = ((List<HtmlAnchor>) page.getByXPath( "//a[@class='bflk']"));
            for (HtmlAnchor score : scores) {
                String id = score.getHrefAttribute().replace("javascript:ShowDetails_en(","").replace(")","");
                HtmlPage page1 = webClient.getPage("http://analyse.7msport.com/"+id+"/index.shtml");

                Integer sumZero1stTime1 = 0;
                List<HtmlTableRow> scoresRow = ((List<HtmlTableRow>) page1.getByXPath("//Table[@class='qdwj1']//tr"));  // 1-й шаг
                for (HtmlTableRow sr: scoresRow) {
                    if (chooseClass(sr)) continue;
                    if (chooseYears(sr)){ //исключили матчи старше 3 лет
                        if (!sr.getCell(3).asText().equals("0-0")){ //исключили общ.результат 0-0
                            if (sr.getCell(9).asText().equals("0-0")) {      //выбрали td без класса
                                sumZero1stTime1++;
                            }
                            if (sumZero1stTime1 > 1){
                                System.out.println("матч " + sr.getCell(2).asText() + " - " + sr.getCell(4).asText() + " попадает в I категорию");
                            }
                            else if (sumZero1stTime1 <= 1){
                                System.out.println("матч " + sr.getCell(2).asText() + " - " + sr.getCell(4).asText() + " попадает вo II категорию");
                            }

                        }
                    }
                }
                Integer sumZeroTotal1 = 0;
                Integer sumZeroHome = 0;
                Integer sumZero1stTimeTotal1 = 0;
                Integer sumZero1stTimeHome = 0;
                List<HtmlTableRow> scoresRowTotal1 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_A_all']//tr")); // 2-й шаг
                HtmlPage pageWithHomeHistory = (HtmlPage) page1.executeJavaScript(JS_CALL_HOME).getNewPage();
                List<HtmlTableRow> scoresRowHome = ((List<HtmlTableRow>) pageWithHomeHistory.getByXPath("//Table[@id='tbTeamHistory_A_home']//tr")); //????????????? only 2 components
//                sumZeroTotal1 = countZeroResult(scoresRowTotal1, sumZeroTotal1, 3);
//                sumZeroHome = countZeroResult(scoresRowHome, sumZeroHome, 3);
                for (HtmlTableRow srt : scoresRowTotal1) {
                    if (chooseClass(srt)) continue;
                    if (chooseYears(srt)) {
                        if (srt.getCell(3).asText().equals("0-0")) {
                            sumZeroTotal1++;
                        }
                    }
                }
                for (HtmlTableRow srh : scoresRowHome) {
                    if (chooseClass(srh)) continue;
                    if (chooseYears(srh)) {
                        if (srh.getCell(3).asText().equals("0-0")) {
                            sumZeroHome++;
                        }
                    }
                }


                if (sumZeroTotal1 < 2 && sumZeroHome < 2) {
//                    sumZero1stTimeTotal1 = countZeroResult(scoresRowTotal1, sumZero1stTimeTotal1, 10);
//                    sumZero1stTimeHome = countZeroResult(scoresRowHome, sumZero1stTimeHome, 10);
                    for (HtmlTableRow srt : scoresRowTotal1) {
                        if (chooseClass(srt)) continue;
                        if (chooseYears(srt)) {
                            if (srt.getCell(10).asText().equals("0-0")) {
                                sumZero1stTimeTotal1++;
                            }
                        }
                    }
                    for (HtmlTableRow srh : scoresRowHome) {
                        if (chooseClass(srh)) continue;
                        if (chooseYears(srh)) {
                            if (srh.getCell(10).asText().equals("0-0")) {
                                sumZero1stTimeHome++;
                            }
                        }
                    }

                    if (sumZero1stTimeTotal1 + sumZero1stTimeHome > 12){
                        for (HtmlTableRow srt : scoresRowTotal1) {
                            System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает в I категорию");
                        }
                    }
                    if (sumZero1stTimeTotal1 + sumZero1stTimeHome <= 12){
                        for (HtmlTableRow srt : scoresRowTotal1) {
                            System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает вo II категорию");
                        }
                    }
                }



                Integer sumZeroTotal2 = 0;
                Integer sumZeroAway = 0;
                Integer sumZero1stTimeTotal2 = 0;
                Integer sumZero1stTimeAway = 0;
                List<HtmlTableRow> scoresRowTotal2 = ((List<HtmlTableRow>) page1.getByXPath("//Table[@id='tbTeamHistory_B_all']//tr")); // 2-й шаг
                HtmlPage pageWithAwayHistory = (HtmlPage) page1.executeJavaScript(JS_CALL_AWAY).getNewPage();
                List<HtmlTableRow> scoresRowAway = ((List<HtmlTableRow>) pageWithAwayHistory.getByXPath("//Table[@id='tbTeamHistory_B_away']//tr")); //????????????? only 2 components
//                sumZeroTotal2 = countZeroResult(scoresRowTotal2, sumZeroTotal2, 3);
//                sumZeroAway = countZeroResult(scoresRowAway, sumZeroAway, 3);
                for (HtmlTableRow srt : scoresRowTotal2) {
                    if (chooseClass(srt)) continue;
                    if (chooseYears(srt)) {
                        if (srt.getCell(3).asText().equals("0-0")) {
                            sumZeroTotal2++;
                        }
                    }
                }
                for (HtmlTableRow sra : scoresRowAway) {
                    if (chooseClass(sra)) continue;
                    if (chooseYears(sra)) {
                        if (sra.getCell(3).asText().equals("0-0")) {
                            sumZeroAway++;
                        }
                    }
                }

                if (sumZeroTotal2 < 2 && sumZeroAway < 2){
//                    sumZero1stTimeTotal2 = countZeroResult(scoresRowTotal2, sumZero1stTimeTotal2, 10);
//                    sumZero1stTimeAway = countZeroResult(scoresRowAway, sumZero1stTimeAway, 10);
                    for (HtmlTableRow srt : scoresRowTotal2) {
                        if (chooseClass(srt)) continue;
                        if (chooseYears(srt)) {
                            if (srt.getCell(10).asText().equals("0-0")) {
                                sumZero1stTimeTotal2++;
                            }
                        }
                    }
                    for (HtmlTableRow sra : scoresRowAway) {
                        if (chooseClass(sra)) continue;
                        if (chooseYears(sra)) {
                            if (sra.getCell(10).asText().equals("0-0")) {
                                sumZero1stTimeAway++;
                            }
                        }
                    }

                    if (sumZero1stTimeTotal2 + sumZero1stTimeAway > 12){
                        for (HtmlTableRow srt : scoresRowTotal2) {
                            System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает в I категорию");
                        }
                    }
                    if (sumZero1stTimeTotal2 + sumZero1stTimeAway <= 12){
                        for (HtmlTableRow srt : scoresRowTotal2) {
                            System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает вo II категорию");
                        }
                    }
                }








            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Match getMatch (Date date){
        return repository.findOne(date);
    }
}
