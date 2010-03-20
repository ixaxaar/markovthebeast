package org.riedelcastro.thebeast.env.combinatorics

import org.riedelcastro.thebeast.env._
import doubles.{DoubleConstant, DoubleTerm}
import collection.mutable.{HashSet, Stack, HashMap, MultiMap}
import java.lang.String

/**
 * A SpanningTreeConstraint is a term that maps graphs to 1 if they are
 * projective spanning trees over the set of vertices, and to 0 otherwise. Note
 * that for efficient processing vertices and root need to be ground
 * and edges needs to be a predicate.
 */
case class SpanningTreeConstraint[V](edges: Term[FunctionValue[(V, V), Boolean]],
                                     vertices: Term[FunctionValue[V, Boolean]],
                                     root: Term[V],
                                     order: Term[FunctionValue[(V, V), Boolean]]) extends DoubleTerm {
  def ground(env: Env): DoubleTerm = {
    SpanningTreeConstraint(edges.ground(env), vertices.ground(env), root.ground(env), order.ground(env))
  }

  def simplify: DoubleTerm = {
    val simplified = SpanningTreeConstraint(edges.simplify, vertices.simplify, root.simplify, order.simplify)
    val constant = simplified.eval(EmptyEnv)
    if (constant.isDefined) DoubleConstant(constant.get) else simplified
  }

  def upperBound = 1.0

  def subterms = Seq(edges, vertices, root)

  def eval(env: Env): Option[Double] = {
    //get edges map
    val v = Set() ++ env(vertices).getSources(Some(true))
    val e = env(this.edges).getSources(Some(true)).filter(edge => v(edge._1) && v(edge._2))
    val r = env(root)
    val heads = new HashMap[V, V]
    //check if each vertex has at most one parent
    for (edge <- e) {
      if (heads.contains(edge._2)) return Some(0.0)
      heads(edge._2) = edge._1
    }
    //check if each vertex has at least one parent, unless it's the root
    if (v.exists(vertex => vertex != r && !heads.isDefinedAt(vertex))) return Some(0.0)
    val indices = new HashMap[V, Int]
    val lowlinks = new HashMap[V, Int]
    val stack = new Stack[V]
    val roots = new HashSet[V]
    var index = 0
    //check for cycles
    for (vertex <- v) {
      if (!indices.isDefinedAt(vertex)) tarjan(vertex)
      if (!roots.isEmpty) return Some(0.0)
    }
    def tarjan(vertex: V) {
      indices(vertex) = index
      lowlinks(vertex) = index
      index += 1
      stack.push(vertex)
      for (head <- heads.get(vertex)) {
        if (!indices.isDefinedAt(head)) {
          tarjan(head)
          lowlinks(vertex) = Math.min(lowlinks(vertex), lowlinks(head))
        } else if (stack.contains(head)) {
          lowlinks(vertex) = Math.min(lowlinks(vertex), indices(head))
        }
      }
      if (lowlinks(vertex) == indices(vertex)) {
        if (stack.top != vertex) roots += vertex
        var top = vertex
        do {
          top = stack.pop
        } while (top != vertex)

      }
    }
    //test projectiveness
    val lessThan = EmptyEnv(this.order)

    //sort vertices according to order
    val sorted = v.toList.sort((x, y) => x == root || lessThan(x, y)).toArray
    val n = sorted.size
    val vertex2index = Map() ++ (for (i <- 0 until n) yield sorted(i) -> i)
    //mapping from vertex to children
    val edges = for (i <- 1 until n) yield (vertex2index(heads(sorted(i))), i)

    def cross(e1: (Int, Int), e2: (Int, Int)): Boolean = {
      val e1l = Math.min(e1._1, e1._2)
      val e1r = Math.max(e1._1, e1._2)
      val e2l = Math.min(e2._1, e2._2)
      val e2r = Math.max(e2._1, e2._2)
      !(e1l >= e2l && e1r <= e2r || e2l >= e1l && e2r <= e1r || e2r <= e1l || e1r <= e2l)
    }
    //todo this should be doable in O(n)
    for (e1 <- edges; e2 <- edges; if (e1 != e2)) {
      if (cross(e1, e2)) return Some(0.0)
    }
    Some(1.0)
  }


  def values = Values(0.0, 1.0)

  def variables = {
    if (vertices.isGround && root.isGround && order.isGround && edges.isInstanceOf[Predicate[_]]) {
      linkVariables.asInstanceOf[Set[EnvVar[Any]]]
    } else
      edges.variables ++ vertices.variables ++ root.variables ++ order.variables
  }

  private def linkVariables: scala.collection.immutable.Set[FunAppVar[(V, V), Boolean]] = {
    val pred = edges.asInstanceOf[Predicate[(V, V)]]
    val v = EmptyEnv(vertices).getSources(Some(true))
    val r = EmptyEnv(root)
    Set() ++ (for (source <- v; dest <- v; if (dest != r && dest != source))
    yield FunAppVar(pred, (source, dest)))
  }


  override def marginalize(incoming: Beliefs[Any, EnvVar[Any]]): Beliefs[Any, EnvVar[Any]] = {
    if (vertices.isGround && root.isGround && order.isGround && edges.isInstanceOf[Predicate[_]]) {

      val pred = edges.asInstanceOf[Predicate[(V, V)]]
      val vertices = EmptyEnv(this.vertices).getSources(Some(true))
      val root = EmptyEnv(this.root)
      val lessThan = EmptyEnv(this.order)

      //sort vertices according to order
      val sorted = vertices.toList.sort((x, y) => x == root || lessThan(x, y)).toArray

      //a la Smith and Eisner 2008
      //weights are by default 0
      val weights = new HashMap[(Int, Int), Double] {
        override def default(p: (Int, Int)) = 0.0
      }
      var pi = 1.0
      //calculate weights and pi
      for (i <- 0 until sorted.size; j <- 1 until sorted.size; if (i != j)) {
        val belief = incoming.belief(FunAppVar(pred, (sorted(i), sorted(j))))
        weights(i -> j) = belief.belief(true) / belief.belief(false)
        pi *= belief.belief(false)
      }
      //calculate total weights of all trees with a given edge, and partitition function
      val insideOutside = InsideOutsideAlgorithm.calculate(sorted, weights)
      //partition function a la S&E 08
      val b = insideOutside.Z * pi

      //calculate beliefs for true and false states
      val beliefs = new MutableBeliefs[Any, EnvVar[Any]]
      for (i <- 0 until sorted.size; j <- 1 until sorted.size; if (i != j)) {
        val atom = FunAppVar(pred, (sorted(i), sorted(j)))
        println("%d %d: %f".format(i, j, insideOutside.total(i, j)))
        val trueBelief = insideOutside.total(i, j) * pi
        beliefs.increaseBelief(atom, true, trueBelief)
        beliefs.increaseBelief(atom, false, b - trueBelief)
      }
      beliefs
    } else
      super.marginalize(incoming)
  }


  object InsideOutsideAlgorithm {
    object SpanType extends Enumeration {
      type SpanType = Value
      val RightParent, LeftParent, NoParents = Value

      val parents = Seq(LeftParent, RightParent)

      def opposite(value: Value) = value match {
        case LeftParent => RightParent
        case RightParent => LeftParent
        case _ => NoParents
      }

      def toType(left: Boolean, right: Boolean): Value = {
        if (left && !right) LeftParent
        else if (!left && right) RightParent
        else if (!left && !right) NoParents
        else null
      }

    }
    import SpanType._


    case class Signature(from: Int, to: Int, left: Boolean, right: Boolean, simple: Boolean) {
      override def toString: String = "(%d,%d,%s,%s,%s)".format(from, to, left, right, simple)
    }
    sealed trait Operation {
      def eval:Signature
    }
    case class Seed(index: Int) extends Operation {
      val eval = Signature(index,index+1,false,false,true)
    }
    case class CloseRight(sig: Signature) extends Operation{
      val eval = Signature(sig.from, sig.to, true, false, true)
    }
    case class CloseLeft(sig: Signature) extends Operation {
      val eval = Signature(sig.from, sig.to, false, true, true)
    }
    case class Join(l: Signature, r: Signature) extends Operation {
      val eval = Signature(l.from,r.to,l.left,r.right,false)
      val defined = l.right != r.left && l.simple
    }



    class InsideOutsideResult {
      val inside = new HashMap[Signature, Double]
      val outside = new HashMap[Signature, Double]
      val total = new HashMap[(Int, Int), Double]
      var Z = 0.0

      def incrOut(sig:Signature, value:Double) =
        outside(sig) = outside.getOrElse(sig, 0.0) + value
      def incrIn(sig:Signature, value:Double) =
        inside(sig) = inside.getOrElse(sig, 0.0) + value

      def in(sig:Signature) = inside.getOrElse(sig,0.0)
      def out(sig:Signature) = outside.getOrElse(sig,0.0)

    }

    def calculate(sorted: Array[V], weights: scala.collection.Map[(Int, Int), Double]): InsideOutsideResult = {

      val bools = Array(false,true)
      val result = new InsideOutsideResult
      import result._
      def add(op:Operation) = op match {
        case Seed(_) => incrIn(op.eval, 1.0)
        case Join(l,r) => incrIn(op.eval, in(l) * in(r))
        case CloseLeft(sig @ Signature(i,j,_,_,_)) => incrIn(op.eval, in(sig) * weights(i,j))
        case CloseRight(sig @ Signature(i,j,_,_,_)) => incrIn(op.eval, in(sig) * weights(j,i))
      }
      val n = sorted.size

      //initialize inside probs
      for (i <- 0 until n-1){
        add(Seed(i))
        add(CloseLeft(Signature(i,i+1,false,false,true)))
        add(CloseRight(Signature(i,i+1,false,false,true))) 
      }
      for (length <- 2 until n){
        for (i <- 0 until n - length){
          val j = i + length
          for (k <- i + 1 until j){
            println("%d %d %d".format(i,k,j))
            for (b_L <- bools; b <- bools; b_R <- bools; s <- bools){
              val sig_L = Signature(i,k,b_L,b,true)
              val sig_R = Signature(k,j,!b, b_R, s)
              val join = Join(sig_L, sig_R)
              if (join.defined) add(join)
            }
          }
          add(CloseLeft(Signature(i,j,false,false,false)))
          add(CloseRight(Signature(i,j,false,false,false)))
        }
      }
      println(inside.mkString("\n"))
      Z = in(Signature(0,n-1,false,true,false)) + in(Signature(0,n-1,false,true,true))
      println("Z: " + Z)

      

      result
    }

  }

}


/*

David's Ptree factor

class TreeFactor : public Factor {
public:
  TreeFactor(const string& name, int slen, bool multirooted)
    : Factor(name), slen_(slen), multirooted_(multirooted) {
    int max_dim = slen_ + 1;
    worksize = max_dim * max_dim;
    tkirmat = (double *)malloc((worksize + max_dim) * sizeof(double));
    gradmat = (double *)malloc((worksize + max_dim) * sizeof(double));
  }
  virtual ~TreeFactor() {
    if ( tkirmat ) free(tkirmat);
    if ( gradmat ) free(gradmat);
  }

  virtual double compute_messages(Vertex v, Graph& g, double damp) {
    // cerr << "# compute_messages TreeFactor" << endl;
    EdgeIterator e = out_edges(v, g).first;
    vector<int> heads(slen_);
    for ( int kid = 1; kid <= slen_; ++kid ) {
      int tkoff = kid * slen_;
      tkirmat[tkoff + kid - 1] = 0;
      int trues = 0, trueMom = -1;
      for ( int mom = 0; mom <= slen_; ++mom ) {
	      if ( mom == kid ) continue;
      	const dvec& m = g[*e++].v2f;
      	// cerr << "# " << mom << " -> " << kid << ": " << m << endl;
      	if ( m(0) == 0 ) {
	         ++trues;
	         trueMom = mom;
	          continue;
	      }
        double score = m(1) / m(0);
        tkirmat[mom * slen_ + kid - 1] = -score;
        tkirmat[tkoff + kid - 1] += score;
      }
      if ( trues == 1 ) {
          heads[kid-1] = trueMom;
          tkirmat[tkoff + kid - 1] = 1;
          for ( int mom = 0; mom <= slen_; ++mom ) {
            if ( kid == mom ) continue;
            tkirmat[mom * slen_ + kid - 1] = ( mom == trueMom ) ? -1 : 0;
          }
      } else if ( trues > 1 ) {
        	heads[kid-1] = -2;
      } else {
        	heads[kid-1] = -1;
      }
    }
    slog Z = sum_tree();
    e = out_edges(v, g).first;
    if ( Z == 0 ) {
      for ( int kid = 1; kid <= slen_; ++kid ) {
        double Z = tkirmat[kid * (slen_ + 1) - 1];
        int koff = (kid - 1) * slen_;
        int head = heads[kid-1];
        for ( int mom = 0; mom <= slen_; ++mom ) {
          if ( kid != mom ) {
            dvec m(2);
            if ( head == -2 ) {
              m(1) = R_NaN;
              m(0) = R_NaN;
            } else if ( head == -1 ) {
              m(1) = 1;
              m(0) = Z + tkirmat[mom * slen_ + kid - 1];
              m /= sum(m);
            } else if ( head == mom ) {
              m = 0, 1;
            } else {
              m = 1, 0;
            }
            damp_assign(g[*e++].f2v, m, damp);
          }
        }
      }
      return 0;
    }
    for ( int kid = 1; kid <= slen_; ++kid ) {
      int koff = (kid - 1) * slen_;
      int tkoff = kid * slen_;
      int head = heads[kid-1];
      for ( int mom = 0; mom <= slen_; ++mom ) {
        if ( mom == kid ) continue;
        dvec m(2);
        if ( head == -2 ) {
          m(1) = R_NaN;
          m(0) = R_NaN;
        } else if ( head == -1 ) {
          m(1) = gradmat[koff + mom - ((mom > kid) ? 1 : 0)];
          m(0) = 1 + tkirmat[mom * slen_ + kid - 1] * m(1); // tkirmat neg
        } else if ( head == mom ) {
          m = 0, 1;
        } else {
          m = 1, 0;
        }
        damp_assign(g[*e++].f2v, m, damp);
      }
    }
    return 0;
  }

private:
  virtual slog sum_tree() {}

protected:
  int slen_;
  bool multirooted_;
  int worksize;
  double *tkirmat, *gradmat;
};



class PTreeFactor : public TreeFactor {
public:
  PTreeFactor(const string& name, int slen, bool multirooted)
    : TreeFactor(name, slen, multirooted),
      sch(extents[slen+1][slen+1]),
      gch(extents[slen+1][slen+1]) {}

private:
  virtual slog sum_tree() {
    int r;
    double res;
    for ( int i = 0; i < slen_*slen_; ++i ) gradmat[i] = R_NegInf;
    for ( int s = 0; s <= slen_; ++s )
      for ( int i = 0; i <= 1; ++i )
	for ( int j = 0; j <= 1; ++j )
	  sch[s][s].val[i][j] = 0;
    int start = multirooted_ ? 0 : 1;
    for ( int width = 1; width <= slen_; ++width ) {
      for ( int s = start; s <= slen_; ++s ) {
	int t = s + width;
	if ( t > slen_ ) break;
	scell *cc = &sch[s][t];
	for ( int i = 0; i <= 1; ++i )
	  for ( int j = 0; j <= 1; ++j )
	    cc->val[i][j] = R_NegInf;
	if ( s > 0 ) {
	  double lkid = log(-tkirmat[t * slen_ + s-1]);
	  for ( r = s; r < t; ++r ) {
	    log_incr(cc->val[0][0],
		     sch[s][r].val[1][1] + sch[r+1][t].val[0][1] + lkid);
	  }
	}
	double rkid = log(-tkirmat[s * slen_ + t-1]);
	for ( r = s; r < t; ++r ) {
	  log_incr(cc->val[1][0],
		   sch[s][r].val[1][1] + sch[r+1][t].val[0][1] + rkid);
	}
	for ( r = s; r < t; ++r ) {
	  log_incr(cc->val[0][1], sch[s][r].val[0][1] + sch[r][t].val[0][0]);
	}
	for ( r = s+1; r <= t; ++r ) {
	  log_incr(cc->val[1][1], sch[s][r].val[1][0] + sch[r][t].val[1][1]);
	}
      }
    }
    if ( !multirooted_ ) {
      scell *cc = &sch[0][slen_];
      cc->val[1][1] = R_NegInf;
      for ( r = 1; r <= slen_; ++r ) {
	log_incr(cc->val[1][1],
		 sch[1][r].val[0][1] + sch[r][slen_].val[1][1] + log(-tkirmat[r - 1]));
      }
    }
    res = sch[0][slen_].val[1][1];
    for ( int s = 0; s <= slen_; ++s ) {
      for ( int t = s; t <= slen_; ++t ) {
	for ( int i = 0; i <= 1; ++i ) {
	  for ( int j = 0; j <= 1; ++j ) {
	    gch[s][t].val[i][j] = R_NegInf;
	  }
	}
      }
    }
    gch[0][slen_].val[1][1] = -res;
    if ( !multirooted_ ) {
      for ( r = 1; r <= slen_; ++r ) {
	log_incr(gch[1][r].val[0][1],
		 -res + sch[r][slen_].val[1][1] + log(-tkirmat[r - 1]));
	log_incr(gch[r][slen_].val[1][1],
		 -res + sch[1][r].val[0][1] + log(-tkirmat[r - 1]));
	log_incr(gradmat[(r - 1) * slen_],
		 -res + sch[1][r].val[0][1] + sch[r][slen_].val[1][1]);

      }
    }
    for ( int width = slen_; width >= 1; --width ) {
      for ( int s = start; s <= slen_; ++s ) {
	int t = s + width;
	if ( t > slen_ ) break;
	scell *gc = &gch[s][t];
	double gpar = gc->val[1][1];
	for ( r = s+1; r <= t; ++r ) {
	  log_incr(gch[s][r].val[1][0], gpar + sch[r][t].val[1][1]);
	  log_incr(gch[r][t].val[1][1], gpar + sch[s][r].val[1][0]);
	}
	gpar = gc->val[0][1];
	for ( r = s; r < t; ++r ) {
	  log_incr(gch[s][r].val[0][1], gpar + sch[r][t].val[0][0]);
	  log_incr(gch[r][t].val[0][0], gpar + sch[s][r].val[0][1]);
	}

	if ( s > 0 ) {
	  double lgrad = R_NegInf;
	  double lkid = log(-tkirmat[t * slen_ + s-1]);
	  gpar = gc->val[0][0];
	  for ( r = s; r < t; ++r ) {
	    log_incr(gch[s][r].val[1][1],
		     gpar + sch[r+1][t].val[0][1] + lkid);
	    log_incr(gch[r+1][t].val[0][1],
		     gpar + sch[s][r].val[1][1] + lkid);
	    log_incr(lgrad,
		     gpar + sch[s][r].val[1][1] + sch[r+1][t].val[0][1]);
	  }
	  log_incr(gradmat[(s-1) * slen_ + t-1], lgrad);
	}

	double rkid = log(-tkirmat[s * slen_ + t-1]);
	double rgrad = R_NegInf;
	gpar = gc->val[1][0];
	for ( r = s; r < t; ++r ) {
	  log_incr(gch[s][r].val[1][1],
		   gpar + sch[r+1][t].val[0][1] + rkid);
	  log_incr(gch[r+1][t].val[0][1],
		   gpar + sch[s][r].val[1][1] + rkid);
	  log_incr(rgrad,
		   gpar + sch[s][r].val[1][1] + sch[r+1][t].val[0][1]);
	}
	log_incr(gradmat[(t-1) * slen_ + s], rgrad);
      }
    }

    for ( int i = 0; i < slen_*slen_; ++i ) gradmat[i] = exp(gradmat[i]);

    return slog(res, 1);

  }

  struct scell {
    double val[2][2];
  };
  multi_array<scell, 2> sch;
  multi_array<scell, 2> gch;
};


*/