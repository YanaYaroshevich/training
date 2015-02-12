package up1juices;

import java.util.*;

public class Sort implements Runnable{
    ArrayList<String> components;
    
    public Sort(ArrayList<String> components){
        this.components = components;
    }

    public ArrayList<String> getComponents(){
        return this.components;
    }

    @Override
    public void run() {
        Collections.sort(components, new CodesComparator());
    }
}
