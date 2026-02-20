package org.jod.kafka.config;

import java.util.HashMap;
import java.util.Map;

public class JodSaslJaasSCRAMConfig implements IJodSaslJaasConfig {

	private String username = "";
	private String password = "";

	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<>();
		
		StringBuilder jaas = new StringBuilder();
		jaas.append("org.apache.kafka.common.security.scram.ScramLoginModule");
		jaas.append(" ");
		jaas.append("required");
		jaas.append(" ");
		jaas.append("username=\"").append(username).append("\"");
		jaas.append(" ");
		jaas.append("password=\"").append(password).append("\"");
		jaas.append(";");
		
		properties.put("sasl.jaas.config", jaas.toString());
		
		return properties;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}