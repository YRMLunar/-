package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
 @Slf4j
 @Service
 @RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

 @Value("${minio.bucket.files}")
 private String bucket_Files;
 private final MediaFilesMapper mediaFilesMapper;
private final MinioClient minioClient;
private final MediaFileService currentPoxy;

 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
  File file = new File(localFilePath);
  if (!file.exists()) {

   XueChengPlusException.cast("文件不存在");
  }
  String filename = uploadFileParamsDto.getFilename();
  String extension = StringUtils.substringAfter(filename, ".");
  String fileMd5 = getFileMd5(file);
  String mimeType=getMimeType(extension);
  String defalutFolderPath = getDefalutFolderPath();
  String objectName=defalutFolderPath+fileMd5+extension;
  boolean b = addFileToMinio(objectName, localFilePath, bucket_Files, mimeType);
  uploadFileParamsDto.setFileSize(file.length());
 MediaFiles mediaFiles = currentPoxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectName);

 UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);


  return uploadFileResultDto;
 }
@Transactional
 public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName) {

  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);

  if (mediaFiles == null) {
    mediaFiles = new MediaFiles();
   BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
   mediaFiles.setId(fileMd5);
   mediaFiles.setFileId(fileMd5);
   mediaFiles.setCompanyId(companyId);
   mediaFiles.setUrl("/" + bucketFiles + "/" + objectName);
   mediaFiles.setBucket(bucketFiles);
   mediaFiles.setCreateDate(LocalDateTime.now());
   mediaFiles.setAuditMind("002003");
   mediaFiles.setStatus("1");
   int insert = mediaFilesMapper.insert(mediaFiles);
   if (insert != 1) {
    log.error("保存文件信息到数据库失败",mediaFiles.toString());
    XueChengPlusException.cast("保存文件信息失败");
   }
log.debug("保存文件信息到数据库成功",mediaFiles.toString());


   return mediaFiles;
  }



  return null;
 }


 private boolean addFileToMinio(String objectName,String localFilePath,String bucketName,String mimeType)
 {
  try {
   UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
           .bucket(bucketName)
           .object(objectName)
           .filename(localFilePath)
           .contentType(mimeType)
           .build();
   minioClient.uploadObject(uploadObjectArgs);
   log.debug("上传文件成功",bucketName,objectName);
   return true;

  } catch (Exception e) {
   e.printStackTrace();
  log.debug("上传文件失败",bucketName,objectName,e.getMessage());
  XueChengPlusException.cast("上传文件失败");

  }
return  false;

 }


 private String getDefalutFolderPath()
 {
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  String folder = simpleDateFormat.format(new Date()).replace("-", "/") + "/";
  return folder;


 }

 private String getMimeType(String extension) {
  if (extension ==null)
  {
   extension ="";
  }
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
  String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  if (extensionMatch != null) {
   mimeType=extensionMatch.getMimeType();
  }
  return mimeType;
 }

 private String getFileMd5(File file) {
  try(FileInputStream inputStream = new FileInputStream(file)) {
   String md5 = DigestUtils.md5DigestAsHex(inputStream);
   return md5;


  } catch (IOException e) {
   e.printStackTrace();
      return null;

  }


 }
}
