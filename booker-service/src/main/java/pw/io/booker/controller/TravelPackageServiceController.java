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
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.exception.AuthenticationException;
import pw.io.booker.model.Service;
import pw.io.booker.model.TravelPackage;
import pw.io.booker.repo.ServiceRepository;
import pw.io.booker.repo.TravelPackageRepository;

@RestController
@RequestMapping("/travel-packages/{travelPackageId}/services")
public class TravelPackageServiceController {

  private TravelPackageRepository travelPackageRepository;
  private ServiceRepository serviceRepository;

  public TravelPackageServiceController(TravelPackageRepository travelPackageRepository,
      ServiceRepository serviceRepository) {
    super();
    this.travelPackageRepository = travelPackageRepository;
    this.serviceRepository = serviceRepository;
  }
  
  @Transactional(readOnly=true)
  @GetMapping
  public List<Service> getAll(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId) {
    return travelPackageRepository.findById(travelPackageId).get().getAvailableServiceList();
  }
  
  @Transactional
  @PostMapping
  public List<Service> saveAll(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @RequestBody List<Service> services) throws AuthenticationException {
    for(Service service : services) {
      if(serviceRepository.findById(service.getServiceId()).isPresent()) {
        throw new AuthenticationException("Services already exist");
      }
    }
    TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId).get();
    travelPackage.getAvailableServiceList().addAll(services);
    return travelPackageRepository.save(travelPackage).getAvailableServiceList();
  }
  
  @Transactional
  @PutMapping
  public List<Service> updateAll(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @RequestBody List<Service> services) throws AuthenticationException {
    for (Service service: services) {
      if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
        throw new AuthenticationException("Service should exist first");
      }
    }
    return (List<Service>) serviceRepository.saveAll(services);
  }

  @Transactional
  @DeleteMapping
  public List<Service> deleteAll(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId) {
    List<Service> availableServiceList =
    travelPackageRepository.findById(travelPackageId).get().getAvailableServiceList();
    serviceRepository.deleteAll(availableServiceList);
    return availableServiceList;
  }

  @Transactional(readOnly=true)
  @GetMapping("/{serviceId}")
  public Service getService(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId) {
    return serviceRepository.findById(serviceId).get();
  }
  
  @Transactional
  @PutMapping("/{serviceId}")
  public Service updateService(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId, @RequestBody Service service) throws AuthenticationException {
    if(serviceId != service.getServiceId()) {
      throw new AuthenticationException("Id is not the same with the object id");
    }
    if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
      throw new AuthenticationException("Service should exist first");
    }
    service.setServiceId(serviceId);
    return serviceRepository.save(service);
  }

  @Transactional
  @DeleteMapping("/{serviceId}")
  public Service deleteService(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId) {
    Service service = serviceRepository.findById(serviceId).get();
    serviceRepository.deleteById(serviceId);
    return service;
  }
}
