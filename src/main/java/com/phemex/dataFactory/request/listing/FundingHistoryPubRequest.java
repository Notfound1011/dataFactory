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
 * @Package: com.phemex.dataFactory.request.listing
 * @Date: 2024年01月17日 10:55
 * @Description:
 */
@Getter
@Setter
public class FundingHistoryPubRequest extends PhemexPubApi {
    @NotEmpty(message = "symbolList不能为空")
    private List<String> symbolList;

    @NotNull
    private long timestamp;

    private int limit = 20;
}
