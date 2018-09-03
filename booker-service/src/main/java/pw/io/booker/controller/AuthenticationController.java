package pw.io.booker.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.exception.AuthenticationException;
import pw.io.booker.model.Authentication;
import pw.io.booker.model.Customer;
import pw.io.booker.repo.AuthenticationRepository;
import pw.io.booker.repo.CustomerRepository;
import pw.io.booker.service.TokenCreator;
@RestController
public class AuthenticationController {
	private AuthenticationRepository authenticationRepository;
	private CustomerRepository customerRepository;
	private TokenCreator tokenCreator;
	
	public AuthenticationController(AuthenticationRepository authenticationRepository,
			CustomerRepository customerRepository, TokenCreator tokenCreator) {
		super();
		this.authenticationRepository = authenticationRepository;
		this.customerRepository = customerRepository;
		this.tokenCreator = tokenCreator;
	}

	@PostMapping("/login")
	public String logIn(@RequestBody Authentication authentication) throws AuthenticationException {
		String token = "";
		Optional<Customer> customer = customerRepository.findByUsernameAndPassword(
		authentication.getCustomer().getUsername(), 
		authentication.getCustomer().getPassword());
		if(!customer.isPresent()) {
			throw new AuthenticationException("Invalid username and password.");
		}
		authenticationRepository.deleteAll(authenticationRepository.findByCustomer(customer.get()));
		token = tokenCreator.encode(customer.get());

		Authentication loginAuth = new Authentication();
		loginAuth.setCustomer(customer.get());
		loginAuth.setToken(token);

		authenticationRepository.save(loginAuth);

		return token;
	}
	
	@DeleteMapping("/logout")
	public void logOut(@RequestParam ("token") String token, @RequestHeader ("Authentication-Token")String currentToken){
		authenticationRepository.delete(authenticationRepository.findByToken(token).get());
	}
}
