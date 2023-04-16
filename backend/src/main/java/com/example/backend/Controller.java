package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/")
public class Controller {
    @GetMapping("/flowGraph")
    public JSONObject handleExampleRequest( @RequestParam("nodes") int num) {
        System.out.println(num);
       // System.out.println(list.toString());
        return null;
    }
}
