package com.phemex.dataFactory.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.scheduler.ScheduledTask
 * @Date: 2023年01月03日 16:37
 * @Description:
 */
@Component
public class ScheduledTask {
    private final static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    //  fixedRate 是 long 类型，表示任务执行的间隔毫秒数，以上代码中的定时任务每 5 秒执行一次。
    @Scheduled(cron="0 0 12 * * ?")
    public void scheduledTask() {
        String msg = "桃花坞裏桃花庵，桃花庵裏桃花仙。\n" +
                "桃花仙人种桃树，又折花枝当酒钱。\n" +
                "酒醒只在花前坐，酒醉还须花下眠。\n" +
                "花前花後日复日，酒醉酒醒年复年。\n" +
                "不愿鞠躬车马前，但愿老死花酒间。\n" +
                "车尘马足贵者趣，酒盏花枝贫者缘。\n" +
                "若将富贵比贫贱，一在平地一在天。\n" +
                "若将贫贱比车马，他得驱驰我得闲。\n" +
                "世人笑我忒风颠，我咲世人看不穿。\n" +
                "记得五陵豪杰墓，无酒无花锄作田。\n";
        logger.trace("日志级别：{},内容：{},本次定时器启动时间：{}" ,"TRACE",msg, new Date());
        logger.debug("日志级别：{},内容：{},本次定时器启动时间：{}" ,"DEBUG",msg, new Date());
        logger.info("日志级别：{},内容：{},本次定时器启动时间：{}" ,"INFO",msg, new Date());
        logger.warn("日志级别：{},内容：{},本次定时器启动时间：{}" ,"WARN",msg, new Date());
        logger.error("日志级别：{},内容：{},本次定时器启动时间：{}" ,"ERROR",msg, new Date());
    }
}
