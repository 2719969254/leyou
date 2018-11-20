package com.leyou.upload.web;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/20
 */
@RestController
@RequestMapping("upload")
public class UploadController {
	private final UploadService uploadService;

	@Autowired
	public UploadController(UploadService uploadService) {
		this.uploadService = uploadService;
	}
	@PostMapping("image")
	public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file){
		return ResponseEntity.ok(uploadService.uploadImage(file));
	}
}
