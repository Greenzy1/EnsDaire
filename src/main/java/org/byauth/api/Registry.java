package org.byauth.api;

import java.util.Collection;
import java.util.Optional;

public interface Registry<K, V> {
    void register(K key, V value);
    void unregister(K key);
    Optional<V> get(K key);
    Collection<V> values();
}
