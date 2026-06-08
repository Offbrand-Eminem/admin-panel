package me.drex.vanish.mixin.death_message;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1282.class})
public abstract class DamageSourceMixin {
   @Shadow
   @Final
   private @Nullable class_1297 field_42292;
   @Shadow
   @Final
   private @Nullable class_1297 field_42293;

   @WrapOperation(
      method = {"method_5506"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1282;field_42292:Lnet/minecraft/class_1297;"
)}
   )
   public class_1297 hideVanished(class_1282 instance, Operation<class_1297> original) {
      class_1297 entity = (class_1297)original.call(new Object[]{instance});
      return VanishAPI.isVanished(entity) ? null : entity;
   }

   @WrapOperation(
      method = {"method_5506"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1282;field_42293:Lnet/minecraft/class_1297;"
)}
   )
   public class_1297 hideVanished2(class_1282 instance, Operation<class_1297> original) {
      class_1297 entity = (class_1297)original.call(new Object[]{instance});
      return VanishAPI.isVanished(entity) ? null : entity;
   }

   @WrapOperation(
      method = {"method_5506"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_6124()Lnet/minecraft/class_1309;"
)}
   )
   public class_1309 hideVanished3(class_1309 instance, Operation<class_1309> original) {
      class_1309 entity = (class_1309)original.call(new Object[]{instance});
      return VanishAPI.isVanished(entity) ? null : entity;
   }

   @WrapOperation(
      method = {"method_5506"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2561;method_43469(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/class_5250;",
   ordinal = 3
)}
   )
   public class_5250 fixMessage(String string, Object[] objects, Operation<class_5250> original) {
      return this.field_42292 == null && this.field_42293 == null ? (class_5250)original.call(new Object[]{string, objects}) : class_2561.method_43469("death.attack.generic", new Object[]{objects[0]});
   }
}
