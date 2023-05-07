package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {}, allowedHeaders = "*")
@RestController
@RequestMapping("/solver")
public class Controller {
    @GetMapping("/flowGraph")
    public String handleExampleRequest(@RequestParam("edges") List<List<Double>> list, @RequestParam("nodes") int nodes, @RequestParam("start") int start, @RequestParam("end") int end) {
        List<Map<Integer, Double>> graph = new ArrayList<>();
        for (int i = 0; i < nodes; i++) {
            graph.add(new HashMap<>());
        }

        for (List<Double> doubles : list) {
            graph.get(doubles.get(0).intValue()).put(doubles.get(1).intValue(), doubles.get(2));
        }

        Cycle_Detector detector = new Cycle_Detector(graph, start, end);
        JSONObject ans = detector.evaluateSystem();

        System.out.println(ans);
        return ans.toString();
    }

    @GetMapping("/routhHurwitz")
    public String routhHurwitzSolver(@RequestParam("coefficient") List<Double> list) {

        Routh_Hurwitz routh = new Routh_Hurwitz(list);
        JSONObject ans = routh.check_stability();
        System.out.println(ans);
        return ans.toString();
    }
}