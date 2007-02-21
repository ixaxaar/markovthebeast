package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemSearchPlan {

  final MemSearchAction[] actions;
  MemDim resultDim;


  public MemSearchPlan(MemDim resultDim, MemSearchAction ... actions) {
    this.resultDim = resultDim;
    this.actions = actions;
  }

  public MemSearchPlan(MemSearchAction... actions) {
    this.actions = actions;
    resultDim = new MemDim(0,0,0);
  }

  



}
