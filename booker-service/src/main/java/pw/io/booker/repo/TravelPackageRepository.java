package pw.io.booker.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pw.io.booker.model.Service;
import pw.io.booker.model.TravelPackage;

@Repository
public interface TravelPackageRepository extends CrudRepository<TravelPackage, Integer> {
	List<TravelPackage> findByavailableServiceListIn(List <Service> ServiceList);
}
