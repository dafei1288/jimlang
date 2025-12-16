import org.junit.Test;

import javax.script.*;

public class TestInvocablePersist {
  @Test
  public void testPersistFunctionCall() throws Exception {
    ScriptEngine e = new ScriptEngineManager().getEngineByName("jim");
    e.eval("function add(a,b){ return a + b }\nvar z = 10");
    Object r1 = ((Invocable)e).invokeFunction("add", 2, 3);
    System.out.println("add(2,3)=" + r1);
    // call again in a later eval to ensure function persists
    e.eval("println(z)");
    Object r2 = ((Invocable)e).invokeFunction("add", 40, 2);
    System.out.println("add(40,2)=" + r2);
  }
}
