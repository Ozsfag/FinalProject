package searchengine.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Default controller v1", description = "Operations related to renders the index.html")
public class DefaultController {
  /**
   * Метод формирует страницу из HTML-файла index.html, который находится в папке
   * resources/templates. Это делает библиотека Thymeleaf.
   */
  @Operation(
      summary = "Render Index.html",
      description = "Renders the index.html page using Thymeleaf.")
  public String index() {
    return "index";
  }
}
