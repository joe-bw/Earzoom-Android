package org.mozilla.focus.login.ui.statistics;

import com.google.gson.annotations.SerializedName;


/**
 * "result": {
 *             "statisticsSeq": "11",
 *             "id": "sorizava",
 *             "connDate": "2021-08-03",
 *             "stTime": "2021-08-03 15:44:00.0",
 *             "endTime": null,
 *             "useContent": "http://www.useContent.com"
 *         }
 *
 *         {
 * 	LoginResponse {
 * 		status = '200',
 * 		data = StartStatisticsDataVO {
 * 			result = DataResultVO {
 * 				statisticsSeq = '43',
 * 				  id = '1826472143',
 * 				  connDate = '2021-10-12',
 * 				  stTime = '2021-10-12 10:56:02',
 * 				  endTime = 'null',
 * 				  useContent = 'http://www.kbs.co.kr/'
 *                        }*
 *                        }, code = '정상'
 * 	}
 * }
 *
 *
 */
public class DataResultVO {

    @SerializedName("statisticsSeq")
    public String statisticsSeq;

    @SerializedName("id")
    public String id;

    @SerializedName("connDate")
    public String connDate;

    @SerializedName("stTime")
    public String stTime;

    @SerializedName("endTime")
    public String endTime;

    @SerializedName("useContent")
    public String useContent;

    @Override
    public String toString() {
        return "DataResultVO{" +
                "statisticsSeq='" + statisticsSeq + '\'' +
                ", id='" + id + '\'' +
                ", connDate='" + connDate + '\'' +
                ", stTime='" + stTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", useContent='" + useContent + '\'' +
                '}';
    }
}
