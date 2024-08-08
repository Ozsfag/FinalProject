package searchengine;

import java.util.Date;
import org.junit.jupiter.api.*;
import org.rnorth.testcontainers.containers.PostgreSQLContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

  @LocalServerPort private Integer port;
  @Autowired SiteRepository siteRepository;
  @Autowired PageRepository pageRepository;
  @Autowired LemmaRepository lemmaRepository;
  @Autowired IndexRepository indexRepository;

  private final TestRestTemplate testRestTemplate = new TestRestTemplate();
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

  @BeforeAll
  public static void beforeAll() {
    postgreSQLContainer.start();
  }

  @AfterAll
  public static void afterAll() {
    postgreSQLContainer.stop();
  }

  @DynamicPropertySource
  public static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
    dynamicPropertyRegistry.add(
        "spring.datasource.username", () -> postgreSQLContainer.getUsername());
    dynamicPropertyRegistry.add(
        "spring.datasource.password", () -> postgreSQLContainer.getPassword());
  }

  @BeforeEach
  public void fillingDB() {
    for (int i = 0; i < 10; i++) {
      SiteModel siteModel =
          SiteModel.builder()
              .status(Status.INDEXING)
              .id(i)
              .url("https://example" + i + ".com")
              .name("Example Site" + i)
              .statusTime(new Date())
              .version(0)
              .lastError("")
              .build();
      siteRepository.saveAndFlush(siteModel);
    }
  }

  @AfterEach
  public void clearDB() {
    siteRepository.deleteAll();
    pageRepository.deleteAll();
    lemmaRepository.deleteAll();
    indexRepository.deleteAll();
  }

  @Test
  public void testFindSiteByPath() {
    testRestTemplate
        .getRestTemplate()
        .getForEntity("http://localhost:" + port + "/site/https://example0.com", SiteModel.class);
  }
}
