package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexPubApi;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.listing.PositionRequest
 * @Date: 2024年01月19日 14:40
 * @Description:
 */

@Getter
@Setter
public class SpotTradePubRequest extends PhemexPubApi {
    @NotEmpty(message = "symbolList不能为空")
    private List<String> symbolList;
    private String ordType;
    @NotEmpty(message = "side不能为空")
    private List<String> sides;
    private String qtyType;
    private String baseQtyEv;
    private String quoteQtyEv;
}
