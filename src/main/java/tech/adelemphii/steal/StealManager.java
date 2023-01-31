package tech.adelemphii.steal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StealManager {

        private final Map<UUID, UUID> stealMap = new HashMap<>();

        public void addSteal(UUID player, UUID target) {
            stealMap.put(player, target);
        }

        public UUID getTarget(UUID player) {
            return stealMap.get(player);
        }

        public void removeSteal(UUID player) {
            stealMap.remove(player);
        }

        public boolean isStealing(UUID player) {
            return stealMap.containsKey(player);
        }
}
