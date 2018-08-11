package xyz.hardliner.offside.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordsRepository extends JpaRepository<ConsumptionRecord, Long> {

	ConsumptionRecord findFirstByClientAndTypeOrderByDateDesc(Client client, ConsumptionType type);

	List<ConsumptionRecord> findAllByClientAndTypeOrderByDateDesc(Client client, ConsumptionType type);

}
