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
public class PhemexRestApi implements Serializable {
    @NotBlank(message = "env不能为空")
    private String env;

    @NotBlank(message = "email不能为空")
    private String email;

    @NotBlank(message = "password不能为空")
    private String password;

    @NotBlank(message = "totpSecret不能为空")
    private String totpSecret;

    private static final long serialVersionUID = 1L;
}
