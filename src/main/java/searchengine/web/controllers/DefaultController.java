package searchengine.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Tag(name = "DefaultController v1", description = "Operations related to renders the index.html")
public class DefaultController {
  /**
   * Метод формирует страницу из HTML-файла index.html, который находится в папке
   * resources/templates. Это делает библиотека Thymeleaf.
   */
  @RequestMapping("/")
  @Operation(
          summary = "Render Index.html",
          description = "Renders the index.html page using Thymeleaf.",
          tags = {"html"}
  )
  public String index() {
    return "index";
  }
}
