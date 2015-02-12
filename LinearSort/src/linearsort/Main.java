package linearsort;

import java.io.*;
import java.util.*;

public class Main {
    private int[] arr;
    private int size;

    private int max(){
        int max = arr[0];
        for (int i = 1; i < size; i++)
            if(max < arr[i])
                max = arr[i];
        return max;
    }

    public void sort(){
        int k = this.max();
        int[] c = new int[k + 1];
        
        for (int i = 0; i < this.size; i++)
            c[arr[i]] = c[arr[i]] + 1;
        
        int b = 0;
        for (int j = 0; j < k; j++)
            for (int i = 0; i < c[j]; i++){
                arr[b] = j;
                b = b + 1;
            }
    }

    public void input() throws FileNotFoundException{
        Scanner sc = new Scanner(new File("input.in"));
        this.size = sc.nextInt();
        arr = new int[this.size];
        
        for (int i = 0; i < this.size; i++){
            this.arr[i] = sc.nextInt();
        }
        sc.close();
    }

    public void output() throws FileNotFoundException{
        PrintWriter out = new PrintWriter(new File("output.out"));
        for(int i = 0; i < size; i++)
            out.print(arr[i] + " ");
        out.println();
        out.flush();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Main obj = new Main();
        obj.input();
        obj.sort();
        obj.output();
    }
}
