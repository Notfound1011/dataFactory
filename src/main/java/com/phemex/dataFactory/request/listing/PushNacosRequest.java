package com.phemex.dataFactory.request.listing;

import com.phemex.dataFactory.request.base.PhemexOpsysApi;
import lombok.Data;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.base.PhemexManageApi
 * @Date: 2024年01月24日 09:54
 * @Description:
 */
@Data
public class PushNacosRequest extends PhemexOpsysApi {
    private String description;
    private String filter;
}
