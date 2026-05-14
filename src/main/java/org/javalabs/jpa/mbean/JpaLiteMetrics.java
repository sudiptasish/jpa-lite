package org.javalabs.jpa.mbean;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Provides metrics and monitoring support for JPA Lite operations.
 *
 * <p>The {@code JpaLiteMetrics} class is responsible for collecting,
 * aggregating, and exposing performance and usage metrics related to
 * persistence operations executed via a lightweight JPA layer.</p>
 *
 * <p>Typical metrics captured may include:</p>
 * <ul>
 *   <li>Number of entity operations (persist, merge, remove, find)</li>
 *   <li>Query execution counts and durations</li>
 *   <li>Transaction counts and timings</li>
 *   <li>Error and exception rates</li>
 * </ul>
 *
 * <p>This class can be used to integrate with monitoring systems or logging
 * frameworks to provide visibility into persistence behavior and performance
 * characteristics.</p>
 *
 * <p>Implementations may support:</p>
 * <ul>
 *   <li>Custom metric reporters (e.g., logs, JMX, Prometheus)</li>
 *   <li>Aggregation and sampling strategies</li>
 *   <li>Hooks for intercepting persistence life cycle events</li>
 * </ul>
 *
 * @author Sudiptasish Chanda
 */
public class JpaLiteMetrics {
    
    public static Map<String, Object> poolMetrics(String poolName) throws InstanceNotFoundException, Exception {
        if (poolName == null || poolName.trim().length() == 0) {
            poolName = "HikariPool-1";
        }
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName pool = new ObjectName("com.zaxxer.hikari:type=Pool (" + poolName + ")");
        
        MBeanInfo info = mBeanServer.getMBeanInfo(pool);
        MBeanAttributeInfo[] attrInfos = info.getAttributes();
        String[] attrNames = new String[attrInfos.length];
        for (int i = 0; i < attrInfos.length; i ++) {
            attrNames[i] = attrInfos[i].getName();
        }
        
        AttributeList attributes = mBeanServer.getAttributes(pool, attrNames);
        Map<String, Object> data = new HashMap<>();
        
        for (int i = 0; i < attributes.size(); i ++) {
            Attribute attr = (Attribute)attributes.get(i);
            data.put(attr.getName(), attr.getValue());
        }
        return data;
    }
    
    public static Map<String, Object> poolConfigMetrics(String poolName) throws InstanceNotFoundException, Exception {
        if (poolName == null || poolName.trim().length() == 0) {
            poolName = "HikariPool-1";
        }
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName pool = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + poolName + ")");
        
        MBeanInfo info = mBeanServer.getMBeanInfo(pool);
        MBeanAttributeInfo[] attrInfos = info.getAttributes();
        String[] attrNames = new String[attrInfos.length];
        for (int i = 0; i < attrInfos.length; i ++) {
            attrNames[i] = attrInfos[i].getName();
        }
        
        AttributeList attributes = mBeanServer.getAttributes(pool, attrNames);
        Map<String, Object> data = new HashMap<>();
        
        for (int i = 0; i < attributes.size(); i ++) {
            Attribute attr = (Attribute)attributes.get(i);
            data.put(attr.getName(), attr.getValue());
        }
        return data;
    }
}
