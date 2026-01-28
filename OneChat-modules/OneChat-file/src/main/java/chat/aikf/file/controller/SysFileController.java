package chat.aikf.file.controller;

import chat.aikf.common.core.utils.DefaultAvatarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.utils.file.FileUtils;
import chat.aikf.file.service.ISysFileService;
import chat.aikf.system.api.domain.SysFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件请求处理
 * 
 * @author ruoyi
 */
@RestController
public class SysFileController
{
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    public R<SysFile> upload(MultipartFile file)
    {
        try
        {
            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            sysFile.setUploadType("local");
            return R.ok(sysFile);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 文件删除请求
     */
    @DeleteMapping("delete")
    public R<Boolean> delete(String fileUrl)
    {
        try
        {
            if (!FileUtils.validateFilePath(fileUrl))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            sysFileService.deleteFile(fileUrl);
            return R.ok();
        }
        catch (Exception e)
        {
            log.error("删除文件失败", e);
            return R.fail(e.getMessage());
        }
    }





    /**
     * im会话附件上传请求(访客端)
     */
    @PostMapping("/chatUploadToVisitor")
    public R<SysFile> chatUploadToVisitor(MultipartFile file)
    {
        try
        {
            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            sysFile.setUploadType("local");
            return R.ok(sysFile);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 聊天图片等附件访问接口
     * @param fileName
     * @return
     */
    @GetMapping("/chatMsgFile")
    public ResponseEntity<Resource> chatMsgFile(String fileName) {

        try {
            // 获取文件资源
            Resource resource = sysFileService.readFile(fileName);

            // 检查文件是否存在
            if (resource.exists() || resource.isReadable()) {
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

                // 返回文件资源
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }



    /**
     * 获取访客头像(内置访客头像,可自定义)
     * @param avatarName
     * @return
     */
    @GetMapping("/avatars/{avatarName}")
    public ResponseEntity<Resource> findVisitorAvatars(@PathVariable String avatarName){

        Resource resource = new ClassPathResource("static" +  DefaultAvatarUtils.getAvatarPathByName(avatarName));

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl("public, max-age=86400"); // 可选缓存

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }



}
