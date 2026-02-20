package drli.sottoscrizione.oss;

import java.util.HashMap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.poste.common.cics.annotations.EnableCICSPool;
 

@SpringBootApplication()
@EnableCICSPool
@ComponentScan(basePackages = "drli.sottoscrizione.oss")
public class SottoscrizioneOssApp implements CommandLineRunner { 
	public static final Integer PORT = 30001; 
	
	protected final MeterRegistry registry = new SimpleMeterRegistry();
	 
	public static void main(String[] args) { 

		HashMap<String, Object> props = new HashMap<>();
		props.put("server.port", PORT);

		new SpringApplicationBuilder().sources(SottoscrizioneOssApp.class).properties(props).run(args);
	}

	@Override
	public void run(String... args) throws Exception { 
		//empty method for quali
	}

	/*
	@Bean
	public GenericObjectPoolConfig genericObjectPoolConfigBean() {
		 GenericObjectPoolConfig config = new GenericObjectPoolConfig();
	        config.setBlockWhenExhausted(configProperties.isBlockWhenExhausted());
	        config.setEvictorShutdownTimeoutMillis(configProperties.getEvictorShutdownTimeoutMillis());
	        config.setFairness(configProperties.isFairness());
	        config.setJmxEnabled(configProperties.isJmEnable());
	        config.setJmxNamePrefix(configProperties.getJmxNamePrefix());
	        config.setLifo(configProperties.isLifo());
	        config.setMaxIdle(configProperties.getMaxIdle());
	        config.setMaxTotal(configProperties.getMaxTotal());
	        config.setMaxWaitMillis(configProperties.getMaxWaitMillis());
	        config.setMinEvictableIdleTimeMillis(configProperties.getMinEvictableIdleTimeMillis());
	        config.setMinIdle(configProperties.getMinIdle());
	        config.setNumTestsPerEvictionRun(configProperties.getNumTestsPerEvictionRun());
	        config.setSoftMinEvictableIdleTimeMillis(configProperties.getSoftMinEvictableIdleTimeMillis());
	        config.setTestOnBorrow(configProperties.isTestOnBorrow());
	        config.setTestOnCreate(configProperties.isTestOnCreate());
	        config.setTestOnReturn(configProperties.isTestOnReturn());
	        config.setTestWhileIdle(configProperties.isTestWhileIdle());
	        config.setTimeBetweenEvictionRunsMillis(configProperties.getTimeBetweenEvictionRunsMillis());
	        
	        return config;
	}

	@Bean
	public CicsDaoSupportFactory cicsDaoSupportFactory() {
		 CicsDaoSupportFactory cf = new CicsDaoSupportFactory(registry, configProperties);
		 return cf;
	}
	
	
	*/
	
	@Bean
	public static JaegerTracer getTracer() {
	   Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
	   Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(false);
	   Configuration config = new Configuration("drli-sottoscrizione-oss").withSampler(samplerConfig).withReporter(reporterConfig);
	   return config.getTracer();
	}
	
}
