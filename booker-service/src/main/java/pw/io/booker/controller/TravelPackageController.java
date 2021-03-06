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
import pw.io.booker.model.TravelPackage;
import pw.io.booker.repo.TravelPackageRepository;

@RestController
@Transactional
@RequestMapping("/travel-packages")
public class TravelPackageController {

  private TravelPackageRepository travelPackageRepository;

  public TravelPackageController(TravelPackageRepository travelPackageRepository) {
    super();
    this.travelPackageRepository = travelPackageRepository;
  }

  @Transactional(readOnly=true)
  @GetMapping
  public List<TravelPackage> getAll(@RequestHeader ("Authentication-Token")String token) {
    return (List<TravelPackage>) travelPackageRepository.findAll();
  }

  @Transactional
  @PostMapping
  public List<TravelPackage> saveAll(@RequestHeader ("Authentication-Token")String token, @RequestBody List<TravelPackage> travelPackages) {
    return (List<TravelPackage>) travelPackageRepository.saveAll(travelPackages);
  }

  @Transactional
  @PutMapping
  public List<TravelPackage> updateAll(@RequestHeader ("Authentication-Token")String token, @RequestBody List<TravelPackage> travelPackages) throws AuthenticationException {
    for (TravelPackage travelPackage : travelPackages) {
      if (!travelPackageRepository.findById(travelPackage.getTravelPackageId()).isPresent()) {
        throw new AuthenticationException("Travel Package should exist first");
      }
    }
    return (List<TravelPackage>) travelPackageRepository.saveAll(travelPackages);
  }

  @Transactional
  @DeleteMapping
  public List<TravelPackage> deleteAll(@RequestHeader ("Authentication-Token")String token, 
      @RequestParam("travelPackageIdList") List<Integer> travelPackageIdList) {
    List<TravelPackage> travelPackageList =
        (List<TravelPackage>) travelPackageRepository.findAllById(travelPackageIdList);
    travelPackageRepository.deleteAll(travelPackageList);
    return travelPackageList;
  }

  @Transactional(readOnly=true)
  @GetMapping("/{travelPackageId}")
  public TravelPackage getTravelPackage(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId) {
    return travelPackageRepository.findById(travelPackageId).get();
  }

  @Transactional
  @PutMapping("/{travelPackageId}")
  public TravelPackage updateTravelPackage(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId,
      @RequestBody TravelPackage travelPackage) throws AuthenticationException {
    if(travelPackageId != travelPackage.getTravelPackageId()) {
      throw new AuthenticationException("Id is not the same with the object id");
    }
    if (!travelPackageRepository.findById(travelPackage.getTravelPackageId()).isPresent()) {
      throw new AuthenticationException("Travel Package should exist first");
    }
    travelPackage.setTravelPackageId(travelPackageId);
    return travelPackageRepository.save(travelPackage);
  }

  @Transactional
  @DeleteMapping("/{travelPackageId}")
  public TravelPackage deleteTravelPackage(@RequestHeader ("Authentication-Token")String token, @PathVariable("travelPackageId") int travelPackageId) {
    TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId).get();
    travelPackageRepository.delete(travelPackage);
    return travelPackage;
  }
}
