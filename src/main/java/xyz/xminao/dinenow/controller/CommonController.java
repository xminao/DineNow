package xyz.xminao.dinenow.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.utils.COSUtils;

import java.io.IOException;
import java.io.OutputStream;

// 文件上传与下载

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private COSUtils cosUtils;

    // 文件上传
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("上传文件：{}", file);
        String fileName = cosUtils.upload(file);
        return Result.success(fileName);
    }

    // 文件下载
    @RequestMapping("/download")
    public void download(@RequestParam("name") String fileName, HttpServletResponse response) throws IOException {
        // 获取输出流
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        byte[] bytes = cosUtils.download(fileName);
        outputStream.write(bytes);
        outputStream.close();
    }
}
