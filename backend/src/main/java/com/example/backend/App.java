package com.example.backend;

import org.json.JSONObject;

import java.util.*;

public class App {

    public static void main(String[] args) {

        int choose ;
        Scanner sc = new Scanner(System.in);
        System.out.println("Signal flow graph Enter 0");
        System.out.println("Routh Hurwitz Enter 1");
        choose =sc.nextInt();
        if(choose == 0){
            int n, m, x, y;
            double c;
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
                JSONObject a = mm.evaluateSystem();
                System.out.println(a);
            }else{
                Cycle_Detector c1 = new Cycle_Detector(graph, 0, x);
                c1.evaluateSystem();
                System.out.println("###########################################");

                Cycle_Detector c2 = new Cycle_Detector(graph, 0, y);
                c2.evaluateSystem();
                System.out.println("###########################################");
                System.out.printf("System Gain: %f",c2.getSystemGain()/c1.getSystemGain());
            }
        }else if(choose == 1){
            System.out.println("###########################################");
            System.out.println("System order:" );
            int system_order = sc.nextInt();
            List<Double> input = new ArrayList<>();
            for (int i = 0; i < system_order + 1; i++) {
                System.out.println("coeff #" + (system_order-i)+" :" );
                double coeff = sc.nextDouble();
                input.add(coeff);
            }
            List<Double> c = new ArrayList<>(Arrays.asList(1.0, 0.0 , 3.0, 0.0, 2.0 ,5.0));
            Routh_Hurwitz r = new Routh_Hurwitz(input);
            r.check_stability();
        }


    }
}
