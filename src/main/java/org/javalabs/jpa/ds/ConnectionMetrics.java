package org.javalabs.jpa.ds;

/**
 *
 * @author schan280
 */
public class ConnectionMetrics {
    
    private final String name;
    
    private Long createTime = 0L;
    private Long lastUsedTime = 0L;
    private Long totalIdleTime = 0L;
    private Long idleStart = 0L;
    private Long workStart = 0L;
    private Long callCount = 0L;
    private Long minUsageMs = Long.MAX_VALUE;
    private Long maxUsageMs = 0L;
    private Long avgUsageMs = 0L;
    
    private String status = "ACTIVE";       // ACTIVE | IDLE | BUSY | INVALID | EVICTED 
    
    public ConnectionMetrics(String name) {
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.idleStart = this.createTime;
    }
    
    public void startCapture() {
        this.totalIdleTime += System.currentTimeMillis() - idleStart;
        this.workStart = System.currentTimeMillis();
        this.callCount ++;
        this.idleStart = 0L;
        this.status = "BUSY";
    }
    
    public void endCapture() {
        Long usage = (this.lastUsedTime = System.currentTimeMillis()) - this.workStart;
        if (usage < this.minUsageMs) {
            this.minUsageMs = usage;
        }
        if (usage > this.maxUsageMs) {
            this.maxUsageMs = usage;
        }
        this.avgUsageMs = (this.avgUsageMs * (this.callCount - 1) + usage) / this.callCount;
        this.workStart = 0L;
        this.idleStart = this.lastUsedTime;
        this.status = "IDLE";
    }

    public String getName() {
        return name;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Long getTotalIdleTime() {
        return totalIdleTime;
    }

    public void setTotalIdleTime(Long totalIdleTime) {
        this.totalIdleTime = totalIdleTime;
    }

    public Long getIdleStart() {
        return idleStart;
    }

    public void setIdleStart(Long idleStart) {
        this.idleStart = idleStart;
    }

    public Long getWorkStart() {
        return workStart;
    }

    public void setWorkStart(Long workStart) {
        this.workStart = workStart;
    }

    public Long getCallCount() {
        return callCount;
    }

    public void setCallCount(Long callCount) {
        this.callCount = callCount;
    }

    public Long getMinUsageMs() {
        return minUsageMs;
    }

    public void setMinUsageMs(Long minUsageMs) {
        this.minUsageMs = minUsageMs;
    }

    public Long getMaxUsageMs() {
        return maxUsageMs;
    }

    public void setMaxUsageMs(Long maxUsageMs) {
        this.maxUsageMs = maxUsageMs;
    }

    public Long getAvgUsageMs() {
        return avgUsageMs;
    }

    public void setAvgUsageMs(Long avgUsageMs) {
        this.avgUsageMs = avgUsageMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
