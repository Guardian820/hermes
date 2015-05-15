package pl.allegro.tech.hermes.common.message.undelivered;


import pl.allegro.tech.hermes.api.SentMessageTrace;
import pl.allegro.tech.hermes.api.TopicName;

import java.util.Optional;

public interface UndeliveredMessageLog {

    void add(SentMessageTrace undeliveredMessage);

    void persist();

    Optional<SentMessageTrace> last(TopicName topicName, String subscriptionName);
}
