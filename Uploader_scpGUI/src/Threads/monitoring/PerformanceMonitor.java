package Threads.monitoring;


import java.io.File;
import java.lang.management.*;


/**
 * Calculation of local load
 * @author Jérémy DEVERDUN
 *
 */
public class PerformanceMonitor { 
    private int  availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    private long lastSystemTime      = 0;
    private long lastProcessCpuTime  = 0;

    /**
     * CPU usage
     * @return
     */
    public synchronized double getCpuUsage()
    {
        if ( lastSystemTime == 0 )
        {
            baselineCounters();
            return 0;
        }

        long systemTime     = System.nanoTime();
        long processCpuTime = 0;

        if ( ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean )
        {
            processCpuTime = ( (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean() ).getProcessCpuTime();
        }

        double cpuUsage = (double) ( processCpuTime - lastProcessCpuTime ) / ( systemTime - lastSystemTime );

        lastSystemTime     = systemTime;
        lastProcessCpuTime = processCpuTime;
        return cpuUsage / availableProcessors;
    }

    /**
     * 
     */
    private void baselineCounters()
    {
        lastSystemTime = System.nanoTime();

        if ( ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean )
        {
            lastProcessCpuTime = ( (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean() ).getProcessCpuTime();
        }
    }
    
    /**
     * Memory usage
     * @return
     */
    public synchronized double getMemoryUsage()
    {
    	MemoryUsage musage=(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
    	return (double)Runtime.getRuntime().freeMemory()/(double)Runtime.getRuntime().totalMemory();
    }
    
    
    
    /**
     * Disk usage
     * @return
     */
    public synchronized double getDiskUsage()
    {
    	File f=new File(".");
    	return 1-((double)f.getFreeSpace()/(double)f.getTotalSpace());
    }
    
}
