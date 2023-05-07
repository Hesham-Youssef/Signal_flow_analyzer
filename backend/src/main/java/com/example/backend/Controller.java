package com.example.backend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {}, allowedHeaders = "*")
@RestController
@RequestMapping("/solver")
public class Controller {
    @GetMapping("/flowGraph")
    public String handleExampleRequest(@RequestParam("edges") List<List<Double>> list, @RequestParam("nodes") int nodes, @RequestParam("start") String startNode, @RequestParam("end") String endNode) {
        List<Map<Integer, Double>> graph = new ArrayList<>();
        int start = 0, end = nodes-1;
        if(!startNode.equals("NaN")){
            start = Integer.parseInt(startNode);
        }
        if(!endNode.equals("NaN")){
            end = Integer.parseInt(endNode);
        }
        if(start>end){
            int x = end;
            end = start;
            start = x;
        }
        for (int i = 0; i < end-start+1; i++) {
            graph.add(new HashMap<>());
        }

        for (List<Double> doubles : list) {
            if(doubles.get(0).intValue()-start>=0 && doubles.get(1).intValue()<=end){
                graph.get(doubles.get(0).intValue()-start).put(doubles.get(1).intValue()-start,doubles.get(2));
            }
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