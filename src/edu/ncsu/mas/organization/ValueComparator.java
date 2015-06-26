package edu.ncsu.mas.organization;

import java.util.Comparator;
import java.util.HashMap;

public class ValueComparator implements Comparator<String>{

	HashMap<String, Integer> base;

    ValueComparator(HashMap<String, Integer> map) {
        this.base = map;
    }

    @Override
    public int compare(String a, String b) {
        if (base.get(a) < base.get(b)) {
            return -1;
        } else if(base.get(a) == base.get(b)){
        	return 0;
        }
        else {
            return 1;
        }
    }
}
