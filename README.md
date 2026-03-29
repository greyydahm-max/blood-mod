# Blood N' Particles — Client-Side Mod

Client-side rewrite of Blood N' Particles for Minecraft **26.1**.

**The server needs absolutely nothing** — no Fabric, no mods, no datapack.

---

## Getting the jar

### Option A — GitHub Actions (automatic)
Every push to `main` builds the jar automatically.
Go to **Actions → latest run → Artifacts** to download it.

### Option B — GitHub Release
Push a version tag and a Release is created with the jar attached:
```
git tag v2.0.0
git push --tags
```

### Option C — Build locally (requires Java 21)
```
gradle build
```
Jar: `build/libs/blood-n-particles-client-2.0.0.jar`

---

## Blood effects

| Entity | Effect |
|---|---|
| Skeleton | Gray powder dust |
| Wither Skeleton | Coal dust |
| Stray | Snowflakes |
| Bogged | Green dust |
| Silverfish | Gray dust |
| Snow Golem | Snowflakes |
| Zombie / Zombie Villager / Zombified Piglin / Zoglin | Green blood |
| Drowned | Teal blood |
| Cave Spider | Dark green blood |
| Spider | Green blood |
| Witch | Purple blood |
| Slime | Green blood (size-scaled) |
| Allay | Light blue blood |
| Armadillo | Brown blood |
| Axolotl | Color matches variant (pink/brown/gold/cyan/blue) |
| Bee | Yellow blood |
| Breeze | Blue blood |
| Guardian | Teal blood |
| Elder Guardian | Pale teal blood |
| Glow Squid | Cyan blood |
| Husk (adult) | Sand dust + tan blood |
| Husk (baby) | Tan blood |
| Ravager | Brown blood |
| Squid | Dark blue blood |
| Vex | Pale blue blood |
| Warden | Dark blue blood |
| Creaking | Dark brown blood |
| Magma Cube | Orange blood (size-scaled) |
| Strider | Purple-pink blood |
| Blaze | Orange blood |
| Endermite | Dark purple blood |
| Shulker | Purple blood |
| Enderman | Purple blood |
| Phantom | Dark purple blood |
| Ghast | Pale blood |
| Pillager / Vindicator / Evoker | Medium red blood |
| Piglin / Piglin Brute | Medium red blood |
| Small animals (Chicken, Cat, Fish…) | Small red blood |
| Medium animals (Cow, Pig, Wolf…) | Medium red blood |
| Large animals (Horse, Camel, Hoglin…) | Large red blood |
| Player | Medium red blood |

## How it works

Uses `ClientTickEvents.END_WORLD_TICK` to detect `hurtTime == 1` on nearby entities
(the same trigger as the original mod's scoreboard `HurtTime:1s`) and spawns vanilla
dust particles locally. The server is never involved.
