package pw.io.booker.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import pw.io.booker.exception.AuthenticationException;
import pw.io.booker.repo.AuthenticationRepository;
@Aspect
@Component
public class AuthenticationAspect {
	private AuthenticationRepository authenticationRepository;


	public AuthenticationAspect(AuthenticationRepository authenticationRepository) {
		super();
		this.authenticationRepository = authenticationRepository;
	}

	
	@Before("execution(* pw.io.booker.controller..*(..)) && args(token,..)")
	public void beforeExecution(JoinPoint joinPoint, String token) throws Throwable{
		Logger logger = Logger.getLogger(AuthenticationAspect.class);
		logger.info("Before the execution: " + joinPoint.getSignature());
		logger.info("Before the execution(Method): " + joinPoint.getSignature().getName());
	}
	
	@After("execution(* pw.io.booker.controller..*(..)) && args(token,..)")
	public void afterExecution(JoinPoint joinPoint, String token) {
		Logger logger = Logger.getLogger(AuthenticationAspect.class);
		logger.info("After the execution: " + joinPoint.getSignature());
		logger.info("After the execution (Method): " + joinPoint.getSignature().getName());
	}
	
	@Around("execution(* pw.io.booker.controller..*(..)) && args(token,..) && !execution(* pw.io.booker.controller.CustomerController.saveAll(..)),*)")
	public Object accountValidation(ProceedingJoinPoint proceedingJoinpoint, String token) throws Throwable {
		if(!(authenticationRepository.findByToken(token).isPresent()) || token == null) {
			throw new AuthenticationException("Access Denied.");
		}
		return proceedingJoinpoint.proceed();
	}
}
