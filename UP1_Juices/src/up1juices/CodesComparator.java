package up1juices;

import java.util.Comparator;

public class CodesComparator implements Comparator<String>{
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
}
