package selenideTest.page;

import com.codeborne.selenide.SelenideElement;
import com.epam.jdi.uitests.web.selenium.elements.composite.WebPage;

public class HtmlForms extends WebPage {
  public SelenideElement next = $("div.w3-clear:nth-child(3) > a:nth-child(2)");
}
