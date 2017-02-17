package com.codependent.mutualauth.web;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeRestController {

	@GetMapping("/")
	public String home(Principal principal){
		return String.format("Hello %s!", principal.getName());
	}
	
}
