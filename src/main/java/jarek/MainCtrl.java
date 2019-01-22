package jarek;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainCtrl {
  @Autowired ApplicationContext ac;
  
  @RequestMapping("/")
  @ResponseBody
  public String pageHtml()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><ul>");
    sb.append("<li><a href='/stop'>stop</a></li>");
    sb.append("<li><a href='/page'>page</a></li>");
    sb.append("<li><a href='/ajax.html'>ajax.html</a></li>");
    sb.append("<li><a href='/wait?seconds=2'>wait</a></li>");
    sb.append("</ul></html>");
    return sb.toString();
  }

  @RequestMapping("/stop")
  public String stop()
  {
    Thread thr = new Thread(() -> {
      Helper.INSTANCE.sleepNoThrow(1000);
      SpringApplication.exit(ac);
    });
    thr.start();
    return "redirect:/";
  }

}
