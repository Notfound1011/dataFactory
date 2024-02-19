package com.phemex.dataFactory.request.listing;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
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
public class CurrencyInfo {
    private String currency;
    private String chain;
    private int depositOpen = 1;
    private int withdrawOpen = 1;
    private int depositConfirmationNo;
    private int assetsPrecision;
    private int withdrawPrecision;
    private int addrExtra = 0;
    private String depositNotes;
    private int inAssetsDisplay = 1;
    private String tagOrMemo;

    // 转换为Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("currency", this.currency);
        map.put("chain", this.chain);
        map.put("depositOpen", this.depositOpen);
        map.put("withdrawOpen", this.withdrawOpen);
        map.put("depositConfirmationNo", this.depositConfirmationNo);
        map.put("assetsPrecision", this.assetsPrecision);
        map.put("withdrawPrecision", this.withdrawPrecision);
        map.put("addrExtra", this.addrExtra);
        map.put("depositNotes", this.depositNotes);
        map.put("inAssetsDisplay", this.inAssetsDisplay);
        map.put("tagOrMemo", this.tagOrMemo);
        return map;
    }

}
