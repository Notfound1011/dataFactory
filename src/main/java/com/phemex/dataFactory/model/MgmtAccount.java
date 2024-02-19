package com.phemex.dataFactory.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.model.ManagementAccount
 * @Date: 2024年01月26日 19:58
 * @Description:
 */
@JsonPropertyOrder({ "id", "owner", "username", "password", "status", "createdAt", "updatedAt" })
@Getter
@Setter
public class MgmtAccount extends Base{
    @NotNull(message = "owner不能为空")
    private String owner;
    @NotNull(message = "username不能为空")
    private String username;
    @NotNull(message = "password不能为空")
    private String password;
}