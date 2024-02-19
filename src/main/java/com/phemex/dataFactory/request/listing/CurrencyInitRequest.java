package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexManageApi;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.listing.CurrencyInitRequest
 * @Date: 2024年01月22日 17:03
 * @Description:
 */

@Getter
@Setter
public class CurrencyInitRequest extends PhemexManageApi{
    private String env;
    private List<CurrencyInfo> currencies;

    // 转换为Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("env", this.env);
        map.put("currencies",currencies);
        return map;
    }
}
