package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.function.Predicate;
import me.drex.vanish.VanishMod;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_1301;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1924;
import net.minecraft.class_1937;
import net.minecraft.class_2231;
import net.minecraft.class_238;
import net.minecraft.class_4481;
import net.minecraft.class_9879;
import net.minecraft.class_9883;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class VanishEntitySelector {
   @Mixin({class_1924.class})
   public interface EntityGetterMixin {
      @WrapOperation(
         method = {"method_18458"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_6155:Ljava/util/function/Predicate;"
)}
      )
      default Predicate<class_1297> preventMobSpawning(Operation<Predicate<class_1297>> original) {
         return ConfigManager.vanish().interaction.mobSpawning ? VanishMod.NO_SPECTATORS_AND_NO_VANISH : (Predicate)original.call(new Object[0]);
      }

      @WrapOperation(
         method = {"method_18459"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_6155:Ljava/util/function/Predicate;"
)}
      )
      default Predicate<class_1297> preventMobSpawning2(Operation<Predicate<class_1297>> original) {
         return ConfigManager.vanish().interaction.mobSpawning ? VanishMod.NO_SPECTATORS_AND_NO_VANISH : (Predicate)original.call(new Object[0]);
      }
   }

   @Mixin({class_1301.class})
   public abstract static class EntitySelectorMixin {
      @WrapOperation(
         method = {"method_5911"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_6155:Ljava/util/function/Predicate;"
)}
      )
      private static Predicate<class_1297> preventEntityCollision(Operation<Predicate<class_1297>> original, class_1297 entity) {
         return ConfigManager.vanish().interaction.entityCollisions ? VanishMod.NO_SPECTATORS_AND_NO_VANISH.and((entity1) -> !VanishAPI.isVanished(entity)) : (Predicate)original.call(new Object[0]);
      }
   }

   @Mixin({class_9883.class})
   public abstract static class OldMinecartBehaviorMixin {
      @WrapOperation(
         method = {"method_62826"},
         at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_8335(Lnet/minecraft/class_1297;Lnet/minecraft/class_238;)Ljava/util/List;"
)}
      )
      private List<class_1297> preventMinecartColision(class_1937 instance, class_1297 entity, class_238 aABB, Operation<List<class_1297>> original) {
         return ConfigManager.vanish().interaction.entityCollisions ? instance.method_8333(entity, aABB, VanishMod.NO_SPECTATORS_AND_NO_VANISH) : (List)original.call(new Object[]{instance, entity, aABB});
      }
   }

   @Mixin({class_9879.class})
   public abstract static class NewMinecartBehaviorMixin {
      @WrapOperation(
         method = {"method_62830"},
         at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_8335(Lnet/minecraft/class_1297;Lnet/minecraft/class_238;)Ljava/util/List;"
)}
      )
      private List<class_1297> preventMinecartColision(class_1937 instance, class_1297 entity, class_238 aABB, Operation<List<class_1297>> original) {
         return ConfigManager.vanish().interaction.entityCollisions ? instance.method_8333(entity, aABB, VanishMod.NO_SPECTATORS_AND_NO_VANISH) : (List)original.call(new Object[]{instance, entity, aABB});
      }
   }

   @Mixin({class_1657.class})
   public abstract static class PlayerMixin {
      @Redirect(
         method = {"method_7263"},
         at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_18467(Ljava/lang/Class;Lnet/minecraft/class_238;)Ljava/util/List;"
)
      )
      private List<class_1309> preventSweepingEdge(class_1937 instance, Class<class_1309> aClass, class_238 aabb) {
         return instance.method_8390(aClass, aabb, VanishMod.NO_SPECTATORS_AND_NO_VANISH);
      }
   }

   @Mixin({class_4481.class})
   public abstract static class BeehiveBlockMixin {
      @Redirect(
         method = {"method_23893"},
         at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_18467(Ljava/lang/Class;Lnet/minecraft/class_238;)Ljava/util/List;"
)
      )
      private List<class_1309> preventBeeAnger(class_1937 instance, Class<class_1309> aClass, class_238 aabb) {
         return instance.method_8390(aClass, aabb, VanishMod.NO_SPECTATORS_AND_NO_VANISH);
      }
   }

   @Mixin({class_2231.class})
   public abstract static class BasePressurePlateBlockMixin {
      @WrapOperation(
         method = {"method_52210"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_6155:Ljava/util/function/Predicate;"
)}
      )
      private static Predicate<class_1297> preventPressurePlatePress(Operation<Predicate<class_1297>> original) {
         return ConfigManager.vanish().interaction.blocks ? VanishMod.NO_SPECTATORS_AND_NO_VANISH : (Predicate)original.call(new Object[0]);
      }
   }
}
