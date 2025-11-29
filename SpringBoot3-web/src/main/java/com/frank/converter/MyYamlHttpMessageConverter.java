package com.frank.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class MyYamlHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	private ObjectMapper yamlObjectMapper = null; // 假設有一個可以處理 YAML 的物件映射器

	public MyYamlHttpMessageConverter() {

		super(new MediaType("application", "yaml", Charset.forName("UTF-8")));
		// 初始化 yamlObjectMapper，例如使用 Jackson 的 YAML 模組
		YAMLFactory yamlFactory = new YAMLFactory();
		this.yamlObjectMapper = new ObjectMapper(yamlFactory);
	}

	@Override // 設定此轉換器支援的類別
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override // @RequestBody
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override // @ResponseBody 把物件寫出去
	protected void writeInternal(Object returnObject, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		try (OutputStream os = outputMessage.getBody()) {
			outputMessage.getHeaders().setContentType(new MediaType("text", "yaml", Charset.forName("UTF-8")));
			this.yamlObjectMapper.writeValue(os, returnObject);
		}

	}

}
