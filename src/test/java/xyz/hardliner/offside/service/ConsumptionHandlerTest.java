package xyz.hardliner.offside.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.hardliner.offside.domain.Client;
import xyz.hardliner.offside.domain.ClientRepository;
import xyz.hardliner.offside.domain.ConsumptionRecord;
import xyz.hardliner.offside.domain.ConsumptionType;
import xyz.hardliner.offside.domain.RecordsRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static xyz.hardliner.offside.domain.ConsumptionType.COLD_WATER;
import static xyz.hardliner.offside.domain.ConsumptionType.GAS;
import static xyz.hardliner.offside.domain.ConsumptionType.HOT_WATER;

@RunWith(SpringRunner.class)
public class ConsumptionHandlerTest {

	@Mock
	private ClientRepository clientRepository;
	@Mock
	private RecordsRepository recordsRepository;

	private ConsumptionHandler consumptionHandler;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		consumptionHandler = new ConsumptionHandler(clientRepository, recordsRepository);
	}

	@Test
	public void newClientTest() {
		consumptionHandler.newClient();
		verify(clientRepository, times(1)).save(any(Client.class));
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(3)).save(any(ConsumptionRecord.class));
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void actualConsumptionTest() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.of(new Client()));
		consumptionHandler.actualConsumption(1L);
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(GAS));
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(COLD_WATER));
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(HOT_WATER));
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void actualConsumptionTestWithNoClientFound() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(consumptionHandler.actualConsumption(1L)).isEqualTo(Collections.emptyList());
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void historyOfConsumptionTest() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.of(new Client()));
		consumptionHandler.historyOfConsumption(1L, "gas");
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(1))
				.findAllByClientAndTypeOrderByDateDesc(any(Client.class), eq(GAS));
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void historyOfConsumptionTestWithNoClientFound() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(consumptionHandler.historyOfConsumption(1L, "gas")).isEqualTo(Collections.emptyList());
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void addConsumptionTest() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.of(new Client()));
		when(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), any(ConsumptionType.class)))
				.thenReturn(new ConsumptionRecord(new Client(), GAS, BigDecimal.valueOf(30)));
		consumptionHandler.addConsumption(1L, "gas", BigDecimal.valueOf(55.2));
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(GAS));
		verify(recordsRepository, times(1))
				.save(any(ConsumptionRecord.class));
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void addConsumptionTestWithNoClientFound() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("No client found with id=1");
		consumptionHandler.addConsumption(1L, "gas", BigDecimal.valueOf(55.2));
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void addConsumptionTestWithNegativeValue() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.of(new Client()));
		when(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), any(ConsumptionType.class)))
				.thenReturn(new ConsumptionRecord(new Client(), GAS, BigDecimal.valueOf(30)));
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Consumption cannot be negative");
		consumptionHandler.addConsumption(1L, "gas", BigDecimal.valueOf(-10));
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(GAS));
		verifyNoMoreInteractions(recordsRepository);
	}

	@Test
	public void addConsumptionTestWithWrongValue() {
		when(clientRepository.findById(anyLong())).thenReturn(Optional.of(new Client()));
		when(recordsRepository.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), any(ConsumptionType.class)))
				.thenReturn(new ConsumptionRecord(new Client(), GAS, BigDecimal.valueOf(30)));
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Consumption cannot be less than previous record");
		consumptionHandler.addConsumption(1L, "gas", BigDecimal.valueOf(20));
		verify(clientRepository, times(1)).findById(anyLong());
		verifyNoMoreInteractions(clientRepository);
		verify(recordsRepository, times(1))
				.findFirstByClientAndTypeOrderByDateDesc(any(Client.class), eq(GAS));
		verifyNoMoreInteractions(recordsRepository);
	}
}