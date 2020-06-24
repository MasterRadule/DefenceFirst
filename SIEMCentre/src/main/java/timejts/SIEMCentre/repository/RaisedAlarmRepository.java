package timejts.SIEMCentre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.SIEMCentre.model.AlarmType;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.RaisedAlarm;
import timejts.SIEMCentre.model.Severity;

import java.math.BigInteger;
import java.util.Date;

public interface RaisedAlarmRepository extends MongoRepository<RaisedAlarm, BigInteger> {

    Page<RaisedAlarm> findAllByOrderByTimeDesc(Pageable pageable);

    Long countBySeverityEqualsAndTimeBetween(Severity severity, Date startDate, Date endDate);

    Long countByFacilityEqualsAndTimeBetween(Facility facility, Date startDate, Date endDate);

    Long countByAlarmTypeEqualsAndTimeBetween(AlarmType alarmType, Date startDate, Date endDate);
}
