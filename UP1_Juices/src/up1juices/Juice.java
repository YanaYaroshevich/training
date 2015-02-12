package up1juices;

import java.util.*;

public class Juice {
    private ArrayList<String> components;

    public Juice(){
        components = new ArrayList<String>();
    }

    public Juice(ArrayList<String> newComponents){
        components = new ArrayList<String>(newComponents);
    }

    public ArrayList<String> getComponents(){
        return this.components;
    }

    @Override
    public String toString(){
        String str = "";
        for (String comp: components) {
            str += comp + " ";
        }
        str += "\n";
        return str;
    }
}