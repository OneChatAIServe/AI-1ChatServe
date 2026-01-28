package chat.aikf.file.service;

import chat.aikf.file.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.utils.file.FileUtils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储
 * 
 * @author ruoyi
 */
@Primary
@Service
public class LocalSysFileServiceImpl implements ISysFileService
{
//    /**
//     * 访客端图片预览前缀
//     */
//    @Value("${file.prefixToUser}")
//    public String prefixToUser;
//
//
//
//    /**
//     * 管理端图片预览前缀
//     */
//    @Value("${file.prefixToVisitor}")
//    public String prefixToVisitor;


    /**
     * 访问前缀
     */
    @Value("${file.prefix}")
    private String prefix;

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;
    
    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;




    /**
     * 本地文件上传接口
     * 
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception
    {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + prefix +"?fileName="+ name;
        return url;
    }



    /**
     * 本地文件删除接口
     * 
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    @Override
    public void deleteFile(String fileUrl) throws Exception
    {
        String localFile = StringUtils.substringAfter(fileUrl, prefix);
        FileUtils.deleteFile(localFilePath + localFile);
    }

    @Override
    public Resource readFile(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(localFilePath, fileName);

        return new UrlResource(filePath.toUri());
    }
}
