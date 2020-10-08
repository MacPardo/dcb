package dcb.core.utils;

import java.util.UUID;

public class UUIDGeneratorImpl implements UUIDGenerator {
    @Override
    public UUID randomUUID() {
        return UUID.randomUUID();
    }
}
