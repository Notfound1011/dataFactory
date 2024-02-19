package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.NacosSync
 * @Date: 2024年01月17日 17:17
 * @Description:
 */
@Service
public class NacosSyncService {
    private static final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6MTY5MDg3ODE5Nn0.0tz2Vh9fU4YXyGKWBLZNnaT_JSDmn_YUg0AofCGfa8o";
    private static final String urlTemplate = "http://alb-sap-nacos-424928052.ap-southeast-1.elb.amazonaws.com/nacos/v1/cs/configs?clone=true&tenant=%s&policy=OVERWRITE&namespaceId=&accessToken=" + accessToken;

    public ResultHolder syncFatNacos(List<String> envs) {
        StringBuilder result = new StringBuilder();
        String body = "[{\"cfgId\":\"31978\",\"dataId\":\"common:phemex-spot-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31979\",\"dataId\":\"common:phemex-spot-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31980\",\"dataId\":\"common:phemex-spot-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31981\",\"dataId\":\"common:phemex-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31982\",\"dataId\":\"common:phemex-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31983\",\"dataId\":\"common:phemex-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31984\",\"dataId\":\"common:contractTrading-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"31985\",\"dataId\":\"common:chain-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"47596\",\"dataId\":\"common:phemex-contract-cfg-app\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"48256\",\"dataId\":\"common:phemex-contract-cfg-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"48258\",\"dataId\":\"common:phemex-contract-cfg-internal-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"50636\",\"dataId\":\"common:index-products\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"57520\",\"dataId\":\"common:engineGray-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"58282\",\"dataId\":\"common:rftEngine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"58283\",\"dataId\":\"common:engine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"160724\",\"dataId\":\"common:phemex-kafka-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"237441\",\"dataId\":\"common:margin-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"251949\",\"dataId\":\"common:phemex-robot-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"264593\",\"dataId\":\"common:contract-engine-shard-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"286407\",\"dataId\":\"common:phemex-geo-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"358562\",\"dataId\":\"common:spot-wallet-check-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"386674\",\"dataId\":\"common:phemex-cfg-currency\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"423197\",\"dataId\":\"common:index\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"429178\",\"dataId\":\"common:product-cfg-misc\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"503444\",\"dataId\":\"common:behaviorTrackerConfig\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"533710\",\"dataId\":\"common:product-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"539261\",\"dataId\":\"common:phemex-oauth2-gateway\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"553694\",\"dataId\":\"common:seq-clients-cfg\",\"group\":\"DEFAULT_GROUP\"},{\"cfgId\":\"584675\",\"dataId\":\"common:uta-margin-shard-cfg\",\"group\":\"DEFAULT_GROUP\"}]";

        for (String env : envs) {
            String url = String.format(urlTemplate, env);
            result.append(postRequest(url, body));
        }
        return ResultHolder.success(result.toString());
    }

    private static String postRequest(String url, String body) {
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
}
