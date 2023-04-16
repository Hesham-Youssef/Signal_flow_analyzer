package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {}, allowedHeaders = "*")
@RestController
@RequestMapping("/solver")
public class Controller {
    @GetMapping("/flowGraph")
    public JSONObject handleExampleRequest(@RequestParam("edges") List<List<Integer>> list, @RequestParam("nodes") int num) {
        List<Map<Integer, Double>> graph = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            graph.add(new HashMap<>());
        }

        for (int i = 0; i < list.size(); i++) {
            graph.get(list.get(i).get(0)).put(list.get(i).get(1), Double.valueOf(list.get(i).get(2)));
        }

        Cycle_Detector detector = new Cycle_Detector(graph, 0, num-1);
        JSONObject ans = detector.evaluateSystem();

        System.out.println(ans);
        return ans;
    }
}
