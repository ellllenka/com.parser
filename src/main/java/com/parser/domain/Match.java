package com.parser.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    public Match(Integer id, String name1, String name2, Integer zeroFirstTime, Integer zeroMatches) {
        this.id = id;
        this.name1 = name1;
        this.name2 = name2;
        this.zeroFirstTime = zeroFirstTime;
        this.zeroMatches = zeroMatches;
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
}
