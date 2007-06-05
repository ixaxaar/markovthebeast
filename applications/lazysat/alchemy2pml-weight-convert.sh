java -cp ../../classes/production/:../../lib/jline-0.9.9.jar:../../lib/java-cup-11a.jar:../../lib/lpsolve55j.jar:../../lib/ thebeast.pml.corpora.AlchemyWeights2TheBeast \
  model.pml bibserv-weight-order.txt \
  < corpora/cora/folds/cora-all.trained-for-fold0of10.mln > /tmp/test.weights