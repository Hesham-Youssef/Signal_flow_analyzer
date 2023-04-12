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

    public void evaluateSystem(){
        detectForwardPaths();
        detectCycles();
        masking();
        int count = 0;

        System.out.println("Forward Paths:");
        for (Pair<Set<Integer>, Double> forwardPath : paths) {
            System.out.println("P" + count++ + ": " + Arrays.toString(forwardPath.first().toArray()) + ", gain: " + forwardPath.second());
        }
        System.out.println();

        count = 0;
        System.out.println("Loops:");
        for (Pair<Set<Integer>, Double> cycle : cycles) {
            System.out.println("L" + count++ + ": " + Arrays.toString(cycle.first().toArray()) + ", gain: " + cycle.second());
        }
        System.out.println();

        systemDeltas();
        System.out.println();

        for (int i = 0; i < paths.size(); i++)
            systemGain += forwardPathMask.get(i).second() * pathsDeltas.get(i);
        systemGain /= systemDelta;

        System.out.println("System gain: " + systemGain);
    }

    private void detectCycles(){
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfsCycles(0);
    }

    private void detectForwardPaths(){
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

        while (!Objects.equals(path.peek(), child)){
            cycle.add(path.peek());
            rePath.add(path.pop());
        }

        cycle.add(path.peek());
        rePath.add(path.pop());

        double gain = 1;
        for (int i = 0; i < cycle.size(); i++)
            gain *= graph.get(cycle.get((i+1)%cycle.size())).get(cycle.get(i));

        cycles.add(new Pair<>(new TreeSet<>(cycle), gain));
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    private void addForwardPath() {
        Stack<Integer> rePath = new Stack<>();
        List<Integer> forwardPath = new ArrayList<>();

        forwardPath.add(endNode);
        while (!Objects.equals(path.peek(), startNode)){
            forwardPath.add(path.peek());
            rePath.add(path.pop());
        }

        forwardPath.add(path.peek());
        rePath.add(path.pop());

        double gain = 1;
        for (int i = 0; i < forwardPath.size()-1; i++)
            gain *= graph.get(forwardPath.get(i+1)).get(forwardPath.get(i));

        paths.add(new Pair<>(new TreeSet<>(forwardPath), gain));
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    private void systemDeltas(){
        pathsDeltas = new ArrayList<>();
        systemDelta = delta(0, 0, 1, 1, loopsMask);
        System.out.println("System Delta: " + systemDelta);
        int count = 0;

        for (Pair<Long, Double> forwardPath : forwardPathMask) {
            List<Pair<Long, Double>> freeLoopsMask = new ArrayList<>();
            for (Pair<Long, Double> loop : loopsMask) {
                if ((forwardPath.first() & loop.first()) == 0) {
                    freeLoopsMask.add(loop);
                }
            }
            double pathDelta = delta(0, 0, 1, 1, freeLoopsMask);
            pathsDeltas.add(pathDelta);
            System.out.println("P" + count++ + " Delta: " + pathDelta);
        }
    }

    private double delta(long nodes, int index, int cnt, double currentGain, List<Pair<Long, Double>> loops){
        double totalGain = 0;

        for (int i = index; i < loops.size(); i++){
            if ((nodes & loops.get(i).first()) == 0) {
                double gain = loops.get(i).second() * currentGain;
                if (cnt%2 == 1)
                    totalGain += delta(nodes | loops.get(i).first(),i+1 ,cnt+1, gain, loops) - gain;
                else
                    totalGain += delta(nodes | loops.get(i).first(),i+1 ,cnt+1, gain, loops) + gain;
            }
        }

        return totalGain + (index == 0? 1:0);
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