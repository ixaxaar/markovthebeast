package com.googlecode.thebeast.world;

/**
 * A UserConstant is a constant defined by the user.
 *
 * @author Sebastian Riedel
 */
public interface UserConstant extends Symbol, Constant {
    /**
     * A UserConstant has an integer id the can be used to represent the constant more compactly than by its name. The id
     * is assigned by the UserType that created and owns this constant.
     *
     * @return the id number of this constant.
     */
    int getId();

    /**
     * Method getUserType returns the type of this constant typed as a UserType, since a UserConstant will always belong
     * to a UserType.
     *
     * @return the userType (type UserType) of this UserConstant object.
     */
    UserType getUserType();
}
