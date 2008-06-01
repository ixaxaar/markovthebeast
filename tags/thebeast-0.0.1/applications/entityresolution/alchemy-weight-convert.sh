java -cp ../../classes/production/:../../lib/jline-0.9.9.jar:../../lib/java-cup-11a.jar:../../lib/lpsolve55j.jar:../../lib/ thebeast.util.alchemy.AlchemyWeightSetter \
  dump model.pml weights/for-fold-0.weights.dmp bibserv-weight-order.txt \
  < bibserv.withneg.weights > /tmp/test.mln