package in.gov.cybercrime.sachet.dto;

import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String name;

    private RoleMaster role;
    private RankMaster rank;
    private PoliceStationMaster ps;

    private String token;
    private String refreshToken;
}
