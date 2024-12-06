package com.xuecheng.media;


import io.minio.*;
import io.minio.errors.MinioException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description 测试MinIO
 * @author Mr.M
 * @date 2022/9/11 21:24
 * @version 1.0
 */

public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://121.40.138.25:9090")
                    .credentials("IifoTAzVGMUFY4D3GHLN", "kGtlnNRIEfEqHaFTwBFD4XyFB4hEIkOwTGPfhhVV")
                    .build();

    //上传文件
    @Test
    public  void upload() {
        try {
            GetObjectArgs testbucket = GetObjectArgs.builder()
                    .bucket("testbucket")
                    .object("test001.exe")
                    .build();
            FilterInputStream object = minioClient.getObject(testbucket);
            FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\\\GetQzoneHistory.exe"));
            IOUtils.copy(object,fileOutputStream);

            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }

}