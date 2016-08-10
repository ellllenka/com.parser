package com.parser.controller;

import com.parser.domain.Match;
import com.parser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Controller
public class ParserController {
    @Autowired
    ParserService service;
    @RequestMapping(value = "/matches/{category}", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> getMatches (@PathVariable Integer category) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        return service.getMatches(category, date);
    }

    @RequestMapping(value = "/match", method = RequestMethod.GET)
    @ResponseBody
    public void startParsing() throws IOException, ParseException {
        service.startParsing();
    }

    @RequestMapping(value = "/match", method = RequestMethod.DELETE)
    @ResponseBody
    public void clearDB() throws IOException, ParseException {
        service.clearDB();
    }
}
