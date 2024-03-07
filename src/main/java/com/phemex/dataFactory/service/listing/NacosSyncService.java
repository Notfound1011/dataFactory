package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.PushNacosRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.NacosSync
 * @Date: 2024年01月17日 17:17
 * @Description:
 */
@Service
public class NacosSyncService {
    private static final Logger log = LoggerFactory.getLogger(NacosSyncService.class);
    private static final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6MTY5MDg3ODE5Nn0.0tz2Vh9fU4YXyGKWBLZNnaT_JSDmn_YUg0AofCGfa8o";
    private static final String urlTemplate = "http://alb-sap-nacos-424928052.ap-southeast-1.elb.amazonaws.com/nacos/v1/cs/configs?clone=true&tenant=%s&policy=OVERWRITE&namespaceId=&accessToken=" + accessToken;
    private static final String diffListPath = "/opsys/ncm/batch_nacos_data/diff_list/?env_key=Fat";
    private static final String createBatchUpdatePath = "/opsys/ncm/batch_nacos_data/";
    private static final String getNacosListPath = "/opsys/ncm/batch_nacos_data/?page=1&page_size=15&cmdb_environments__key=Fat";
    private final String phemexOpsysHost = "http://opsys.cmex.corp";
    private final TokenService tokenService;

    public NacosSyncService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public ResultHolder syncFatNacos(List<String> envs) {
        StringBuilder result = new StringBuilder();
        String body = "[{\"cfgId\":\"31978\",\"dataId\":\"common:phemex-spot-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31979\",\"dataId\":\"common:phemex-spot-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31980\",\"dataId\":\"common:phemex-spot-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31981\",\"dataId\":\"common:phemex-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31982\",\"dataId\":\"common:phemex-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31983\",\"dataId\":\"common:phemex-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31984\",\"dataId\":\"common:contractTrading-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31985\",\"dataId\":\"common:chain-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"47596\",\"dataId\":\"common:phemex-contract-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"48256\",\"dataId\":\"common:phemex-contract-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"48258\",\"dataId\":\"common:phemex-contract-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"50636\",\"dataId\":\"common:index-products\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"57520\",\"dataId\":\"common:engineGray-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"58282\",\"dataId\":\"common:rftEngine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"58283\",\"dataId\":\"common:engine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"160724\",\"dataId\":\"common:phemex-kafka-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"237441\",\"dataId\":\"common:margin-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"251949\",\"dataId\":\"common:phemex-robot-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"264593\",\"dataId\":\"common:contract-engine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"286407\",\"dataId\":\"common:phemex-geo-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"358562\",\"dataId\":\"common:spot-wallet-check-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"386674\",\"dataId\":\"common:phemex-cfg-currency\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"423197\",\"dataId\":\"common:index\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"429178\",\"dataId\":\"common:product-cfg-misc\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"503444\",\"dataId\":\"common:behaviorTrackerConfig\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"533710\",\"dataId\":\"common:product-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"539261\",\"dataId\":\"common:phemex-oauth2-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"553694\",\"dataId\":\"common:seq-clients-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"584675\",\"dataId\":\"common:uta-margin-shard-cfg\",\"group\":\"DEFAULT_GROUP\"}]";

        for (String env : envs) {
            String url = String.format(urlTemplate, env);
            result.append(postRequest(url, body));
        }
        return ResultHolder.success(result.toString());
    }

    private String postRequest(String url, String body) {
        HashMap<String, String> header = new HashMap<>();

        header.put("Accept", "application/json, text/javascript, */*; q=0.01");
        header.put("Authorization", "{\"accessToken\":\"" + accessToken + "\",\"tokenTtl\":18000,\"globalAdmin\":true}");

        String res;
        try {
            res = HttpClientUtil.jsonPost(url, body, header);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }


    public ResultHolder pushNacos(PushNacosRequest pushNacosRequest) {
        if (StringUtils.isEmpty(pushNacosRequest.getUserName()) || StringUtils.isEmpty(pushNacosRequest.getPassword())) {
            return ResultHolder.error("初始化失败，请先到账号设置 设置LDAP账号！！！");
        }

        Map<String, String> header = tokenService.getOpsysToken(phemexOpsysHost, pushNacosRequest);
        JSONObject diffListJsonObject = getDiffList(header);
        if (diffListJsonObject == null || !"1".equals(diffListJsonObject.getString("status"))) {
            log.error("get diff_list failed for : {}", diffListJsonObject);
            return ResultHolder.error("获取diff列表失败！");
        }

        log.info("get diff_list success for : {}", diffListJsonObject);
        JSONArray filteredFiles = filterFilesByDataId(diffListJsonObject.getJSONObject("data").getJSONArray("files"));
        if (filteredFiles.isEmpty()) {
            return ResultHolder.error("没有获取到diff内容，请确认PR是否合并!");
        }

        JSONObject createBatchUpdateBody = createBatchUpdateBody(pushNacosRequest.getDescription(), filteredFiles);
        JSONObject createBatchUpdateJsonObject = createBatchUpdate(header, createBatchUpdateBody);
        if (createBatchUpdateJsonObject == null || !"1".equals(createBatchUpdateJsonObject.getString("status"))) {
            return ResultHolder.error("创建批量更新失败！");
        }

        log.info("create batchNacosUpdate success: {}", createBatchUpdateJsonObject);
        return pushToNacos(header);
    }

    private JSONObject getDiffList(Map<String, String> header) {
        String url = phemexOpsysHost + diffListPath;
        try {
            String response = HttpClientUtil.get(url, header);
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            log.error("Failed to get diff_list: ", e);
            return null;
        }
    }

    private JSONArray filterFilesByDataId(JSONArray files) {
        JSONArray filteredFiles = new JSONArray();
        for (int i = 0; i < files.size(); i++) {
            JSONObject file = files.getJSONObject(i);
            if (StringUtils.startsWith(file.getString("dataid"), "common:")) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private JSONObject createBatchUpdateBody(String description, JSONArray files) {
        JSONObject body = new JSONObject();
        body.put("cmdb_environments", "Fat");
        body.put("description", description);
        body.put("files", files);
        return body;
    }

    private JSONObject createBatchUpdate(Map<String, String> header, JSONObject body) {
        String url = phemexOpsysHost + createBatchUpdatePath;
        try {
            String response = HttpClientUtil.jsonPost(url, body, header);
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            log.error("Failed to create batchNacosUpdate: ", e);
            return null;
        }
    }

    private Integer getNacosDeployId(Map<String, String> header) {
        String url = phemexOpsysHost + getNacosListPath;
        try {
            String response = HttpClientUtil.get(url, header);
            JSONObject responseObject = JSONObject.parseObject(response);
            JSONArray results = responseObject.getJSONArray("results");
            if (results != null && !results.isEmpty()) {
                JSONObject firstResult = results.getJSONObject(0);
                Integer id = firstResult.getInteger("id");
                return id;
            } else {
                // 处理没有结果的情况，或者返回null或者抛出异常
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to get nacos list: ", e);
            return null;
        }
    }

    private ResultHolder pushToNacos(Map<String, String> header) {
        String url = phemexOpsysHost + "/opsys/ncm/batch_nacos_data/" + getNacosDeployId(header) + "/values/";
        try {
            String response = HttpClientUtil.jsonPost(url, header);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if ("1".equals(jsonObject.getString("status"))) {
                log.info("push nacos success: {}", jsonObject);
                return ResultHolder.success("push nacos success");
            }
        } catch (Exception e) {
            log.error("Failed to push to Nacos: ", e);
        }
        return ResultHolder.error("推送到Nacos失败！");
    }

}
