# holatus — mods para Minecraft 26.2 (Fabric)

Cuatro mods portados desde 1.21.11 (Unbreakable, desde 1.21.1) a **Minecraft 26.2**.

| Carpeta | Mod |
|---|---|
| `aplanar/` | Aplanar — selecciona un área con la rueda del ratón y la mina/aplana |
| `elytra-armor-mod/` | Elytra + Chestplate — ranura extra para llevar élitros y peto a la vez |
| `retrogen-mods/` | Retrogen Mods — aplica generación de otros mods en chunks ya existentes |
| `unbreakable/` | Unbreakable — ningún objeto con durabilidad se desgasta |

## Estado: PORT SIN COMPILAR

Este port se escribió **sin poder compilar** (el entorno donde se hizo no tenía acceso a Maven/Mojang/Fabric),
a partir de las guías de migración 26.1/26.2. Es muy probable que queden errores de compilación o de nombres
en los mixins. **Lo primero que hay que hacer es mirar el workflow `build` en la pestaña Actions**:
compila cada mod por separado y sube el `.jar` si funciona.

### Cambios hechos
- Build: Loom `net.fabricmc.fabric-loom` (sin remapeo), sin línea `mappings`, `implementation` en vez de
  `modImplementation`, Java 25, Gradle 9.5.1, `minecraft ~26.2`.
- **Aplanar**: HUD y pantalla de config → `GuiGraphicsExtractor` (`extractRenderState`, `centeredText`, `outline`);
  `Minecraft.screen/setScreen` → `minecraft.gui.screen()/setScreen`; el recuadro 3D pasa de
  `WorldRenderEvents` + `RenderTypes.LINES_TRANSLUCENT` (ya no existe `MultiBufferSource`) a
  `LevelRenderEvents.BEFORE_GIZMOS` + `Gizmos.line` (cada arista en tramos para mantener el degradado de color).
- **Elytra Armor**: reescrito de Yarn a nombres Mojang (`InventoryMenu`, `Container`, `Slot`, `getItemBySlot`,
  `canGlideUsing`, `updateFallFlying`…).
- **Retrogen Mods**: `ChunkPos.x/z` ya no son públicos → `RetrogenMods.chunkX/chunkZ`.
- **Unbreakable**: reescrito desde cero (un único mixin sobre `ItemStack.hurtAndBreak`).

### Cosas a verificar si no compila
- Versiones en `gradle.properties` (loader, Fabric API, Loom, `modmenu_version` de Aplanar).
- Nombres que no pude confirmar: `Gizmos.line(Vec3, Vec3, int, float)`, paquete de `LevelRenderEvents`,
  `Gui#screen()`, y los métodos de los mixins de Elytra (`canGlideUsing`, `updateFallFlying`, `getFallFlyingTicks`).
- Retrogen usa mucha API interna de worldgen (`Structure.generate`, `Holder.Reference#getRegisteredName`…)
  que puede haber cambiado en 26.x y no se pudo comprobar.
