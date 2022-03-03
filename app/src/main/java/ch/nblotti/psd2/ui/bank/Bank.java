package ch.nblotti.psd2.ui.bank;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Bank {

    private String bankName;
    private int bankPictureId;
    private BANKSERVICE[] bankservices;
    private String moduleName;
    private String serviceName;
    private MODULE_SERVICE_STATUS installed;
    private BigDecimal installationStatus;
}
