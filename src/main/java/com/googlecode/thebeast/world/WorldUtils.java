package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.query.Variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public class WorldUtils {




    /**
     * This method resolves general objects and transforms them into terms. If an argument can not be resolved the given
     * a new variable is created with the name of the argument (after transforming it to a String via toString()) and
     * added to the map.
     *
     * @param pred      the predicate for which the objects are arguments.
     * @param args      the predicate arguments, either terms or general java objects.
     * @return a list of terms corresponding to the given arguments.
     */
    public static List<Term> resolveArguments(final Predicate pred, final Object... args) {
        return resolveArguments(pred, new LinkedHashMap<String, Variable>(), args);
    }

    /**
     * This method resolves general objects and transforms them into terms. If an argument can not be resolved the given
     * map of variables is used to get a variable for the argument (after transforming the argument to a string). If no
     * variable with the given name exists a new variable is created and added to the map.
     *
     * @param pred      the predicate for which the objects are arguments.
     * @param variables the variable mapping (may be altered by this method!)
     * @param args      the predicate arguments, either terms or general java objects.
     * @return a list of terms corresponding to the given arguments.
     */
    public static List<Term> resolveArguments(final Predicate pred,
                                              final Map<String, Variable> variables,
                                              final Object... args) {

        List<Term> argTerms = new ArrayList<Term>();
        for (int i = 0; i < args.length; ++i) {
            Type type = pred.getArgumentTypes().get(i);
            if (args[i] instanceof Term) {
                argTerms.add((Term) args[i]);
            } else {
                String arg = args[i].toString();
                if (type.containsConstantWithName(arg)) {
                    argTerms.add(type.getConstant(arg));
                } else {
                    Variable var = variables.get(arg);
                    if (var == null) {
                        var = new Variable(arg, type);
                        variables.put(arg, var);
                    }
                    argTerms.add(var);
                }
            }
        }

        return argTerms;
    }

}
