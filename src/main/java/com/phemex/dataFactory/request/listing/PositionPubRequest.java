package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexPubApi;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class PositionPubRequest extends PhemexPubApi {
    @NotEmpty(message = "symbolList不能为空")
    private List<String> symbolList;
    private String orderQty;
    @NotEmpty(message = "sides不能为空")
    private List<String> sides;
    private String ordType;
}
