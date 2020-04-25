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
import java.net.URLDecoder;

public class AWSs3 {
	final private AmazonS3Client s3access;

	@Value("${bucket.name}")
	private String bucketName;

	@Value("${image.directory}")
	private String imageDirectory;

	@Value("${bucket.outputdir}")
	private String bucketOutDir;

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
			System.out.println("New uri: " + uri);
			URL url = new URL(uri);
			if (url.getHost().contains(bucketName+".s3.")) {
				fileName = getFromAws(url);
			} else {
				fileName = writeImageToFile(url);
			}
			System.out.println("Saved new file " + fileName);

		} catch(Exception e) {
			// anything else
			System.out.println(e);
			return "";
		}
		return fileName;
	}

	private String getFromAws(URL url) throws AmazonServiceException, IOException {
		String key = URLDecoder.decode(url.getFile().substring(1), "UTF-8");
		S3Object o = s3access.getObject(bucketName, key);
		S3ObjectInputStream s3inp = o.getObjectContent();
		String fileName = key.substring(key.lastIndexOf('/') + 1).replaceAll("[^\\w\\.]","");
		writeImageToFile(s3inp, imageDirectory+fileName);
		return fileName;
	}

	public String touchBucketFile(String fileName) {
		String itemUrl;
		try {
			s3access.putObject(bucketName, bucketOutDir + fileName, "");
			itemUrl = s3access.getResourceUrl(bucketName, bucketOutDir + fileName);
		} catch(AmazonServiceException e) {
			// aws went wrong
			System.out.println(e);
			return "AWS_ERROR";
		}
		return itemUrl;
	}

	public boolean uploadFileToBucket(String fileName) {
		try {
			s3access.putObject(bucketName, bucketOutDir+fileName, new File(imageDirectory + fileName));
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
		fileName = fileName.replaceAll("[^\\w\\.]","");
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