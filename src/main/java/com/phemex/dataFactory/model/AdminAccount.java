package com.phemex.dataFactory.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.model.ManagementAccount
 * @Date: 2024年01月26日 19:58
 * @Description:
 */
@JsonPropertyOrder({"id", "owner", "username", "password", "type", "status", "createdAt", "updatedAt"})
@Getter
@Setter
public class AdminAccount extends Base {
    @NotNull(message = "owner不能为空")
    private String owner;

    @NotNull(message = "username不能为空")
    private String username;

    @NotNull(message = "password不能为空")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "type不能为空")
    private Type type;

    @Getter
    public enum Type {
        MGMT("MGMT"),
        LDAP("LDAP");

        private final String value;

        Type(String value) {
            this.value = value;
        }
    }
}