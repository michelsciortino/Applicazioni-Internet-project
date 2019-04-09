package it.polito.ai.labs.lab2.controllers;

import it.polito.ai.labs.lab2.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainRestController {
    @Autowired
    private LineRepository repository;

    @RequestMapping(value = "/lines", method = RequestMethod.GET)
    public List getAllLines() {

        return repository.findAllNameAndId();
    }
}
