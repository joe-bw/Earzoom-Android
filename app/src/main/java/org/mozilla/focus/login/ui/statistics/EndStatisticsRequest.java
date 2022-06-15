package org.mozilla.focus.login.ui.statistics;

import com.google.gson.annotations.SerializedName;

/**
 * {
 *     "statisticsSeq": "11",
 *     "endTime":"20210803_154500"
 * }
 */
public class EndStatisticsRequest {

    @SerializedName("statisticsSeq")
    public String statisticsSeq;

    @SerializedName("endTime")
    public String endTime;

    public String getStatisticsSeq() {
        return statisticsSeq;
    }

    public void setStatisticsSeq(String statisticsSeq) {
        this.statisticsSeq = statisticsSeq;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public EndStatisticsRequest(String statisticsSeq, String endTime) {
        this.statisticsSeq = statisticsSeq;
        this.endTime = endTime;
    }
}
