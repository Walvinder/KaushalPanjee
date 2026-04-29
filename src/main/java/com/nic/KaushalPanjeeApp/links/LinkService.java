package com.nic.KaushalPanjeeApp.links;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class LinkService {

	public LinkResponse getLinksByLanguage(String language) {
		Map<String, String> response = new HashMap<>();

		switch (language.toUpperCase()) {
		case "HI":
			response.put("DDUGKY", "https://files.catbox.moe/7tjlr9.mp4");
			response.put("RSETI", "https://files.catbox.moe/en2rt6.mp4");
			response.put("NRLM", "https://files.catbox.moe/en2rt6.mp4");
			response.put("PMKVY", "https://files.catbox.moe/6p39cx.mp4");
			response.put("PM_VISHWAKARMA", "https://files.catbox.moe/en2rt6.mp4");
			return new LinkResponse("200", "Success", response);

		case "EN":
			response.put("DDUGKY", "https://files.catbox.moe/9h13by.mp4");
			response.put("RSETI", "https://files.catbox.moe/78msk6.mp4");
			response.put("NRLM", "https://files.catbox.moe/78msk6.mp4");
			response.put("PMKVY", "https://files.catbox.moe/6p39cx.mp4");
			response.put("PM_VISHWAKARMA", "https://files.catbox.moe/78msk6.mp4");
			return new LinkResponse("200", "Success", response);

		default:
			// Default to English if unsupported language is provided
			response.put("DDUGKY", "https://files.catbox.moe/9h13by.mp4");
			response.put("RSETI", "https://files.catbox.moe/78msk6.mp4");
			response.put("NRLM", "https://files.catbox.moe/78msk6.mp4");
			response.put("PMKVY", "https://files.catbox.moe/6p39cx.mp4");
			response.put("PM_VISHWAKARMA", "https://files.catbox.moe/78msk6.mp4");
			return new LinkResponse("200", "Success", response);
		}
	}
}
