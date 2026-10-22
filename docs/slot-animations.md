# Slot animations

An inventory can reveal its items one after the other when it opens, instead of appearing complete.
This is separate from the title animation, which animates the inventory name and needs PacketEvents;
a slot animation is plain item placement and works everywhere.

Driving animations from code — playing one, registering your own, reacting to them — is documented
separately in [animations-api.md](animations-api.md).

## Examples

Every one of these ships in the plugin folder and is ready to open.

`inventories/examples/hub_menu.yml` is the one to read first: a menu of the kind a server actually
ships, animated the way one should be. The rest take a feature each and show it on its own.

| File | Shows |
|---|---|
| `inventories/examples/hub_menu.yml` | A real menu, animated tastefully |
| `inventories/examples/animation.yml` | The four line version, and the list of types |
| `inventories/examples/animation_advanced.yml` | Groups, per button delays, temporary items, keyframes, sounds |
| `inventories/examples/animation_matrix.yml` | `MATRIX`: three rings closing in, drawn in the file |
| `inventories/examples/animation_custom.yml` | `CUSTOM`: five items dealt outwards from the middle |
| `inventories/examples/animation_shared.yml` | Using a shared animation and overriding two of its keys |
| `inventories/examples/animation_loading.yml` | Temporary items used as a loading state over the content |
| `inventories/examples/animation_action.yml` | Buttons playing animations on the open menu |
| `inventories/examples/animation_idle.yml` | Buttons that keep animating while the menu is open |
| `inventories/examples/animation_random.yml` | Everything an animation can leave to chance |
| `inventories/examples/animation_variants.yml` | Several animations, one picked per open, and `count` |
| `animations/sparkle.yml` | A shared scatter, meant to be merged on top of a motion |
| `animations/pulse.yml` | A shared animation made only of temporary items |
| `animations/glint.yml` | An overlay pack meant to be merged on top of another |
| `animations/fancy_open.yml` | A shared animation: a spiral, then the content from the middle |
| `animations/wave_in.yml` | A shared animation: a reversed diagonal sweep |

## Configuration

Add an `open-animation` section to any inventory file:

```yaml
name: "&8Animation"
size: 27

open-animation:
  type: HORIZONTAL_SNAKE
  interval: 40
  slots-per-step: 1
  freeze: true
```

| Key | Default | Description |
|---|---|---|
| `type` | none | Reveal order, see below. Without it there is no animation |
| `interval` | `50` | Milliseconds between two steps |
| `slots-per-step` | `1` | Slots revealed per step. Use `9` to reveal a full row at a time |
| `freeze` | `true` | Ignore clicks while the animation plays |
| `reverse` | `false` | Play the reveal order backwards |
| `interval-jitter` | `0` | How far a step may land from its scheduled time, in milliseconds |
| `chance` | `100` | The chance of the whole animation playing, in percent |
| `variants` | none | Alternatives, one picked each time it plays, see [Variants](#variants) |
| `seed` | `random` | Where the randomness comes from, see [Making it personal](#making-it-personal) |
| `start-actions` | none | What runs when the animation starts |
| `end-actions` | none | What runs when it is over |
| `groups` | none | Per group overrides, see [Groups](#groups) |
| `sound` | none | Played when a slot is revealed, see [Sounds](#sounds) |
| `play` | `ALWAYS` | When the animation plays, see [When the animation plays](#when-the-animation-plays) |
| `file` / `files` | none | Use one or several shared animations, see [Reusable animations](#reusable-animations) |
| `requirement` | none | What the player must satisfy for it to play, see [Who it plays for](#who-it-plays-for) |

Names are matched ignoring case, and `-` and `_` are interchangeable, so `HORIZONTAL_SNAKE`,
`horizontal-snake` and `Horizontal_Snake` are the same type.

| Type | Order |
|---|---|
| `SEQUENTIAL` or `ROWS` | Left to right, row by row |
| `COLUMNS` | Top to bottom, column by column |
| `HORIZONTAL_SNAKE` | Row by row, alternating the direction of each row |
| `VERTICAL_SNAKE` | Column by column, alternating the direction of each column |
| `SPIRAL` | From the outside inwards, clockwise from the top left corner |
| `FROM_CENTER` | From the middle outwards |
| `WAVE` | A diagonal sweep, from the top left corner to the bottom right one |
| `CHECKERBOARD` | One slot out of two, then the gaps |
| `MATRIX` | The order you draw yourself, see [Drawing the order](#drawing-the-order) |
| `CUSTOM` | The order you list yourself, see [Listing the order](#listing-the-order) |
| `RANDOM` | Random order |

`reverse: true` plays the order backwards, so `FROM_CENTER` becomes a reveal towards the middle,
`SEQUENTIAL` starts at the bottom right, and `SPIRAL` unwinds anticlockwise from the inside. It
applies to any type, including ones added by another plugin.

`ROWS` is `SEQUENTIAL` under a name that says the intent; neither reveals a whole row at a time on
its own. Set `slots-per-step: 9` for that, or `slots-per-step` to the number of rows with `COLUMNS`.

Only the slots that actually hold an item are animated, so a partially filled inventory does not
spend steps on empty slots. An example ships in `inventories/examples/animation.yml`.

## Reusable animations

Once an animation is more than a few lines, it does not belong in forty menus. Every file in the
`animations/` folder is an animation any inventory can use by name:

```yaml
# plugins/zMenu/animations/fancy_open.yml
type: SPIRAL
interval: 25
sound:
  sound: BLOCK_NOTE_BLOCK_HAT
groups:
  2:
    type: FROM_CENTER
    interval: 60
```

```yaml
# any inventory
open-animation:
  file: fancy_open
```

The content of the file is exactly what you would write inline under `open-animation`.

### Merging several

`files` takes a list, merged in the order it is given, so an animation can be built from parts — a
motion in one file, an overlay in another:

```yaml
open-animation:
  files:
    - fancy_open      # the motion
    - glint           # only adds its temporary items
  interval: 15        # and this menu is faster than either
```

A file may also build on another with `extends`, so a variant does not repeat its base:

```yaml
# animations/fancy_open_fast.yml
extends: fancy_open
interval: 10
```

Anything a file builds on is merged before it, so the file always wins over what it extends. A file
that ends up naming itself, however far down the chain, is reported and merged once.

### Overriding

Anything written next to `file` or `files` overrides every shared animation, key by key rather than
block by block, so a menu can change one value without redefining what it belongs to:

```yaml
open-animation:
  file: fancy_open
  interval: 10        # everything else still comes from the file
  groups:
    2:
      delay: 400      # the type and interval of group 2 are untouched
```

`fancy_open.yml`, `wave_in.yml` and `pulse.yml` ship as examples. If the named file does not exist
the inventory reports it and falls back to whatever it declared itself.

A shared file may hold temporary items too, so an animation can be entirely self contained —
`pulse.yml` is made of nothing else. Reloading the plugin reloads the folder.

Developers reach the same animations through the manager:

```java
AnimationManager manager = inventoryManager.getAnimationManager();
Optional<Animation> pulse = manager.findAnimation("pulse");

manager.registerAnimation(plugin, new Animation(Key.key("myplugin", "my_animation"), settings));
```

A registered animation belongs to the plugin that registered it and survives a reload of the folder,
so register yours once at startup. The `animation` action plays it with
`file: myplugin:my_animation`; `open-animation` and `close-animation` only read files of the folder.

## Drawing the order

When none of the types does what you want, draw it. Each row of the matrix describes a row of the
inventory and each character a slot; every distinct character is a step, played in the order the
characters sort in.

```yaml
open-animation:
  type: MATRIX
  interval: 60
  matrix:
    - "111111111"
    - "122222221"
    - "111111111"
```

That reveals the frame first, then the inside. Digits sort before letters, so `1` … `9` then `a` …
`z` gives up to 35 steps. A space leaves a slot out of the drawing, and slots the matrix does not
cover — including whole rows below it — are revealed last, in ascending order.

Rows longer than 9 characters are truncated, since a chest row is 9 slots wide.

## Listing the order

`MATRIX` draws the order; `CUSTOM` lists it. Use whichever describes what you want more directly — a
shape is easier to draw, a precise sequence is easier to list.

```yaml
open-animation:
  type: CUSTOM
  interval: 80
  order:
    - 13        # the middle first
    - 12
    - 14
    - 4
    - 22
    - 3-5       # the same syntax as the slots of a button
```

Each line is a single slot or a range, exactly like `slots` on a button. A slot listed twice keeps
its first place, and slots the list does not mention are revealed last, in ascending order, so a
partial list still describes a complete animation.

`slots-per-step` still applies: with `slots-per-step: 3` the first three slots of the list appear
together, then the next three.

## Per button

A button can opt out of the animation entirely, join a group, or arrive late.

```yaml
items:
  border:
    animation: false       # displayed immediately, never animated
  content:
    animation:
      group: 2             # groups play in ascending order
      delay: 400           # extra ms, counted from the start of its group
```

| Key | Default | Description |
|---|---|---|
| `animation: false` | — | The slots of this button are displayed immediately |
| `animation.enabled` | `true` | Same as above, in section form |
| `animation.group` | `1` | Which group the button belongs to |
| `animation.delay` | `0` | Extra delay for the slots of this button, in milliseconds |
| `animation.sound` | inherited | Played when a slot of this button is revealed, overriding the group one |
| `animation.allow-temporary-items` | `true` | Whether a temporary item may be drawn over this button |

A delay shifts only the slots of that button. The next group still waits for it, so groups never
overlap however large a delay is.

With `freeze: true` a button excluded from the animation is visible immediately but still not
clickable: freezing covers the whole inventory until the last slot is revealed.

## Groups

Every animated slot belongs to a group, group 1 by default. Groups play one after the other, and
each may override how it is revealed:

```yaml
open-animation:
  type: SPIRAL           # defaults, used by any group without an override
  interval: 25
  groups:
    2:
      type: FROM_CENTER
      interval: 60
      delay: 150         # pause between the end of group 1 and the start of group 2
```

Ordering is a property of the **group**, not of a button. That is deliberate: it lets one spiral or
one snake sweep across several buttons, which is what makes a border-then-content reveal look like a
single motion instead of two unrelated ones. A button chooses *which* group it is in; the group
decides *how* that group is revealed.

A full example ships in `inventories/examples/animation_advanced.yml`.

## Who it plays for

An animation may be gated behind a requirement, written exactly like the requirements of a button:

```yaml
open-animation:
  type: SPIRAL
  requirement:
    permissions:
      - myserver.fancy
```

A player who does not satisfy it opens the menu complete, as if it had no animation. The requirement
is checked on every play, so it can depend on anything that changes.

`requirements` is accepted as a spelling of `requirement`.

## When the animation plays

**Refreshing never replays the animation.** A page change, a `refresh inventory` action, or anything
else that re-opens the inventory the player is already looking at is treated as a refresh and is
drawn instantly. Without that rule, an animated menu with pagination would replay its whole
animation, and re-freeze itself, on every page click.

For genuine opens, `play` decides:

| Mode | Behaviour |
|---|---|
| `ALWAYS` | Plays every time the player opens the inventory. The default |
| `ONCE` | Plays the first time the player opens this inventory, then never again until they reconnect |
| `ONCE_FOREVER` | Plays once and never again, remembered in the database so it survives a reconnect and a restart |

`FIRST_OPEN` is accepted as a spelling of `ONCE`. `ONCE` keeps its state in memory only and clears
it on disconnect; `ONCE_FOREVER` is the one to use for a welcome flourish that should genuinely
happen once in a player's life.

```yaml
open-animation:
  type: SPIRAL
  play: ONCE_FOREVER
```

### Not too often

`cooldown` sets the least time between two plays for one player. It works with `ALWAYS`, and is
also remembered in the database:

```yaml
open-animation:
  type: SPIRAL
  cooldown: 1h          # a special open at most once an hour
```

Write it as a plain number of milliseconds, or with a unit: `500ms`, `30s`, `10m`, `2h`, `7d`. A
cooldown on a once mode does nothing, since there is no second play to delay.

Both of these are read back from the database once, when the player joins, and answered from memory
afterwards — opening a menu never waits on the database. In the moment between joining and the read
coming back, an animation that remembers itself is held rather than played, so a `ONCE_FOREVER`
flourish is never spent on an open nobody saw.

With the database disabled, `ONCE_FOREVER` and `cooldown` fall back to lasting only for the session,
and a warning says so when the menu is loaded.

To let one play again:

```
/zmenu resetanimation <player> [menu]
```

Without a menu it forgets every animation for that player. Needs `zmenu.animation.reset`.

## Temporary items

Declared under `temporary-items` (or `items`, which is accepted for compatibility but reads badly
next to the `items` of an inventory, which are its buttons).

Items that exist only while the animation plays. They are not buttons — no click, no requirement —
and they are gone once the animation is over.

```yaml
open-animation:
  type: SPIRAL
  interval: 25

  items:
    flash:
      slots:
        - 20
        - 21
      item:
        material: REDSTONE_BLOCK
      show: group:2      # appears when group 2 starts
      hide: group:2.end  # gone when group 2 is over
```

| Key | Default | Description |
|---|---|---|
| `slots` | — | The slots the item occupies, as a list. `slot` accepts a single one |
| `item` | — | The item to display |
| `show` | `start` | When it appears |
| `hide` | `end` | When it is removed |
| `chance` | `100` | The chance of it appearing at all, in percent |
| `chance-per-slot` | `false` | Roll the chance, and pick the item, separately for each slot |
| `choices` | none | Several items to pick one from, instead of `item` |
| `count` | all | Use only this many of its slots, picked at random |
| `use-cache` | `true` | Whether the item may be served from its build cache, like a button's `use-cache` |

`show` and `hide` accept several forms, so you can think in whichever unit fits:

| Form | Meaning |
|---|---|
| `250` | 250 ms after the animation started |
| `group:2` or `group:2.start` | The moment group 2 starts |
| `group:2.end` | The moment group 2 is over |
| `start` / `end` | The beginning of the animation / the end of the last group |

Both ends of a group can be written explicitly; the bare `group:2` is shorthand for `group:2.start`.
The end of a group is not the same time as the start of the next one: the next group may not exist,
and if it declares a `delay` there is a gap between the two.

The same time can also be written as a section, which some people find easier to read:

```yaml
      show:
        group: 2
        at: start      # start by default, or end

      hide:
        at: 600        # `at` alone accepts anything the compact form does
```

The two forms are interchangeable everywhere a time is expected.

An anchor pointing at a group that holds no button resolves to the end of the animation.

A temporary item may sit on a slot no button will ever occupy, so it can draw on an empty part of
the menu.

**Slot ownership.** A temporary item hides the button under it for as long as it is shown. When it
is removed the slot is not blanked but re-resolved: it goes back to the real item if that button
would have been revealed by then, and is left empty otherwise. A button whose own reveal would have
happened behind a temporary item is simply revealed when the item goes away, so it never flashes
through. Nothing is ever left behind: anything still shown at `end` is removed.

**Two temporary items may share a slot.** The one declared last wins while it is shown; when it goes
away the one under it is seen again. A step is only emitted when what the slot displays actually
changes, so removing an item that was hidden under another one shows nothing.

An item placed and removed at the same instant is never displayed, and nothing is emitted for it. A
keyframe landing at or after the end of the animation therefore needs its own `hide`, or the
implicit removal lands on the same instant and the item is not seen.

**A button has to agree to be covered.** Buttons refuse by default, so a temporary item silently
skips their slots; drawing over one is opt in:

```yaml
items:
  content:
    animation:
      allow-temporary-items: true
```

Drawing over a button hides it until the item goes away, which is wanted for a loading state and
unwanted almost everywhere else — hence the default.

For developers, the default is one method, and the file can still set it either way for a button of
its own:

```java
public class MyButton extends Button {

    @Override
    public boolean allowsTemporaryItems() {
        return true;   // false by default
    }
}
```

`ItemDragButton` overrides it to `true`: a drag slot is exactly where a placeholder is useful, since
it shows the player where an item is meant to go. Items drawn by an animation are marked as
belonging to the menu, so closing the inventory in the middle of one never hands them to the
player.

### Frames

`show` and `hide` are a shorthand for two frames. Writing the frames out lets the item change while
it is displayed, and lets each moment play its own sound:

```yaml
    countdown:
      slot: 4
      frames:
        one:
          at: start
          item: { material: RED_STAINED_GLASS_PANE, name: "&c3" }
          sound: { sound: BLOCK_NOTE_BLOCK_HAT, pitch: 0.8 }
        two:
          at: 250
          item: { material: ORANGE_STAINED_GLASS_PANE, name: "&62" }
        gone:
          at: group:2
          remove: true
          sound: { sound: BLOCK_NOTE_BLOCK_BELL }
```

| Key | Required | Description |
|---|---|---|
| `at` | yes | When the frame happens, in either anchor form |
| `item` | unless `remove` | What the temporary item looks like from then on |
| `remove` | no | `true` removes the temporary item at that moment |
| `sound` | no | Played once when the frame happens, whatever the number of slots |
| `chance` | no | The chance of the frame happening, in percent. Ignored on a removal |
| `choices` | no | Several items to pick one from, instead of `item` |

Frame names are labels; only `at` decides the order, so frames written out of order still play in
time order. Frames written at the same time keep the order of the file.

A temporary item with no removal frame is removed when the last group is over, so nothing is ever
left behind. While an item is displayed it hides the button under it, across as many frames as it
lasts, and that button is revealed when the item finally goes away.

## Leaving it to chance

A temporary item does not have to appear every time:

```yaml
temporary-items:
  flash:
    slots:
      - 10-16
    chance: 30            # three opens out of ten
    item: { material: REDSTONE_BLOCK }
    show: start
    hide: 400
```

`chance` is a percentage, like the `chance` of an action: `100` always happens and is the default,
`0` never does. It is rolled **once, when the animation is compiled**, so an item that wins appears
for all of its keyframes rather than flickering, and one that loses is simply not there.

A single keyframe may have its own chance, so an item can appear reliably but change unpredictably:

```yaml
    frames:
      one:   { at: start, item: { material: WHITE_STAINED_GLASS_PANE } }
      flash: { at: 200,   item: { material: REDSTONE_BLOCK }, chance: 50 }
      gone:  { at: 400,   remove: true }
```

**A keyframe that removes the item ignores `chance`** — one that might not happen would leave the
item in the finished menu. The loader warns if you write one.

### One item out of several

`choices` stands in for `item` wherever an item is expected, on the shorthand and on a frame alike:

```yaml
  flash:
    slots:
      - 10-16
    choices:
      redstone:
        material: REDSTONE_BLOCK
      emerald:
        material: EMERALD_BLOCK
        weight: 3         # three times as likely as the redstone
```

`weight` defaults to 1, so choices without one are equally likely.

### Scattering

By default an item rolls once: it appears on all of its slots or on none, showing the same choice
everywhere. `chance-per-slot: true` makes each slot decide for itself, and pick its own item:

```yaml
  sparks:
    slots:
      - 11-15
    chance: 50
    chance-per-slot: true
    choices:
      redstone: { material: REDSTONE_BLOCK }
      emerald:  { material: EMERALD_BLOCK, weight: 3 }
```

Roughly half the slots light up, each with its own item, differently every time the menu opens. That
is the difference between a block that blinks and something that sparkles.

`inventories/examples/animation_loading.yml` has one.

### Exactly so many slots

`chance-per-slot` gives a spread: with `chance: 45` over six slots you might get two one time and
five the next. `count` asks for a fixed number, in different places every time:

```yaml
  sparks:
    slots:
      - 10-16
    count: 3            # three of those seven, wherever they fall
```

The two can be combined, but `count` is usually what you mean when you say "three sparkles".

### Variants

An animation may be several animations, one picked each time it plays:

```yaml
open-animation:
  variants:
    spiral:
      type: SPIRAL
      interval: 25
    sweep:
      file: wave_in     # a variant may name shared files like any animation
      weight: 2         # twice as likely as the spiral
```

`weight` defaults to 1. The picked variant describes everything that happens; only the `chance` and
the `requirement` written next to `variants` still belong to the animation as a whole, so a menu can
be rare *and* varied. A variant declaring variants of its own is reported and its own are ignored.

### Items that change while the animation plays

zMenu caches a built item so it does not rebuild the same stack on every draw. The cache is skipped
on its own when the item needs it — anything containing a `%` placeholder, and a `PLAYER_HEAD` with
no `url` — so most items need nothing from you.

An item that changes for another reason, such as one an item component fills in, opts out the same
way a button does:

```yaml
temporary-items:
  countdown:
    slots: [ 13 ]
    use-cache: false    # rebuilt on every frame
    item:
      material: CLOCK
```

A button's own `use-cache` is obeyed everywhere it applies, including by its `idle-animation`: an
idle animation repaints its slot forever, so a button that asked not to be cached is rebuilt on each
turn.

The menu's `fill-item` takes the same option:

```yaml
fill-item:
  material: GRAY_STAINED_GLASS_PANE
  use-cache: false
```

### Making it personal

By default every play rolls afresh. `seed: player` rolls the same way every time **for a given
player**, so their menu always opens the same variant, with the same scatter and the same choices:

```yaml
open-animation:
  seed: player
  variants:
    spiral: { type: SPIRAL }
    sweep:  { type: WAVE }
```

That turns variety into personality — the menu looks like theirs rather than looking different every
time they open it. The name of the animation is mixed into the seed, so two animations do not end up
producing the same pattern for the same player.

A number instead, such as `seed: 42`, gives everyone the same thing every time, which is useful while
you are designing one and want to see the same result on each reload.

`seed` covers the opening animation only. An idle animation keeps rolling freshly even under a seed,
and deliberately so: it belongs to a button rather than to the menu, a button may have one with no
opening animation at all, and a cycle meant to run for as long as the menu is open should keep
surprising rather than repeat the same order every time.

### The whole animation, now and then

`chance` on the animation itself decides whether it plays at all:

```yaml
open-animation:
  type: SPIRAL
  chance: 20            # two opens out of ten do something special
```

Playing one from a button needs nothing new either: the `animation` action takes the same `chance` as
every other action.

```yaml
    actions:
      - type: animation
        file: pulse
        chance: 20
```

## Changing a menu while it animates

An animation takes a copy of the items when it starts and puts them back as it reveals them, so
anything writing to the menu in between has to be accounted for. Three cases, all handled:

- **A plugin adding an item** with `addItem` while a slot is still hidden. The animation is told, and
  reveals the new item rather than the one it copied. The slot keeps showing whatever the animation
  is drawing there until its turn comes.
- **A head finishing late.** The texture of a player head is looked up off the main thread and can
  land after the animation started; it reaches the animation the same way, so the skin survives the
  reveal.
- **A player putting something in.** On a draggable button of a menu that does not freeze, a player
  can drop an item into a slot the animation still owns. The animation leaves any slot holding
  something it did not draw itself alone, so their item is never overwritten.

A refreshing button is handled separately: it skips its repaint while the animation owns its slot,
and resumes afterwards.

The simplest guarantee remains `freeze: true`, the default, which stops a player interacting at all
until the animation is over.

## Idle animations

An idle animation is not the opening of a menu: it keeps playing on the slots of **one button** for
as long as the menu stays open.

```yaml
items:
  featured:
    slot: 13
    idle-animation:
      interval: 400
      loop: true
      items:
        one:
          material: DIAMOND
        two:
          material: EMERALD
    item:
      material: DIAMOND
      name: "&bFeatured"
```

| Key | Default | Description |
|---|---|---|
| `items` | — | The frames to cycle through, in the order they are written |
| `interval` | `200` | Milliseconds a frame stays, for every frame that declares no `duration` |
| `loop` | `true` | Restart at the end; `false` plays once, then the button goes back to its own item |
| `random` | `false` | Pick the next frame at random instead of taking them in order, ignoring `seed` |

Each entry under `items` is an item, and may also carry two keys of its own:

| Key | Default | Description |
|---|---|---|
| `duration` | `interval` | How long this frame stays, in milliseconds |
| `sound` | none | A sound played when this frame appears, written like any other animation sound |

That is what makes a cycle read as a beat rather than a metronome: hold one picture, flash two
others past, and ring on the one that matters.

```yaml
idle-animation:
  interval: 400
  items:
    resting:
      material: DIAMOND
      duration: 2000      # sits still for two seconds
    flash:
      material: GLOWSTONE
      duration: 80        # then blinks
      sound:
        sound: BLOCK_NOTE_BLOCK_PLING
        pitch: 2.0
    back:
      material: DIAMOND
      duration: 80
```

Idle cycles start somewhere inside their own interval rather than all on the same tick, so several
of them on one menu never march in lockstep.

There is no timeline, no group and no freeze — the button keeps working throughout, and clicking it
does what it always does. It waits for the opening animation to reveal its slot before starting, so
the two never fight over it.

`random: true` picks the next frame at random rather than taking them in order, which turns a short
list into something that never quite repeats.

A frame lasts its own duration rather than the cycle running on one fixed period, so mixing a long
frame with short ones costs no extra work, and a button covering many slots runs one cycle for all
of them: its slots change together and a frame sound is heard once. The slots are only repainted
when the frame actually changes.

A menu with few idle buttons gives each its own task. From `shared-idle-timer-threshold` in
`config.yml` on, 8 by default, one timer per viewer drives all of them, so a busy menu stays cheap
however many buttons cycle; frames then land on the tick, which only matters for durations that are
not a multiple of 50 ms. Lower it on a busy server, or set it to 0 to always share.

Clicking a button that rebuilds itself, with `refresh-on-click`, does not restart its cycle: it
carries on from the frame it was showing.

Keep `interval` sane: an idle animation repaints its slots forever, and a menu full of them at 50 ms
is a menu repainting itself twenty times a second. Avoid pairing one with `update` or a refresh
requirement on the same button, since both repaint the same slot.

A cycle stops the moment the menu closes, including when the player dies with it open, so nothing
is left ticking behind a menu nobody is looking at.

An example ships in `inventories/examples/animation_idle.yml`.

### Sharing one

An idle animation may live in the `animations` folder and be used by name, exactly like an opening
animation — `file`, `files` and `extends` all work, and anything written next to them wins:

```yaml
items:
  featured:
    slot: 13
    idle-animation:
      file: heartbeat
      interval: 300     # overrides what the file says
    item:
      material: DIAMOND
```

Such a file holds what the section holds — `interval`, `loop`, `random` and `items` — rather than
the `type` and groups of an opening animation. `animations/heartbeat.yml` ships as one.

The other way is a pattern: a `patterns` file holding a button with its `idle-animation` is reused
like any other pattern, and every menu using it gets the animation with it. A pattern shares the
whole button; `file` shares only the cycle, so use it when the buttons differ but should beat
together.

## Sounds

A sound may be attached to the whole animation, to one group, or to one button, the narrowest
winning:

```yaml
open-animation:
  type: SPIRAL
  sound:
    sound: BLOCK_NOTE_BLOCK_HAT
    pitch: 1.5
  groups:
    2:
      sound:
        sound: BLOCK_NOTE_BLOCK_BELL
```

| Key | Default | Description |
|---|---|---|
| `sound` | — | The sound name |
| `pitch` | `1` | Pitch. Two values, such as `[0.8, 1.2]`, pick one inside that range on every play |
| `volume` | `1` | Volume |
| `category` | `MASTER` | Sound category |

**At most one sound plays per tick.** Revealing nine slots at once with `slots-per-step: 9`, a
keyframe landing on the same moment as a reveal, or an `interval` under 50 ms putting several steps
in the same tick, plays the sound once rather than several times — otherwise it is heard as noise
rather than as an effect. Keyframes carry their own sound and follow the same rule. An animation cut
short, by a click or another animation taking over, finishes silently.

## Running something around it

An animation may run actions when it starts and when it ends, written exactly like the actions of a
button:

```yaml
open-animation:
  type: SPIRAL
  start-actions:
    - type: sound
      sound: BLOCK_CHEST_OPEN
  end-actions:
    - type: message
      messages:
        - "&7Welcome, %player%."
```

`start-actions` run once the animation is known to be playing — after its chance, its requirement and
its variant have been settled, so they never fire for an animation that does not happen.

`end-actions` run when it is over **however it ended**: played to its last step, cut short by another
animation taking over, or the player closing the menu. Anything that must happen exactly once, such
as unlocking something, belongs there rather than in `close-actions`.

Both take the full action vocabulary, including `delay`, `chance` and requirements.

## Playing an animation on an open menu

An animation does not have to be the opening of a menu. It can be played on an inventory the player
is already looking at, which is what an overlay is for.

| Playback | Behaviour |
|---|---|
| `REVEAL` | The buttons are hidden and revealed again, as opening the menu does |
| `OVERLAY` | Only the temporary items play; the buttons are never hidden and keep working |
| `CONCEAL` | The buttons are taken away one by one, the reverse of `REVEAL`. What a closing menu plays |

### From a menu

The `animation` action plays one on the inventory the player is looking at:

```yaml
actions:
  - type: animation
    file: pulse         # a name from the animations folder
    mode: OVERLAY       # OVERLAY (default), REVEAL or CONCEAL
    freeze: false       # optional, overrides the default of the mode
```

| Key | Default | Description |
|---|---|---|
| `file` | — | The animation to play, by name |
| `animation` | — | An animation written inline, instead of `file` |
| `mode` | `OVERLAY` | How to play it, from the table above |
| `freeze` | per mode | Whether the menu ignores clicks until it is over |
| `stop` | `false` | Finish whatever is playing, at once, instead of playing something |

`mode: CONCEAL` empties the menu **and leaves it empty** — it takes the buttons away without
closing anything, since an action has no say over the window. It is there for a menu that replaces
its own contents straight after, not as a way of closing one: for that, give the inventory a
`close-animation` and use a `close` action.

A named animation is resolved when the action runs, so `/zm reload` is enough to change it. An
animation short enough to not deserve a file of its own can be written inline:

```yaml
    actions:
      - type: animation
        animation:
          type: SEQUENTIAL
          interval: 40
          temporary-items:
            sparkle:
              slots: [10-16]
              show: start
              hide: 400
              item: { material: GLOWSTONE }
```

`inventories/examples/animation_action.yml` has a button for each of these.

### On the way out

`close-animation` is played when the menu closes itself, taking the buttons away instead of putting
them there. It is written exactly like an opening animation:

```yaml
name: "&8Example"
size: 27

open-animation:
  type: SPIRAL
  interval: 25

close-animation:
  type: SPIRAL
  interval: 20
  reverse: true         # unwinds the way it came in
```

It plays for a close the **menu** decides on: a `close` action, a `back` action, and an `inventory`
action switching to another menu. The next menu opens once it is over.

It does **not** play when the player presses escape. Nothing can hold a window open once the client
has closed it, and a menu that came back for a moment after you dismissed it would read as a bug
rather than a flourish. Escape stays instant, which is what a player asking to leave wants.

If the player presses escape *during* one, it simply stops where it is and no menu is opened behind
them.

`freeze` defaults to true here as everywhere, which is what you want: a menu on its way out should
not take clicks on buttons that are half gone. Set `freeze: false` only if you mean it.
`max-animation-duration` in the config applies as it does anywhere else, so a long one cannot hold
someone hostage.

**A button with `animation: false` is still taken away.** That flag means "do not animate me in",
and it is about arriving: honouring it on the way out would strand the button alone in an empty
menu until the window shut. Its `group` and `delay` still apply, so it leaves with its group.

**`play` and `cooldown` do nothing here** and are reported when the menu loads. They belong to the
remembering an opening animation does, which a close does not go through — there is no second close
of a menu you have already left. `chance` and `requirement` do work, and are the way to make a close
animation occasional or conditional.

## How it works, and what that implies

The buttons are built normally first — click handlers, requirements and refresh tasks are all
registered as usual — and only then are the displayed ItemStacks withheld and put back step by step.
Nothing about button behaviour changes; only what the player sees is delayed.

Three consequences worth knowing:

- **Freeze exists because the buttons are already live.** A button that has not appeared yet can
  still be clicked, since its handler is registered. With `freeze: true` the inventory ignores every
  click until the last slot is revealed. Set it to `false` only when an early click is harmless.
- **A refreshing button cannot reveal itself early.** A button whose `update-interval` is shorter
  than the animation skips its repaint while the animation still owns the slot, and resumes its
  normal schedule once the slot has been revealed.
- **The animation never loses items.** If the server shuts down mid-animation, every remaining slot
  is filled in at once. On close the task is cancelled with the inventory's other timers.
- **Jitter keeps a reveal from looking mechanical.** `interval-jitter: 15` lets each step land up to
  15 ms either side of its scheduled time. It never produces a negative time, and it is taken from
  the same source as every other roll.
- **A very long animation never freezes the menu.** An animation lasting more than
  `max-animation-duration` in `config.yml`, 10 seconds by default, is reported in the console and
  plays without freezing, so a mistyped `interval` cannot lock a menu.

Grid based orders (`HORIZONTAL_SNAKE`, `VERTICAL_SNAKE`, `SPIRAL`, `FROM_CENTER`) assume a 9 wide
grid, which covers every chest size. An inventory whose size is not a multiple of 9, such as a
hopper, is treated as a single row, so those types degrade to `SEQUENTIAL` rather than computing
meaningless coordinates.

## Buttons in the player inventory

Buttons placed in the player inventory take part in the animation like any other, and are laid out
**after** the slots of the menu, in the order they appear on screen: the three rows of storage, then
the hotbar. A spiral or a snake therefore sweeps from the menu straight into the player inventory
without a seam, and `MATRIX` or `CUSTOM` can address those slots by continuing past the size of the
menu — slot 54 of a double chest menu is the first slot of the player inventory.

**Only the slots a button of the menu occupies take part.** Whatever the player was carrying is left
alone: it is never hidden, never covered and never touched, whatever the animation asks for.

Nothing has to be configured for this; a button with `isPlayerInventory: true` is animated by the
same `open-animation` block as the rest of the menu, and picks its group and delay the same way.
