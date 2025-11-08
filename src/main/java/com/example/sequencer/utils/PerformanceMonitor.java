package com.example.sequencer.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PerformanceMonitor - Tracks execution time and memory usage
 * Provides detailed performance metrics for optimization analysis
 */
public class PerformanceMonitor {
    
    private final Map<String, Long> startTimes;
    private final Map<String, Long> durations;
    private final MemoryMXBean memoryBean;
    private long initialMemory;
    private long peakMemory;
    private long processingStartTime;
    private long processingEndTime;
    
    public PerformanceMonitor() {
        this.startTimes = new LinkedHashMap<>();
        this.durations = new LinkedHashMap<>();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.peakMemory = 0;
    }
    
    /**
     * Start monitoring the entire process
     */
    public void startProcessing() {
        this.processingStartTime = System.nanoTime();
        this.initialMemory = getUsedMemory();
        this.peakMemory = this.initialMemory;
    }
    
    /**
     * End monitoring the entire process
     */
    public void endProcessing() {
        this.processingEndTime = System.nanoTime();
        updatePeakMemory();
    }
    
    /**
     * Start timing a specific operation
     */
    public void startOperation(String operationName) {
        startTimes.put(operationName, System.nanoTime());
        updatePeakMemory();
    }
    
    /**
     * End timing a specific operation
     */
    public void endOperation(String operationName) {
        Long startTime = startTimes.get(operationName);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            durations.put(operationName, duration);
        }
        updatePeakMemory();
    }
    
    /**
     * Get current used memory in bytes
     */
    private long getUsedMemory() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed();
    }
    
    /**
     * Update peak memory usage
     */
    private void updatePeakMemory() {
        long currentMemory = getUsedMemory();
        if (currentMemory > peakMemory) {
            peakMemory = currentMemory;
        }
    }
    
    /**
     * Get total processing time in milliseconds
     */
    public long getTotalProcessingTimeMs() {
        return (processingEndTime - processingStartTime) / 1_000_000;
    }
    
    /**
     * Get total processing time in seconds
     */
    public double getTotalProcessingTimeSec() {
        return getTotalProcessingTimeMs() / 1000.0;
    }
    
    /**
     * Get duration of a specific operation in milliseconds
     */
    public long getOperationDurationMs(String operationName) {
        Long duration = durations.get(operationName);
        return duration != null ? duration / 1_000_000 : 0;
    }
    
    /**
     * Get memory used (difference from initial to peak)
     */
    public long getMemoryUsed() {
        return peakMemory - initialMemory;
    }
    
    /**
     * Get peak memory usage
     */
    public long getPeakMemory() {
        return peakMemory;
    }
    
    /**
     * Get current memory usage
     */
    public long getCurrentMemory() {
        return getUsedMemory();
    }
    
    /**
     * Format bytes to human-readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Print detailed performance report to console
     */
    public void printReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERFORMANCE METRICS");
        System.out.println("=".repeat(80));
        
        // Time metrics
        System.out.println("\n📊 THỜI GIAN XỬ LÝ:");
        System.out.println("  ├─ Tổng thời gian: " + String.format("%.3f", getTotalProcessingTimeSec()) + " giây (" + getTotalProcessingTimeMs() + " ms)");
        
        if (!durations.isEmpty()) {
            System.out.println("  └─ Chi tiết từng bước:");
            for (Map.Entry<String, Long> entry : durations.entrySet()) {
                long ms = entry.getValue() / 1_000_000;
                double percentage = (ms * 100.0) / getTotalProcessingTimeMs();
                System.out.println("     ├─ " + entry.getKey() + ": " + ms + " ms (" + String.format("%.1f", percentage) + "%)");
            }
        }
        
        // Memory metrics
        System.out.println("\n💾 DUNG LƯỢNG SỬ DỤNG:");
        System.out.println("  ├─ Bộ nhớ ban đầu: " + formatBytes(initialMemory));
        System.out.println("  ├─ Bộ nhớ đỉnh: " + formatBytes(peakMemory));
        System.out.println("  ├─ Bộ nhớ sử dụng: " + formatBytes(getMemoryUsed()));
        System.out.println("  └─ Bộ nhớ hiện tại: " + formatBytes(getCurrentMemory()));
        
        // System info
        Runtime runtime = Runtime.getRuntime();
        System.out.println("\n🖥️  HỆ THỐNG:");
        System.out.println("  ├─ Tổng bộ nhớ JVM: " + formatBytes(runtime.totalMemory()));
        System.out.println("  ├─ Bộ nhớ tối đa: " + formatBytes(runtime.maxMemory()));
        System.out.println("  └─ Số lõi CPU: " + runtime.availableProcessors());
        
        System.out.println("\n" + "=".repeat(80));
    }
    
    /**
     * Get a summary string for logging
     */
    public String getSummary() {
        return String.format("Time: %.3fs | Memory: %s | Peak: %s",
                getTotalProcessingTimeSec(),
                formatBytes(getMemoryUsed()),
                formatBytes(peakMemory));
    }
}
