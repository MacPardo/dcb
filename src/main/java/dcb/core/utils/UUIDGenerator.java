package dcb.core.utils;

import java.util.UUID;

/**
 * This interface is used to allow mocking in TranslatorTest
 */
public interface UUIDGenerator {
    UUID randomUUID();
}
