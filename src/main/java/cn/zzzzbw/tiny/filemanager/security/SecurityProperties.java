package cn.zzzzbw.tiny.filemanager.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("security")
public class SecurityProperties {

    private String securityKey;

}
