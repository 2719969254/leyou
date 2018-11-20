package com.leyou.upload.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/20
 */
@Service
@Slf4j
public class UploadService {
	public String uploadImage(MultipartFile file) {
		try {
			//上传文件路径
			File dest = new File("G:\\image", file.getOriginalFilename());
			//将文件保存在本地
			file.transferTo(dest);
			//返回正常路径
			return "hhh";
		} catch (IOException e) {
			//上传失败
			e.printStackTrace();
			log.error("上传文件失败",e);
			throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
		}


		return null;
	}
}
