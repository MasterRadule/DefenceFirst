package timejts.SIEMCentre.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.SIEMCentre.model.Alarm;

import java.math.BigInteger;

public interface AlarmRepository extends MongoRepository<Alarm, BigInteger> {
}
