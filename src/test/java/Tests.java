import java.util.logging.Logger;
import org.junit.Test;

public class Tests
{
  Logger logger;

  public Tests()
  {
    logger = Logger.getLogger("bs");
  }

  @Test
  public void test()
  {
    logger.info("This is just a simple test...");
  }
}