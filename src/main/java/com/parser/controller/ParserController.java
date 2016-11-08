package com.parser.controller;

import com.parser.domain.Match;
import com.parser.service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
public class ParserController {
    @Autowired
    ParserService service;

    private static final Logger logger = LoggerFactory.getLogger(ParserController.class);

    @RequestMapping(value = "/matches/{category}", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> getMatches (@PathVariable Integer category) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        return service.getMatches(category, date);
    }

    @RequestMapping(value = "/match", method = RequestMethod.PUT)
    @ResponseBody
    public void startParsing(@RequestBody String number) throws IOException, ParseException {
        logger.info("parsing is starting, number = "+number);
        service.startParsing(Integer.parseInt(number));
    }

    @RequestMapping(value = "/match", method = RequestMethod.DELETE)
    @ResponseBody
    public void clearDB() throws IOException, ParseException {
        service.clearDB();
    }
}
