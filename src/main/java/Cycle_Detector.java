import org.testng.internal.collections.Pair;
import java.util.*;

public class Cycle_Detector {
    private int startNode, endNode;
    private List<List<Pair<Integer, Integer>>> graph;
    private boolean[] stack;
    private Stack<Integer> path;
    private Set<Set<Integer>> cycles;
    private Set<Set<Integer>> forwardPaths;

    public Cycle_Detector(List<List<Pair<Integer, Integer>>> graph, Integer startNode, Integer endNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public List<Set<Integer>> detectCycles(){
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfsCycles(0);
        return new ArrayList<>(cycles);
    }

    public List<Set<Integer>> detectForwardPaths(){
        stack = new boolean[graph.size()];
        path = new Stack<>();
        forwardPaths = new HashSet<>();

        dfsPaths(startNode);
        return new ArrayList<>(forwardPaths);
    }
    private void dfsCycles(int node) {
        stack[node] = true;
        path.add(node);

        for (Pair<Integer, Integer> child : graph.get(node)) {
            if (stack[child.first()])
                addCycle(child.first());
            else
                dfsCycles(child.first());
        }

        path.pop();
        stack[node] = false;
    }
    private void dfsPaths(int node) {
        stack[node] = true;
        path.add(node);

        for (Pair<Integer, Integer> child : graph.get(node)) {
            if (child.first() == endNode)
                addForwardPath();
            else if (!stack[child.first()])
                dfsPaths(child.first());
        }

        path.pop();
        stack[node] = false;
    }

    private void addCycle(Integer child) {
        Stack<Integer> rePath = new Stack<>();
        Set<Integer> cycle = new TreeSet<>();

        while (!Objects.equals(path.peek(), child)){
            cycle.add(path.peek());
            rePath.add(path.pop());
        }

        cycle.add(path.peek());
        rePath.add(path.pop());

        cycles.add(cycle);
        while (!rePath.empty())
            path.add(rePath.pop());
    }

    private void addForwardPath() {
        Stack<Integer> rePath = new Stack<>();
        Set<Integer> forwardPath = new TreeSet<>();

        forwardPath.add(endNode);
        while (!Objects.equals(path.peek(), startNode)){
            forwardPath.add(path.peek());
            rePath.add(path.pop());
        }

        forwardPath.add(path.peek());
        rePath.add(path.pop());

        forwardPaths.add(forwardPath);
        while (!rePath.empty())
            path.add(rePath.pop());
    }
}