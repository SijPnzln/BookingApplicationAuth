package pw.io.booker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import pw.io.booker.model.Authentication;
import pw.io.booker.repo.AuthenticationRepository;
import pw.io.booker.repo.CustomerRepository;
import pw.io.booker.service.TokenCreator;

public class AuthenticationController {
	private AuthenticationRepository authenticationRepository;
	private CustomerRepository customerRepository;
	
	public AuthenticationController(AuthenticationRepository authenticationRepository, CustomerRepository customerRepository) {
		super();
		this.authenticationRepository = authenticationRepository;
	}

	@RequestMapping("/login")
	@PostMapping
	  public Authentication logIn(@RequestBody Authentication authentication) {
		String username = authentication.getCustomer().getUsername();
		String password = authentication.getCustomer().getPassword();
		if(customerRepository.findByUsernameAndPassword(username, password).isPresent()){
			authenticationRepository.deleteAll(authenticationRepository.findByCustomer(authentication.getCustomer()));
			TokenCreator tokenCreator = new TokenCreator();
			authentication.setToken(tokenCreator.encode(authentication.getCustomer()));
			authenticationRepository.save(authentication);
		} else {
			throw new RuntimeException("Access Denied."); // gawa ng personalized na exception
		}
	    return authentication;
	  }
}
