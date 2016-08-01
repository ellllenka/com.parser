package com.parser.domain;

import com.parser.service.ParserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.util.Map;

/**
 * Created by lena on 30.06.16.
 */
@Entity
public class Match {
    @GeneratedValue
    @Id
    Integer id;
    String command1;
    String command2;
    Integer zeroMatches;
    Integer zeroFirstTime;
    Integer category;
    Date date;

    public Match(){};

    public Match(Date date, String command1, String command2, Map<String, Integer> zeroResults) {
        this.date = date;
        this.command1 = command1;
        this.command2 = command2;
        zeroFirstTime = ParserService.sumZerosInHT;   //zeroResults.get("zerosInFirstTime");  //////////////////zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime")
        zeroMatches = zeroResults.get("zerosInMatch");
        category = zeroFirstTime > 12 ? 1 : 2;
    }
    public Match(Date date, String command1, String command2, int zeroResult) {
        this.date = date;
        this.command1 = command1;
        this.command2 = command2;
        category = zeroResult > 1 ? 1 : 2;
    }

    public Match(Date date, String command1, String command2){
        this.date = date;
        this.command1 = command1;
        this.command2 = command2;
    }


    public String getСommand1() {
        return command1;
    }

    public void setСommand1(String command1) {
        this.command1 = command1;
    }

    public String getСommand2() {
        return command2;
    }

    public void setСommand2(String name2) {
        this.command2 = name2;
    }

    public Integer getZeroMatches() {
        return zeroMatches;
    }

    public void setZeroMatches(Integer zeroMatches) {
        this.zeroMatches = zeroMatches;
    }

    public Integer getZeroFirstTime() {
        return zeroFirstTime;
    }

    public Integer getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}
