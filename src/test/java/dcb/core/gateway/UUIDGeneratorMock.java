package dcb.core.gateway;

import dcb.utils.UUIDGenerator;

import java.util.UUID;

public class UUIDGeneratorMock implements UUIDGenerator {
    private final UUID mockedValue;

    public UUIDGeneratorMock(UUID mockedValue) {
        this.mockedValue = mockedValue;
    }

    @Override
    public UUID randomUUID() {
        return mockedValue;
    }
}
