package com.example.backend;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Routh_Hurwitz{
    List<Double> coefficients;
    double[][] table;
    int system_order;
    int array_length;
    int rows;
    int cols;
    public Routh_Hurwitz (List<Double> coefficients){
        this.coefficients = coefficients;
    }
    private List<List<Double>> creatTable(){
        List<List<Double>> Table = new ArrayList<>();
        System.out.println(rows+"  " +cols);
        for(int i = 0 ; i < rows ; i++){
            List<Double> row = new ArrayList<>();
            for(int j = 0 ; j < cols ; j++){
                row.add(table[i][j]);
            }
            Table.add(row);
        }
        return Table;
    }
    private void initialize(){
        array_length = coefficients.toArray().length;   // array_length == system_order + 1
        system_order =array_length - 1;
        if(array_length % 2 == 1){
            coefficients.add(0.0);
        }
        rows = array_length;
        cols = (array_length+1)/2;
        table = new double[rows][cols];

        for(int i = 0 ; i < rows ; i++){
            for(int j = 0 ; j < cols ; j++){
                table[i][j] = 0;
            }
        }

        int len = coefficients.toArray().length;

        for(int i = 0; i < 2 ; i++){
            for (int j = i; j < len; j += 2) {
                table[i][j/2] = coefficients.get(j);
            }
        }

    }
    private boolean check_zero_row(int index){
        boolean zero_row = true;
        for (int i = 0; i < cols && zero_row; i++) {
            if(table[index][i] != 0){
                zero_row =false;
            }
        }
        return zero_row;
    }
    private void replacement_for_zero_row(int index){  // (temp-(index+1)) is the exponent of the row like S^3 in routh table
        int temp = system_order;
        System.out.println(temp);
        for (int i = 0; i < cols && (temp-(index+1)) >= 0; i++) {
            table[index+2][i] = table[index+1][i] * (temp-(index+1));
            temp -= 2;
        }
    }
    private int num_roots_in_the_RHS() {
        int counter = 0;
        for (int i = 0; i < rows - 1; i++) {
            if(!((table[i][0] < 0 && table[i+1][0] < 0) ||
                    (table[i][0] > 0 && table[i+1][0] > 0) ||
                    table[i][0] == 0 || table[i+1][0] == 0)){
                counter++;
            }
        }
        return counter;
    }
    private boolean first_col_contain_zero(){
        boolean zero = false;
        for(int i = 0; i < rows && !zero; i++){
            if(table[i][0] == 0){
                zero = true;
            }
        }
        return zero;
    }
    private void evaluate_system(){
        for (int i = 0; i < rows - 2; i++) {
            double s1 = table[i][0], s2 = table[i+1][0];

            for (int j = 0; j < cols - 1; j++) {
                System.out.println("s3="+table[i][j+1] +" s4="+table[i+1][j+1]);
                if(s2 == 0){  // substitute s2 == 0.001 which is a small value near to zero
                    table[i+2][j] = (s2*table[i][j+1]-s1*table[i+1][j+1])*100000;
                }else{
                    table[i+2][j] = (s2*table[i][j+1]-s1*table[i+1][j+1])/s2;
                }
            }
            if(check_zero_row(i+2) && i+2 != rows-1){
                replacement_for_zero_row(i);
            }
        }
    }

    public JSONObject check_stability(){
        JSONObject jsonAns = new JSONObject();
        initialize();
        evaluate_system();

        if(num_roots_in_the_RHS() > 0){
            jsonAns.put("State","UnStable");
        }else if(first_col_contain_zero()){
            jsonAns.put("State","Critically Stable");
        }else{
            jsonAns.put("State","Stable");
        }
        jsonAns.put("Table",creatTable());
        jsonAns.put("NumberOfRoots",num_roots_in_the_RHS());

        return jsonAns;
    }

}
