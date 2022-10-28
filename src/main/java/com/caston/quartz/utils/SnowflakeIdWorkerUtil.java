package com.caston.quartz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 雪花算法生成ID
 *
 * @author WangJiao
 * @since 2020/12/21
 */
@Slf4j
public class SnowflakeIdWorkerUtil {
    /**
     * 开始时间截 (2020-01-01)
     */
    private final long startEpoch = 1577808000000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 4L;

    /**
     * 数据标识id所占的位数
     */
    private final long dataCenterIdBits = 4L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = 15L;

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDataCenterId = 15L;

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 5L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = 5L;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long dataCenterIdShift = 9L;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = 13L;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = 31L;

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private static final SnowflakeIdWorkerUtil idWorker;

    static {
        idWorker = new SnowflakeIdWorkerUtil(getWorkId(), getDataCenterId());
    }

    public SnowflakeIdWorkerUtil(long workerId, long dataCenterId) {
        log.info("SnowflakeIdWorkerUtil:[workerId:{},dataCenterId:{}]", workerId, dataCenterId);
        if (workerId <= 15L && workerId >= 0L) {
            if (dataCenterId <= 15L && dataCenterId >= 0L) {
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
            } else {
                throw new IllegalArgumentException(
                        String.format("datacenter Id can't be greater than %d or less than 0", 15L));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", 15L));
        }
    }

    /**
     * 工作机器id
     *
     * @return workId
     */
    private static Long getWorkId() {
        try {
            // 获取本机IP地址
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            log.info("getWorkId:本地ip地址[hostAddress:{}]", hostAddress);
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            long l = (sums % 32);
            // WorkId太长，数据库使用的是long类型,如果按照原长度返回给前端，出现存入数据库正常,查询返回给前端后后两位变为0的情况,导致不正确.
            // js支持的最大整数是2的53次方减1,所以损失了精度;
            //
            // 解决办法:
            // 1.存储到数据库为varchar
            // 2.取出后返回前端前转为String类型
            // 3.取长度15位
            return l > 15 ? new Random().nextInt(15) : l;
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 15);
        }
    }

    private static Long getDataCenterId() {
        String hostName = SystemUtils.getHostName();
        log.info("getDataCenterId:[hostName:{}]", hostName);
        int[] ints = StringUtils.toCodePoints(hostName);
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        long l = (sums % 32);
        return l > 15 ? new Random().nextInt(15) : l;
    }

    /**
     * 静态工具类
     *
     * @return id
     */
    public static synchronized Long generateId() {
        return idWorker.nextId();
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(
                    String.format(
                            "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            this.lastTimestamp - timestamp));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & 31L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - 1529942400000L << 13
                    | this.dataCenterId << 9
                    | this.workerId << 5
                    | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}

