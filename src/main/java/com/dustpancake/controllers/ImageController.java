package com.dustpancake.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.dustpancake.models.MethodInterface;
import com.dustpancake.imageio.FrequencyModulation;
import com.dustpancake.imageio.ImageProcessor;

@RestController
public class ImageController {
	private static final String template = "%s";

	@GetMapping("/interface/{method}")
	public MethodInterface getInterface(@PathVariable("method") String method) {
		/* fetch the image from uri and pass file descriptor to python cli */
		MethodInterface mi;

		if (method.equals("fm")) {
			mi = new MethodInterface(new FrequencyModulation());
		} else {
			mi = new MethodInterface();
		}

		return mi;
	}

	@PostMapping("/image/{method}")
	public ResponseEntity methodCaller(@PathVariable("method") String method, @RequestBody String body) {
		ImageProcessor ip;

		if (method.equals("fm")) {
			ip = new FrequencyModulation(body);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD GLITCH METHOD");
		}

		if (ip.processBody()) {
			ip.process();

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD BODY");
		}

		return ResponseEntity.ok("");
	}

}