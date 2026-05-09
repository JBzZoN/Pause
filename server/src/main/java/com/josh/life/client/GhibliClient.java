package com.josh.life.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.josh.life.dto.ImagesResponse;



@FeignClient(url = "https://api.openai.com/v1/", name = "ghibli")
public interface GhibliClient {

	@PostMapping(
            value = "/images/edits",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
	public ImagesResponse getGhibli(@RequestHeader("Authorization") String key, @RequestPart String prompt, @RequestPart String model, @RequestPart Integer n, @RequestPart String quality, @RequestPart MultipartFile image);
	
}
