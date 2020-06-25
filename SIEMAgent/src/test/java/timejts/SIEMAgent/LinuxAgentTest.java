package timejts.SIEMAgent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LinuxAgentTest {

    @Autowired
    private AgentMain agentMain;

    @Test
    void testAgent(){
        //agentMain.
        agentMain.linuxProcess();
    }

}
