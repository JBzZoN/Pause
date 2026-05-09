package com.josh.life.controller;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.josh.life.dto.ResponseDTO;
import com.josh.life.service.AIService;
import com.josh.life.tool.ToolConfiguration;

@RestController
@RequestMapping("ai")
@CrossOrigin(origins = "*")
public class AIController {
	
	@Autowired
	private AIService aiService;
	
	@GetMapping("test")
	public ResponseEntity<?> test() {
		return ResponseEntity.ok(new ResponseDTO("success", aiService.test()));
	}
	
	@GetMapping("chat")
	public ResponseEntity<?> chat(@RequestParam String prompt) {
		return ResponseEntity.ok(aiService.normalChat(prompt));
	}
	
	@PostMapping("ghibli")
	public ResponseEntity<?> ghibli(@RequestBody MultipartFile image) {
		return ResponseEntity.ok(aiService.ghibliImage(image));
	}
	
	@GetMapping("wallpaper")
	public ResponseEntity<?> wallpaper(@RequestParam String context, @RequestParam String width, @RequestParam String height) {
		return ResponseEntity.ok(aiService.wallpaper(context, width, height));
	}
	
	@PostMapping("pdf")
	public ResponseEntity<?> pdf(MultipartFile file) {
		return ResponseEntity.ok(aiService.generateSummary(file));
	}
	
}
