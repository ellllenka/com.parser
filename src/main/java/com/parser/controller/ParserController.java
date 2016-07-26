package com.parser.controller;

import com.parser.domain.Match;
import com.parser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by lena on 24.07.16.
 */
@Controller
public class ParserController {
    @Autowired
    ParserService service;

    @RequestMapping(value = "/match", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> getMatches (Date date) {
        return service.getMatches(date);
    }

    @RequestMapping(value = "/match", method = RequestMethod.PUT)
    @ResponseBody
    public void startParsing() {
        service.startParsing();
    }



}
