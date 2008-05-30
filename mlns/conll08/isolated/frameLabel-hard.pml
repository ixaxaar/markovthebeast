/*
 * The following formulas ensure consistency for frameLabel:
 *          if isArgument it has to had a frameLabel
 */

//factor[0]: for Int a if word(a,_): isPredicate(a) => |FrameLabel f: frameLabel(a,f)| >= 1;
//factor[0]: for Int a, FrameLabel f if word(a,_): frameLabel(a,f) => isPredicate(a);
factor: for Int a if word(a,_): |FrameLabel f: frameLabel(a,f)| <= 1;
