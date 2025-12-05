package gui.application;
/*
 * @(#) ConfigManager.java  1.0  [11:35:59 PM] Dec 4, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 4, 2025
 * @version: 1.0
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
	private static ConfigManager instance;
	private Properties properties;

	// Tên file config
	private static final String CONFIG_FILE = "config.properties";

	private ConfigManager() {
		properties = new Properties();
		loadConfig();
	}

	public static synchronized ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	private void loadConfig() {
		// Dùng ClassLoader để tìm file trong thư mục resources/classpath
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (input == null) {
				System.err.println("Xin lỗi, không tìm thấy file cấu hình: " + CONFIG_FILE);
				return;
			}
			// Load dữ liệu từ file vào object Properties
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Hàm lấy giá trị theo key
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	// Hàm lấy giá trị với giá trị mặc định (nếu key không tồn tại)
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}