import org.testng.internal.collections.Pair;

import java.util.*;

public class App {

    public static void main(String[] args) {
        int n, m, x, y, c;
        Pair<Integer, Integer> p;
        Scanner sc = new Scanner(System.in);
        List<List<Pair<Integer, Integer>>> graph = new ArrayList<>();

        n = sc.nextInt();
        m = sc.nextInt();

        for (int i = 0; i < n; i++) {
            graph.add(new LinkedList<>());
        }

        for (int i = 0; i < m; i++) {
            x = sc.nextInt();
            y = sc.nextInt();
            c = sc.nextInt();
            p = new Pair<>(y, c);

            graph.get(x).add(p);
        }

        Cycle_Detector mm = new Cycle_Detector();
        List<Set<Integer>> cycles = mm.detectCycles(graph);

        for (Set<Integer> cycle : cycles) {
            System.out.println(Arrays.toString(cycle.toArray()));
        }
    }
}
