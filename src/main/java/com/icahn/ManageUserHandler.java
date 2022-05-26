package com.icahn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;


public class ManageUserHandler implements RequestStreamHandler{

	private static final Logger logger = LogManager.getLogger(ManageUserHandler.class);

	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		System.out.print("handleRequest method called");
		//output.write(0);
	}
}
