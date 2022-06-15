package org.mozilla.focus.login.ui.statistics;

/**
 *
 * {
 *     "status": 200,
 *     "data": {
 *         "result": {
 *             "statisticsSeq": "11",
 *             "id": "sorizava",
 *             "connDate": "2021-08-03",
 *             "stTime": "2021-08-03 15:44:00.0",
 *             "endTime": null,
 *             "useContent": "http://www.useContent.com"
 *         }
 *     },
 *     "code": "정상"
 * }
 *
 * {
 * 		"status": "200",
 * 		"data": {
 * 			"result": {
 * 				"statisticsSeq": "43",
 * 				"id": "1826472143",
 * 				"connDate": "2021-10-12",
 * 				"stTime": "2021-10-12 10:56:02",
 * 				"endTime": "null",
 * 				"useContent": "http://www.kbs.co.kr/"
 *      },
 * 		"code": "정상"
 * 	}
 * }
 *
 *
 */
import com.google.gson.annotations.SerializedName;

public class StartStatisticsDataVO {

    @SerializedName("result")
    public DataResultVO result;

    @Override
    public String toString() {
        return "StartStatisticsDataVO{" +
                "result=" + result +
                '}';
    }
}
