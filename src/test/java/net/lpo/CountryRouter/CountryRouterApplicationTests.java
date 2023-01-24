package net.lpo.CountryRouter;

import net.lpo.CountryRouter.dto.Route;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryRouterApplicationTests extends AbstractTest {
	private static final Logger log = LoggerFactory.getLogger(CountryRouterApplicationTests.class);

	@Override
	@BeforeAll
	public void setUp() {
		super.setUp();
	}

	@Test
	public void helloNeighbour() throws Exception {
		String uri = "/routing/CZE/POL";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		Route resultRoute = mapFromJson(mvcResult.getResponse().getContentAsString(), Route.class);
		Route expectedRoute = new Route(Arrays.asList("CZE", "POL"));
		assertEquals(expectedRoute, resultRoute);
	}

	@Test
	public void threeSteps() throws Exception {
		String uri = "/routing/CZE/ITA";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		Route resultRoute = mapFromJson(mvcResult.getResponse().getContentAsString(), Route.class);
		Route expectedRoute = new Route(Arrays.asList("CZE", "AUT", "ITA"));
		assertEquals(expectedRoute, resultRoute);
	}

	@Test
	public void fiveSteps() throws Exception {
		String uri = "/routing/CZE/PRT";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		Route resultRoute = mapFromJson(mvcResult.getResponse().getContentAsString(), Route.class);
		Route expectedRoute = new Route(Arrays.asList("CZE", "DEU", "FRA", "ESP", "PRT"));
		assertEquals(expectedRoute, resultRoute);
	}

	@Test
	public void originUnknown() throws Exception {
		String uri = "/routing/QQQ/USA";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(406, status);
	}

	@Test
	public void destinationUnknown() throws Exception {
		String uri = "/routing/CZE/QQQ";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(406, status);
	}

	@Test
	public void noWayFromNagano() throws Exception {
		String uri = "/routing/JPN/CZE";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
	}

	@Test
	public void noWayToNagano() throws Exception {
		String uri = "/routing/USA/JPN";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
	}

	@Test
	public void stayingHome() throws Exception {
		String uri = "/routing/CZE/CZE";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(406, status);
	}

	@Test
	public void myBonnieIsOverTheOcean() throws Exception {
		String uri = "/routing/USA/FRA";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
	}

	@Test
	public void thereAndBackAgain() throws Exception {
		String uri = "/routing/PRT/MYS";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		Route resultRoute = mapFromJson(mvcResult.getResponse().getContentAsString(), Route.class);
		Route expectedRoute = new Route(Arrays.asList("PRT", "ESP", "FRA", "DEU", "POL", "RUS", "CHN", "MMR", "THA", "MYS"));
		assertEquals(expectedRoute, resultRoute);

		uri = "/routing/MYS/PRT";
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		Route routeBack = mapFromJson(mvcResult.getResponse().getContentAsString(), Route.class);

		Collections.reverse(routeBack.getRoute());
		assertEquals(expectedRoute, routeBack);
	}

}
