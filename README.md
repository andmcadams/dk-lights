# Dorgesh-Kaan Lights Plugin
A plugin to help you identify which lamps need to be fixed.

Dorgesh-Kaan is split into two map squares and three planes, resulting in six "areas".
A varbit (4038) gives information about which lamps are broken. This varbit is essentially a bitmap, where each active bit indicates a broken lamp.
The position of the bit and the area that the player is in can be used to immediately figure out which lamps are out in the current area.
Additionally, some bits are shared between areas on the same plane. Some low order bits (little endian) are shared between areas on the same plane as well.

## Usage
With the plugin on, run around Dorgesh-Kaan. As you pass into each of the six "areas", the overlay will update to let you know where the broken lamps are.
Each lamp has a hint associated with it, but due to the lack of well-known landmarks in the area, the hints are sometimes vague.
The closest lamp will be indicated by a hint arrow. Note that due to the layout of the city, the closest point will not necessarily take the fewest steps to reach.

There are always ten broken lamps for a player.
When a lamp is fixed, it will be removed from the overlay. 
If multiple lamps have the same hint, they will be collapsed in the overlay and indicated by a (xN) appended at the end.
