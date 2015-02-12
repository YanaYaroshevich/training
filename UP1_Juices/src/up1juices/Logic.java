package up1juices;

import java.io.*;
import java.util.*;

public class Logic {
    private ArrayList<Juice> juices;
    private ArrayList<String> allComponents;
    private PrintWriter out;

    public Logic(){
        juices = new ArrayList<Juice>();
        allComponents = new ArrayList<String>();
    }

    public void input() throws FileNotFoundException{
        Scanner sc = new Scanner(new File("juice.in"));
        StringTokenizer st;
        ArrayList<String> components = new ArrayList<String>();
        while (sc.hasNextLine()){
            st = new StringTokenizer(sc.nextLine(), " ");
            while(st.hasMoreTokens()){
                components.add(st.nextToken());
            }
            juices.add(new Juice(components));
            components.clear();
        }
    }

    public void setComponents(){
        ArrayList<String> components = new ArrayList<String>();
        for (Juice juice : juices) {
            components = juice.getComponents();
            for (String comp : components) {
                if (!allComponents.contains(comp))
                    allComponents.add(comp);
            }
        }
    }

    public void output(String fileName) throws IOException{
        out = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
        for (String comp : allComponents) {
            if (allComponents.indexOf(comp) != allComponents.size() - 1)
                out.println(comp);
            else
                out.print(comp);
        }
        out.flush();
    }

    public void outputCount(String fileName, int count) throws IOException{
        out = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
        out.print(count);
        out.flush();
    }

    public void sort(){
        Sort s = new Sort(allComponents);
        s.run();
        allComponents = s.getComponents();
    }

    public int minWashes(){
        int count = 0;
        for (Juice juice : juices) {

        }
        return count;
    }
}