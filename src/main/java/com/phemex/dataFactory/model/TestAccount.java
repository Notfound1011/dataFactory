package com.phemex.dataFactory.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.listing.TestAccountRequest
 * @Date: 2024年01月26日 15:30
 * @Description:
 */
@JsonPropertyOrder({"id", "owner", "uid", "email", "accessToken", "secretKey", "status", "createdAt", "updatedAt"})
@Getter
@Setter
public class TestAccount extends Base {
    @NotNull(message = "owner不能为空")
    private String owner;
    @NotNull(message = "uid不能为空")
    private Integer uid;
    @NotNull(message = "email不能为空")
    private String email;
    @NotNull(message = "accessToken不能为空")
    private String accessToken;
    @NotNull(message = "secretKey不能为空")
    private String secretKey;
}
