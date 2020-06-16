package timejts.SIEMCentre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.Severity;

import java.math.BigInteger;
import java.util.Date;

public interface LogRepository extends MongoRepository<Log, BigInteger> {

    @Query("{$and:[" +
            "{$or:[{'message':{$regex:?0,$options:'i'}}, {$expr: ?0 == null}]}, " +
            "{$or:[{'hostIP':{$regex:?1,$options:'i'}}, {$expr: ?1 == null}]}, " +
            "{$or:[{'hostname':{ $eq: ?2 }}, {$expr: ?2 == null}]}, " +
            "{$or:[{'timestamp':{$gt:?3,$lt:?4}}, {$expr: ?3 == null && ?4 == null}]}, " +
            "{$or:[{'severity':{ $eq: ?5 }}, {$expr: ?5 == null}]}, " +
            "{$or:[{'facility':{ $eq: ?6 }}, {$expr: ?6 == null}]}" +
            "]}")
    Page<Log> searchLogs(String messageRegex, String hostIPRegex, String hostname, Date startDate,
                         Date endDate, Severity severity, Facility facility, Pageable pageable);
}
