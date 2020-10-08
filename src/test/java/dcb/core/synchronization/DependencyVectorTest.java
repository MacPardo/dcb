package dcb.core.synchronization;

import dcb.core.exceptions.DcbException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class DependencyVectorTest {
    static final int ID = 0;

    @Test
    void updateOwnerTimestamp() throws Exception {
        var vec = DependencyVector.createNew(ID);
        Assertions.assertEquals(0L, vec.get(ID));
        var vec2 = vec.updateOwnerTimestamp(10L);
        Assertions.assertEquals(10L, vec2.get(ID));
        Assertions.assertThrows(DcbException.class, () -> vec2.updateOwnerTimestamp(5L));
    }

    @Test
    void update() throws DcbException {
        var firstMap = new HashMap<Integer, Long>();
        firstMap.put(1, 15L);
        var secondMap = new HashMap<Integer, Long>();
        secondMap.put(1, 10L);
        secondMap.put(2, 20L);
        var thirdMap = new HashMap<Integer, Long>();
        thirdMap.put(ID, 30L);

        var vec = DependencyVector.createNew(ID);
        vec = vec.update(firstMap);
        Assertions.assertEquals(15L, vec.get(1));
        vec = vec.update(secondMap);
        Assertions.assertEquals(15L, vec.get(1));
        Assertions.assertEquals(20L, vec.get(2));

        var finalVec = vec;
        Assertions.assertThrows(DcbException.class, () -> finalVec.update(thirdMap));
    }
}