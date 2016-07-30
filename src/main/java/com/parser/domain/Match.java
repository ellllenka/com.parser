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
    String name1;
    String name2;
    Integer zeroMatches;
    Integer zeroFirstTime;
    Integer category;
    Date date;

    public Match(String name1, String name2, Map<String, Integer> zeroResults) {
        this.name1 = name1;
        this.name2 = name2;
        zeroFirstTime = ParserService.sumZerosInHT;   //zeroResults.get("zerosInFirstTime");  //////////////////zerosInAllMatches1.get("zerosInFirstTime") + zerosInHomeMatches.get("zerosInFirstTime")
        zeroMatches = zeroResults.get("zerosInMatch");
        category = zeroFirstTime > 12 ? 1 : 2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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
}
