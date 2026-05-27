# TaCZ Runtime Compat

**An enterprise-quality NeoForge 1.21.1 runtime compatibility shim for old TaCZ Unofficial gun packs.**

Many TaCZ (Timeless and Classics Zero) custom gun packs were authored for Forge 1.20.1, where `ResourceLocation` validation was lenient. Minecraft 1.21.1 on NeoForge enforces strict path rules, causing crashes when loading older packs.

**TaCZ Runtime Compat** intercepts resource-location construction and silently sanitizes invalid IDs before the exception is thrown. 

### Why choose this mod?
- **High Performance**: Features a zero-allocation fast-path and a bounded concurrent memory cache.
- **Modpack Safe**: Contains advanced hook resilience and a `Safe Mode` to ensure your pack boots even with core-mods like ModernFix.
- **Customizable**: Strip unicode, replace spaces, and set custom character mappings via config.
- **API Ready**: Modpack devs can use the internal API to register custom sanitization rules.

### Installation
Drop it in your `mods` folder! It works purely at runtime, meaning **no ZIP files are modified**.

Check the console for Startup Diagnostics to verify compatibility with your modpack environment!
