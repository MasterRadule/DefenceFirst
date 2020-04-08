package timejts.PKI.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.PKI.model.CertificateSigningRequest;

import java.util.Optional;

public interface CertificateSigningRequestRepository extends MongoRepository<CertificateSigningRequest, Integer> {

    Optional<CertificateSigningRequest> findById(Integer id);
}
