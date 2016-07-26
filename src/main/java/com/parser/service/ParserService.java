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

    private boolean chooseClass(HtmlTableRow sr){
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
                    if (checkYears(sr, 3)){ //исключили матчи старше 3 лет
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
                    if (checkYears(srt, 3)) {
                        if (srt.getCell(3).getTextContent().equals("0-0")) {
                            sumZeroTotal1++;
                        }
                        if (sumZeroTotal1 < 2) {
                            if (srt.getCell(10).getTextContent().equals("0-0")) {
                                sumZero1stTimeTotal1++;
                            }
                            if (sumZero1stTimeTotal1 > 12) {
                                System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает в I категорию");
                            } else if (sumZero1stTimeTotal1 <= 12) {
                                System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает вo II категорию");
                            }
                        }
                    }
                }
                for (HtmlTableRow srh : scoresRowHome) {
                    if (chooseClass(srh)) continue;
                    if (checkYears(srh, 3)) {
                        if (srh.getCell(3).getTextContent().equals("0-0")) {
                            sumZeroHome++;
                        }
                        if (sumZeroHome < 2) {
                            if (srh.getCell(10).getTextContent().equals("0-0")) {
                                sumZero1stTimeHome++;
                            }
                            if (sumZero1stTimeHome > 12) {
                                System.out.println("матч " + srh.getCell(2).asText() + " - " + srh.getCell(4).asText() + " попадает в I категорию");
                            } else if (sumZero1stTimeHome <= 12) {
                                System.out.println("матч " + srh.getCell(2).asText() + " - " + srh.getCell(4).asText() + " попадает вo II категорию");
                            }
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
                for (HtmlTableRow srt : scoresRowTotal2) {
                    if (chooseClass(srt)) continue;
                    if (checkYears(srt, 3)) {
                        if (srt.getCell(3).getTextContent().equals("0-0")) {
                            sumZeroTotal2++;
                        }
                        if (sumZeroTotal2 < 2) {
                            if (srt.getCell(10).getTextContent().equals("0-0")) {
                                sumZero1stTimeTotal2++;
                            }
                            if (sumZero1stTimeTotal2 > 12) {
                                System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает в I категорию");
                            } else if (sumZero1stTimeTotal2 <= 12) {
                                System.out.println("матч " + srt.getCell(2).asText() + " - " + srt.getCell(4).asText() + " попадает вo II категорию");
                            }
                        }
                    }
                }
                for (HtmlTableRow sra : scoresRowAway) {
                    if (chooseClass(sra)) continue;
                    if (checkYears(sra, 3)) {
                        if (sra.getCell(3).getTextContent().equals("0-0")) {
                            sumZeroAway++;
                        }
                        if (sumZeroAway < 2) {
                            if (sra.getCell(10).getTextContent().equals("0-0")) {
                                sumZero1stTimeAway++;
                            }
                            if (sumZero1stTimeAway > 12) {
                                System.out.println("матч " + sra.getCell(2).asText() + " - " + sra.getCell(4).asText() + " попадает в I категорию");
                            } else if (sumZero1stTimeAway <= 12) {
                                System.out.println("матч " + sra.getCell(2).asText() + " - " + sra.getCell(4).asText() + " попадает вo II категорию");
                            }
                        }

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
}
