package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class Controller {
    @GetMapping("/flowGraph")
    public JSONObject handleExampleRequest(@RequestParam("num") int num, @RequestParam("list") List<List<Integer>> list) {
        System.out.println(num);
        System.out.println(list.toString());
        return null;
    }
}
