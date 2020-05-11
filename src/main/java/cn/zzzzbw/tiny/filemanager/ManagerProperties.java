package cn.zzzzbw.tiny.filemanager;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("manager")
public class ManagerProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = "upload-dir";

	private String securityKey;

}
