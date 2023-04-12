import java.util.*;

public class App {

    public static void main(String[] args) {
        int n, m, x, y;
        double c;
        Scanner sc = new Scanner(System.in);
        List<Map<Integer, Double>> graph = new ArrayList<>();

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
            c = sc.nextDouble();
            graph.get(x).put(y, c);
        }

        System.out.println("enter starting and ending node:");
        x = sc.nextInt();
        y = sc.nextInt();
        if(x == 0){
            Cycle_Detector mm = new Cycle_Detector(graph, x, y);
            mm.evaluateSystem();
        }else{
            Cycle_Detector c1 = new Cycle_Detector(graph, 0, x);
            c1.evaluateSystem();
            System.out.println("###########################################");

            Cycle_Detector c2 = new Cycle_Detector(graph, 0, y);
            c2.evaluateSystem();
            System.out.println("###########################################");
            System.out.printf("System Gain: %f",c2.getSystemGain()/c1.getSystemGain());
        }
    }
}
