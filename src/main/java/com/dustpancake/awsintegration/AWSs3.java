package com.dustpancake.awsintegration;

import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import com.amazonaws.regions.Regions;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.MalformedURLException;

public class AWSs3 {
	final private AmazonS3Client s3access;

	@Value("${bucket.name}")
	private String bucketName;

	@Value("${image.directory}")
	private String imageDirectory;

	public AWSs3(AWSCredentialsProvider awscred) {
		s3access = (AmazonS3Client)AmazonS3ClientBuilder
			.standard()
			.withCredentials(awscred)
			.withRegion(Regions.EU_WEST_1)
			.build();
	}

	public String getFile(String uri) {
		String fileName = "";
		try {
			URL url = new URL(uri);
			fileName = writeImageToFile(url);
			System.out.println("Saved new file " + fileName);

		} catch(MalformedURLException e) {
			System.out.println(e);
			return "";

		}  catch(IOException e) {
			// file reading went wrong
			System.out.println(e);
			return "";

		} catch(Exception e) {
			// anything else
			System.out.println(e);
			return "";
		}
		return fileName;
	}

	public String touchBucketFile(String fileName) {
		String itemUrl;
		try {
			s3access.putObject(bucketName, fileName, "");
			itemUrl = s3access.getResourceUrl(bucketName, fileName);
		} catch(AmazonServiceException e) {
			// aws went wrong
			System.out.println(e);
			return "AWS_ERROR";
		}
		return itemUrl;
	}

	public boolean uploadFileToBucket(String fileName) {
		try {
			s3access.putObject(bucketName, fileName, new File(imageDirectory + fileName));
		} catch(AmazonServiceException e) {
			// aws went wrong
			System.out.println(e);
			return false;
		} finally {
			new File(imageDirectory + fileName).delete();
		}
		return true;
	}

	private String writeImageToFile(URL uri) throws IOException {
		String fileName = uri.getFile();
		fileName = fileName.substring(fileName.lastIndexOf('/') + 1).replaceAll("[^\\w\\s]","");
		BufferedInputStream bif = new BufferedInputStream(uri.openStream());
		writeImageToFile(bif, imageDirectory + fileName);
		return fileName;
	}

	private static <T extends InputStream> void writeImageToFile(T reader, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buffer = new byte[1024];
		int i = 0;
		while ((i = reader.read(buffer, 0, 1024)) > 0) {
			fos.write(buffer, 0, i);
		}
		fos.close();
	}
}