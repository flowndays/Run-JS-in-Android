package com.cantang.jsexp;

/**
 * Created by cantang on 3/13/17.
 */

public class TestContext {
    private final int executeTimes;
    private final int experimentTimes;

    private TestContext(int executeTimes, int experimentTimes) {
        this.executeTimes = executeTimes;
        this.experimentTimes = experimentTimes;
    }

    public int getExecuteTimes() {
        return executeTimes;
    }

    public int getExperimentTimes() {
        return experimentTimes;
    }

    public static class Builder {
        private int executeTimes;
        private int experimentTimes;

        public Builder setExecuteTimes(int executeTimes) {
            this.executeTimes = executeTimes;
            return this;
        }

        public Builder setExperimentTimes(int experimentTimes) {
            this.experimentTimes = experimentTimes;
            return this;
        }

        public TestContext build() {
            return new TestContext(executeTimes, experimentTimes);
        }
    }
}
