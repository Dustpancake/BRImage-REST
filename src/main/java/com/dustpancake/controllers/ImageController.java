package com.dustpancake.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dustpancake.models.MethodInterface;

import com.dustpancake.awsintegration.AWSContext;
import com.dustpancake.awsintegration.AWSs3;

import com.dustpancake.imageio.FrequencyModulation;
import com.dustpancake.imageio.ImageProcessor;

import java.lang.Thread;


@RestController
public class ImageController {
	@Autowired 
	private AWSContext awscontext;

	@Value("${image.directory}")
	private String imageDirectory;

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
		String newUri;
		String resp;

		if (method.equals("fm")) {
			ip = new FrequencyModulation(body, imageDirectory);

		} else return badResponse("BAD GLITCH METHOD");

		resp = ip.processBody();
		if (!resp.equals("")) return badResponse(resp);

		AWSs3 s3 = awscontext.S3Context();

		resp = s3.getFile(ip.getUri());
		if(!resp.equals("")) return badResponse(resp);

		ip.process();
		new Thread(() -> {if ( ip.finish() == 0 ) s3.writeToBucket(ip.outputName);} ).start();

		return ResponseEntity.ok(
			ip.outputName
		);
	}

	private ResponseEntity badResponse(String body) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
}