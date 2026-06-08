package me.drex.vanish.util;

import net.minecraft.class_1297;
import net.minecraft.class_3222;

public class Arguments {
   public static final ThreadLocal<class_1297> ACTIVE_ENTITY = ThreadLocal.withInitial(() -> null);
   public static final ThreadLocal<class_3222> PACKET_CONTEXT = ThreadLocal.withInitial(() -> null);
}
