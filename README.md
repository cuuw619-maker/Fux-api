# Fux API

Client-side Minecraft 1.21.1 NeoForge base for a configurable menu/client framework.

## Current stage

- NeoForge 1.21.1 / Java 21.
- Modern client menu inspired by the structure and visual language of PulseVisuals, adapted to NeoForge rather than copied from its Fabric implementation.
- Right Shift opens the menu.
- The NeoForge Mods screen exposes the same menu through the mod configuration extension point.
- Sidebar categories and draggable panel are implemented.
- No gameplay/cheat modules are registered yet; this stage is UI/framework only.
- Sodium is not a hard dependency. The UI is built on Minecraft/NeoForge client APIs, so Sodium and similar rendering/options mods can coexist without a direct API dependency.

## Build

Use Java 21 and a recent Gradle installation:

```bash
gradle build
```

The resulting jar is written to `build/libs/`.

## Reference projects

The initial UI direction is based on the public PulseVisuals project, which is a Fabric 1.21.4 visual-effects mod. Its architecture and APIs are not copied directly because this project targets NeoForge 1.21.1.

Sodium/Reese's Sodium Options compatibility is treated as an interoperability requirement rather than a dependency: Fux API does not replace or modify Sodium's options screen at this stage.
