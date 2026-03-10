package org.byauth.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record Party(UUID leader, Set<UUID> members) {
    public Party(UUID leader) {
        this(leader, new HashSet<>());
        members.add(leader);
    }
}
