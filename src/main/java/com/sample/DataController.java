package com.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-service, used for testing KafkaDAO.
 */
@Slf4j
@RestController
public class DataController {
    @Autowired
    private KafkaDAO kafkaDAO;

    /**
     * http://localhost:8080/sample/
     */
    @RequestMapping("/")
    public String print() {
        return kafkaDAO.print();
    }

    /**
     * http://localhost:8080/sample/add?key=one&value=1
     */
    @RequestMapping("/add")
    public String addOrUpdate(@RequestParam(name = "key") String key,
                              @RequestParam(name = "value") String value) {
        kafkaDAO.addOrUpdate(key, value);
        return kafkaDAO.print();
    }

    /**
     * http://localhost:8080/sample/find?key=one
     */
    @RequestMapping("/find")
    public String find(@RequestParam(name = "key") String key) {
        return kafkaDAO.find(key);
    }
}
