package com.cantang.jsexp;

import java.util.List;

/**
 * Created by cantang on 3/13/17.
 */

public class Util {

    public static long average(List<Long> list) {
        if (list.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (long item : list) {
            sum += item;
        }
        return sum / list.size();
    }
}
