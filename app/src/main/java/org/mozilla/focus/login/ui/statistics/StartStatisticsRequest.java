package org.mozilla.focus.login.ui.statistics;

import com.google.gson.annotations.SerializedName;

/**
 * {
 *     "id":"sorizava",
 *     "connDate":"2021-08-03",
 *     "stTime":"20210803_154400",
 *     "useContent":"http://www.useContent.com"
 * }
 */
public class StartStatisticsRequest {

    @SerializedName("id")
    public String id;

    @SerializedName("connDate")
    public String connDate;

    @SerializedName("stTime")
    public String stTime;

    @SerializedName("useContent")
    public String useContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnDate() {
        return connDate;
    }

    public void setConnDate(String connDate) {
        this.connDate = connDate;
    }

    public String getStTime() {
        return stTime;
    }

    public void setStTime(String stTime) {
        this.stTime = stTime;
    }

    public String getUseContent() {
        return useContent;
    }

    public void setUseContent(String useContent) {
        this.useContent = useContent;
    }

    public StartStatisticsRequest(String id, String connDate, String stTime, String useContent) {
        this.id = id;
        this.connDate = connDate;
        this.stTime = stTime;
        this.useContent = useContent;
    }
}
