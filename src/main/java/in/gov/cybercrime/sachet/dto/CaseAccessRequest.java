package in.gov.cybercrime.sachet.dto;

public class CaseAccessRequest {

    private Long districtId;
    private Long psId;
    private Long userId;
    private String rankName;

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getPsId() {
        return psId;
    }

    public void setPsId(Long psId) {
        this.psId = psId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }
}