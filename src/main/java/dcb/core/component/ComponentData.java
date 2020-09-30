package dcb.core.component;

import dcb.core.gateway.Translator;
import dcb.core.messaging.Messenger;
import dcb.core.models.Message;
import dcb.core.utils.BlockingQueueReceiver;

public record ComponentData<State>(
        int id,
        ComponentCore<State> core,
        BlockingQueueReceiver<Message> receiver,
        Messenger messenger,
        Translator translator
) {}
