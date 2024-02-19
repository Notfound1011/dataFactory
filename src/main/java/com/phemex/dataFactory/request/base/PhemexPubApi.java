package com.phemex.dataFactory.request.base;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.base
 * @Date: 2024年01月05日 11:58
 * @Description:
 */
@Data
public class PhemexPubApi implements Serializable {
    @NotBlank(message = "env不能为空")
    private String env;

    @NotBlank(message = "accessToken不能为空")
    private String accessToken;

    @NotBlank(message = "secretKey不能为空")
    private String secretKey;

    private static final long serialVersionUID = 1L;
}
