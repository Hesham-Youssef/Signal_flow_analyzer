package com.example.backend;

import org.json.JSONObject;
import org.testng.internal.collections.Pair;

import java.util.*;

public class Cycle_Detector {
    private final int startNode;
    private final int endNode;
    private double systemGain = 0;
    private double systemDelta = 1;
    private List<Double> pathsDeltas;
    private final List<Map<Integer, Double>> graph;
    private boolean[] stack;
    private Stack<Integer> path;
    private Set<Pair<Set<Integer>, Double>> cycles;
    private Set<Pair<Set<Integer>, Double>> paths;
    List<Pair<Long, Double>> loopsMask, forwardPathMask;

    public double getSystemGain() {
        return systemGain;
    }

    public Cycle_Detector(List<Map<Integer, Double>> graph, Integer startNode, Integer endNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public JSONObject evaluateSystem() {
        detectForwardPaths();
        detectCycles();
        masking();
        systemDeltas();

        JSONObject jsonAns = new JSONObject();

        ArrayList<Object[]> Paths = new ArrayList<>();
        ArrayList<Double> pathsGain = new ArrayList<>();
        for (Pair<Set<Integer>, Double> forwardPath : paths) {
            Paths.add(forwardPath.first().toArray());
            pathsGain.add(forwardPath.second());
        }
        jsonAns.put("Paths",Paths).put("pathsGain",pathsGain);

        ArrayList<Object[]> loops = new ArrayList<>();
        ArrayList<Double> loopsGain = new ArrayList<>();

        for (Pair<Set<Integer>, Double> cycle : cycles) {
            loops.add(cycle.first().toArray());
            loopsGain.add(cycle.second());
        }
        jsonAns.put("Loops",loops).put("loopsGain",loopsGain);
        jsonAns.put("SystemDelta",systemDelta).put("Deltas",pathsDeltas);

        for (int i = 0; i < paths.size(); i++)
            systemGain += forwardPathMask.get(i).second() * pathsDeltas.get(i);
        systemGain /= systemDelta;

        jsonAns.put("SystemGain" ,systemGain);
        return jsonAns;
    }

    private void detectCycles() {
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfsCycles(0);
    }

    private void detectForwardPaths() {
        stack = new boolean[graph.size()];
        path = new Stack<>();
        paths = new HashSet<>();

        dfsPaths(startNode);
    }

    private void dfsCycles(int node) {
        stack[node] = true;
        path.add(node);

        for (Map.Entry<Integer, Double> child : graph.get(node).entrySet()) {
            if (stack[child.getKey()])
                addCycle(child.getKey());
            else
                dfsCycles(child.getKey());
        }

        path.pop();
        stack[node] = false;
    }

    private void dfsPaths(int node) {
        stack[node] = true;
        path.add(node);

        for (Map.Entry<Integer, Double> child : graph.get(node).entrySet()) {
            if (child.getKey() == endNode)
                addForwardPath();
            else if (!stack[child.getKey()])
                dfsPaths(child.getKey());
        }

        path.pop();
        stack[node] = false;
    }

    private void addCycle(Integer child) {
        Stack<Integer> rePath = new Stack<>();
        List<Integer> cycle = new ArrayList<>();

        while (!Objects.equals(path.peek(), child)) {
            cycle.add(path.peek());
            rePath.add(path.pop());
        }

        cycle.add(path.peek());
        rePath.add(path.pop());

        double gain = 1;
        for (int i = 0; i < cycle.size(); i++)
            gain *= graph.get(cycle.get((i + 1) % cycle.size())).get(cycle.get(i));

        cycles.add(new Pair<>(new TreeSet<>(cycle), gain));
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    private void addForwardPath() {
        Stack<Integer> rePath = new Stack<>();
        List<Integer> forwardPath = new ArrayList<>();

        forwardPath.add(endNode);
        while (!Objects.equals(path.peek(), startNode)) {
            forwardPath.add(path.peek());
            rePath.add(path.pop());
        }

        forwardPath.add(path.peek());
        rePath.add(path.pop());

        double gain = 1;
        for (int i = 0; i < forwardPath.size() - 1; i++)
            gain *= graph.get(forwardPath.get(i + 1)).get(forwardPath.get(i));

        paths.add(new Pair<>(new TreeSet<>(forwardPath), gain));
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    private void systemDeltas() {
        pathsDeltas = new ArrayList<>();
        systemDelta = delta(0, 0, 1, 1, loopsMask);

        for (Pair<Long, Double> forwardPath : forwardPathMask) {
            List<Pair<Long, Double>> freeLoopsMask = new ArrayList<>();
            for (Pair<Long, Double> loop : loopsMask) {
                if ((forwardPath.first() & loop.first()) == 0) {
                    freeLoopsMask.add(loop);
                }
            }
            double pathDelta = delta(0, 0, 1, 1, freeLoopsMask);
            pathsDeltas.add(pathDelta);
        }
    }

    private double delta(long nodes, int index, int cnt, double currentGain, List<Pair<Long, Double>> loops) {
        double totalGain = 0;
        for (int i = index; i < loops.size(); i++) {
            if ((nodes & loops.get(i).first()) == 0) {
                double gain = loops.get(i).second() * currentGain;
                if (cnt % 2 == 1)
                    totalGain += delta(nodes | loops.get(i).first(), i + 1, cnt + 1, gain, loops) - gain;
                else
                    totalGain += delta(nodes | loops.get(i).first(), i + 1, cnt + 1, gain, loops) + gain;
            }
        }

        return totalGain + (index == 0 ? 1 : 0);
    }

    private void masking() {
        loopsMask = new ArrayList<>();
        for (Pair<Set<Integer>, Double> cycle : cycles) {
            long mask = 0;
            for (int node : cycle.first())
                mask += 1L << node;
            loopsMask.add(new Pair<>(mask, cycle.second()));
        }

        forwardPathMask = new ArrayList<>();
        for (Pair<Set<Integer>, Double> forwardPath : paths) {
            long mask = 0;
            for (int node : forwardPath.first())
                mask += 1L << node;
            forwardPathMask.add(new Pair<>(mask, forwardPath.second()));
        }
    }
}