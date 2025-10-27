package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.shell.ConceptREPL;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        logger.info("Starting Concept REPL...");

        ConceptREPL repl = new ConceptREPL();
        repl.start();
    }
}