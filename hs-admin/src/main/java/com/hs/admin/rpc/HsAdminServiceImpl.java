package com.hs.admin.rpc;

import com.hs.admin.api.HsAdminUserService;
import org.apache.dubbo.config.annotation.Service;

@Service
public class HsAdminServiceImpl implements HsAdminUserService {

    @Override
    public void getUserInfo(String userId) {
        return;
    }
}
