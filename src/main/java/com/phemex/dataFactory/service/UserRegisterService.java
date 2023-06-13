package com.phemex.dataFactory.service;

import com.alibaba.fastjson.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.common.utils.PspMintBatch;
import com.phemex.dataFactory.dto.RegistrationInfo;
import com.phemex.dataFactory.dto.UserRegisterRequest;
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
    private static final String API_REGISTER = "https://api10-fat.phemex.com/phemex-user/users/register";
    private static final String API_CONFIRM_REGISTER = "https://api10-fat.phemex.com/phemex-user/users/confirm/register";
    private static final String OUTPUT_FILE_PATH = "src/main/resources/output/user_register_result.csv";

    public static List<RegistrationInfo> registerUsers(UserRegisterRequest userRegisterRequest) throws Exception {
        List<RegistrationInfo> registrationInfo = new ArrayList<>();
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("id,email,result\n");

        for (int i = userRegisterRequest.getNumStart(); i <= userRegisterRequest.getNumEnd(); i++) {
            String email = generateEmail(userRegisterRequest.getEmailPrefix(), i, userRegisterRequest.getEmailSuffix());
            try {
                JSONObject res = registerUser(email, userRegisterRequest.getPassword());
                int code = (int) res.get("code");
                if (code == 0) {
                    registrationInfo.add(new RegistrationInfo(i, email, "success", (String) res.get("msg")));
                    logger.info("Registered user: {}", email);
                } else {
                    registrationInfo.add(new RegistrationInfo(i, email, "failed", (String) res.get("msg")));
                }
                strBuffer.append(i + "," + email + ",").append(code == 0 ? "success" : "failed").append("\n");
            } catch (Exception e) {
                logger.error("Failed to register user: {}", email, e);
                registrationInfo.add(new RegistrationInfo(i, email, "failed", e.toString()));
                continue; // 继续下一次循环
            }
            Thread.sleep(userRegisterRequest.getReqDelayMs());
        }
        writeToFile(OUTPUT_FILE_PATH, strBuffer.toString(), false);
        return registrationInfo;
    }

    private static String generateEmail(String emailPrefix, int index, String emailSuffix) {
        return emailPrefix + index + emailSuffix;
    }

    private static JSONObject registerUser(String email, String password) throws Exception {
        HashMap<String, String> header = getHeader();

        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("encryptVersion", 0);
        body.put("referralCode", "");
        body.put("nickName", "");
        body.put("lang", "en");
        body.put("group", 0);
        JSONObject jsonObj = new JSONObject(body);

        String res = HttpClientUtil.jsonPost(API_REGISTER, jsonObj.toString(), header);
        JSONObject jsonRes = (JSONObject) JSONObject.parse(res);
        logger.info("Register info: {}", jsonRes);
        String code = (String) jsonRes.getJSONObject("data").get("code");

        String confirmUrl = API_CONFIRM_REGISTER + "?code=" + code + "&mailCode=111111" + "&email=" + email;
        String response = HttpClientUtil.get(confirmUrl, header);
        JSONObject jsonConfirmRes = (JSONObject) JSONObject.parse(response);
        logger.info("Register confirm info: {}", jsonConfirmRes);
        return jsonConfirmRes;
    }
}
