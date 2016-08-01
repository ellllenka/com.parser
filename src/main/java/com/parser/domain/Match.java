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
    private Integer id;
    private String command1;
    private String command2;
    private Integer zeroMatches;
    private Integer zeroFirstTime;
    private Integer category;
    private Date date;

    public Match()
    {}

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommand1() {
        return command1;
    }

    public void setCommand1(String command1) {
        this.command1 = command1;
    }

    public String getCommand2() {
        return command2;
    }

    public void setCommand2(String command2) {
        this.command2 = command2;
    }

    public void setZeroFirstTime(Integer zeroFirstTime) {
        this.zeroFirstTime = zeroFirstTime;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
