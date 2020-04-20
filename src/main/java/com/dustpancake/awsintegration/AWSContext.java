package com.dustpancake.awsintegration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.AWSCredentialsProvider;

import com.dustpancake.awsintegration.AWSs3;

@Configuration
public class AWSContext {

	private AWSCredentialsProvider credentialsProvider;

	public AWSContext() {
		credentialsProvider = new DefaultAWSCredentialsProviderChain();
	}

	@Bean
	@Scope(value="singleton")
	public AWSs3 S3Context() {
		return new AWSs3(credentialsProvider);
	}

}