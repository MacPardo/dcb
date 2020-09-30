package dcb.core.synchronization;

import dcb.core.exceptions.DcbException;

import java.util.HashMap;
import java.util.Map;

public class DependencyVector {
    private final int ownerComponentId;
    private final Map<Integer, Long> componentIdToTimestamp;

    public DependencyVector(int componentId, Map<Integer, Long> componentIdToTimestamp) {
        this.ownerComponentId = componentId;
        this.componentIdToTimestamp = componentIdToTimestamp;
    }

    public static DependencyVector createNew(int componentId) {
        return new DependencyVector(componentId, new HashMap<>());
    }

    public DependencyVector copy() {
        Map<Integer, Long> mapClone = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : componentIdToTimestamp.entrySet()) {
            mapClone.put(entry.getKey(), entry.getValue());
        }
        return new DependencyVector(ownerComponentId, mapClone);
    }

    public long get(int componentId) {
        return componentIdToTimestamp.getOrDefault(componentId, 0L);
    }

    public DependencyVector updateOwnerTimestamp(long timestamp) throws Exception {
        if (get(ownerComponentId) > timestamp) {
            throw new Exception();
        }
        DependencyVector newDependencyVector = copy();
        newDependencyVector.componentIdToTimestamp.put(ownerComponentId, timestamp);
        return newDependencyVector;
    }

    public DependencyVector update(Map<Integer, Long> arrivingDependencyVector) throws DcbException {
        DependencyVector newDependencyVector = copy();
        for (Map.Entry<Integer, Long> entry : arrivingDependencyVector.entrySet()) {
            int id = entry.getKey();
            long timestamp = entry.getValue();

            if (id == ownerComponentId && timestamp > get(ownerComponentId)) {
                throw new DcbException();
            }

            if (id != ownerComponentId) {
                long correctTimestamp = Math.max(timestamp, newDependencyVector.get(id));
                newDependencyVector.componentIdToTimestamp.put(id, correctTimestamp);
            }
        }
        return newDependencyVector;
    }
}
