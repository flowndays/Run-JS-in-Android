package com.cantang.jsexp.javatest;

import android.util.Log;

import com.cantang.jsexp.TestContext;
import com.cantang.jsexp.Tester;
import com.cantang.jsexp.Util;

import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cantang on 3/13/17.
 */

public class JavaTester extends Tester{
    public JavaTester(TestContext testContext) {
        super(testContext);
    }

    /**
     * Start java test
     *
     * @return Single
     */
    public Single<Long> start() {
        return Single.fromCallable(() -> {
            for (int i = 0; i < testContext.getExperimentTimes(); i++) {
                long timeCost = test();
                records.add(timeCost);
            }

            long average = Util.average(records);
            Log.d("tctest", "java average of 100 times: " + average);
            return average;
        }).subscribeOn(Schedulers.io());
    }

    private long test() {
        long before = System.nanoTime();
        Pattern pattern = Pattern.compile("\\d{15}");
        String text = "123456789012345";
        for (int i = 0; i < testContext.getExecuteTimes(); i++) {
            if (doTheWork(pattern, text)) {
                continue;
            }
        }
        return (System.nanoTime() - before) / 1000000;
    }

    /**
     * Do a Regex checking, the result doesn't matter.
     */
    private boolean doTheWork(Pattern pattern, String target) {
        return pattern.matcher(target).matches();
    }

}
