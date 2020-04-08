package timejts.PKI.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import timejts.PKI.model.RevokedCertificate;

import java.util.Optional;

public interface RevokedCertificatesRepository extends MongoRepository<RevokedCertificate, String> {

    Optional<RevokedCertificate> findById(String id);

    Optional<RevokedCertificate> findByCommonName(String commonName);
}
