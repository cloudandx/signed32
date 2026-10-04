package org.cloudandx.signed32;

import net.fabricmc.api.ModInitializer;
import org.cloudandx.signed32.config.Signed32Config;

public class Signed32 implements ModInitializer {
    @Override
    public void onInitialize() {
        Signed32Config.load();
    }
}
