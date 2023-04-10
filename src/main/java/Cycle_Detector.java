import org.testng.internal.collections.Pair;
import java.util.*;

public class Cycle_Detector {
    private final int startNode;
    private final int endNode;
    private final List<Map<Integer, Integer>> graph;
    private boolean[] stack;
    private Stack<Integer> path;
    private Set<Pair<Set<Integer>, Integer>> cycles;
    private Set<Pair<Set<Integer>, Integer>> paths;
    List<Pair<Long, Integer>> loopsMask, forwardPathMask;

    public Cycle_Detector(List<Map<Integer, Integer>> graph, Integer startNode, Integer endNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public List<Pair<Set<Integer>, Integer>> detectCycles(){
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfsCycles(0);
        return new ArrayList<>(cycles);
    }

    public List<Pair<Set<Integer>, Integer>> detectForwardPaths(){
        stack = new boolean[graph.size()];
        path = new Stack<>();
        paths = new HashSet<>();

        dfsPaths(startNode);
        return new ArrayList<>(paths);
    }
    private void dfsCycles(int node) {
        stack[node] = true;
        path.add(node);

        for (Map.Entry<Integer, Integer> child : graph.get(node).entrySet()) {
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

        for (Map.Entry<Integer, Integer> child : graph.get(node).entrySet()) {
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

        int gain = 0;
        for (int i = 0; i < cycle.size(); i++)
            gain += graph.get(cycle.get((i+1)%cycle.size())).get(cycle.get(i));

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

        int gain = 0;
        for (int i = 0; i < forwardPath.size()-1; i++)
            gain += graph.get(forwardPath.get(i+1)).get(forwardPath.get(i));

        paths.add(new Pair<>(new TreeSet<>(forwardPath), gain));
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    public void systemDeltas(){
        System.out.println("System Delta: " + delta(0, 0, 1, 1, loopsMask));
        List<Pair<Long, Integer>> freeLoopsMask = new ArrayList<>();
        int count = 0;

        for (Pair<Long, Integer> integerPair : forwardPathMask) {
            for (Pair<Long, Integer> longIntegerPair : loopsMask) {
                if ((integerPair.first() & longIntegerPair.first()) == 0) {
                    freeLoopsMask.add(longIntegerPair);
                }
            }
            System.out.println("Delta P" + count++ + ": " + delta(0, 0, 1, 1, freeLoopsMask));
        }
    }

    public long delta(long nodes, int index, int cnt, long currentGain, List<Pair<Long, Integer>> loops){
        long gain, totalGain = 0;
        for (int i = index; i < loops.size(); i++){
            if ((nodes & loops.get(i).first()) == 0) {
                if (cnt%2 == 1)
                    gain = -loops.get(i).second() * currentGain;
                else
                    gain = loops.get(i).second() * currentGain;
                totalGain += delta(nodes | loops.get(i).first() ,index+1 ,cnt+1, gain, loops) + gain;
            }
        }
        return totalGain + (index == 0? 1:0);
    }

    public void biting() {
        loopsMask = new ArrayList<>();
        for (Pair<Set<Integer>, Integer> cycle : cycles) {
            long mask = 0;
            for (int node : cycle.first())
                mask += 1L << node;
            loopsMask.add(new Pair<>(mask, cycle.second()));
        }

        forwardPathMask = new ArrayList<>();
        for (Pair<Set<Integer>, Integer> forwardPath : paths) {
            long mask = 0;
            for (int node : forwardPath.first())
                mask += 1L << node;
            forwardPathMask.add(new Pair<>(mask, forwardPath.second()));
        }
    }
}