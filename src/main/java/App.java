import org.testng.internal.collections.Pair;

import java.util.*;

public class App {

    public static void main(String[] args) {
        int n, m, x, y, c;
        Pair<Integer, Integer> p;
        Scanner sc = new Scanner(System.in);
        List<List<Pair<Integer, Integer>>> graph = new ArrayList<>();

        System.out.println("enter number of nodes and edges:");
        n = sc.nextInt();
        m = sc.nextInt();

        for (int i = 0; i < n; i++) {
            graph.add(new LinkedList<>());
        }

        System.out.println("enter edges with weight:");
        for (int i = 0; i < m; i++) {
            x = sc.nextInt();
            y = sc.nextInt();
            c = sc.nextInt();
            p = new Pair<>(y, c);

            graph.get(x).add(p);
        }

        System.out.println("enter starting and ending node:");
        x = sc.nextInt();
        y = sc.nextInt();

        Cycle_Detector mm = new Cycle_Detector(graph, x, y);
        List<Set<Integer>> cycles = mm.detectCycles();
        List<Set<Integer>> forwardPaths = mm.detectForwardPaths();

        for (Set<Integer> cycle : cycles) {
            System.out.println(Arrays.toString(cycle.toArray()));
        }

        System.out.println();
        for (Set<Integer> forwardPath : forwardPaths) {
            System.out.println(Arrays.toString(forwardPath.toArray()));
        }
    }
}
