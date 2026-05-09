package com.josh.life.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.josh.life.client.GhibliClient;
import com.josh.life.dto.ImagesResponse;
import com.josh.life.tool.ToolConfiguration;

@Service
public class AIService {

	ChatClient chatClient;
	
	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	@Autowired
	OpenAiImageModel imageModel;
	
	@Autowired
	GhibliClient ghibliClient;
	
	ChatMemory cm = MessageWindowChatMemory.builder().build();
	
	public AIService(ChatClient.Builder builder) {
		chatClient = builder.
				build();
	}
	
	@Autowired
	private ToolConfiguration tools;
	
	
	public String test() {
		return "test succeeded";
	}
	
	public String normalChat(String prompt) {
		return chatClient.prompt(prompt)
				.tools(tools)
				.call()
				.content();
	}


	public String ghibliImage(MultipartFile image) {
		
		String base64 = "";
		try {
			base64 = Base64.getEncoder().encodeToString(image.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ImagesResponse response = ghibliClient.getGhibli(
			"Bearer " + apiKey,
			"""
				Convert this image into Studio Ghibli style image
			""",
			"gpt-image-1-mini",
			1, 
			"low",
			image
		);
		
		byte[] generatedImage = Base64.getDecoder().decode(response.getData().get(0).getB64_json());
		

		
		String unique = UUID.randomUUID().toString();
		
		String fileName = unique + ".png";
		
		try {
			Files.write(Path.of("uploads", fileName), generatedImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "http://localhost:8080/" + fileName;
		
	}


	public String wallpaper(String context, String width, String height) {
		
		String prompt = context;
		
		String b64 = imageModel
			.call(
				new ImagePrompt(prompt,
					ImageOptionsBuilder
						.builder()
						.N(1)
						.responseFormat("b64_json")
						.width(Integer.valueOf(width))
						.height(Integer.valueOf(height))
						.build()
				)
			)
			.getResult()
			.getOutput()
			.getB64Json();
		
		byte[] generatedImage = Base64.getDecoder().decode(b64);
		
		String unique = UUID.randomUUID().toString();
		
		String fileName = unique + ".png";
		
		try {
			Files.write(Path.of("uploads", fileName), generatedImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "http://localhost:8080/" + fileName;
	}


	public String generateSummary(MultipartFile multipartFile) {
		// TODO Auto-generated method stub
		
		String prompt = """
			You are an expert resume reviewer, career mentor, and productivity coach.

			Analyze the uploaded resume PDF carefully and provide a highly structured, detailed, and professional response.
			
			Your response must contain the following sections with proper headings, bullet points, and "clear formatting with HTML tags".
			I will render your response direclty in HTML so use tags.
			
			Return ONLY raw HTML.
			Do not include markdown code fences like ```html or ```. Give the response inside of a <div> block.
			Use inline simple styles only. Use good emoji as well
			
			# 1. Resume Overview
			- Briefly summarize the candidate’s profile.
			- Mention their likely career direction, strengths, and current level (beginner/intermediate/experienced).
			- Highlight standout skills, technologies, projects, certifications, or achievements.
			
			# 2. Resume Improvement Suggestions
			Review the resume critically and provide actionable improvements for:
			- Resume structure and formatting
			- Clarity and readability
			- ATS (Applicant Tracking System) optimization
			- Technical skills section
			- Projects section
			- Experience section
			- Education section
			- Grammar and wording improvements
			- Missing sections or weak areas
			- Portfolio/GitHub/LinkedIn suggestions
			- Quantification of achievements
			- Professional summary improvements
			
			For every issue:
			- Explain WHY it is a problem
			- Explain HOW to improve it
			- Give EXAMPLES whenever possible
			
			# 3. Career Growth Analysis
			Based on the candidate’s profile:
			- Suggest suitable career paths
			- Identify the technologies or skills they should learn next
			- Mention important concepts they may be missing
			- Suggest advanced topics relevant to their career
			- Recommend projects they should build
			- Recommend certifications, courses, or learning roadmaps
			- Suggest industry trends relevant to their profile
			- Mention possible job roles they can target
			
			Make the advice personalized to the resume.
			
			# 4. Atomic Habits for Career Growth
			Suggest practical daily/weekly habits that can significantly improve the candidate’s career over time.
			
			Include habits related to:
			- Learning consistency
			- Coding practice
			- Reading/documentation habits
			- Building projects
			- Networking
			- Resume maintenance
			- Interview preparation
			- Health/productivity
			- Time management
			- Communication skills
			
			For each habit:
			- Explain why it matters
			- Explain how to implement it realistically
			- Keep habits small, practical, and sustainable
			
			# 5. Priority Action Plan
			Create a prioritized roadmap with:
			- Immediate improvements (this week)
			- Short-term goals (1–3 months)
			- Long-term goals (6–12 months)
			
			Use clear bullet points.
			
			# 6. Final Encouragement
			End with motivating but realistic career advice tailored to the candidate’s profile.
			
			Important Instructions:
			- Be constructive, honest, and specific.
			- Do not give generic advice unless necessary.
			- Personalize recommendations based on the actual resume.
			- Keep the response professional and easy to read.
			- Use headings, subheadings, and bullet points extensively.
			- If the resume lacks information in some areas, explicitly mention what is missing.
		""";
		
		String output = chatClient
				.prompt(prompt).user(us -> us.media(
						MimeTypeUtils.parseMimeTypes("application/pdf").getFirst(), 
						multipartFile.getResource()
					)
				)
				.call()
				.content();
		
		return output;
	}
	
}
