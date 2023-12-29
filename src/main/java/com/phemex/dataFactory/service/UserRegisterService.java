package com.phemex.dataFactory.service;

import com.alibaba.fastjson.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.common.utils.PspMintBatch;
import com.phemex.dataFactory.response.RegistrationResp;
import com.phemex.dataFactory.request.UserRegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.getHeader;
import static com.phemex.dataFactory.common.utils.LoadTestCommon.writeToFile;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.UserRegisterService
 * @Date: 2023年06月12日 09:58
 * @Description:
 */
public class UserRegisterService {
    private static Logger logger = LoggerFactory.getLogger(PspMintBatch.class);

    /**
     * @Description: 批量注册账号
     * @Date: 2023/6/9
     * @Param append: true or false (是否追加)
     **/
    private static final String API_REGISTER = "https://api10-fat3.phemex.com/phemex-user/users/register";
    private static final String API_CONFIRM_REGISTER = "https://api10-fat3.phemex.com/phemex-user/users/confirm/register";
    private static final String OUTPUT_FILE_PATH = "src/main/resources/output/user_register_result.csv";

    public static List<RegistrationResp> registerUsers(UserRegisterRequest userRegisterRequest) throws Exception {
        List<RegistrationResp> registrationResp = new ArrayList<>();
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("id,email,result\n");
        List<Integer> numList = userRegisterRequest.getNumList();
        if (null == numList || numList.isEmpty()) {
            for (int i = userRegisterRequest.getNumStart(); i <= userRegisterRequest.getNumEnd(); i++) {
                register(userRegisterRequest, registrationResp, strBuffer, i);
            }
        } else {
            for (int i : numList) {
                register(userRegisterRequest, registrationResp, strBuffer, i);
            }
        }

        writeToFile(OUTPUT_FILE_PATH, strBuffer.toString(), false);
        return registrationResp;
    }

    private static void register(UserRegisterRequest userRegisterRequest, List<RegistrationResp> registrationResp, StringBuffer strBuffer, int i) throws InterruptedException {
        String email = generateEmail(userRegisterRequest.getEmailPrefix(), i, userRegisterRequest.getEmailSuffix());
        try {
            JSONObject res = registerUser(email, userRegisterRequest.getPassword(), userRegisterRequest.getReferralCode());
            int code = (int) res.get("code");
            if (code == 0) {
                registrationResp.add(new RegistrationResp(i, email, "success", (String) res.get("msg")));
                logger.info("Registered user: {}", email);
            } else {
                registrationResp.add(new RegistrationResp(i, email, "failed", (String) res.get("msg")));
            }
            strBuffer.append(i + "," + email + ",").append(code == 0 ? "success" : "failed").append("\n");
        } catch (Exception e) {
            logger.error("Failed to register user: {}", email, e);
            registrationResp.add(new RegistrationResp(i, email, "failed", e.toString()));
            return;
        }
        Thread.sleep(userRegisterRequest.getReqDelayMs());
    }

    private static String generateEmail(String emailPrefix, int index, String emailSuffix) {
        return emailPrefix + index + emailSuffix;
    }

    private static JSONObject registerUser(String email, String password, String referralCode) throws Exception {
        HashMap<String, String> header = getHeader();

        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("encryptVersion", 0);
        body.put("referralCode", referralCode);
        body.put("nickName", "");
        body.put("lang", "en");
        body.put("group", 0);
        JSONObject jsonObj = new JSONObject(body);

        String res = HttpClientUtil.jsonPost(API_REGISTER, jsonObj.toString(), header);
        JSONObject jsonRes = (JSONObject) JSONObject.parse(res);
        logger.info("Register header: {}", header);
        logger.info("Register info: {}", jsonRes);
        String code = (String) jsonRes.getJSONObject("data").get("code");

        String confirmUrl = API_CONFIRM_REGISTER + "?code=" + code + "&mailCode=111111" + "&email=" + email;
        String response = HttpClientUtil.get(confirmUrl, header);
        JSONObject jsonConfirmRes = (JSONObject) JSONObject.parse(response);
        logger.info("Register confirm info: {}", jsonConfirmRes);
        return jsonConfirmRes;
    }
}
