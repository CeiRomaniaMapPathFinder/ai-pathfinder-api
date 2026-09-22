package com.ceiromania.pathfinder.services;

import java.lang.management.ManagementFactory;

public final class AllocationMeter {

    private static final com.sun.management.ThreadMXBean BEAN = load();

    private AllocationMeter() {
    }

    private static com.sun.management.ThreadMXBean load() {
        try {
            if (ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean bean
                    && bean.isThreadAllocatedMemorySupported()) {
                return bean;
            }
        } catch (RuntimeException e) {
            return null;
        }
        return null;
    }

    public static long allocatedBytes() {
        if (BEAN == null) {
            return -1;
        }
        try {
            return BEAN.getCurrentThreadAllocatedBytes();
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static double kbBetween(long before, long after) {
        if (before < 0 || after < 0) {
            return -1;
        }
        return (after - before) / 1024.0;
    }
}
