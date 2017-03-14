package com.cantang.jsexp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cantang on 3/13/17.
 */

public abstract class Tester {
    protected List<Long> records = new ArrayList<>();
    protected TestContext testContext;

    public Tester(TestContext testContext) {
        this.testContext = testContext;
    }
}
