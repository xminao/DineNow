package xyz.xminao.dinenow.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.properties.COSProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class COSUtils {
    @Autowired
    private COSProperties cosProperties;

    public String upload(MultipartFile multipartFile) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = multipartFile.getInputStream();

        // 避免文件覆盖
        String originFilename = multipartFile.getOriginalFilename();
        if (originFilename == null) {
            throw new CustomException("文件名为空");
        }
        String fileName = UUID.randomUUID() + originFilename.substring(originFilename.lastIndexOf("."));

        // 上传文件到COS
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-beijing");
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 指定文件将要存放的存储桶
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = "images/" + fileName;
        // 上传文件到 COS
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucketName(), key, inputStream, new ObjectMetadata());
        cosClient.putObject(putObjectRequest);

        String url = cosProperties.getEndpoint().split("//")[0] + "//" + cosProperties.getBucketName() + "." + cosProperties.getEndpoint().split("//")[1] + "/" + key;

        cosClient.shutdown();
        return key;
    }

    public String getFileURL(String fileName) {
        String url = cosProperties.getEndpoint().split("//")[0] + "//" + cosProperties.getBucketName() + "." + cosProperties.getEndpoint().split("//")[1] + "/" + fileName;
        return url;
    }

    public byte[] download(String fileName) throws IOException {
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        Region region = new Region("ap-beijing");
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);

        log.info("download filename: {}", fileName);
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosProperties.getBucketName(), fileName);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInputStream = cosObject.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);
        cosObjectInputStream.close();
        cosClient.shutdown();
        return bytes;
    }
}