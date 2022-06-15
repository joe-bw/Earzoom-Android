package kr.co.sorizava.asrplayer.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZerothResult {

    @SerializedName("hypotheses")
    @Expose
    private List<Hypothesis> hypotheses = null;
    @SerializedName("final")
    @Expose
    private Boolean _final;

    public List<Hypothesis> getHypotheses() {
        return hypotheses;
    }

    public void setHypotheses(List<Hypothesis> hypotheses) {
        this.hypotheses = hypotheses;
    }

    public Boolean getFinal() {
        return _final;
    }

    public void setFinal(Boolean _final) {
        this._final = _final;
    }

}