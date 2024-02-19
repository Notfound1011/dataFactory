package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexRestApi;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.listing
 * @Date: 2024年01月17日 12:00
 * @Description:
 */
@Getter
@Setter
public class FundingHistoryRestRequest extends PhemexRestApi {
    @NotEmpty(message = "symbolList不能为空")
    private List<String> symbolList;

    @NotNull
    private Long timestamp;

    private int limit = 20;
}
