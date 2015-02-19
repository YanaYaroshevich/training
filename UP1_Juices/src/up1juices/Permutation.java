package up1juices;

import java.util.Arrays;

public class Permutation{
    private Juice[] arr;
    private int[] swaps;

    public Permutation(Juice[] arr) {
        this(arr, arr.length);
    }

    public Permutation(Juice[] arr, int permSize) {
        this.arr = arr.clone();
        this.swaps = new int[permSize];
        for(int i = 0; i < swaps.length; i++)
            swaps[i] = i;
    }

    public Juice[] next() {
        if (arr == null)
            return null;

        Juice[] res = Arrays.copyOf(arr, swaps.length);
        int i = swaps.length - 1;
        while (i >= 0 && swaps[i] == arr.length - 1) {
            swap(i, swaps[i]); 
            swaps[i] = i;
            i--;
        }
        if (i < 0)
            arr = null;
        else {
            int prev = swaps[i];
            swap(i, prev);
            int next = prev + 1;
            swaps[i] = next;
            swap(i, next);
        }
        return res;
    }

    private void swap(int i, int j) {
        Juice tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}