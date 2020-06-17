package timejts.SIEMCentre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.SIEMCentre.model.RaisedAlarm;

import java.math.BigInteger;

public interface RaisedAlarmRepository extends MongoRepository<RaisedAlarm, BigInteger> {

    Page<RaisedAlarm> findAllByOrderByTimeDesc(Pageable pageable);
}
