package com.dustpancake.awsintegration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProvider;

import com.dustpancake.awsintegration.AWSs3;

@Configuration
public class AWSContext {

	private AWSCredentialsProvider credentialsProvider;

	public AWSContext() {
		credentialsProvider = new ProfileCredentialsProvider();
	}

	@Bean
	@Scope(value="singleton")
	public AWSs3 S3Context() {
		return new AWSs3(credentialsProvider);
	}

}