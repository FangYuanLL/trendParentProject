package cn.how2j.trend.service;

import cn.how2j.trend.client.IndexDataClient;
import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {


    int winCount = 0;
    float totalWinRate = 0;
    float avgWinRate = 0;
    float totalLossRate = 0;
    int lossCount = 0;
    float avgLossRate = 0;

    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> getIndexData(String code){
        List<IndexData> indexDataList = indexDataClient.getIndexData(code);
        Collections.reverse(indexDataList);
        return indexDataList;
    }
    //ma 20天
    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {

        List<Profit> profits =new ArrayList<>();

        List<Trade> trades = new ArrayList<>();


        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        float value = 0;

        float init =0;
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();

        for (int i = 0; i<indexDatas.size() ; i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i,ma,indexDatas);
            float max = getMax(i,ma,indexDatas);

            float increase_rate = closePoint/avg;
            float decrease_rate = closePoint/max;

            if(avg!=0) {
                //buy 超过了均线
                if(increase_rate>buyRate  ) {
                    //如果没买
                    if(0 == share) {
                        share = cash / closePoint;
                        cash = 0;

                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getData());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                //sell 低于了卖点
                else if(decrease_rate<sellRate ) {
                    //如果没卖
                    if(0!= share){
                        cash = closePoint * share * (1-serviceCharge);
                        share = 0;
                        Trade trade =trades.get(trades.size()-1);
                        trade.setSellDate(indexData.getData());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        float rate = cash / initCash;
                        trade.setRate(rate);

                        if(trade.getSellClosePoint()-trade.getBuyClosePoint()>0){
                            totalWinRate += (trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        }else{
                            totalLossRate += (trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount++;
                        }
                    }
                }
                //do nothing
                else{

                }
            }

            if(share!=0) {
                value = closePoint * share;
            }
            else {
                value = cash;
            }
            float rate = value/initCash;

            Profit profit = new Profit();
            profit.setDate(indexData.getData());
            profit.setValue(rate*init);

            System.out.println("profit.value:" + profit.getValue());
            profits.add(profit);

        }


        avgWinRate = (totalWinRate)/(winCount);
        avgLossRate = (totalLossRate)/(lossCount);



        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);


        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);
        map.put("annualProfits", annualProfits);


        return map;
    }

    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i-1-day;
        if(start<0)
            start = 0;
        int now = i-1;

        if(start<0)
            return 0;

        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }

    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i-1-ma;
        int now = i-1;

        if(start<0)
            return 0;

        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }

    public float getYear(List<IndexData> mDataList){
        String first = mDataList.get(0).getData();
        String last = mDataList.get(mDataList.size()-1).getData();
        Date firstDate = DateUtil.parse(first);
        Date lastDate = DateUtil.parse(last);
        long dates = DateUtil.between(lastDate,firstDate, DateUnit.DAY);
        float mYears = dates/365f;
        return mYears;
    }

    public int getYear(String date){
        String[] index = date.split("-");
        return Integer.valueOf(index[0]);
    }

    private float getIndexIncome(int Year,List<IndexData> list){
        IndexData first = null;
        IndexData last = null;

        for (IndexData temp:list){
            int tempYear = getYear(temp.getData());
            if (tempYear == Year){
                if (null == first){
                    first = temp;
                }else{
                    last = temp;
                }
            }
        }
      return (last.getClosePoint() - first.getClosePoint())/first.getClosePoint();
    }

    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }

    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {

        List<AnnualProfit> caculateAnnualProfits = new ArrayList<>();

        int firstYear = getYear(indexDatas.get(0).getData());
        int lastYear = getYear(indexDatas.get(indexDatas.size()-1).getData());
        for (int i=firstYear;i<= lastYear;i++){
            float tempIndexIncome = getIndexIncome(i,indexDatas);
            float tempTrendIncome = getTrendIncome(i,profits);
            AnnualProfit temp = new AnnualProfit();
            temp.setYear(i);
            temp.setIndexIncome(tempIndexIncome);
            temp.setTrendIncome(tempTrendIncome);
            caculateAnnualProfits.add(temp);
        }
        return caculateAnnualProfits;
    }

}
