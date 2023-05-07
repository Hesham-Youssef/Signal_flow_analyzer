package com.example.backend;

import org.json.JSONArray;
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
        int start = 0, end = nodes-1, offset = 0;
        if(!startNode.equals("NaN")){
            start = Integer.parseInt(startNode);
            offset = start;
            start-= offset;
        }
        if(!endNode.equals("NaN")){
            end = Integer.parseInt(endNode);
            end-=offset;
        }
       /* if(start>end){
            int x = end;
            end = start;
            start = x;
        }*/
        for (int i = 0; i < end-start+1; i++) {
            graph.add(new HashMap<>());
        }

        for (List<Double> doubles : list) {
            if(doubles.get(0).intValue()-offset >= 0 && doubles.get(1).intValue()-offset>=0 && doubles.get(1)-offset <= end && doubles.get(0).intValue()-offset <= end ){
                graph.get(doubles.get(0).intValue()-offset).put(doubles.get(1).intValue()-offset,doubles.get(2));
            }
        }

        Cycle_Detector detector = new Cycle_Detector(graph, start, end);
        JSONObject ans = detector.evaluateSystem();

        JSONArray loopsArray = ans.getJSONArray("Loops");
        JSONArray pathArray = ans.getJSONArray("Paths");

        JSONArray loops = new JSONArray();
        JSONArray paths = new JSONArray();

        for (int i = 0; i < loopsArray.length(); i++) {
            JSONArray innerArray = loopsArray.getJSONArray(i);
            JSONArray array = new JSONArray();
            for (int j = 0; j < innerArray.length(); j++) {
                array.put(innerArray.getInt(j) + offset);
            }
            loops.put(array);
        }

        for (int i = 0; i < pathArray.length(); i++) {
            JSONArray innerArray = pathArray.getJSONArray(i);
            JSONArray array = new JSONArray();
            for (int j = 0; j < innerArray.length(); j++) {
                array.put(innerArray.getInt(j) + offset);
            }
          paths.put(array);
        }

        ans.put("Loops",loops).put("Paths",paths);
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