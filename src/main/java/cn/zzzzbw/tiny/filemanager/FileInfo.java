package cn.zzzzbw.tiny.filemanager;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.Date;

@Builder
@Data
public class FileInfo {

    private Path path;

    private String downloadPath;

    private long size;

    private Date lastModifiedTime;
}
