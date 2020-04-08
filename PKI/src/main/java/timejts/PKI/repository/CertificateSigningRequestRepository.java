package timejts.PKI.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.PKI.model.CertificateSigningRequest;

import java.math.BigInteger;

public interface CertificateSigningRequestRepository extends MongoRepository<CertificateSigningRequest, BigInteger> {
}
