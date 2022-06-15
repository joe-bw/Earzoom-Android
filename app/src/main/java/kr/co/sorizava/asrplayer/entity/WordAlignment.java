package kr.co.sorizava.asrplayer.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WordAlignment {

    @SerializedName("start")
    @Expose
    private Double start;
    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("word")
    @Expose
    private String word;
    @SerializedName("confidence")
    @Expose
    private Double confidence;

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

}