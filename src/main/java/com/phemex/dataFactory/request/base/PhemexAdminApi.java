package com.phemex.dataFactory.request.base;

import lombok.Data;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.base.PhemexManageApi
 * @Date: 2024年01月24日 09:54
 * @Description:
 */
@Data
public class PhemexAdminApi {
    private String owner;
    private String accountType;
}
