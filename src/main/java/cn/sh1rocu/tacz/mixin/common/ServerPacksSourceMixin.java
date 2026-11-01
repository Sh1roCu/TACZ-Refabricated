package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.event.AddPackFindersEvent;
import cn.sh1rocu.tacz.api.mixin.PackRepositoryExtension;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {
    @Inject(method = "createPackRepository(Ljava/nio/file/Path;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN"))
    private static void tacz$addPacks(Path path, CallbackInfoReturnable<PackRepository> cir) {
        AddPackFindersEvent event = new AddPackFindersEvent(PackType.SERVER_DATA, ((PackRepositoryExtension) cir.getReturnValue())::tacz$addPackFinder, false);
        AddPackFindersEvent.CALLBACK.invoker().onAddPackFinders(event);
    }
}
