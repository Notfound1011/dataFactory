package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.mapper.AdminAccountMapper;
import com.phemex.dataFactory.model.AdminAccount;
import com.phemex.dataFactory.request.ResultHolder;
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
 * @Package: com.phemex.dataFactory.service.listing.MgmtAccountService
 * @Date: 2024年01月26日 15:38
 * @Description:
 */
@Service
public class AdminAccountService {
    private static final Logger log = LoggerFactory.getLogger(AdminAccountService.class);
    @Resource
    private AdminAccountMapper adminAccountMapper;

    public ResultHolder saveOrUpdateAdminAccount(AdminAccount adminAccount) {
        AdminAccount existingAdminAccount = adminAccountMapper.selectByOwner(adminAccount.getOwner(), adminAccount.getType().getValue());

        if (existingAdminAccount != null) {
            // 更新操作
            BeanUtils.copyProperties(adminAccount, existingAdminAccount, "createdAt"); // 不覆盖创建时间
            existingAdminAccount.setUpdatedAt(getFormattedDate());
            adminAccountMapper.updateByPrimaryKeySelective(existingAdminAccount);
        } else {
            // 新增操作
            createAdminAccount(adminAccount);
        }
        return ResultHolder.success(getAdminAccount(adminAccount.getOwner(), adminAccount.getType().getValue()));
    }

    private void createAdminAccount(AdminAccount adminAccountRequest) {
        AdminAccount adminAccount = new AdminAccount();

        String formattedDate = getFormattedDate();

        BeanUtils.copyProperties(adminAccountRequest, adminAccount);
        adminAccount.setStatus(1);
        adminAccount.setCreatedAt(formattedDate);
        adminAccount.setUpdatedAt(formattedDate);

        adminAccountMapper.insertSelective(adminAccount);
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


    public AdminAccount getAdminAccount(String owner, String type) {
        AdminAccount adminAccount = adminAccountMapper.selectByOwner(owner, type);
        if (adminAccount == null) {
            return null;
        }
        AdminAccount adminAccountDTO = new AdminAccount();
        BeanUtils.copyProperties(adminAccount, adminAccountDTO);
        return adminAccountDTO;
    }

    public void updateMgmtAccount(AdminAccount adminAccount) {
        adminAccount.setUpdatedAt(getFormattedDate());
        adminAccountMapper.updateByPrimaryKeySelective(adminAccount);
    }

    public List<AdminAccount> getAdminAccountList(String username, String type, String owner, Integer id) {
        return adminAccountMapper.selectByKey(username, type, owner, id);
    }
}
