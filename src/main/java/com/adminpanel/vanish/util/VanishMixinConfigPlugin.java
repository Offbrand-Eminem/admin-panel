package me.drex.vanish.util;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class VanishMixinConfigPlugin implements IMixinConfigPlugin {
   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.startsWith("me.drex.vanish.mixin.compat.")) {
         String compatName = mixinClassName.replace("me.drex.vanish.mixin.compat.", "");
         String[] parts = compatName.split("\\.", 3);
         String modId = parts[0];
         String status = parts[1];
         boolean enabled = status.equals("enabled");
         return FabricLoader.getInstance().isModLoaded(modId) == enabled;
      } else {
         return true;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
