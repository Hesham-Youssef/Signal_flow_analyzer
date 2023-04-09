import org.testng.internal.collections.Pair;
import java.util.*;

public class Cycle_Detector {
    private List<List<Pair<Integer, Integer>>> graph;
    private boolean[] stack;
    private Stack<Integer> path;
    private Set<Set<Integer>> cycles;
    private Set<Set<Integer>> forwardPaths;

    public List<Set<Integer>> detectCycles(List<List<Pair<Integer, Integer>>> g){
        graph = g;
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfs(0);
        return new ArrayList<>(cycles);
    }

    public List<Set<Integer>> forwardPaths(List<List<Pair<Integer, Integer>>> g){
        graph = g;
        stack = new boolean[graph.size()];
        path = new Stack<>();
        cycles = new HashSet<>();

        dfs(0);
        return new ArrayList<>(cycles);
    }
    private void dfs(int node) {
        stack[node] = true;
        path.add(node);

        for (Pair<Integer, Integer> child : graph.get(node)) {
            if (stack[child.first()])
                addCycle(child.first());
            else
                dfs(child.first());
        }

        path.pop();
        stack[node] = false;
    }
    private void fdfs(int node) {
        stack[node] = true;
        path.add(node);

        for (Pair<Integer, Integer> child : graph.get(node)) {
            if (child.first() == graph.size() - 1)
                addForwardPath();
            else
                fdfs(child.first());
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

        forwardPath.add(graph.size() - 1);
        while (!Objects.equals(path.peek(), 0)){
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
