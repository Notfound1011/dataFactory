package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexPubApi;
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
 * @Date: 2024年01月05日 12:00
 * @Description:
 */
@Getter
@Setter
public class LeveragePubRequest extends PhemexPubApi {
    @Enumerated(EnumType.STRING)
    @NotNull(message = "positionMode必须为Hedged或OneWay")
    private PositionMode positionMode = PositionMode.Hedged;

    private String leverage = "-20.00";

    @NotEmpty(message = "symbolList不能为空")
    private List<String> symbolList;

    @Getter
    public enum PositionMode {
        Hedged("Hedged"),
        OneWay("OneWay");

        private final String value;

        PositionMode(String value) {
            this.value = value;
        }
    }
}
