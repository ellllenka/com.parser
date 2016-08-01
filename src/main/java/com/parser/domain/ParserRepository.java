package com.parser.domain;


import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public interface ParserRepository extends CrudRepository<Match, Integer> {

    ArrayList<Match> findByCategoryAndDate(Integer category, Date date);

    List<Match> findByDateAndCommand1AndCommand2(Date date, String command1, String command2);
}
