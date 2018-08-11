package xyz.hardliner.offside.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.hardliner.offside.domain.Client;
import xyz.hardliner.offside.domain.ConsumptionRecord;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebServiceController {

	private final ConsumptionHandler consumptionHandler;

	@PostMapping("/client")
	public Client addClient() {
		Client client = consumptionHandler.newClient();
		log.info("Incoming POST request on '/client'\nNew client successfully created: " + client.toString());
		return client;
	}

	@GetMapping(value = "/consumption")
	public List<ConsumptionRecord> getConsumption(@RequestParam(value = "id") Long id,
	                                              @RequestParam(value = "type", required = false) String type) {
		if (type == null) {
			List<ConsumptionRecord> data = consumptionHandler.actualConsumption(id);
			log.info("Incoming GET request on '/consumption'\nActual consumption data {" + data.toString() + "}");
			return data;
		} else {
			List<ConsumptionRecord> data = consumptionHandler.historyOfConsumption(id, type);
			log.info("Incoming GET request on '/consumption'\nHistory of consumption {" + data.toString() + "}");
			return data;
		}
	}

	@PostMapping(value = "/consumption")
	public ResponseEntity addConsumption(@RequestParam(value = "id") Long id,
	                                     @RequestParam(value = "type") String type,
	                                     @RequestParam(value = "value") BigDecimal value) {
		consumptionHandler.addConsumption(id, type, value);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleConflict(IllegalArgumentException e) {
		log.error("Bad request:", e);
		return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
	}
}
