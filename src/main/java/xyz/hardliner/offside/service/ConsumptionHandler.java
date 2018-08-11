package xyz.hardliner.offside.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.hardliner.offside.domain.Client;
import xyz.hardliner.offside.domain.ClientRepository;
import xyz.hardliner.offside.domain.ConsumptionRecord;
import xyz.hardliner.offside.domain.ConsumptionType;
import xyz.hardliner.offside.domain.RecordsRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static xyz.hardliner.offside.domain.ConsumptionType.COLD_WATER;
import static xyz.hardliner.offside.domain.ConsumptionType.GAS;
import static xyz.hardliner.offside.domain.ConsumptionType.HOT_WATER;

@Service
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
public class ConsumptionHandler {

	private final ClientRepository clientRepository;
	private final RecordsRepository recordsRepository;

	public Client newClient() {
		Client client = new Client();
		clientRepository.save(client);
		recordsRepository.save(new ConsumptionRecord(client, GAS));
		recordsRepository.save(new ConsumptionRecord(client, COLD_WATER));
		recordsRepository.save(new ConsumptionRecord(client, HOT_WATER));
		return client;
	}

	public List<ConsumptionRecord> actualConsumption(Long id) {
		Optional<Client> optionalClient = clientRepository.findById(id);
		if (!optionalClient.isPresent()) {
			return Collections.emptyList();
		}
		Client client = optionalClient.get();
		List<ConsumptionRecord> data = new ArrayList<>();
		data.add(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(client, GAS));
		data.add(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(client, COLD_WATER));
		data.add(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(client, HOT_WATER));
		return data;
	}

	public List<ConsumptionRecord> historyOfConsumption(Long id, String type) {
		ConsumptionType consumptionType = ConsumptionType.get(type);
		Optional<Client> optionalClient = clientRepository.findById(id);
		if (!optionalClient.isPresent()) {
			return Collections.emptyList();
		}
		Client client = optionalClient.get();
		return recordsRepository.findAllByClientAndTypeOrderByDateDesc(client, consumptionType);
	}

	public void addConsumption(Long id, String type, BigDecimal value) {
		ConsumptionType consumptionType = ConsumptionType.get(type);
		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No client found with id=" + id));
		ConsumptionRecord oldRecord = recordsRepository.findFirstByClientAndTypeOrderByDateDesc(client, consumptionType);
		ConsumptionRecord newRecord = new ConsumptionRecord(client, consumptionType, value);
		validateValues(oldRecord, newRecord);
		recordsRepository.save(newRecord);
	}

	private void validateValues(ConsumptionRecord oldRecord, ConsumptionRecord newRecord) {
		if (newRecord.getValue().compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Consumption cannot be negative");
		}
		if (newRecord.getValue().compareTo(oldRecord.getValue()) < 0) {
			throw new IllegalArgumentException("Consumption cannot be less than previous record");
		}
	}
}
