package timejts.PKI.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.PKI.model.CertificateSigningRequest;

import java.math.BigInteger;
import java.util.Optional;

public interface CertificateSigningRequestRepository extends MongoRepository<CertificateSigningRequest, BigInteger> {

    Optional<CertificateSigningRequest> findById(BigInteger id);
}
