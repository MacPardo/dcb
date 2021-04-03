package dcb.components.utils;

import dcb.exceptions.DcbException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class DependencyVector {
    private final int ownerId;

    // Keys: component IDs, Values: Timestamps
    public final Map<Integer, Long> timestamps;

    public DependencyVector(int ownerId, Map<Integer, Long> timestamps) {
        this.ownerId = ownerId;
        this.timestamps = timestamps;
    }

    public static DependencyVector createNew(int componentId) {
        return new DependencyVector(componentId, new HashMap<>());
    }

    public DependencyVector copy() {
        Map<Integer, Long> mapClone = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : timestamps.entrySet()) {
            mapClone.put(entry.getKey(), entry.getValue());
        }
        return new DependencyVector(ownerId, mapClone);
    }

    public long get(int componentId) {
        return timestamps.getOrDefault(componentId, 0L);
    }

    public DependencyVector updateOwnerTimestamp(long timestamp) throws DcbException {
        if (get(ownerId) > timestamp) {
            throw new DcbException();
        }
        DependencyVector newDependencyVector = copy();
        newDependencyVector.timestamps.put(ownerId, timestamp);
        return newDependencyVector;
    }

    public DependencyVector update(Map<Integer, Long> newVec) throws DcbException {
        DependencyVector newDependencyVector = copy();
        for (Map.Entry<Integer, Long> entry : newVec.entrySet()) {
            int id = entry.getKey();
            long timestamp = entry.getValue();

            if (id == ownerId && timestamp > get(ownerId)) {
                throw new DcbException();
            }

            if (id != ownerId) {
                long correctTimestamp = Math.max(timestamp, newDependencyVector.get(id));
                newDependencyVector.timestamps.put(id, correctTimestamp);
            }
        }
        return newDependencyVector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyVector that = (DependencyVector) o;
        return ownerId == that.ownerId &&
                Objects.equals(timestamps, that.timestamps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, timestamps);
    }
}
