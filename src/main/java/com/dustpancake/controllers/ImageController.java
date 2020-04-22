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

import com.dustpancake.awsintegration.AWSContext;
import com.dustpancake.awsintegration.AWSs3;

import com.dustpancake.imageio.ImplementMethod;
import com.dustpancake.imageio.ImageProcessor;

import java.lang.Thread;

import java.util.LinkedHashMap;

import com.dustpancake.propertyread.MethodReader;

@RestController
public class ImageController {
	@Autowired 
	private AWSContext awscontext;

	@Autowired
	private MethodReader methodReader;

	@Value("${image.directory}")
	private String imageDirectory;

	@GetMapping("/interface/{method}")
	public ResponseEntity<?> getInterface(@PathVariable("method") String method) {
		LinkedHashMap<String, ?> content = methodReader.getInterface(method);
		if (content == null) return new ResponseEntity<>("BAD METHOD", HttpStatus.BAD_REQUEST);
		else return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@PostMapping("/image/{method}")
	public ResponseEntity methodCaller(@PathVariable("method") String method, @RequestBody String body) {
		ImageProcessor ip;
		String newUri;
		String resp;

		try {
			ip = new ImplementMethod(
				body, 
				imageDirectory, 
				methodReader.getConfigOf(method)
			);

		} catch(Exception e) { 
			return badResponse("BAD GLITCH METHOD");
		}

		if (!ip.processBody()) return badResponse(ip.info());

		AWSs3 s3 = awscontext.S3Context();

		resp = s3.getFile(ip.inputUri);
		if(resp.equals("")) return badResponse("BAD FILE");
		ip.inputUri = resp;

		
		newUri = s3.touchBucketFile(ip.outputName);
		if (resp.equals("AWS_ERROR")) return badResponse(resp);

		new Thread(() -> { 
			if ( ip.process() == 0 ) 
				s3.uploadFileToBucket(ip.outputName);
			else {
				System.out.println("BRIMAGE CRASH");
			}
		}).start();

		return ResponseEntity.ok(
			newUri
		);
	}

	private ResponseEntity badResponse(String body) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
}