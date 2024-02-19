package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.mapper.MgmtAccountMapper;
import com.phemex.dataFactory.model.MgmtAccount;
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
public class MgmtAccountService {
    private static final Logger log = LoggerFactory.getLogger(MgmtAccountService.class);
    @Resource
    private MgmtAccountMapper mgmtAccountMapper;

    public ResultHolder saveOrUpdateMgmtAccount(MgmtAccount mgmtAccount) {
        MgmtAccount existingMgmtAccount = mgmtAccountMapper.selectByOwner(mgmtAccount.getOwner());

        if (existingMgmtAccount != null) {
            // 更新操作
            BeanUtils.copyProperties(mgmtAccount, existingMgmtAccount, "createdAt"); // 不覆盖创建时间
            existingMgmtAccount.setUpdatedAt(getFormattedDate());
            mgmtAccountMapper.updateByPrimaryKeySelective(existingMgmtAccount);
        } else {
            // 新增操作
            createMgmtAccount(mgmtAccount);
        }
        return ResultHolder.success(getMgmtAccount(mgmtAccount.getOwner()));
    }

    private void createMgmtAccount(MgmtAccount mgmtAccountRequest) {
        MgmtAccount mgmtAccount = new MgmtAccount();

        String formattedDate = getFormattedDate();

        BeanUtils.copyProperties(mgmtAccountRequest, mgmtAccount);
        mgmtAccount.setStatus(1);
        mgmtAccount.setCreatedAt(formattedDate);
        mgmtAccount.setUpdatedAt(formattedDate);

        mgmtAccountMapper.insertSelective(mgmtAccount);
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


    public MgmtAccount getMgmtAccount(String owner) {
        MgmtAccount mgmtAccount = mgmtAccountMapper.selectByOwner(owner);
        if (mgmtAccount == null) {
            return null;
        }
        MgmtAccount mgmtAccountDTO = new MgmtAccount();
        BeanUtils.copyProperties(mgmtAccount, mgmtAccountDTO);
        return mgmtAccountDTO;
    }

    public void updateMgmtAccount(MgmtAccount mgmtAccount) {
        mgmtAccount.setUpdatedAt(getFormattedDate());
        mgmtAccountMapper.updateByPrimaryKeySelective(mgmtAccount);
    }

    public List<MgmtAccount> getMgmtAccountList(String username, String owner, Integer id) {
        return mgmtAccountMapper.selectByKey(username, owner, id);
    }
}
