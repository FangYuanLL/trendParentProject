package cn.how2j.trend;

import cn.how2j.trend.pojo.Index;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.service.IndexDataService;
import cn.how2j.trend.service.IndexService;
import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    IndexDataService indexDataService;

    @Autowired
    IndexService indexService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("启动定时器：" + DateUtil.now());
        List<Index> IndexList = indexService.fresh();
        for (Index index : IndexList) {
            indexDataService.fresh(index.getCode());
            System.out.println("Code:" + index.getCode() + "  Name:" + index.getName() + " " + DateUtil.now());
        }
        System.out.println("定时器完成任务:" + DateUtil.now());
    }
}
