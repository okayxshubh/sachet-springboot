package in.gov.cybercrime.sachet.dto;

public class ApprovalPoolRequest {

    private Long districtId;
    private Long psId;

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
}