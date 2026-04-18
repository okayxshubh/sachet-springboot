package in.gov.cybercrime.sachet.dto;

import java.util.List;

public class RankFilterRequest {

    private List<Long> rankIds;

    public List<Long> getRankIds() {
        return rankIds;
    }

    public void setRankIds(List<Long> rankIds) {
        this.rankIds = rankIds;
    }
}