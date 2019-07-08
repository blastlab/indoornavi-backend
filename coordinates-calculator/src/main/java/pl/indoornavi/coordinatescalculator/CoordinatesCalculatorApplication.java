package pl.indoornavi.coordinatescalculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pl.indoornavi.coordinatescalculator.algorithms.Algorithm;
import pl.indoornavi.coordinatescalculator.algorithms.GeoN2d;
import pl.indoornavi.coordinatescalculator.algorithms.GeoN3d;
import pl.indoornavi.coordinatescalculator.algorithms.Taylor;

import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration
@EnableCaching
@EnableScheduling
public class CoordinatesCalculatorApplication {
    private static final Logger logger = LoggerFactory.getLogger(CoordinatesCalculatorApplication.class);
    private final Map<String, Class<? extends Algorithm>> algorithms = ImmutableMap.of(
            "GeoN3d", GeoN3d.class,
            "Taylor", Taylor.class,
            "GeoN2d", GeoN2d.class);
    @Value("${pl.indoornavi.algorithm}")
    private String algorithmName;
    @Autowired
    private ApplicationContext ctx;

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setErrorHandler(throwable -> {
            logger.debug("An error occurred during executing task: ");
            logger.debug(throwable.getLocalizedMessage());
        });
        return taskScheduler;
    }

    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }

    @Bean
    public Algorithm getAlgorithm() {
        if (algorithmName != null && algorithms.containsKey(algorithmName)) {
            Class<? extends Algorithm> algorithmClass = algorithms.get(algorithmName);
            AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
            return factory.createBean(algorithmClass);
        } else {
            throw new UnknownAlgorithmRuntimeException();
        }
    }

    private class UnknownAlgorithmRuntimeException extends RuntimeException {
        UnknownAlgorithmRuntimeException() {
            super("No ALGORITHM in environment variables");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(CoordinatesCalculatorApplication.class, args);
    }
}
