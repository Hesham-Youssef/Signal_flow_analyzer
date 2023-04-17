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
    public String flowGraphSolver(@RequestParam("edges") List<List<Double>> list, @RequestParam("nodes") int num) {
        List<Map<Integer, Double>> graph = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            graph.add(new HashMap<>());
        }

        for (int i = 0; i < list.size(); i++) {
            graph.get(list.get(i).get(0).intValue()).put(list.get(i).get(1).intValue(), list.get(i).get(2));
        }

        Cycle_Detector detector = new Cycle_Detector(graph, 0, num-1);
        JSONObject ans = detector.evaluateSystem();

        System.out.println(ans);
        return ans.toString();
    }

    @GetMapping("/routhHurwitz")
    public String routhHurwitzSolver(@RequestParam("edges") List<Double> list) {

        Routh_Hurwitz routh = new Routh_Hurwitz(list);
        JSONObject ans = routh.check_stability();
        System.out.println(ans);
        return ans.toString();
    }
}
