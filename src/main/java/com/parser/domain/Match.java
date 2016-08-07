package com.parser.domain;

import com.parser.service.ParserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.util.Map;

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
    private String time; //the start time of the match

    public Match()
    {}

    public Match(Date date, String command1, String command2, int category, int zeroMatches, int zeroFirstTime, String time) {
        this.time = time;
        this.date = date;
        this.command1 = command1;
        this.command2 = command2;
        this.zeroFirstTime = zeroFirstTime;
        this.zeroMatches = zeroMatches;
        this.category = category;
    }
    public Match(Date date, String command1, String command2, int category) {
        this.date = date;
        this.command1 = command1;
        this.command2 = command2;
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
