package com.frank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
public class RenderController {
	// Rendering：一種視圖物件。
	@GetMapping("/yahoo")
	public Rendering render() {
		// Rendering.redirectTo("/aaa"); //重新導向到目前專案根路徑下的 aaa
		return Rendering.redirectTo("https://tw.yahoo.com").build();
	}
}
