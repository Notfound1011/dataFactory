package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.mapper.TestAccountMapper;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.model.TestAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.TestAccountService
 * @Date: 2024年01月26日 15:38
 * @Description:
 */
@Service
public class TestAccountService {
    private static final Logger log = LoggerFactory.getLogger(TestAccountService.class);
    @Resource
    private TestAccountMapper testAccountMapper;

    public ResultHolder saveOrUpdateTestAccount(TestAccount testAccount) {
        TestAccount existingTestAccount = testAccountMapper.selectByEmailUid(testAccount.getEmail(), testAccount.getUid());

        if (existingTestAccount != null) {
            // 更新操作
            BeanUtils.copyProperties(testAccount, existingTestAccount, "createdAt"); // 不覆盖创建时间
            existingTestAccount.setUpdatedAt(getFormattedDate());
            log.info("Update account!");
            testAccountMapper.updateByPrimaryKeySelective(existingTestAccount);
        } else {
            // 新增操作
            createTestAccount(testAccount);
        }
        return ResultHolder.success(getTestAccount(testAccount.getEmail(), testAccount.getUid()));
    }

    private void createTestAccount(TestAccount testAccountRequest) {
        TestAccount testAccount = new TestAccount();

        String formattedDate = getFormattedDate();

        BeanUtils.copyProperties(testAccountRequest, testAccount);
        testAccount.setStatus(1);
        testAccount.setCreatedAt(formattedDate);
        testAccount.setUpdatedAt(formattedDate);

        testAccountMapper.insertSelective(testAccount);
    }

    private static String getFormattedDate() {
        long timestampSeconds = System.currentTimeMillis() / 1000;

        // 创建日期对象
        Date date = new Date(timestampSeconds * 1000); // Date需要毫秒值

        // 创建SimpleDateFormat对象，用于格式化日期为MySQL的日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化日期
        return sdf.format(date);
    }


    public TestAccount getTestAccount(String email, Integer uid) {
        TestAccount testAccount = testAccountMapper.selectByEmailUid(email, uid);
        if (testAccount == null) {
            return null;
        }
        TestAccount testAccountDTO = new TestAccount();
        BeanUtils.copyProperties(testAccount, testAccountDTO);
        return testAccountDTO;
    }

    public void updateTestAccount(TestAccount testAccount) {
        testAccount.setUpdatedAt(getFormattedDate());
        testAccountMapper.updateByPrimaryKeySelective(testAccount);
    }

    public List<TestAccount> getTestAccountList(String email, String owner, Integer id) {
        return testAccountMapper.selectByKey(email, owner, id);
    }
}
