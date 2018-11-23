package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/20
 */
@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
	private final UploadProperties uploadProperties;
	private final FastFileStorageClient fastFileStorageClient;
	//private static final List<String> ALL_TYPE = Arrays.asList("image/png","image/jpeg","image/bmp");

	@Autowired
	public UploadService(FastFileStorageClient fastFileStorageClient, UploadProperties uploadProperties) {
		this.fastFileStorageClient = fastFileStorageClient;
		this.uploadProperties = uploadProperties;
	}

	public String uploadImage(MultipartFile file) {
		//校验文件后缀
		String contentType = file.getContentType();
		if(!uploadProperties.getAllowTypes().contains(contentType)){
			throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
		}
		try {
			//校验文件内容是否为图片
			BufferedImage read = ImageIO.read(file.getInputStream());
			if (read == null) {
				throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
			}

			/*上传文件路径
			File dest = new File("G:\\image", file.getOriginalFilename());
			将文件保存在本地
			file.transferTo(dest);
			上传到FastDFS服务器*/

			//String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
			String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");

			StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

			//返回正常路径
			return uploadProperties.getBaseUrl()+storePath.getFullPath();
		} catch (IOException e) {
			//上传失败
			e.printStackTrace();
			log.error("[文件上传]上传文件失败",e);
			throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
		}

	}
}
