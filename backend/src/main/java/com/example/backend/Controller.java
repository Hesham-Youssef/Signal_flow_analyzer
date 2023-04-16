package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {}, allowedHeaders = "*")
@RestController
@RequestMapping("/solver")
public class Controller {
    @GetMapping("/flowGraph")
    public JSONObject handleExampleRequest(@RequestParam("edges") List<List<Integer>> list, @RequestParam("nodes") int num) {
        System.out.println(num);
        System.out.println(list.toString());
        return null;
    }
}
