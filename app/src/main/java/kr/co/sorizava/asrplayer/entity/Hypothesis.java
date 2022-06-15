package kr.co.sorizava.asrplayer.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hypothesis {

    @SerializedName("likelihood")
    @Expose
    private Double likelihood;
    @SerializedName("transcript")
    @Expose
    private String transcript;
    @SerializedName("word-alignment")
    @Expose
    private List<WordAlignment> wordAlignment = null;

    public Double getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(Double likelihood) {
        this.likelihood = likelihood;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public List<WordAlignment> getWordAlignment() {
        return wordAlignment;
    }

    public void setWordAlignment(List<WordAlignment> wordAlignment) {
        this.wordAlignment = wordAlignment;
    }

}