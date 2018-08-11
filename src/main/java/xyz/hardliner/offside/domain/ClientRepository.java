package xyz.hardliner.offside.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ClientRepository extends JpaRepository<Client, Long> {
}
