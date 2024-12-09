package project.asm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {

    @Test
    public void testGetLoopOpcodesCount() {
        Analyzer analyzer = new Analyzer();
        analyzer.setLoopOpcodesCount(10);
        assertEquals(10, analyzer.getLoopOpcodesCount());
    }

    @Test
    public void testGetConditionalOpcodesCount() {
        Analyzer analyzer = new Analyzer();
        analyzer.setConditionalOpcodesCount(20);
        assertEquals(20, analyzer.getConditionalOpcodesCount());
    }

    @Test
    public void testGetVariableDeclarationsCount() {
        Analyzer analyzer = new Analyzer();
        analyzer.setVariableDeclarationsCount(30);
        assertEquals(30, analyzer.getVariableDeclarationsCount());
    }
}
