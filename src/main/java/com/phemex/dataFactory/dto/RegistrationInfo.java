package com.phemex.dataFactory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.dto.RegistrationInfo
 * @Date: 2023年06月12日 10:36
 * @Description:
 */
@Data
public class RegistrationInfo implements Serializable {
    private int id;
    private String email;
    private String result;
    private String msg;

    public RegistrationInfo(int id, String email, String result, String msg) {
        this.id = id;
        this.email = email;
        this.result = result;
        this.msg = msg;
    }

    // Getters and setters (if needed)

    // toString() or toJson() method to convert to string representation
    @Override
    public String toString() {
        return "RegistrationInfo{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
