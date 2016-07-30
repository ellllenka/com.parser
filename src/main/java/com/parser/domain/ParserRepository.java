package com.parser.domain;


import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lena on 21.07.16.
 */
public interface ParserRepository extends CrudRepository<Match, Integer> {
    ArrayList<Match> findByDate(Date date);
}
