package com.googlecode.thebeast.inference.firstorder;

import com.googlecode.thebeast.world.World;

/**
 * @author Sebastian Riedel
 */
public class FirstOrderMAPResult {

    private World world;

    public FirstOrderMAPResult(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
