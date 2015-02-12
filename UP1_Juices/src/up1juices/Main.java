package up1juices;

import java.io.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Logic obj = new Logic();
        obj.input();
        obj.setComponents();
        obj.output("juice1.out");
        obj.sort();
        obj.output("juice2.out");
        int count = obj.minWashes();
        obj.outputCount("juice3.out", count);
    }
}