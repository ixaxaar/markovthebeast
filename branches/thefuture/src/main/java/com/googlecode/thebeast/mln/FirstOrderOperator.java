package com.googlecode.thebeast.mln;

import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.Tuple;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public interface FirstOrderOperator {

  double evaluate(World world,
                  UserPredicate predicate,
                  Collection<Tuple> tuples);

}
