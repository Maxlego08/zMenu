# Animations for developers

How to drive, extend and react to the animation system from code. The configuration side is in
[slot-animations.md](slot-animations.md); everything here assumes you have read it, since an
animation written in code is the same object a file describes.

## Playing an animation

An action receives the engine of the menu the player is looking at, and the manager finds it for you
from a player.

```java
InventoryEngine engine = ...;                       // an action receives one
Animation pulse = inventoryManager.getAnimationManager().findAnimation("pulse").orElseThrow();

engine.playAnimation(pulse, AnimationPlayback.OVERLAY);
engine.playAnimation(pulse, AnimationPlayback.OVERLAY, true);   // and freeze the menu
engine.stopAnimation();                                          // finish whatever is playing, now
```

`playAnimation` returns how long the animation lasts in milliseconds, or 0 if nothing was played, so
anything that has to happen afterwards can be scheduled without guessing.

Only one animation plays at a time, so two `animation` actions in a row do not stack: the second
finishes the first and takes over. Put a `delay` on the second one, or merge them into a single
animation with `files`, to play them one after the other. Idle animations are separate and keep
running throughout; `stopAnimation` does not touch them.

The manager plays one on whatever menu a player has open, without having to find the engine first:

```java
AnimationManager manager = inventoryManager.getAnimationManager();

manager.play(player, ZMenuKeys.key("pulse"), AnimationPlayback.OVERLAY);
manager.stop(player);
```

Named animations live in the reloadable `Registries.ANIMATION` registry. The files of the
`animations` folder are known as `zmenu:<file name>`; `getAnimation(Key)` reads one by key, and
`findAnimation(String)` reads a name written in a file (`pulse`, or `myplugin:glow` for one
registered by a plugin). `/zm reload` rebuilds the registry: a file that cannot be read keeps its
previous version, and `loadAnimations()` returns what the reload found.

## Reacting to an animation

`AnimationStartEvent` is called before anything is displayed, and cancelling it stops the animation
from playing at all — the inventory is shown complete, as if it had none. It can also change whether
the menu freezes. `AnimationEndEvent` is called once it is over, with `isCompleted()` telling you
whether it played to its end or was finished early by a close, another animation, or a shutdown.

```java
@EventHandler
public void onAnimationStart(AnimationStartEvent event) {
    if (event.getPlayer().hasPermission("myserver.noanimations")) event.setCancelled(true);
}
```

Three rules:

- **An overlay does not freeze the menu.** The player is using it; locking it would be surprising. A
  reveal freezes if the animation says `freeze: true`, and either can be overridden per play.
- **Only one animation plays at a time.** Starting another finishes the one in progress at once
  instead of queueing behind it, so a player clicking a button repeatedly cannot build a backlog.
- **Group anchors keep their meaning.** The groups are laid out even for an overlay, so `group:2`
  is still the moment group 2 would have started and an animation reads the same either way.

When an overlay item is removed, the slot goes back to whatever was under it rather than being
emptied — nothing was hidden in the first place.

## Registering your own animation

Another plugin adds a reveal order with a `SlotAnimationLoader`, which names the animation and
builds it:

```java
public class MyAnimationLoader extends SlotAnimationLoader {

    public MyAnimationLoader() {
        super(Key.key("myplugin", "my_animation"), Key.key("myplugin", "mine"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return new MyAnimation();
    }
}
```

and registers it from its `onEnable`, together with its aliases:

```java
inventoryManager.registerSlotAnimation(plugin, new MyAnimationLoader());
```

Menus write `type: myplugin:my_animation`. A name without a namespace belongs to `zmenu`, so
`type: spiral` is `zmenu:spiral`. Registering the same key twice fails and names both owners.

Static registries stop accepting entries once the server has finished loading, so register from
`onEnable`. Read them with `inventoryManager.getRegistries().registry(Registries.SLOT_ANIMATION)`.

`create` receives the section the `type` was written in, so an animation can be configured by the
file it is used in — that is how `MATRIX` and `CUSTOM` read their `matrix` and `order`. Throw an
`IllegalArgumentException` with a message for whoever wrote the file when an option is wrong.

zMenu's own animations are classes annotated with `@AutoSlotAnimationLoader`, found and registered
at startup like its actions and buttons.

`reverse` is applied by the compiler, so a registered animation gets it for free.

## Ordering per player

`SlotAnimation` is called with an `AnimationContext`, which knows the player and the menu:

```java
public class PerPlayerAnimation implements SlotAnimation {

    @Override
    public List<Integer> order(int inventorySize, List<Integer> slots) {
        return slots;                                   // used when there is no context
    }

    @Override
    public List<Integer> order(AnimationContext context) {
        List<Integer> ordered = new ArrayList<>(context.slots());
        if (context.player() != null) {
            Collections.shuffle(ordered, new Random(context.player().getUniqueId().hashCode()));
        }
        return ordered;
    }
}
```

The context is null-safe: `player()` and `inventory()` are null when an animation is compiled outside
of a menu, such as in a test, so an implementation using them has to cope with their absence.

## Writing an animation in code

Extend `CustomAnimation`, override only what you care about, and register it once at startup. Every
hook has a default, so an animation may be as small as an ordering.

```java
public class GlowAnimation extends CustomAnimation {

    public GlowAnimation() {
        super(Key.key("myplugin", "glow"));
    }

    @Override
    public SlotAnimation getAnimation() {
        return new SpiralSlotAnimation();
    }

    @Override
    public long getInterval() {
        return 30;
    }

    @Override
    public List<TemporaryItem> getTemporaryItems() {
        return List.of(temporaryItem("flash", slots(10, 16), item, AnimationAnchor.START, AnimationAnchor.at(400)));
    }
}

inventoryManager.getAnimationManager().registerAnimation(plugin, new GlowAnimation());
```

The `animation` action plays it with `file: myplugin:glow`, and the API with
`play(player, Key.key("myplugin", "glow"), playback)`. The `open-animation` and `close-animation` of
a menu only read files of the `animations` folder. Give it a key in the
namespace of your plugin: the `zmenu` namespace holds the files of the animations folder, and a key
that is already taken is refused with a `RegistrationException`. An animation registered this way
belongs to your plugin and survives `/zm reload`, so registering it once is enough;
`unregisterAnimation(key)` removes it.

| Hook | Default | What it decides |
|---|---|---|
| `getAnimation()` | `SequentialSlotAnimation` | The reveal order |
| `isReverse()` | `false` | Whether that order is played backwards |
| `getInterval()` | `50` | Milliseconds between two steps |
| `getSlotsPerStep()` | `1` | Slots revealed per step |
| `getDelay()` | `0` | Delay before the first group |
| `getSound()` | none | Sound played when a slot is revealed |
| `isFreeze()` | `true` | Whether the menu ignores clicks while it plays |
| `getPlayMode()` | `ALWAYS` | Whether it plays on every open or only the first |
| `getGroups()` | none | Per group overrides |
| `getRequirement()` | none | What the player must satisfy for it to play |
| `getSeed()` | `RANDOM` | Where its randomness comes from |
| `getStartActions()` | none | What runs when it starts |
| `getEndActions()` | none | What runs when it ends |
| `getTemporaryItems()` | none | The items that only exist while it plays |

The protected helpers `group`, `groups`, `temporaryItem`, `frame`, `removal` and `slots` build those
last two without the record constructors; a group built with `group(...)` inherits the sound and
direction of the class, exactly as a group in a file inherits the defaults above it.

`toSettings()` and `toAnimation()` are final: they assemble what the hooks returned, so a subclass
cannot end up with settings that do not match what it declared.

## How it is compiled

zMenu compiles the whole animation before anything is displayed, in two passes. The first lays the
groups out and records where each of them starts and ends; the second resolves everything positioned
relative to those boundaries. Every step carries an absolute time in milliseconds, so the passes
never have to know about each other: whatever they produce is merged by sorting, and the engine only
has to apply the steps whose time has come.

The compiler is internal to zMenu and not part of the API: an addon describes an animation with
`SlotAnimationSettings` (or a `CustomAnimation`), and zMenu compiles and plays it.

Each step does one of three things to its slot:

| Action | Effect |
|---|---|
| `REVEAL` | Displays the real item of the button owning the slot, and hands the slot back to it |
| `SET` | Displays an item belonging to the animation; the slot stays owned by the animation |
| `CLEAR` | Empties the slot; the slot stays owned by the animation |

A point in time is written as an `AnimationAnchor`, resolved against the group boundaries of the
first pass:

| Form | Meaning |
|---|---|
| `250` | 250 ms after the animation started |
| `group:2` or `group:2.start` | The moment group 2 starts |
| `group:2.end` | The moment group 2 is over |
| `start` / `end` | The beginning of the animation / the end of the last group |

Both ends of a group can be written explicitly; the bare `group:2` is shorthand for `group:2.start`.
The end of a group is not the same time as the start of the next one: the next group may not exist,
and if it declares a `delay` there is a gap between the two.

An anchor pointing at a group that holds no slot resolves to the end of the animation.

```java
long time = compiled.timing().resolve(AnimationAnchor.parse("group:2"));
```

`SlotAnimation` is a single method that reorders slots; the timing and the number of slots per step
come from `SlotAnimationGroup`:

```java
public interface SlotAnimation {
    List<Integer> order(int inventorySize, List<Integer> slots);
}
```

An implementation must return every slot it receives, exactly once, otherwise items would be lost.
`SlotGrid.width(size)` and `SlotGrid.height(size)` give the grid dimensions.

Register a custom type at startup with a loader, as shown in
[Registering your own animation](#registering-your-own-animation):

```java
inventoryManager.registerSlotAnimation(plugin, new MyAnimationLoader());
```

`InventoryEngine#isFrozen()` reports whether the animation is still playing, for code that needs to
know the inventory is not interactive yet.
