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

    Page<Log> findAllByOrderByTimestampDesc(Pageable pageable);

    Long countBySystemEqualsAndTimestampBetween(String system, Date startDate, Date endDate);

    Long countByHostIPEqualsAndTimestampBetween(String machine, Date startDate, Date endDate);

    @Query("{$and:[" +
            "{$or:[{$expr: {$eq: [?0, '']}}, {'message':{$regex:?0,$options:'s'}}]}, " +
            "{$or:[{$expr: {$eq: [?1, '']}}, {'hostIP':{$regex:?1,$options:'s'}}]}, " +
            "{$or:[{$expr: {$eq: [?2, '']}}, {'hostname':{ $eq: ?2 }}]}, " +
            "{$or:[{$expr: {$eq: [?3, null]}}, {'timestamp':{$gt:?3}}]}, " +
            "{$or:[{$expr: {$eq: [?4, null]}}, {'timestamp':{$lt:?4}}]}, " +
            "{$or:[{$expr: {$eq: [?5, null]}}, {'severity':{ $eq: ?5 }}]}, " +
            "{$or:[{$expr: {$eq: [?6, null]}}, {'facility':{ $eq: ?6 }}]}" +
            "]}")
    Page<Log> searchLogs(String messageRegex, String hostIPRegex, String hostname, Date startDate,
                         Date endDate, Severity severity, Facility facility, Pageable pageable);
}
