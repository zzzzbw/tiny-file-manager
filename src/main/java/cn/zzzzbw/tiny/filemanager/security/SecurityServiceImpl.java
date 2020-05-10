package cn.zzzzbw.tiny.filemanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by zzzzbw
 * @since 2020/5/10 21:01
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public boolean login(String key) {
        return securityProperties.getSecurityKey().equals(key);
    }
}
