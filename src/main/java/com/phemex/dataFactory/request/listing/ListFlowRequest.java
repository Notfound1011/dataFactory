package com.phemex.dataFactory.request.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.request.base.PhemexAdminApi;
import lombok.Data;

import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.base.PhemexManageApi
 * @Date: 2024年01月24日 09:54
 * @Description:
 */
@Data
public class ListFlowRequest extends PhemexAdminApi {
    private String cmdb_environments;
    private String description;
    private List<JSONObject> params;
    private String type;
    private String env;
}
