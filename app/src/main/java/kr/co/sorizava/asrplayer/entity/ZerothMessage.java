package kr.co.sorizava.asrplayer.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZerothMessage {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("segment")
    @Expose
    private Integer segment;
    @SerializedName("result")
    @Expose
    private ZerothResult result;
    @SerializedName("segment-start")
    @Expose
    private Double segmentStart;
    @SerializedName("segment-length")
    @Expose
    private Double segmentLength;
    @SerializedName("total-length")
    @Expose
    private Double totalLength;
    @SerializedName("bayes-risk")
    @Expose
    private Double bayesRisk;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSegment() {
        return segment;
    }

    public void setSegment(Integer segment) {
        this.segment = segment;
    }

    public ZerothResult getResult() {
        return result;
    }

    public void setResult(ZerothResult result) {
        this.result = result;
    }

    public Double getSegmentStart() {
        return segmentStart;
    }

    public void setSegmentStart(Double segmentStart) {
        this.segmentStart = segmentStart;
    }

    public Double getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Double segmentLength) {
        this.segmentLength = segmentLength;
    }

    public Double getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Double totalLength) {
        this.totalLength = totalLength;
    }

    public Double getBayesRisk() {
        return bayesRisk;
    }

    public void setBayesRisk(Double bayesRisk) {
        this.bayesRisk = bayesRisk;
    }

}