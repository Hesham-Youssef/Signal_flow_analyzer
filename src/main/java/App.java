import org.testng.internal.collections.Pair;
import java.util.*;

public class App {

    public static void main(String[] args) {
        int n, m, x, y, c;
        Scanner sc = new Scanner(System.in);
        List<Map<Integer, Integer>> graph = new ArrayList<>();

        System.out.println("enter number of nodes and edges:");
        n = sc.nextInt();
        m = sc.nextInt();

        for (int i = 0; i < n; i++) {
            graph.add(new HashMap<>());
        }

        System.out.println("enter edges with weight:");
        for (int i = 0; i < m; i++) {
            x = sc.nextInt();
            y = sc.nextInt();
            c = sc.nextInt();
            graph.get(x).put(y, c);
        }

        System.out.println("enter starting and ending node:");
        x = sc.nextInt();
        y = sc.nextInt();

        Cycle_Detector mm = new Cycle_Detector(graph, x, y);
        List<Pair<Set<Integer>, Integer>> cycles = mm.detectCycles();
        List<Pair<Set<Integer>, Integer>> forwardPaths = mm.detectForwardPaths();
        mm.biting();

        System.out.println("Loops:");
        for (Pair<Set<Integer>, Integer> cycle : cycles) {
            System.out.println(Arrays.toString(cycle.first().toArray()));
        }

        System.out.println();
        System.out.println("Forward Paths:");
        for (Pair<Set<Integer>, Integer> forwardPath : forwardPaths) {
            System.out.println(Arrays.toString(forwardPath.first().toArray()));
        }

        System.out.println();
        mm.systemDeltas();
    }
}
