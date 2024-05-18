package venture.Aust1n46.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.Filter;
import venture.Aust1n46.chat.xcut.VersionService;

@ExtendWith(MockitoExtension.class)
public class FormatServiceTest {
	@Mock
	private VentureChat plugin;
	@Mock
	private PlayerApiService playerApiService;
	@Mock
	private ConfigService configService;
	@Mock
	private VersionService versionService;
	@InjectMocks
	private FormatService formatService;

	private static final List<Filter> FILTERS = List.of(new Filter("(b[i1]a?tch(es)?)", "puppy"));

	@Test
	public void testFilter() {
		when(configService.getFilters()).thenReturn(FILTERS);
		final String input = "You are a bitch!";
		final String expected = "You are a puppy!";
		final String actual = formatService.filterChat(input);
		assertEquals(expected, actual);
	}
}
