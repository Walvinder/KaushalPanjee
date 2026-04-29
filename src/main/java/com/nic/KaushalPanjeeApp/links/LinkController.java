package com.nic.KaushalPanjeeApp.links;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
public class LinkController {

	@Autowired
	private LinkService linkService;

	@PostMapping("/getLink")
	public LinkResponse getLink(@RequestBody LanguageRequest request) {
		String language = request.getLanguage();
		return linkService.getLinksByLanguage(language);
	}
}
