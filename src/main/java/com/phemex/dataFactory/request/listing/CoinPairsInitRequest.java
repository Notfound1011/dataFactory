package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexAdminApi;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.listing.CurrencyInitRequest
 * @Date: 2024年01月22日 17:03
 * @Description:
 */

@Getter
@Setter
public class CoinPairsInitRequest {
    private String env;

    @Valid
    private PhemexAdminApi mgmtAccount;
    @Valid
    private PhemexAdminApi ldapAccount;
    @NotEmpty
    private List<String> symbolList;
}
