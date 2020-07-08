package br.com.dcc.springbatchexamples.configuration;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ErrorHandlingRestatConfiguration {

	@Bean
	public Tasklet errorHandlingRestatTasklet() {
		return (contribution, chunkContext) -> {
			Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
			if (stepExecutionContext.containsKey("ran")) {
				log.info("This time we'll let it go.");
				return RepeatStatus.FINISHED;
			} else {
				log.info("I don't think so...");
				chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);
				throw new RuntimeException("Not this time...");
			}
		};
	}

	@Bean
	public Step errorHandlingRestatStep1(StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("ErrorHandlingRestatStep1")
				.tasklet(errorHandlingRestatTasklet())
				.build();
	}

	@Bean
	public Step errorHandlingRestatStep2(StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("ErrorHandlingRestatStep2")
				.tasklet(errorHandlingRestatTasklet())
				.build();
	}

	@Bean
	public Job errorHandlingRestatJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		return jobBuilderFactory.get("ErrorHandlingRestatJob")
				.start(errorHandlingRestatStep1(stepBuilderFactory))
				.next(errorHandlingRestatStep2(stepBuilderFactory))
				.build();
	}

}
