package cn.how2j.trend.controller;


import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.how2j.trend.service.BackTestService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;

@RestController
public class BackTestController {

    @Autowired
    BackTestService backTestService;

    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    public Map<String, Object> getIndexData(@PathVariable("code") String code, @PathVariable("ma") int ma,
                                            @PathVariable("buyThreshold") float buyThreshold, @PathVariable("sellThreshold") float sellThreshold,
                                            @PathVariable("serviceCharge") float serviceCharge, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        List<IndexData> indexDataList = backTestService.getIndexData(code);
        List<IndexData> resultList = FilterDate(indexDataList, startDate, endDate);
        String indexStartDate = resultList.get(0).getData();
        String indexEndDate = resultList.get(resultList.size() - 1).getData();

        //int ma = 20;
        float sellRate = sellThreshold;
        float buyRate = buyThreshold;
        //float serviceCharge = 0f;
        Map<String, ?> simulateResult = backTestService.simulate(ma, sellRate, buyRate, serviceCharge, resultList);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");

        List<Trade> trades = (List<Trade>) simulateResult.get("trades");

        float mYears = backTestService.getYear(resultList);

        float indexIncomeTotal = (resultList.get(resultList.size() - 1).getClosePoint() - resultList.get(0).getClosePoint()) / resultList.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1 + indexIncomeTotal, 1 / mYears) - 1;
        float trendIncomeTotal = (profits.get(profits.size() - 1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1 + trendIncomeTotal, 1 / mYears) - 1;


        int winCount = (Integer) simulateResult.get("winCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        int lossCount = (Integer) simulateResult.get("lossCount");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");


        Map<String, Object> result = new HashMap<>();
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("indexDatas", resultList);
        result.put("profits", profits);
        result.put("trades", trades);
        result.put("years", mYears);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);

        result.put("winCount", winCount);
        result.put("avgWinRate", avgWinRate);
        result.put("lossCount", lossCount);
        result.put("avgLossRate", avgLossRate);

        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");
        result.put("annualProfits", annualProfits);

        return result;
    }

    public List<IndexData> FilterDate(List<IndexData> list, String startDate, String endDate) {
        if (StrUtil.isBlankOrUndefined(startDate) || StrUtil.isBlankOrUndefined(endDate))
            return list;

        Date mStartDate = DateUtil.parse(startDate);
        Date mEndDate = DateUtil.parse(endDate);
        ArrayList<IndexData> result = new ArrayList<>();
        for (IndexData temp : list) {
            Date tempDate = DateUtil.parse(temp.getData());
            if (tempDate.getTime() >= mStartDate.getTime() && tempDate.getTime() <= mEndDate.getTime()) {
                result.add(temp);
            }
        }
        System.out.println("result:" + result.size());
        return result;
    }
}
