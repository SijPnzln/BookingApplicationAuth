package pw.io.booker.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.exception.AuthenticationException;
import pw.io.booker.model.Customer;
import pw.io.booker.repo.CustomerRepository;

@RestController
@Transactional
@RequestMapping("/customers")
public class CustomerController {

  private CustomerRepository customerRepository;

  public CustomerController(CustomerRepository customerRepository) {
    super();
    this.customerRepository = customerRepository;
  }
  
  @Transactional(readOnly=true)
  @GetMapping
  public List<Customer> getAll(@RequestHeader ("Authentication-Token")String token) {
    return (List<Customer>) customerRepository.findAll();
  }

  @Transactional
  @PostMapping
  public List<Customer> saveAll(@RequestBody List<Customer> customers) throws AuthenticationException {
    for(Customer customer : customers) {
      if(customerRepository.findById(customer.getCustomerId()).isPresent()) {
        throw new AuthenticationException("Customers already exist");
      }
    }
    return (List<Customer>) customerRepository.saveAll(customers);
  }

  @Transactional
  @PutMapping
  public List<Customer> updateAll(@RequestHeader ("Authentication-Token")String token, @RequestBody List<Customer> customers) throws AuthenticationException {
    for(Customer customer : customers) {
      if(!customerRepository.findById(customer.getCustomerId()).isPresent()) {
        throw new AuthenticationException("Customers should exist first");
      }
    }
    return (List<Customer>) customerRepository.saveAll(customers);
  }

  @Transactional
  @DeleteMapping
  public List<Customer> deleteAll(@RequestHeader ("Authentication-Token")String token, @RequestParam("customerIdList") List<Integer> customerIdList) {
    List<Customer> customerList = (List<Customer>) customerRepository.findAllById(customerIdList);
    customerRepository.deleteAll(customerList);
    return customerList;
  }
  
  @Transactional(readOnly=true)
  @GetMapping("/{customerId}")
  public Customer getCustomer(@RequestHeader ("Authentication-Token")String token, @PathVariable("customerId") int customerId) {
    return customerRepository.findById(customerId).get();
  }

  @Transactional
  @PutMapping("/{customerId}")
  public Customer updateCustomer(@RequestHeader ("Authentication-Token")String token, @PathVariable("customerId") int customerId,
      @RequestBody Customer customer) throws AuthenticationException {
    if(customerId != customer.getCustomerId()) {
      throw new AuthenticationException("Id is not the same with the object id");
    }
    if (!customerRepository.findById(customer.getCustomerId()).isPresent()) {
      throw new AuthenticationException("Customers should exist first");
    }
    customer.setCustomerId(customerId);
    return customerRepository.save(customer);
  }

  @Transactional
  @DeleteMapping("/{customerId}")
  public Customer deleteCustomer(@RequestHeader ("Authentication-Token")String token, @PathVariable("customerId") int customerId) {
    Customer customer = customerRepository.findById(customerId).get();
    customerRepository.delete(customer);
    return customer;
  }
}
