package com.eldercare.system.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
@Component
public class ImgUploadUtil {
    @Value("${elder-care.alioss.endpoint}")
    private String endpoint;

    @Value("${elder-care.alioss.access-key-id}")
    private String accessKeyId;

    @Value("${elder-care.alioss.access-key-secret}")
    private String accessKeySecret;

    @Value("${elder-care.alioss.bucket-name}")
    private String bucketName;
    //阿里云oss
    public String upload(byte[] bytes, String objectName, String contentType) throws com.aliyuncs.exceptions.ClientException {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建PutObject请求。
            log.info("开始上传文件{},,{},,{}",bucketName,objectName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes),objectMetadata);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);
        log.info("ImgUploadUtil_upload()_文件上传到:{}", stringBuilder.toString());
        return stringBuilder.toString();
    }
}
